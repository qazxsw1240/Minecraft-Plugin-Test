package com.github.test.entity.nutrition;

import com.github.test.sql.Connectable;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.Optional;

public class NutritionContainer extends Connectable {
    public NutritionContainer(Connection connection) {
        super(connection);
    }

    public boolean contains(Player player) {
        String sql = "SELECT COUNT(*) AS result FROM player_nutrition WHERE uuid='" + player.getUniqueId() + "'";
        Optional<Integer> result = executeStatement(sql, set -> set.getInt(1));
        return result.filter(i -> i == 1).isPresent();
    }

    public PlayerNutrition create(Player player) {
        fetchConnection();
        String uuid = player.getUniqueId().toString();
        PlayerNutrition playerNutrition = new PlayerNutrition(this.connection, uuid, 0, 0, 0, 0);
        String sql = String.format(
                "INSERT INTO player_nutrition (uuid, carbohydrate, protein, fat, vitamin) VALUES ('%s', %d, %d, %d, %d)",
                playerNutrition.getUuid(),
                playerNutrition.getCarbohydrate(),
                playerNutrition.getProtein(),
                playerNutrition.getFat(),
                playerNutrition.getVitamin());
        if (execute(sql)) {
            throw new IllegalStateException("Failed executing statement " + sql);
        }
        return playerNutrition;
    }

    public Optional<PlayerNutrition> get(Player player) {
        fetchConnection();
        String uuid = player.getUniqueId().toString();
        String sql = String.format("SELECT * FROM player_nutrition WHERE uuid='%s'", uuid);
        return executeStatement(sql, set -> {
            int carbohydrate = set.getInt("carbohydrate");
            int protein = set.getInt("protein");
            int fat = set.getInt("fat");
            int vitamin = set.getInt("vitamin");
            return new PlayerNutrition(this.connection, uuid, carbohydrate, protein, fat, vitamin);
        });
    }
}
