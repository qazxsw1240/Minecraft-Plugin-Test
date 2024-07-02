package com.github.test.entity.career;

import com.github.test.sql.Connectable;
import org.bukkit.entity.Player;

import java.sql.Connection;

class CareerCoinImpl extends Connectable implements CareerCoin {
    private final Player player;
    private int coins;

    public CareerCoinImpl(Connection connection, Player player, int coins) {
        super(connection);
        this.player = player;
        this.coins = coins;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public int getCoins() {
        return this.coins;
    }

    @Override
    public void setCoins(int coins) {
        this.coins = coins;
        String sql = String.format("UPDATE player_career_coins SET amount=%d WHERE uuid='%s'", this.coins, this.player.getUniqueId());
        execute(sql);
    }

    @Override
    public String toString() {
        return String.format("CareerCoin(uuid=%s, amount=%d)", this.player.getUniqueId(), this.coins);
    }
}
