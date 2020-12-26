package com.shatteredpixel.yasd.general.ui.attack;

import com.shatteredpixel.yasd.general.Chrome;
import com.watabou.noosa.Group;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

public class ActionFrame extends Group {

    private final NinePatch bg;

    public ActionFrame(float xPos, float yPos, ActionIcon[] contents) {
        add(this);
        bg = Chrome.get(Chrome.Type.GREY_BUTTON_TR);
        assert bg != null;
        bg.x = xPos;
        bg.y = yPos;
        bg.size(24, 24*contents.length);
        add(bg);

        float pos = yPos;
        for (ActionIcon actionIcon : contents) {
            pos += actionIcon.height()*0.25f;
            actionIcon.setPos(xPos + actionIcon.width()/4, pos);
            pos += actionIcon.height()*1.25f;
            add(actionIcon);
        }
    }
}
