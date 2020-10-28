package com.shatteredpixel.yasd.general.items;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Bleeding;
import com.shatteredpixel.yasd.general.actors.buffs.Blindness;
import com.shatteredpixel.yasd.general.actors.buffs.Buff;
import com.shatteredpixel.yasd.general.actors.buffs.Cripple;
import com.shatteredpixel.yasd.general.actors.buffs.Drowsy;
import com.shatteredpixel.yasd.general.actors.buffs.Healing;
import com.shatteredpixel.yasd.general.actors.buffs.Poison;
import com.shatteredpixel.yasd.general.actors.buffs.Slow;
import com.shatteredpixel.yasd.general.actors.buffs.Vertigo;
import com.shatteredpixel.yasd.general.actors.buffs.Vulnerable;
import com.shatteredpixel.yasd.general.actors.buffs.Weakness;
import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.shatteredpixel.yasd.general.sprites.ItemSpriteSheet;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class EstusFlask extends Item {

    {
        cursed = false;
        cursedKnown = true;

        defaultAction = AC_DRINK;
    }

    public static final float HEAL_TIME = 3f;

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    private static final int NORMAL_MAX_CHARGES = 4;
    private int charges = NORMAL_MAX_CHARGES;

    @Override
    public int image() {
        return charges > 0 ? ItemSpriteSheet.FLASK_FULL : ItemSpriteSheet.FLASK_EMPTY;
    }

    @Override
    public String status() {
        return Integer.toString(charges);
    }

    private static String AC_DRINK = "drink";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = new ArrayList<>();
        actions.add(AC_DRINK);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_DRINK)) {
            doDrink(hero);
        }
    }

    public void refill() {
        charges = NORMAL_MAX_CHARGES;
    }

    public static void refill(Char ch) {
        EstusFlask flask = ch.belongings.getItem(EstusFlask.class);
        if (flask != null) {
            flask.refill();
        }
    }

    public void doDrink(Char ch) {
        if (charges > 0) {
            charges--;
            Camera.main.shake(4f, 1f);
            GameScene.flash(0xFFFFFF);
            Sample.INSTANCE.play(Assets.Sounds.DRINK);
            ch.heal(Math.round(ch.HT*0.7f));
            cure(ch);
            ch.busy();
            ch.spend(HEAL_TIME);
            if (ch instanceof Hero) {
                ch.sprite.operate(ch.pos);
            }
            updateQuickslot();
            GLog.p(Messages.get(this, "heal"));
        } else {
            GLog.n(Messages.get(this, "no_charges"));
        }
    }

    public static void cure(Char ch) {
        Buff.detach(ch, Poison.class);
        Buff.detach(ch, Cripple.class);
        Buff.detach(ch, Weakness.class);
        Buff.detach(ch, Vulnerable.class);
        Buff.detach(ch, Bleeding.class);

        Buff.detach(ch, Blindness.class);
        Buff.detach(ch, Drowsy.class);
        Buff.detach(ch, Slow.class);
        Buff.detach(ch, Vertigo.class);

    }

    public static final String CHARGE = "charge";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CHARGE, charges);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        charges = bundle.contains(CHARGE) ? bundle.getInt(CHARGE) : NORMAL_MAX_CHARGES;
    }
}
