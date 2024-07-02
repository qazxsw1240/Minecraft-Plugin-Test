package com.github.test.entity.nutrition;

public interface PlayerNutritionConsumeListener extends PlayerNutritionListener {
    public abstract void onNutritionConsume(PlayerNutritionConsumeEvent event);
}
