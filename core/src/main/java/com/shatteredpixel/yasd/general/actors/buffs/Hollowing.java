package com.shatteredpixel.yasd.general.actors.buffs;

import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.items.rings.RingOfBinding;
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

    public static void retrieveHumanity(Hero hero) {
        Buff.affect(hero, Hollowing.class).retrieveHumanity();
    }

    public static float hollowingPercentage(Hero hero) {
        return Buff.affect(hero, Hollowing.class).hollowingPercentage();
    }

    private static final int FULL_HOLLOW = 20;
    private int level = 0;

    //-2% HP each death, down to a maximum reduction of 40%.
    private float hpFactor() {
        return Math.max(0.6f, 1f - 0.02f*Math.max(0, level-RingOfBinding.hollowingReduction(target)));
    }

    private void die() {
        level++;
        processCap();
    }

    private void regainHumanity() {
        level = 0;
    }

    private void processCap() {
        if (level > FULL_HOLLOW) level = FULL_HOLLOW;
    }

    private void fullyHollow() {
        level = FULL_HOLLOW;
    }

    private void retrieveHumanity() {
        if (level > 0) level--;
    }

    private float hollowingPercentage() {
        return level/(float)FULL_HOLLOW;
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
