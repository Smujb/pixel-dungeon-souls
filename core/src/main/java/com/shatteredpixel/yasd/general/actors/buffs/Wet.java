/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Powered Pixel Dungeon
 *  * Copyright (C) 2014-2020 Samuel Braithwaite
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *
 */

package com.shatteredpixel.yasd.general.actors.buffs;

import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.sprites.CharSprite;
import com.shatteredpixel.yasd.general.ui.BuffIndicator;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class Wet extends FlavourBuff {
    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    public static final float DURATION = 5f;

    @Override
    public boolean attachTo(@NotNull Char target) {
        if (target.buff(Frost.class) != null) return false;
        Buff chill = target.buff(Chill.class);
        if (chill != null) {//If enemy is chilled, freeze them instead.
            chill.detach();
            Buff.affect( target, Frost.class, DURATION );
            return false;
        }

        if (super.attachTo(target)){
            Buff.detach( target, Burning.class );
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add( CharSprite.State.WET );
        else if (target.invisible == 0) target.sprite.remove( CharSprite.State.WET );
    }

    public float evasionFactor(){
        return (float) Math.pow( 0.85, cooldown());
    }

    @Override
    public int icon() {
        return BuffIndicator.FROST;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (DURATION*2 - visualcooldown()) / DURATION*2);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(), new DecimalFormat("#.##").format((1f-evasionFactor())*100f));
    }
}
