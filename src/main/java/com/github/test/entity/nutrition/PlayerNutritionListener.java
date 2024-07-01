package com.github.test.entity.nutrition;

import org.bukkit.entity.Player;

public interface PlayerNutritionListener {
    public abstract void onNutritionUpdate(Player player, PlayerNutrition playerNutrition);
}
