package hu.bendi.betterarchery.interfaces;

import hu.bendi.betterarchery.arrows.ArrowAttribute;

public interface StatsHolder {
    ArrowAttribute getStats();

    void setStats(ArrowAttribute stats);
}
