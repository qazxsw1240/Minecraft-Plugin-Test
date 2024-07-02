package com.github.test.entity.nutrition;

public interface PlayerNutritionAcquireListener extends PlayerNutritionListener {
    public abstract void onNutritionAcquire(PlayerNutritionAcquireEvent event);
}
