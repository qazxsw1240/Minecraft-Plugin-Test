package com.github.test.entity.nutrition;

import java.util.Set;

public interface PlayerNutritionListenerManager {
    public abstract void addListener(PlayerNutritionListener listener);

    public abstract void removeListener(PlayerNutritionListener listener);

    public abstract void removeAllListeners();

    public abstract <L extends PlayerNutritionListener> Set<L> getListener(Class<L> listenerType);
}
