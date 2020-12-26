package com.shatteredpixel.yasd.general.actors.mobs.npcs;

import com.shatteredpixel.yasd.general.PDSGame;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.scenes.EndingScene;

public class FirstFlame extends Bonfire {

    @Override
    public void light(Char ch) {
        EndingScene.noText = false;
        PDSGame.switchScene(EndingScene.class);
    }
}
