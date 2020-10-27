package com.shatteredpixel.yasd.general.windows;

import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.LevelHandler;
import com.shatteredpixel.yasd.general.PDSGame;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.scenes.PixelScene;
import com.shatteredpixel.yasd.general.scenes.TitleScene;
import com.shatteredpixel.yasd.general.sprites.ItemSprite;
import com.shatteredpixel.yasd.general.sprites.ItemSpriteSheet;
import com.shatteredpixel.yasd.general.ui.RedButton;
import com.shatteredpixel.yasd.general.ui.RenderedTextBlock;
import com.shatteredpixel.yasd.general.ui.Window;
import com.sun.org.apache.bcel.internal.generic.DUP;
import com.watabou.noosa.Game;

import java.io.IOException;

public class WndRetry extends Window {

    private static void resurrect() {
        Dungeon.level.reset();
        Dungeon.hero.pos = Dungeon.level.heroSpawnCell();
        Dungeon.hero.updateHT(false);
        Dungeon.hero.HP = Dungeon.hero.HT;
        Dungeon.hero.live();
    }

    public WndRetry() {
        IconTitle titlebar = new IconTitle();
        titlebar.icon( new ItemSprite(ItemSpriteSheet.SKULL, null ) );
        titlebar.label( Messages.get(this, "retry") );
        titlebar.setRect( 0, 0, WIDTH, 0 );
        add( titlebar );

        RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "message"), 6 );
        message.maxWidth(WIDTH);
        message.setPos(0, titlebar.bottom() + GAP);
        add( message );

        RedButton btnYes = new RedButton( Messages.get(this, "yes") ) {
            @Override
            protected void onClick() {
                hide();
                resurrect();
                LevelHandler.reload();
            }
        };
        btnYes.setRect( 0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT );
        add( btnYes );

        RedButton btnNo = new RedButton( Messages.get(this, "no") ) {
            @Override
            protected void onClick() {
                hide();
                resurrect();
                try {
                    Dungeon.saveAll();
                    PDSGame.switchScene(TitleScene.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        btnNo.setRect( 0, btnYes.bottom() + GAP, WIDTH, BTN_HEIGHT );
        add( btnNo );

        resize( WIDTH, (int)btnNo.bottom() );
    }

    @Override
    public void onBackPressed() {}
}
