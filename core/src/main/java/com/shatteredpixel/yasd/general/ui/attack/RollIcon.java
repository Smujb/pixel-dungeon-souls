package com.shatteredpixel.yasd.general.ui.attack;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.hero.HeroAction;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.scenes.CellSelector;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.watabou.noosa.Image;

public class RollIcon extends ActionIcon {

    public RollIcon() {
        super(ROLL);
        width = height = SIZE;
    }

    @Override
    protected void onClick() {
        super.onClick();
        GameScene.selectCell(selector);
    }

    private static final CellSelector.Listener selector = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell != null && Dungeon.hero.ready) {
                Dungeon.hero.curAction = new HeroAction.Roll(cell);
                Dungeon.hero.next();
            }
        }

        @Override
        public String prompt() {
            return Messages.get(RollIcon.class, "roll_direction");
        }
    };
}
