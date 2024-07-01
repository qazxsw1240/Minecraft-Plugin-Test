package com.github.test.entity.nutrition;

public interface PlayerNutritionListenerManager {
    public abstract void addListener(PlayerNutritionListener listener);

    public abstract void removeListener(PlayerNutritionListener listener);

    public abstract void removeAllListeners();
}
