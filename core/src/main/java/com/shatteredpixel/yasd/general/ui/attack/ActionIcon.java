package com.shatteredpixel.yasd.general.ui.attack;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.ui.IconButton;
import com.watabou.noosa.Image;

public class ActionIcon extends IconButton {

    protected static final String ICONS = Assets.Interfaces.ACTION_ICON;

    protected static final int SIZE = 16;

    public static final int BASIC_ATTACK = 0;
    public static final int STAB_ATTACK  = 1;
    public static final int GUARD_BREAK  = 2;
    public static final int PARRY_WEAPON = 3;
    public static final int BLOCK_SHIELD = 4;
    public static final int PARRY_SHIELD = 5;
    public static final int SHIELD_BASH  = 6;
    public static final int ROLL         = 7;

    //imageIndex decides the icon, slot decides the weapon slot it's for (0 or 1) and basic whether the indicator triggers the basic attack or secondary
    public ActionIcon(int imageIndex) {
        super(null);
        setImageIndex(imageIndex);
    }

    protected void setImageIndex(int imageIndex) {
        icon(new Image(ICONS, SIZE*imageIndex, 0, SIZE, SIZE));
        width = height = SIZE;
    }
}
