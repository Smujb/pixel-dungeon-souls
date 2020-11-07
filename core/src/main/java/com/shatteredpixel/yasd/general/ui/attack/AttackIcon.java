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

    private int slot;

    private Mob curEnemy;
    private final ArrayList<Mob> candidates = new ArrayList<>();

    public AttackIcon(int imageIndex, int slot) {
        super(new Image(ICONS, SIZE*imageIndex, 0, SIZE+SIZE*imageIndex, SIZE));
        this.slot = slot;
        width = height = SIZE;
    }

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
                icon.alpha(0.3f);
            }
        }
    }
}
