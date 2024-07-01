package com.github.test;

import com.github.test.util.FormattedLogger;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ExperienceService implements Listener {
    private final FormattedLogger logger;
    private final ConcurrentMap<UUID, Integer> blockBreakCounts;

    public ExperienceService(FormattedLogger logger) {
        this.logger = logger;
        this.blockBreakCounts = new ConcurrentHashMap<>();
    }

    @EventHandler
    public void onPlayerGetAchievement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        this.logger.info("advancement %s  has been achieved", advancement
                .key()
                .value());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        this.logger.info(String.format(
                "entity damaged %f: %s ",
                event.getDamage(),
                entity.getType().getKey()));
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            player.heal(event.getFinalDamage());
        }
    }

    @EventHandler
    public void onBlockRemove(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!this.blockBreakCounts.containsKey(uuid)) {
            this.blockBreakCounts.put(uuid, 0);
        }
        int value = Objects.requireNonNull(this.blockBreakCounts.computeIfPresent(uuid, (p, v) -> v + 1));
        player.sendMessage(String.format("You destroyed %d blocks", value));
        if (value % 10 == 0) { // invoke when each 10 block is destroyed
            player.setLevel(player.getLevel() + 1);
            player.setExp(0);
        }
    }
}
