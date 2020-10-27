package com.shatteredpixel.yasd.general.actors.mobs.npcs;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Badges;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.PDSGame;
import com.shatteredpixel.yasd.general.Statistics;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Hollowing;
import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.shatteredpixel.yasd.general.sprites.CharSprite;
import com.shatteredpixel.yasd.general.sprites.StatueSprite;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.shatteredpixel.yasd.general.windows.WndBag;
import com.shatteredpixel.yasd.general.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Scene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Bonfire extends NPC {

    {
        //FIXME actual sprite
        spriteClass = StatueSprite.class;
    }

    private boolean lit = false;



    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public boolean interact(Char ch) {
        Dungeon.level.reset();
        Statistics.lastBonfireDepth = Dungeon.depth;
        PDSGame.seamlessResetScene(new Game.SceneChangeCallback() {
            @Override
            public void beforeCreate() {

            }

            @Override
            public void afterCreate() {
                if (lit()) {
                    ch.heal(ch.HT, false, true);
                    PDSGame.runOnRenderThread(new Callback() {
                        @Override
                        public void call() {
                            PDSGame.scene().addToFront(new WndBonfire());
                        }
                    });
                } else {
                    sprite.showStatus(CharSprite.POSITIVE, Messages.get(Bonfire.this, "bonfire_lit"));
                    light();
                }
            }
        });
        return true;
    }

    public boolean lit() {
        return lit;
    }

    public void light() {
        lit = true;
    }

    public static class WndBonfire extends WndOptions {
        public WndBonfire() {
            super(Messages.get(Bonfire.class, "name"), Messages.get(Bonfire.class, "desc"), Messages.get(Bonfire.class, "level_up"), Messages.get(Bonfire.class, "repair_item"), Messages.get(Bonfire.class, "reverse_hollowing"));
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
                    GameScene.selectItem(listener, WndBag.Mode.REPAIRABLE, Messages.get(Bonfire.class, "choose_item_repair"));
                    break;
                case 2:
                    //Hollowing.regainHumanity(Dungeon.hero);
                    break;
            }
        }
    }

    public static WndBag.Listener listener = new WndBag.Listener() {
        @Override
        public void onSelect(Item item) {
            int reqSouls = item.price() * 10;
            if (Dungeon.hero.souls >= reqSouls) {
                PDSGame.scene().addToFront(new WndOptions(Messages.get(Bonfire.class, "confirm_repair", reqSouls), "", Messages.get(Bonfire.class, "yes"), Messages.get(Bonfire.class, "no")) {
                    @Override
                    protected void onSelect(int index) {
                        super.onSelect(index);
                        if (index == 0)  {
                            item.fullyRepair();
                            Dungeon.hero.souls -= reqSouls;
                        }
                    }
                });
            } else {
                GLog.n(Messages.get(Bonfire.class, "cant_repair"));
            }
        }
    };
}
