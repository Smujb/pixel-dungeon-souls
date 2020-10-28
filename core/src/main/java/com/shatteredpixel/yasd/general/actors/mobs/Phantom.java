package com.shatteredpixel.yasd.general.actors.mobs;

import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.buffs.Hollowing;
import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.levels.Level;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.sprites.CharSprite;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.watabou.utils.Bundle;

public class Phantom extends Wraith {

    {
        properties.add(Property.MINIBOSS);

        healthFactor = 1f;
    }

    @Override
    public boolean reset() {
        return false;
    }

    @Override
    protected boolean act() {
        if (enemy != Dungeon.hero) target = Dungeon.hero.pos;
        return super.act();
    }

    public static Phantom createFromHero(Hero hero, Level level) {
        Phantom phantom = Mob.create(Phantom.class, hero.levelToScaleFactor());
        phantom.copySouls(hero);
        phantom.pos = level.randomRespawnCell(phantom);
        return phantom;
    }

    private void copySouls(Hero hero) {
        heroSouls = hero.souls;
        hero.souls = 0;
    }

    private void retrieve(Hero hero) {
        hero.souls = heroSouls;
        heroSouls = 0;
        Hollowing.retrieveHumanity(hero);
        hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "retrieval"));
    }


    @Override
    public void die(DamageSrc cause) {
        super.die(cause);
        retrieve(Dungeon.hero);
    }

    private int heroSouls = 0;

    private static final String SOULS = "souls";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SOULS, heroSouls);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        heroSouls = bundle.getInt(SOULS);
    }
}
