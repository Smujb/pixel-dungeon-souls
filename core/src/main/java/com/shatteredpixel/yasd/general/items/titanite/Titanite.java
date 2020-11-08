package com.shatteredpixel.yasd.general.items.titanite;

import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.items.Item;

public abstract class Titanite extends Item {

    {
        cursed = false;
        cursedKnown = true;
        stackable = true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    //+1 to +3 = titanite shard, +4 to +6 = large titanite shard, etc
    public static Class<? extends Titanite> titaniteReq(int itemLvl) {
        if (itemLvl < 4) return TitaniteShard.class;
        else if (itemLvl < 7) return LargeTitaniteShard.class;
        else if (itemLvl < 10) return TitaniteChunk.class;
        else return TitaniteSlab.class;
    }

    //1 titanite shard for +1, 2 for +2, 3 for +3, then 1 large titanite shard for +4, 2 for +5, etc
    public static int titaniteReqAmt(int itemLvl) {
        return itemLvl % 3 + 1;
    }

    //Checks if an item can be upgraded by the Titanite the hero has
    public static boolean canUpgradeItem(Hero hero, Item item) {
        if (!hero.belongings.contains(item) || !item.isUpgradable()) return false;
        int level = item.trueLevel();
        Titanite titanite = hero.belongings.getItem(titaniteReq(level));
        int amt = titaniteReqAmt(level);
        return titanite != null && titanite.quantity >= amt;
    }

    //NOTE: this function assumes canUpgradeItem has already been called for speed, and doesn't check it. May cause bugs if triggered without checking.
    //Actually upgrades the item
    public static void upgradeItem(Hero hero, Item item) {
        int level = item.trueLevel();
        Titanite titanite = hero.belongings.getItem(titaniteReq(level));
        int amt = titaniteReqAmt(level);
        if (titanite.quantity <= amt) titanite.detachAll(hero.belongings.backpack);
        else titanite.split(amt);
        item.upgrade();
    }
}
