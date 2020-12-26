package com.shatteredpixel.yasd.general.actors.buffs;

public class RollBuff extends Buff {

    public static final float ROLL_DURATION = 0.5f;

    //TODO improve this once I add equipment load
    @Override
    public boolean act() {
        spend(ROLL_DURATION);
        detach();
        return true;
    }
}
