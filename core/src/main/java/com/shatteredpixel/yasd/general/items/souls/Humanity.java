package com.shatteredpixel.yasd.general.items.souls;

import com.shatteredpixel.yasd.general.actors.buffs.Hollowing;
import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.sprites.ItemSpriteSheet;

import java.util.ArrayList;
import java.util.Arrays;

public class Humanity extends Soul {

    {
        image = ItemSpriteSheet.HUMANITY;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        return new ArrayList<>(Arrays.asList(AC_THROW, AC_DROP));
    }

    @Override
    protected void doUse(Hero hero) {}
}
