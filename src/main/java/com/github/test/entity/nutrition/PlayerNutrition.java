package com.github.test.entity.nutrition;

import com.github.test.sql.Connectable;

import java.sql.Connection;

public class PlayerNutrition extends Connectable {
    private final String uuid;
    private int carbohydrate;
    private int protein;
    private int fat;
    private int vitamin;

    public PlayerNutrition(
            Connection connection,
            String uuid,
            int carbohydrate,
            int protein,
            int fat,
            int vitamin) {
        super(connection);
        this.uuid = uuid;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
        this.vitamin = vitamin;
    }

    public String getUuid() {
        return uuid;
    }

    public int getCarbohydrate() {
        return this.carbohydrate;
    }

    public void setCarbohydrate(int carbohydrate) {
        this.carbohydrate = carbohydrate;
        String sql = String.format("UPDATE player_nutrition SET carbohydrate=%d WHERE uuid='%s'", this.carbohydrate, this.uuid);
        execute(sql);
    }

    public int getProtein() {
        return this.protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
        String sql = String.format("UPDATE player_nutrition SET protein=%d WHERE uuid='%s'", this.protein, this.uuid);
        execute(sql);
    }

    public int getFat() {
        return this.fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
        String sql = String.format("UPDATE player_nutrition SET fat=%d WHERE uuid='%s'", this.fat, this.uuid);
        execute(sql);
    }

    public int getVitamin() {
        return this.vitamin;
    }

    public void setVitamin(int vitamin) {
        this.vitamin = vitamin;
        String sql = String.format("UPDATE player_nutrition SET vitamin=%d WHERE uuid='%s'", this.vitamin, this.uuid);
        execute(sql);
    }

    @Override
    public String toString() {
        return String.format(
                "PlayerNutrition(uuid=%s, carbohydrate=%d, protein=%d, fat=%d, vitamin=%d)",
                this.uuid,
                this.carbohydrate,
                this.protein,
                this.fat,
                this.vitamin);
    }
}
