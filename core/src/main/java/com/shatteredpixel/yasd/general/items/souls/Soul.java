package com.shatteredpixel.yasd.general.items.souls;

import com.shatteredpixel.yasd.general.actors.buffs.Hollowing;
import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class Soul extends Item {

    {
        stackable = true;

        cursed = false;
        cursedKnown = true;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    private static final String AC_USE = "use";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_USE)) {
            doUse(hero);
            detach(hero.belongings.backpack);
        }
    }

    public abstract void doUse(Hero hero);

    public static Item randomSoul() {
        if (Random.Int(10) == 0) return new Humanity();
        else return new SoulOfLostUndead();
    }
}
