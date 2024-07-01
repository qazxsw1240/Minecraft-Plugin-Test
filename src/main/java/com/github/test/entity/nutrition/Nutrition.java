package com.github.test.entity.nutrition;

public enum Nutrition {
    CARBOHYDRATE("carbohydrate"),
    PROTEIN("protein"),
    FAT("fat"),
    VITAMIN("vitamin");

    private final String name;

    private Nutrition(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "Nutrition(type=" + this.getName() + ")";
    }
}
