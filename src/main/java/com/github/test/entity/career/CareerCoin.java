package com.github.test.entity.career;

import org.bukkit.entity.Player;

public interface CareerCoin {
    public abstract Player getPlayer();

    public abstract int getCoins();

    public abstract void setCoins(int coins);
}
