package com.shatteredpixel.yasd.general.items.souls;

import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.sprites.ItemSpriteSheet;

public class SoulOfLostUndead extends Soul {

    {
        image = ItemSpriteSheet.SOUL_LOST_UNDEAD;
    }

    protected int souls = 500;

    @Override
    protected void doUse(Hero hero) {
        hero.gainSouls(souls, getClass());
    }
}
