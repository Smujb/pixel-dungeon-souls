package com.shatteredpixel.yasd.general.ui.attack;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.mobs.Mob;
import com.shatteredpixel.yasd.general.ui.IconButton;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AttackIcon extends IconButton {

    private static final String ICONS = Assets.Interfaces.ATTK_ICON;
    private static final int SIZE = 16;

    private final int slot;
    private final boolean basic;

    private Mob curEnemy;
    private final ArrayList<Mob> candidates = new ArrayList<>();

    //imageIndex decides the icon, slot decides the weapon slot it's for (0 or 1) and basic whether the indicator triggers the basic attack or secondary
    public AttackIcon(int slot, boolean basic) {
        super(null);
        int imageIndex;
        if (basic) {
            imageIndex = 0;
        } else {
            imageIndex = 1;
        }
        icon(new Image(ICONS, SIZE*imageIndex, 0, SIZE+SIZE*imageIndex, SIZE));
        this.slot = slot;
        this.basic = basic;
        width = height = SIZE;
    }

    //Get the slot this indicator activates
    public int getSlot() {
        return slot;
    }

    @Override
    protected void onClick() {
        super.onClick();
        Dungeon.hero.belongings.currentWeapon = slot;
        if (curEnemy == null || !curEnemy.isAlive()) curEnemy = findEnemy();
        if (curEnemy != null) {
            if (Dungeon.hero.handle( curEnemy.pos )) {
                Dungeon.hero.next();
            }
        }
    }

    //Choose an enemy to attack - default to the last one chosen if available
    private synchronized Mob findEnemy() {

        candidates.clear();
        int v = Dungeon.hero.visibleEnemies();
        for (int i=0; i < v; i++) {
            Mob mob = Dungeon.hero.visibleEnemy( i );
            if ( Dungeon.hero.canAttack( mob) ) {
                candidates.add( mob );
            }
        }
        if (curEnemy == null || !candidates.contains(curEnemy)) curEnemy = Random.element(candidates);
        return curEnemy;
    }

    @Override
    public void update() {
        super.update();
        if (icon != null) {
            if (Dungeon.hero.ready && active) {
                icon.alpha(1.0f);
            } else {
                icon.alpha(0.7f);
            }
        }
    }
}
