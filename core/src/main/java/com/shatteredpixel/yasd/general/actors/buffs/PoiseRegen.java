package com.shatteredpixel.yasd.general.actors.buffs;

import com.shatteredpixel.yasd.general.actors.hero.Hero;

public class PoiseRegen extends Buff {

    {
        actPriority = HERO_PRIO + 1;
    }

    @Override
    public boolean act() {
        if (target.isAlive()) {
            float increase = target.maxPoise()/100;
            if (target instanceof Hero && ((Hero) target).isStarving()) {
                increase /= 2f;
            }
            float max = target.maxPoise();
            if (target.poise < max) {
                target.poise = Math.min(max, target.poise + increase);
            }
        }
        spend(0.1f);
        return true;
    }
}
