package com.shatteredpixel.yasd.general.actors.mobs.npcs;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Badges;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.PDSGame;
import com.shatteredpixel.yasd.general.Statistics;
import com.shatteredpixel.yasd.general.actors.Actor;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Buff;
import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.effects.Speck;
import com.shatteredpixel.yasd.general.items.EstusFlask;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.yasd.general.items.souls.Humanity;
import com.shatteredpixel.yasd.general.items.titanite.Titanite;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.shatteredpixel.yasd.general.sprites.BonfireSprite;
import com.shatteredpixel.yasd.general.sprites.CharSprite;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.shatteredpixel.yasd.general.windows.WndBag;
import com.shatteredpixel.yasd.general.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class Bonfire extends NPC {

    {
        spriteClass = BonfireSprite.class;
    }

    private boolean lit = false;

    @Override
    public void damage(int dmg, DamageSrc src) {
        light();
    }

    @Override
    public void add(Buff buff) {
        light();
    }

    @Override
    public void aggro(Char ch) {}

    @Override
    protected boolean act() {
        throwItem();
        spend(1f);
        return true;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return Char.INFINITE_EVASION;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public boolean interact(Char ch) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Actor.fixTime();
                Actor.clear();
                Dungeon.level.reset();
                Dungeon.keysNoReset.clear();
                Actor.init();
                light();
                Statistics.lastBonfireDepth = Dungeon.depth;
                PDSGame.seamlessResetScene(new Game.SceneChangeCallback() {
                    @Override
                    public void beforeCreate() {

                    }

                    @Override
                    public void afterCreate() {
                        if (lit()) {
                            ch.heal(ch.HT, false, true);
                            EstusFlask.refill(ch);
                            PDSGame.runOnRenderThread(new Callback() {
                                @Override
                                public void call() {
                                    PDSGame.scene().addToFront(new WndBonfire());
                                }
                            });
                        } else {
                            sprite.showStatus(CharSprite.POSITIVE, Messages.get(Bonfire.this, "bonfire_lit"));
                        }
                    }
                });
            }
        }.start();

        return true;
    }

    public boolean lit() {
        return lit;
    }

    public void light() {
        lit = true;
    }

    public static final String LIT = "lit";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LIT, lit);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        lit = bundle.getBoolean(LIT);
    }

    public static class WndBonfire extends WndOptions {

        public WndBonfire() {
            super(Messages.get(Bonfire.class, "name"), Messages.get(Bonfire.class, "desc"), Messages.get(Bonfire.class, "level_up", Dungeon.hero.soulsToLevelUp()), Messages.get(Bonfire.class, "repair_item"), Messages.get(Bonfire.class, "upgrade_item"), Messages.get(Bonfire.class, "reverse_hollowing"));
        }

        @Override
        protected void onSelect(int index) {
            super.onSelect(index);
            hide();
            Hero h = Dungeon.hero;
            switch (index) {
                case 0:
                    int reqSouls = h.soulsToLevelUp();
                    if (h.souls >= reqSouls) {
                        h.lvl++;
                        h.souls -= reqSouls;
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
                    GameScene.selectItem(repairListener, WndBag.Mode.REPAIRABLE, Messages.get(Bonfire.class, "choose_item_repair"));
                    return;
                case 2:
                    GameScene.selectItem(upgradeListener, WndBag.Mode.UPGRADEABLE, Messages.get(Bonfire.class, "choose_item_upgrade"));
                    return;
                case 3:
                    Humanity humanity = h.belongings.getItem(Humanity.class);
                    if (humanity != null) {
                        humanity.doUse(h);
                        humanity.detach(h.belongings.backpack);
                    } else {
                        GLog.n(Messages.get(Bonfire.class, "no_humanity"));
                    }
                    return;

            }
        }
    }

    public static WndBag.Listener repairListener = new WndBag.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item == null) return;
            int reqSouls = item.price() * 10;
            if (Dungeon.hero.souls >= reqSouls) {
                PDSGame.scene().addToFront(new WndOptions(Messages.get(Bonfire.class, "confirm_repair", reqSouls), "", Messages.get(Bonfire.class, "yes"), Messages.get(Bonfire.class, "no")) {
                    @Override
                    protected void onSelect(int index) {
                        super.onSelect(index);
                        if (index == 0)  {
                            item.fullyRepair();
                            Dungeon.hero.sprite.centerEmitter().start( Speck.factory( Speck.REPAIR ), 0.05f, 10 );
                            Dungeon.hero.spend( Actor.TICK );
                            Dungeon.hero.busy();
                            Dungeon.hero.sprite.operate( Dungeon.hero.pos );
                            Sample.INSTANCE.play( Assets.Sounds.EVOKE );
                            Dungeon.hero.souls -= reqSouls;
                        }
                    }
                });
            } else {
                GLog.n(Messages.get(Bonfire.class, "cant_repair", reqSouls));
            }
        }
    };

    public static WndBag.Listener upgradeListener = new WndBag.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item == null) return;
            int reqSouls = item.price() * 20;
            int nTit = Titanite.titaniteReqAmt(item.level());
            Class<? extends Titanite> clTit = Titanite.titaniteReq(item.level());
            if (Dungeon.hero.souls >= reqSouls) {
                if (Titanite.canUpgradeItem(Dungeon.hero, item)) {
                    PDSGame.scene().addToFront(new WndOptions(Messages.get(Bonfire.class, "confirm_upgrade", reqSouls, nTit, Messages.get(clTit, "name")), "", Messages.get(Bonfire.class, "yes"), Messages.get(Bonfire.class, "no")) {
                        @Override
                        protected void onSelect(int index) {
                            super.onSelect(index);
                            if (index == 0) {
                                Titanite.upgradeItem(Dungeon.hero, item);
                                ScrollOfUpgrade.upgrade(Dungeon.hero);
                                Dungeon.hero.souls -= reqSouls;
                            }
                        }
                    });
                } else {
                    GLog.n(Messages.get(Bonfire.class, "not_enough_titanite_upgrade", nTit, Messages.get(clTit, "name")));
                }
            } else {
                GLog.n(Messages.get(Bonfire.class, "not_enough_souls_upgrade", reqSouls));
            }
        }
    };
}
