package com.github.test.entity.nutrition;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

public interface PlayerNutritionConsumeListener extends PlayerNutritionListener {
    public abstract void onNutritionConsume(PlayerNutritionConsumeEvent event);
}
