package com.github.test.entity.nutrition;

import org.bukkit.entity.Player;

public interface PlayerNutritionConsumeEvent {
    public static PlayerNutritionConsumeEvent of(Player player, PlayerNutrition playerNutrition) {
        return new PlayerNutritionConsumeEventImpl(player, playerNutrition);
    }

    public abstract Player getPlayer();

    public abstract PlayerNutrition getNutrition();
}
