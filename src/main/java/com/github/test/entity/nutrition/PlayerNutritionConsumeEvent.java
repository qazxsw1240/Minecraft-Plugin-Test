package com.github.test.entity.nutrition;

import org.bukkit.entity.Player;

public interface PlayerNutritionConsumeEvent {
    public abstract Player getPlayer();

    public abstract PlayerNutrition getNutrition();
}
