package com.github.test.entity.nutrition;

import org.jetbrains.annotations.NotNull;

public interface NutritionFact extends Comparable<NutritionFact> {
    public static NutritionFact of(Nutrition nutrition, int amount) {
        return new NutritionFactImpl(nutrition, amount);
    }

    public abstract Nutrition getNutrition();

    public abstract int getAmount();

    @Override
    public default int compareTo(@NotNull NutritionFact o) {
        return getNutrition().compareTo(o.getNutrition());
    }
}
