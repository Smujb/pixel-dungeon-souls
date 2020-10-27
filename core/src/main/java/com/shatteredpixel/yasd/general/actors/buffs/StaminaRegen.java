package com.shatteredpixel.yasd.general.actors.buffs;

import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.actors.hero.HeroAction;

public class StaminaRegen extends Buff {

	{
		actPriority = HERO_PRIO + 1;
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {
			float increase = 1f;
			if (target instanceof Hero && ((Hero) target).isStarving()) {
				increase /= 2f;
			}
			float max = target.maxStamina();
			if (target.stamina < max) {
				target.stamina = Math.min(max, target.stamina + increase);
			}
		}
		spend(3f);
		return true;
	}
}
