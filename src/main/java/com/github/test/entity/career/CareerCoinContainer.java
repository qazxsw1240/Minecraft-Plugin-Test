package com.github.test.entity.career;

import com.github.test.sql.Connectable;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.Optional;

public class CareerCoinContainer extends Connectable {
    public CareerCoinContainer(Connection connection) {
        super(connection);
    }

    public boolean contains(Player player) {
        String sql = "SELECT COUNT(*) AS result FROM player_career_coins WHERE uuid='" + player.getUniqueId() + "'";
        Optional<Integer> result = executeStatement(sql, set -> set.getInt(1));
        return result.filter(i -> i == 1).isPresent();
    }

    public CareerCoin create(Player player) {
        CareerCoin playerNutrition = new CareerCoinImpl(this.connection, player, 0);
        String sql = String.format(
                "INSERT INTO player_career_coins (uuid, amount) VALUES ('%s', %d)",
                playerNutrition.getPlayer().getUniqueId(),
                playerNutrition.getCoins());
        if (execute(sql)) {
            throw new IllegalStateException("Failed executing statement " + sql);
        }
        return playerNutrition;
    }

    public Optional<CareerCoin> get(Player player) {
        String uuid = player.getUniqueId().toString();
        String sql = String.format("SELECT * FROM player_career_coins WHERE uuid='%s'", uuid);
        return executeStatement(sql, set -> {
            int amount = set.getInt("amount");
            return new CareerCoinImpl(this.connection, player, amount);
        });
    }
}
