package com.github.test;

import com.github.test.entity.career.CareerCoin;
import com.github.test.entity.career.CareerCoinContainer;
import com.github.test.util.FormattedLogger;
import net.kyori.adventure.text.Component;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.awt.*;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

public class CareerService extends SqlService implements Listener {
    private final FormattedLogger logger;
    private final CareerCoinContainer careerCoinContainer;
    private final Map<UUID, CareerCoin> playerCareerCoins;

    public CareerService(Connection connection, FormattedLogger logger) {
        super(connection, logger, "player_career_coins");
        this.logger = logger;
        this.careerCoinContainer = new CareerCoinContainer(this.connection);
        this.playerCareerCoins = new ConcurrentSkipListMap<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!this.careerCoinContainer.contains(player)) {
            this.careerCoinContainer.create(player);
        }
        this.careerCoinContainer
                .get(player)
                .ifPresent(careerCoin -> {
                    this.playerCareerCoins.put(careerCoin
                            .getPlayer()
                            .getUniqueId(), careerCoin);
                    this.logger.info("Player %s successfully retrieved career coins", player.getName());
                    this.logger.info("%s", careerCoin);
                });
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();
        CareerCoin careerCoin = this.playerCareerCoins.get(player.getUniqueId());
        if (careerCoin == null) {
            throw new IllegalStateException("Cannot retrieve the career coins of player " + player.getName());
        }
        this.logger.info("player %s has done achievement %s", player.getName(), advancement
                .key()
                .value());
        int coins = careerCoin.getCoins();
        careerCoin.setCoins(coins + 1);
        player.sendMessage(Component.text("Now your have " + careerCoin.getCoins() + " career coin(s)."));
    }

    @Override
    protected List<String> createTableQueries() {
        return List.of(
                """
                 create table player_career_coins (
                    uuid         varchar not null constraint player_nutrition_pk primary key,
                    amount integer not null
                )
                """,
                """
                create unique index player_career_coins_uuid_uindex
                   on player_career_coins (uuid)
                """
        );
    }
}
