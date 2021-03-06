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

package com.shatteredpixel.yasd.general.actors.hero;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Badges;
import com.shatteredpixel.yasd.general.Challenges;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.items.BrokenSeal;
import com.shatteredpixel.yasd.general.items.DeveloperItem;
import com.shatteredpixel.yasd.general.items.Generator;
import com.shatteredpixel.yasd.general.items.KindOfWeapon;
import com.shatteredpixel.yasd.general.items.KindofMisc;
import com.shatteredpixel.yasd.general.items.MainHandItem;
import com.shatteredpixel.yasd.general.items.WeaponEditor;
import com.shatteredpixel.yasd.general.items.armor.Armor;
import com.shatteredpixel.yasd.general.items.armor.ChainArmor;
import com.shatteredpixel.yasd.general.items.armor.HuntressArmor;
import com.shatteredpixel.yasd.general.items.armor.MageArmor;
import com.shatteredpixel.yasd.general.items.armor.RandomArmor;
import com.shatteredpixel.yasd.general.items.armor.RogueArmor;
import com.shatteredpixel.yasd.general.items.bags.MagicalHolster;
import com.shatteredpixel.yasd.general.items.bags.PotionBandolier;
import com.shatteredpixel.yasd.general.items.bags.ScrollHolder;
import com.shatteredpixel.yasd.general.items.bags.VelvetPouch;
import com.shatteredpixel.yasd.general.items.food.Food;
import com.shatteredpixel.yasd.general.items.food.SmallRation;
import com.shatteredpixel.yasd.general.items.potions.PotionOfHealing;
import com.shatteredpixel.yasd.general.items.potions.PotionOfInvisibility;
import com.shatteredpixel.yasd.general.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.yasd.general.items.potions.PotionOfMindVision;
import com.shatteredpixel.yasd.general.items.rings.RingOfBinding;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfRage;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.yasd.general.items.souls.Humanity;
import com.shatteredpixel.yasd.general.items.souls.SoulOfLostUndead;
import com.shatteredpixel.yasd.general.items.spells.MagicalInfusion;
import com.shatteredpixel.yasd.general.items.titanite.LargeTitaniteShard;
import com.shatteredpixel.yasd.general.items.titanite.TitaniteChunk;
import com.shatteredpixel.yasd.general.items.titanite.TitaniteShard;
import com.shatteredpixel.yasd.general.items.titanite.TitaniteSlab;
import com.shatteredpixel.yasd.general.items.wands.WandOfMagicMissile;
import com.shatteredpixel.yasd.general.items.weapon.SpiritBow;
import com.shatteredpixel.yasd.general.items.weapon.melee.MagesStaff;
import com.shatteredpixel.yasd.general.items.weapon.melee.RandomMeleeWeapon;
import com.shatteredpixel.yasd.general.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.yasd.general.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;

import org.jetbrains.annotations.Contract;

public enum HeroClass {

	WARRIOR( "warrior", HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR ),
	MAGE( "mage", HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK ),
	ROGUE( "rogue", HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER ),
	HUNTRESS( "huntress", HeroSubClass.SNIPER, HeroSubClass.WARDEN );

	private String title;
	private HeroSubClass[] subClasses;

	HeroClass( String title, HeroSubClass...subClasses ) {
		this.title = title;
		this.subClasses = subClasses;
	}

	public void initHero( Hero hero ) {

		hero.heroClass = this;

		initCommon( hero );

		switch (this) {
			case WARRIOR:
				initWarrior( hero );
				break;

			case MAGE:
				initMage( hero );
				break;

			case ROGUE:
				initRogue( hero );
				break;

			case HUNTRESS:
				initHuntress( hero );
				break;
		}

		for (MainHandItem item : hero.belongings.weapons) if (item != null) item.activate(hero);
		if (hero.belongings.armor != null) hero.belongings.armor.activate(hero);
		for (KindofMisc misc : hero.belongings.miscs) if (misc != null) misc.activate(hero);
	}

	private static void initCommon( Hero hero ) {
		if (Dungeon.isChallenged(Challenges.NO_FOOD)){
			new SmallRation().collect();
		} else {
			new Food().collect();
		}
		new ScrollOfIdentify().identify().collect();
		if (Dungeon.testing) {
			initTest(hero);
		}
	}

	public static void initTest(Hero hero) {
		if (hero.belongings.getItem(VelvetPouch.class) == null) {
			new VelvetPouch().collect();
		}
		if (hero.belongings.getItem(ScrollHolder.class) == null) {
			new ScrollHolder().collect();
		}
		if (hero.belongings.getItem(PotionBandolier.class) == null) {
			new PotionBandolier().collect();
		}
		if (hero.belongings.getItem(MagicalHolster.class) == null) {
			new MagicalHolster().collect();
		}
		new Humanity().collect();
		new SoulOfLostUndead().collect();
		new DeveloperItem().collect(hero.belongings.backpack, hero);
		for (int i = 0; i < Belongings.BACKPACK_SIZE; i++) {
			new ScrollOfUpgrade().collect();
			new MagicalInfusion().collect();
		}
		new RingOfBinding().collect();
		new TitaniteShard().collect();
		new LargeTitaniteShard().collect();
		new TitaniteChunk().collect();
		new TitaniteSlab().collect();
		new RandomArmor().rollStats().random().collect();
		Generator.randomWand().identify().collect();
	}

	@Contract(pure = true)
	public Badges.Badge masteryBadge() {
		switch (this) {
			case WARRIOR:
				return Badges.Badge.MASTERY_WARRIOR;
			case MAGE:
				return Badges.Badge.MASTERY_MAGE;
			case ROGUE:
				return Badges.Badge.MASTERY_ROGUE;
			case HUNTRESS:
				return Badges.Badge.MASTERY_HUNTRESS;
		}
		return null;
	}

	private static void initWarrior( Hero hero ) {
		RandomMeleeWeapon weapon = new RandomMeleeWeapon();
		(hero.belongings.weapons[0] = weapon.matchProfile()).identify();
		(hero.belongings.armor = new ChainArmor()).identify();
		ThrowingStone stones = new ThrowingStone();
		stones.quantity(3).collect();
		Dungeon.quickslot.setSlot(0, stones);

		new BrokenSeal().collect();
		
		new PotionBandolier().collect();
		Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
		
		new PotionOfHealing().identify();
		new ScrollOfRage().identify();
		hero.increasePower();
		hero.STR();
	}

	private static void initMage( Hero hero ) {
		MagesStaff staff;
		
		staff = new MagesStaff(new WandOfMagicMissile());

		(hero.belongings.weapons[0] = staff).identify();
		(hero.belongings.armor = new MageArmor()).identify();
		hero.belongings.getWeapons().get(0).activate(hero);

		Dungeon.quickslot.setSlot(0, staff);

		new ScrollHolder().collect();
		Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
		
		new ScrollOfUpgrade().identify();
		new PotionOfLiquidFlame().identify();
		hero.increaseFocus();
	}

	private static void initRogue( Hero hero ) {
		RandomMeleeWeapon weapon = new RandomMeleeWeapon();
		weapon.properties.add(KindOfWeapon.Property.SURPRISE_ATTK_BENEFIT);
		(hero.belongings.weapons[0] = weapon.matchProfile()).identify();
		(hero.belongings.armor = new RogueArmor()).identify();

		WeaponEditor editor = new WeaponEditor();
		editor.collect();

		ThrowingKnife knives = new ThrowingKnife();
		knives.quantity(3).collect();

		Dungeon.quickslot.setSlot(0, editor);
		Dungeon.quickslot.setSlot(1, knives);

		new VelvetPouch().collect();
		Dungeon.LimitedDrops.VELVET_POUCH.drop();
		
		new ScrollOfMagicMapping().identify();
		new PotionOfInvisibility().identify();
		hero.increaseEvasion();
	}

	private static void initHuntress( Hero hero ) {
		RandomMeleeWeapon weapon = new RandomMeleeWeapon();
		weapon.DLY = 0.5f;
		(hero.belongings.weapons[0] = weapon.matchProfile()).identify();
		(hero.belongings.armor = new HuntressArmor()).identify();
		SpiritBow bow = new SpiritBow();
		bow.identify().collect();

		Dungeon.quickslot.setSlot(0, bow);

		new VelvetPouch().collect();
		Dungeon.LimitedDrops.VELVET_POUCH.drop();
		
		new PotionOfMindVision().identify();
		new ScrollOfLullaby().identify();

		hero.increasePerception();
	}
	
	public String title() {
		return Messages.get(HeroClass.class, title);
	}
	
	public HeroSubClass[] subClasses() {
		return subClasses;
	}
	
	public String spritesheet() {
		switch (this) {
			case WARRIOR: default:
				return Assets.Sprites.WARRIOR;
			case MAGE:
				return Assets.Sprites.MAGE;
			case ROGUE:
				return Assets.Sprites.ROGUE;
			case HUNTRESS:
				return Assets.Sprites.HUNTRESS;
		}
	}

	public String splashArt(){
		switch (this) {
			case WARRIOR: default:
				return Assets.Splashes.WARRIOR;
			case MAGE:
				return Assets.Splashes.MAGE;
			case ROGUE:
				return Assets.Splashes.ROGUE;
			case HUNTRESS:
				return Assets.Splashes.HUNTRESS;
		}
	}
	
	public String[] perks() {
		switch (this) {
			case WARRIOR: default:
				return new String[]{
						Messages.get(HeroClass.class, "warrior_perk1"),
						Messages.get(HeroClass.class, "warrior_perk2"),
						Messages.get(HeroClass.class, "warrior_perk3"),
						Messages.get(HeroClass.class, "warrior_perk4"),
						Messages.get(HeroClass.class, "warrior_perk5"),
				};
			case MAGE:
				return new String[]{
						Messages.get(HeroClass.class, "mage_perk1"),
						Messages.get(HeroClass.class, "mage_perk2"),
						Messages.get(HeroClass.class, "mage_perk3"),
						Messages.get(HeroClass.class, "mage_perk4"),
						Messages.get(HeroClass.class, "mage_perk5"),
				};
			case ROGUE:
				return new String[]{
						Messages.get(HeroClass.class, "rogue_perk1"),
						Messages.get(HeroClass.class, "rogue_perk2"),
						Messages.get(HeroClass.class, "rogue_perk3"),
						Messages.get(HeroClass.class, "rogue_perk4"),
						Messages.get(HeroClass.class, "rogue_perk5"),
				};
			case HUNTRESS:
				return new String[]{
						Messages.get(HeroClass.class, "huntress_perk1"),
						Messages.get(HeroClass.class, "huntress_perk2"),
						Messages.get(HeroClass.class, "huntress_perk3"),
						Messages.get(HeroClass.class, "huntress_perk4"),
						Messages.get(HeroClass.class, "huntress_perk5"),
				};
		}
	}
	
	public boolean locked(){
		//always unlock on debug builds
		if (DeviceCompat.isDebug()) return false;
		
		switch (this){
			case WARRIOR: default:
				return false;
			case MAGE:
				return !Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
			case ROGUE:
				return !Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
			case HUNTRESS:
				return !Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
		}
	}
	
	public String unlockMsg() {
		switch (this){
			case WARRIOR: default:
				return "";
			case MAGE:
				return Messages.get(HeroClass.class, "mage_unlock");
			case ROGUE:
				return Messages.get(HeroClass.class, "rogue_unlock");
			case HUNTRESS:
				return Messages.get(HeroClass.class, "huntress_unlock");
		}
	}

	private static final String CLASS	= "class";
	
	public void storeInBundle( Bundle bundle ) {
		bundle.put( CLASS, toString() );
	}
	
	public static HeroClass restoreInBundle( Bundle bundle ) {
		String value = bundle.getString( CLASS );
		return value.length() > 0 ? valueOf( value ) : ROGUE;
	}
}
