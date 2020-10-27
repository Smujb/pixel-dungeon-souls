package com.shatteredpixel.yasd.general.sprites;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.mobs.npcs.Bonfire;
import com.watabou.noosa.TextureFilm;

public class BonfireSprite extends MobSprite {

    public BonfireSprite() {
        super();

        texture( Assets.Sprites.BONFIRE );
        setAnimations( false );
    }

    private void setAnimations( boolean lit ) {
        TextureFilm frames = new TextureFilm( texture, 16, 16 );
        idle = new Animation( 4, true );
        if (lit) {
            idle.frames(frames, 1, 2, 3, 4);
        } else {
            idle.frames(frames, 0);
        }
        run = idle.clone();
        attack = idle.clone();
        die = idle.clone();
        play( idle );
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (ch instanceof Bonfire) {
            setAnimations(((Bonfire)ch).lit());
        }
    }
}
