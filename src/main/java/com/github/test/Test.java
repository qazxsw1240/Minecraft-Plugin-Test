package com.github.test;

import com.github.test.entity.nutrition.Nutrition;
import com.github.test.entity.nutrition.NutritionFact;
import com.github.test.entity.nutrition.NutritionFactMap;
import com.github.test.entity.nutrition.NutritionFactMapImpl;
import com.github.test.item.ItemStackBuilder;
import com.github.test.util.FormattedLogger;
import com.github.test.util.LoggerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Test extends JavaPlugin {
    private final NutritionFactMap nutritionFactMap;
    private final List<Listener> listeners;
    private final FormattedLogger logger;

    private Connection connection;

    public Test() {
        this.nutritionFactMap = new NutritionFactMapImpl();
        this.listeners = new CopyOnWriteArrayList<>();
        this.logger = new LoggerUtil(getLogger());
        this.connection = null;
    }

    @Override
    public void onDisable() {
        this.logger.info("Plugin %s disabled", getClass().getName());
        if (this.connection != null) {
            try {
                this.connection.close();
                this.connection = null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onEnable() {
        createDatabaseConnection();
        registerEvents(
                new PeacefulWorldService(),
                new PlayerNutritionService(this, this.nutritionFactMap, this.connection, this.logger),
                new ExperienceService(this.logger),
                ChatService.getInstance());

        NamespacedKey nutritionKey = getListener(PlayerNutritionService.class)
                .map(PlayerNutritionService::getNutritionKey)
                .orElseThrow();

        ItemStack warriorSword = ItemStackBuilder.of(Material.DIAMOND_SWORD)
                .setMetaBuilder(meta -> meta.displayName(Component.text("갓칼", Style.style(TextColor.color(0, 255, 0), TextDecoration.BOLD))))
                .build();

        ItemStack mudCookie = ItemStackBuilder.of(Material.COOKIE, 16)
                .setMetaBuilder(meta -> {
                    meta.displayName(Component
                            .text("Mud Cookie", Style.empty()
                                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                                    .decorate(TextDecoration.BOLD)));
                    FoodComponent foodComponent = meta.getFood();
                    foodComponent.setCanAlwaysEat(true);
                    foodComponent.setEatSeconds(0.1f);
                    foodComponent.setNutrition(0);
                    foodComponent.setSaturation(0);
                    meta.setFood(foodComponent);
                })
                .setMetaContainerBuilder(container -> container.set(nutritionKey, PersistentDataType.STRING, "mud_cookie"))
                .build();

        ShapedRecipe warriorSwordRecipe = new ShapedRecipe(new NamespacedKey(this, "WarriorSword"), warriorSword)
                .shape(" A ", "AAA", " A ")
                .setIngredient('A', Material.DIRT);

        ShapedRecipe mudCookieRecipe = new ShapedRecipe(new NamespacedKey(this, "MudCookie"), mudCookie)
                .shape("AAA", "AAA", "AAA")
                .setIngredient('A', Material.DIRT);

        this.nutritionFactMap.put("mud_cookie", Set.of(
                NutritionFact.of(Nutrition.CARBOHYDRATE, 1),
                NutritionFact.of(Nutrition.PROTEIN, 1),
                NutritionFact.of(Nutrition.FAT, 1),
                NutritionFact.of(Nutrition.VITAMIN, 1)));

        getServer().addRecipe(warriorSwordRecipe, true);
        getServer().addRecipe(mudCookieRecipe, true);
        getServer().updateRecipes();

        this.logger.info("Plugin %s enabled", getClass().getName());
    }

    public void registerEvents(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
            this.logger.info("listener %s is registered", listener
                    .getClass()
                    .getName());
        }
        this.listeners.addAll(List.of(listeners));
    }

    public <L extends Listener> Optional<L> getListener(Class<L> listenerType) {
        return this.listeners
                .stream()
                .filter(listenerType::isInstance)
                .map(listenerType::cast)
                .findFirst();
    }

    private void createDatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:plugins/TestPlugin/database.db");
            this.connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
