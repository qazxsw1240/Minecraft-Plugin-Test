package com.github.test.entity.nutrition;

import org.bukkit.entity.Player;

class PlayerNutritionConsumeEventImpl implements PlayerNutritionConsumeEvent {
    private final Player player;
    private final PlayerNutrition nutrition;

    public PlayerNutritionConsumeEventImpl(Player player, PlayerNutrition nutrition) {
        this.player = player;
        this.nutrition = nutrition;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public PlayerNutrition getNutrition() {
        return this.nutrition;
    }
}
