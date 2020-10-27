package com.shatteredpixel.yasd.general.actors.buffs;

import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.watabou.utils.Bundle;

public class Hollowing extends Buff {
    //Does not ever need to be manually applied to hero, as these functions apply it when it is first needed

    public static float hpFactor(Hero hero) {
        return Buff.affect(hero, Hollowing.class).hpFactor();
    }

    public static void die(Hero hero) {
        Buff.affect(hero, Hollowing.class).die();
    }

    public static void regainHumanity(Hero hero) {
        Buff.affect(hero, Hollowing.class).regainHumanity();
    }

    public static void fullyHollow(Hero hero) {
        Buff.affect(hero, Hollowing.class).fullyHollow();
    }

    public static void setLevel(Hero hero, int lvl) {
        Buff.affect(hero, Hollowing.class).setLevel(lvl);
    }

    public static int getLevel(Hero hero) {
        return Buff.affect(hero, Hollowing.class).getLevel();
    }

    private int level = 0;

    //-2.5% HP each death, down to a maximum reduction of 25%
    private float hpFactor() {
        return (float) Math.max(0.75f, 1f - 0.025f*level);
    }

    private void die() {
        level++;
    }

    private void regainHumanity() {
        level = 0;
    }

    private void fullyHollow() {
        level = Math.max(level, 10);
    }

    private void setLevel(int lvl) {
        level = lvl;
    }

    private int getLevel() {
        return level;
    }

    @Override
    public boolean act() {
        spend(TICK);
        return true;
    }

    public static final String LEVEL = "level";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        level = bundle.getInt(LEVEL);
    }
}
