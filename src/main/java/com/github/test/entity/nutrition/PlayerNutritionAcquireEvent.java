package com.github.test.entity.nutrition;

import org.bukkit.entity.Player;

public interface PlayerNutritionAcquireEvent {
    public static PlayerNutritionAcquireEvent of(Player player, PlayerNutrition nutrition) {
        return new PlayerNutritionAcquireEventImpl(player, nutrition);
    }

    public abstract Player getPlayer();

    public abstract PlayerNutrition getNutrition();
}
