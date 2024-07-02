package com.github.test;

import com.github.test.entity.nutrition.*;
import com.github.test.util.FormattedLogger;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class PlayerNutritionService implements Listener, PlayerNutritionListenerManager {
    private static final Duration DEFAULT_EXHAUSTION_DELAY = Duration.ofSeconds(10);
    private static final int DEFAULT_EXHAUSTION_DECREMENT = 1;

    private final NutritionFactMap nutritionFactMap;
    private final Connection connection;
    private final FormattedLogger logger;
    private final NamespacedKey nutritionKey;
    private final ConcurrentMap<UUID, PlayerNutrition> playerNutritionMap;
    private final ConcurrentMap<UUID, ScheduledExecutorService> executorServiceMap;
    private final NutritionContainer nutritionContainer;

    private final Set<PlayerNutritionListener> listeners;

    private Duration exhaustionDelay;
    private int decrement;
    private Set<NutritionFact> decrementNutritionFacts;

    public PlayerNutritionService(
            Plugin plugin,
            NutritionFactMap nutritionFactMap,
            Connection connection,
            FormattedLogger logger) {
        this.nutritionFactMap = nutritionFactMap;
        this.connection = connection;
        this.logger = logger;
        this.nutritionKey = new NamespacedKey(plugin, "nutrition");
        this.playerNutritionMap = new ConcurrentSkipListMap<>();
        this.executorServiceMap = new ConcurrentSkipListMap<>();
        this.nutritionContainer = new NutritionContainer(connection);

        this.listeners = new CopyOnWriteArraySet<>();

        this.exhaustionDelay = DEFAULT_EXHAUSTION_DELAY;
        this.decrement = DEFAULT_EXHAUSTION_DECREMENT;
        this.decrementNutritionFacts = calculateDecrementNutritionFacts();

        initializeSqlTable();
        addListener((PlayerNutritionAcquireListener) event -> {
            Player player = event.getPlayer();
            PlayerNutrition nutrition = playerNutritionMap.get(player.getUniqueId());
            player.sendMessage(Component.text("now your nutrition status is " + nutrition));
        });
    }

    private static int trimRange(int x) {
        return Math.max(0, Math.min(10, x));
    }

    public NamespacedKey getNutritionKey() {
        return this.nutritionKey;
    }

    public Map<UUID, PlayerNutrition> getPlayerNutritionMap() {
        return Collections.unmodifiableMap(this.playerNutritionMap);
    }

    public Duration getExhaustionDelay() {
        return this.exhaustionDelay;
    }

    public void setExhaustionDelay(Duration delay) {
        this.exhaustionDelay = delay;
    }

    public int getExhaustionDecrement() {
        return this.decrement;
    }

    public void setExhaustionDecrement(int decrement) {
        this.decrement = decrement;
        this.decrementNutritionFacts = calculateDecrementNutritionFacts();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!this.nutritionContainer.contains(player)) {
            this.nutritionContainer.create(player);
        }
        this.nutritionContainer
                .get(player)
                .ifPresent(nutrition -> {
                    this.playerNutritionMap.put(UUID.fromString(nutrition.getUuid()), nutrition);
                    this.logger.info("Player %s successfully retrieved nutrition info", player.getName());
                    this.logger.info("%s", nutrition);
                });
        if (this.executorServiceMap.containsKey(uuid)) {
            ScheduledExecutorService executorService = this.executorServiceMap.get(uuid);
            executorService.shutdown();
        }
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorServiceMap.put(uuid, executorService);
        long delay = this.exhaustionDelay.toMillis();
        executorService.scheduleAtFixedRate(() -> updateNutritionFacts(player, this.decrementNutritionFacts, false), delay, delay, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (this.executorServiceMap.containsKey(uuid)) {
            ScheduledExecutorService executorService = this.executorServiceMap.remove(uuid);
            executorService.shutdown();
        }
    }

    @EventHandler
    public void onPlayerCraftItem(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        Recipe recipe = event.getRecipe();
        ItemStack resultItem = recipe.getResult();
        PersistentDataContainer container = resultItem
                .getItemMeta()
                .getPersistentDataContainer();
        if (item == null) {
            return;
        }
        this.logger.info("crafted " + item.getType().name());
        if (container.has(this.nutritionKey)) {
            String nutritionKey = container.get(this.nutritionKey, PersistentDataType.STRING);
            assert nutritionKey != null;
            this.logger.info("find nutrition facts " + nutritionKey);
            item.getItemMeta()
                    .getPersistentDataContainer()
                    .set(this.nutritionKey, PersistentDataType.STRING, nutritionKey);
        }
    }

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        PersistentDataContainer container = item
                .getItemMeta()
                .getPersistentDataContainer();
        if (!container.has(this.nutritionKey)) {
            return;
        }
        Player player = event.getPlayer();
        String key = container.get(this.nutritionKey, PersistentDataType.STRING);
        Set<NutritionFact> nutritionFacts = this.nutritionFactMap.get(key);
        updateNutritionFacts(player, nutritionFacts, true);
    }

    @Override
    public void addListener(PlayerNutritionListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerNutritionListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        this.listeners.clear();
    }

    @Override
    public <L extends PlayerNutritionListener> Set<L> getListener(Class<L> listenerType) {
        return this.listeners
                .stream()
                .filter(listenerType::isInstance)
                .map(listenerType::cast)
                .collect(Collectors.toSet());
    }

    private void initializeSqlTable() {
        try {
            DatabaseMetaData databaseMetaData = this.connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, "player_nutrition", new String[]{"TABLE"});
            try (resultSet) {
                if (!resultSet.next()) {
                    try (Statement statement = this.connection.createStatement()) {
                        statement.execute("""
                                           create table player_nutrition (
                                              uuid         varchar not null constraint player_nutrition_pk primary key,
                                              carbohydrate integer not null,
                                              protein      integer not null,
                                              fat          integer not null,
                                              vitamin      integer not null
                                          )
                                          """);
                        statement.execute("""
                                          create unique index player_nutrition_uuid_uindex
                                             on player_nutrition (uuid)
                                          """);
                        this.logger.info("Successfully create table 'player_nutrition'");
                    }
                } else {
                    this.logger.info("found existing table 'player_nutrition'");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<NutritionFact> calculateDecrementNutritionFacts() {
        return Arrays.stream(Nutrition.values())
                .map(nutrition -> NutritionFact.of(nutrition, -this.decrement))
                .collect(Collectors.toSet());
    }

    private void updateNutritionFacts(Player player, Set<NutritionFact> nutritionFacts, boolean isAcquire) {
        PlayerNutrition playerNutrition = this.playerNutritionMap.get(player.getUniqueId());
        for (NutritionFact nutritionFact : nutritionFacts) {
            switch (nutritionFact.getNutrition()) {
                case CARBOHYDRATE -> {
                    int carbohydrate = playerNutrition.getCarbohydrate();
                    int newCarbohydrate = trimRange(carbohydrate + nutritionFact.getAmount());
                    playerNutrition.setCarbohydrate(newCarbohydrate);
                }
                case PROTEIN -> {
                    int protein = playerNutrition.getProtein();
                    int newProtein = trimRange(protein + nutritionFact.getAmount());
                    playerNutrition.setProtein(newProtein);
                }
                case FAT -> {
                    int fat = playerNutrition.getFat();
                    int newFat = trimRange(fat + nutritionFact.getAmount());
                    playerNutrition.setFat(newFat);
                }
                case VITAMIN -> {
                    int vitamin = playerNutrition.getVitamin();
                    int newVitamin = trimRange(vitamin + nutritionFact.getAmount());
                    playerNutrition.setVitamin(newVitamin);
                }
            }
        }
        if (isAcquire) {
            PlayerNutritionAcquireEvent event = PlayerNutritionAcquireEvent.of(player, playerNutrition);
            getListener(PlayerNutritionAcquireListener.class)
                    .forEach(listener -> listener.onNutritionAcquire(event));
        } else {
            PlayerNutritionConsumeEvent event = PlayerNutritionConsumeEvent.of(player, playerNutrition);
            getListener(PlayerNutritionConsumeListener.class)
                    .forEach(listener -> listener.onNutritionConsume(event));
        }
    }
}
