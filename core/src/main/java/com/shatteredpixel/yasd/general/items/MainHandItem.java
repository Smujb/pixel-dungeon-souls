package com.shatteredpixel.yasd.general.items;

import com.shatteredpixel.yasd.general.Constants;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.PDSGame;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.shatteredpixel.yasd.general.windows.WndOptions;

public class MainHandItem extends EquipableItem {

    public static final int MAIN_HAND = 0;
    public static final int OFF_HAND = 1;

    private static final float TIME_TO_EQUIP = 1f;

    @Override
    public boolean doEquip(Char ch) {
        PDSGame.scene().addToFront(new WndOptions(Messages.get(this, "equip_slot"), Messages.get(this, "info"), Messages.get(this, "main_hand"), Messages.get(this, "off_hand")) {
            @Override
            protected void onSelect(int index) {
                super.onSelect(index);
                doEquip(ch, index);
            }
        });
        return false;
    }

    private void doEquip(Char ch, int index) {
        MainHandItem[] weapons = Dungeon.hero.belongings.weapons;

        MainHandItem equipped = null;

        for (int i = 0; i < weapons.length; i++) {
            if (index == i) {
                equipped = weapons[i];
                break;
            }
        }

        if (equipped == null) {
            Dungeon.hero.belongings.weapons[index] = this;
            detach( ch.belongings.backpack );

            activate(ch);

            cursedKnown = true;
            if (cursed) {
                equipCursed(ch);
                GLog.n( Messages.get(this, "equip_cursed", this) );
            }

            ch.spendAndNext( TIME_TO_EQUIP );
            return;
        }

        int slot = Dungeon.quickslot.getSlot(this);
        detach(ch.belongings.backpack);
        if (equipped.doUnequip(ch, true, false)) {
            doEquip(ch);
        } else {
            collect();
        }
        if (slot != -1) Dungeon.quickslot.setSlot(slot, this);
    }

    @Override
    public boolean doUnequip(Char ch, boolean collect, boolean single) {
        if (super.doUnequip(ch, collect, single)){

            for (int i = 0; i < ch.belongings.weapons.length; i++) {
                if (ch.belongings.weapons[i] == this) {
                    ch.belongings.weapons[i] = null;
                }
            }

            return true;

        } else {

            return false;

        }
    }
}
