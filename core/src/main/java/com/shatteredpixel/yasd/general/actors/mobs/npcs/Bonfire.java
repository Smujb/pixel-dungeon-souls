package com.shatteredpixel.yasd.general.actors.mobs.npcs;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Badges;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.PDSGame;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Hollowing;
import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.sprites.CharSprite;
import com.shatteredpixel.yasd.general.sprites.StatueSprite;
import com.shatteredpixel.yasd.general.ui.Window;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.shatteredpixel.yasd.general.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Bonfire extends NPC {
    {
        //FIXME actual sprite
        spriteClass = StatueSprite.class;
    }

    @Override
    public boolean interact(Char ch) {
        PDSGame.runOnRenderThread(new Callback() {
            @Override
            public void call() {
                PDSGame.scene().addToFront(new WndBonfire());
            }
        });
        ch.heal(ch.HT);
        return true;
    }

    public static class WndBonfire extends WndOptions {
        public WndBonfire() {
            super(Messages.get(Bonfire.class, "name"), Messages.get(Bonfire.class, "desc"), Messages.get(Bonfire.class, "level_up"), Messages.get(Bonfire.class, "reverse_hollowing"));
        }

        @Override
        protected void onSelect(int index) {
            super.onSelect(index);
            hide();
            switch (index) {
                case 0:
                    Hero h = Dungeon.hero;
                    if (h.souls > h.soulsToLevelUp()) {
                        h.lvl++;
                        h.souls -= h.soulsToLevelUp();
                        GLog.newLine();
                        GLog.p(Messages.get(h, "new_level"), h.lvl);
                        h.sprite.showStatus(CharSprite.POSITIVE, Messages.get(h, "level_up"));
                        Sample.INSTANCE.play(Assets.Sounds.LEVELUP);
                        h.distributePoints();
                        Item.updateQuickslot();

                        Badges.validateLevelReached();
                    } else {
                        GLog.n(Messages.get(h, "not_enough_souls"));
                    }
                    return;
                case 1:
                    //Hollowing.regainHumanity(Dungeon.hero);
                    break;
            }
        }
    }
}
