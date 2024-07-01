package com.github.test.entity.nutrition;

class NutritionFactImpl implements NutritionFact {
    private final Nutrition nutritionKind;
    private final int amount;

    public NutritionFactImpl(Nutrition nutrition, int amount) {
        this.nutritionKind = nutrition;
        this.amount = amount;
    }

    @Override
    public Nutrition getNutrition() {
        return this.nutritionKind;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }
}
