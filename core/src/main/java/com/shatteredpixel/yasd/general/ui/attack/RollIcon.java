package com.shatteredpixel.yasd.general.ui.attack;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.hero.HeroAction;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.scenes.CellSelector;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.shatteredpixel.yasd.general.ui.IconButton;
import com.watabou.noosa.Image;

public class RollIcon extends IconButton {

    private static final String ICONS = Assets.Interfaces.ATTK_ICON;

    private static final int SIZE = 16;

    public RollIcon() {
        super(new Image(ICONS, 0, 0, 16, 16));
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
