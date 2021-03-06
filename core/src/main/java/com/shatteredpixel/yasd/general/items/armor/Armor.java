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

package com.shatteredpixel.yasd.general.items.armor;

import com.shatteredpixel.yasd.general.Badges;
import com.shatteredpixel.yasd.general.Constants;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.PDSGame;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Buff;
import com.shatteredpixel.yasd.general.actors.buffs.MagicImmune;
import com.shatteredpixel.yasd.general.actors.buffs.ShieldBuff;
import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.effects.Speck;
import com.shatteredpixel.yasd.general.items.BrokenSeal;
import com.shatteredpixel.yasd.general.items.EquipableItem;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.items.armor.curses.AntiEntropy;
import com.shatteredpixel.yasd.general.items.armor.curses.Bulk;
import com.shatteredpixel.yasd.general.items.armor.curses.Corrosion;
import com.shatteredpixel.yasd.general.items.armor.curses.Displacement;
import com.shatteredpixel.yasd.general.items.armor.curses.Metabolism;
import com.shatteredpixel.yasd.general.items.armor.curses.Multiplicity;
import com.shatteredpixel.yasd.general.items.armor.curses.Overgrowth;
import com.shatteredpixel.yasd.general.items.armor.curses.Stench;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Affection;
import com.shatteredpixel.yasd.general.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Brimstone;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Camouflage;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Entanglement;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Flow;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Obfuscation;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Potential;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Repulsion;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Stone;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Swiftness;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Thorns;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Viscosity;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.sprites.HeroSprite;
import com.shatteredpixel.yasd.general.sprites.ItemSprite;
import com.shatteredpixel.yasd.general.sprites.ItemSpriteSheet;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Armor extends EquipableItem {

	public float EVA = 1f;
	public float STE = 1f;
	public float speedFactor = 1f;
	public float magicDamageFactor = 1f;
	public float physicalDamageFactor = 1f;
	public float regenFactor = 1f;
	public float poiseFactor = 1f;

	protected static final String AC_DETACH       = "DETACH";
	
	public enum Augment {
		EVASION (2f , -1f),
		DEFENSE (-2f, 1f),
		NONE	(0f   ,  0f);
		
		private float evasionFactor;
		private float defenceFactor;
		
		Augment(float eva, float df){
			evasionFactor = eva;
			defenceFactor = df;
		}
		
		public int evasionFactor(int level){
			return Math.round((2 + level) * evasionFactor);
		}
		
		public int defenseFactor(int level){
			return Math.round((2 + level) * defenceFactor);
		}
	}

	@Override
	public boolean canDegrade() {
		return Constants.DEGRADATION;
	}

	public Augment augment = Augment.NONE;
	
	public Glyph glyph;
	public boolean curseInfusionBonus = false;
	
	private BrokenSeal seal;
	
	public int tier = 1;
	public int appearance = 1;
	
	private static final int USES_TO_ID = 10;
	private int usesLeftToID = USES_TO_ID;
	private float availableUsesToID = USES_TO_ID/2f;
	
	private static final String USES_LEFT_TO_ID = "uses_left_to_id";
	private static final String AVAILABLE_USES  = "available_uses";
	private static final String GLYPH			= "glyph";
	private static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";
	private static final String SEAL            = "seal";
	private static final String AUGMENT			= "augment";
	private static final String TIER = "tier";

	@Override
	public void storeInBundle(  Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( USES_LEFT_TO_ID, usesLeftToID );
		bundle.put( AVAILABLE_USES, availableUsesToID );
		bundle.put( GLYPH, glyph );
		bundle.put( CURSE_INFUSION_BONUS, curseInfusionBonus );
		bundle.put( SEAL, seal);
		bundle.put( AUGMENT, augment);
		bundle.put(TIER, tier);
	}

	@Override
	public void restoreFromBundle(  Bundle bundle ) {
		super.restoreFromBundle(bundle);
		usesLeftToID = bundle.getInt(USES_LEFT_TO_ID);
		availableUsesToID = bundle.getInt(AVAILABLE_USES);
		inscribe((Glyph) bundle.get(GLYPH));
		curseInfusionBonus = bundle.getBoolean(CURSE_INFUSION_BONUS);
		seal = (BrokenSeal) bundle.get(SEAL);

		//pre-0.7.2 saves
		if (bundle.contains("unfamiliarity")) {
			usesLeftToID = bundle.getInt("unfamiliarity");
			availableUsesToID = USES_TO_ID / 2f;
		}

		augment = bundle.getEnum(AUGMENT, Augment.class);

		tier = bundle.getInt(TIER);

	}

	@Override
	public int image() {
		return ItemSpriteSheet.adjustForTier(image, tier);
	}

	@Override
	public void reset() {
		super.reset();
		usesLeftToID = USES_TO_ID;
		availableUsesToID = USES_TO_ID/2f;
		//armours can be kept in bones between runs, the seal cannot.
		seal = null;
	}

	public float poise(int lvl) {
		return (2f + 0.3f * lvl * tier) * poiseFactor;
	}

	public final float poise() {
		return poise(level());
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (seal != null) actions.add(AC_DETACH);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_DETACH) && seal != null){
			BrokenSeal.WarriorShield sealBuff = hero.buff(BrokenSeal.WarriorShield.class);
			if (sealBuff != null) sealBuff.setArmor(null);

			GLog.i( Messages.get(Armor.class, "detach_seal") );
			hero.sprite.operate(hero.pos);
			if (!seal.collect()){
				Dungeon.level.drop(seal, hero.pos);
			}
			seal = null;
		}
	}


	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (seal != null) Buff.affect(ch, BrokenSeal.WarriorShield.class).setArmor(this);
	}

	public void affixSeal(BrokenSeal seal){
		this.seal = seal;
		if (isEquipped(Dungeon.hero)){
			Buff.affect(Dungeon.hero, BrokenSeal.WarriorShield.class).setArmor(this);
		}
	}

	public float getDefenseFactor() {
		float DRfactor = 1f;
		DRfactor *= 1/EVA;
		DRfactor *= 1/STE;
		DRfactor *= 1/speedFactor;
		DRfactor *= 1/regenFactor;
		DRfactor *= magicDamageFactor;
		DRfactor *= physicalDamageFactor;
		DRfactor *= 1/poiseFactor;
		return DRfactor;
	}

	public int appearance() {
		return appearance;
	}

	public BrokenSeal checkSeal(){
		return seal;
	}

	@Override
	protected float time2equip( Char hero ) {
		return 2 / hero.speed();
	}

	public int defense() {
		return defense(level());
	}

	public int defense(int lvl) {
		return (int) ((2*(tier+1) +    //base
				lvl*tier)*getDefenseFactor());   //level scaling
	}

	@Override
	public boolean doEquip( Char ch ) {

		detach(ch.belongings.backpack);

		if (ch.belongings.armor == null || ch.belongings.armor.doUnequip( ch, true, false )) {

			ch.belongings.armor = this;

			cursedKnown = true;
			if (cursed) {
				equipCursed( ch );
				GLog.n( Messages.get(Armor.class, "equip_cursed") );
			}

			((HeroSprite)ch.sprite).updateArmor();
			activate(ch);
			ch.spendAndNext( time2equip( ch ) );
			return true;

		} else {

			collect( ch.belongings.backpack, ch );
			return false;

		}
	}

	@Override
	public boolean doUnequip( Char ch, boolean collect, boolean single ) {
		if (super.doUnequip( ch, collect, single )) {

			ch.belongings.armor = null;
			((HeroSprite)ch.sprite).updateArmor();

			BrokenSeal.WarriorShield sealBuff = ch.buff(BrokenSeal.WarriorShield.class);
			if (sealBuff != null) sealBuff.setArmor(null);

			return true;

		} else {

			return false;

		}
	}

	@Override
	public int level() {
		int lvl = super.level();
		if (curseInfusionBonus) {
			lvl += Constants.CURSE_INFUSION_BONUS_AMT;
		}
		if (seal != null) {
			lvl += seal.level();
		}
		lvl = Math.min(upgradeLimit(), lvl);
		return lvl;
	}
	
	@Override
	public Item upgrade() {
		return upgrade( false );
	}
	
	public Item upgrade( boolean inscribe ) {

		if (inscribe && (glyph == null || glyph.curse())){
			inscribe( Glyph.random() );
		} else if (!inscribe && level() >= 4 && Random.Float(10) < Math.pow(2, level()-4)){
			inscribe(null);
		}
		
		cursed = false;

		if (seal != null && seal.level() == 0) {
			seal.upgrade();
			return this;
		}

		return super.upgrade();
	}
	
	public int proc( Char attacker, Char defender, int damage ) {

		if (glyph != null && defender.buff(MagicImmune.class) == null) {
			damage = glyph.proc( this, attacker, defender, damage );
		}

		if (!levelKnown && defender == Dungeon.hero && availableUsesToID >= 1) {
			availableUsesToID--;
			usesLeftToID--;
			if (usesLeftToID <= 0) {
				identify();
				GLog.p( Messages.get(Armor.class, "identify") );
				Badges.validateItemLevelAquired( this );
			}
		}

		return damage;
	}

	public int magicalProc( Char attacker, Char defender, int damage ) {

		if (glyph != null && defender.buff(MagicImmune.class) == null) {
			damage = glyph.magicalProc( this, attacker, defender, damage );
		}

		if (!levelKnown && defender == Dungeon.hero && availableUsesToID >= 1) {
			availableUsesToID--;
			usesLeftToID--;
			if (usesLeftToID <= 0) {
				identify();
				GLog.p( Messages.get(Armor.class, "identify") );
				Badges.validateItemLevelAquired( this );
			}
		}

		return damage;
	}
	
	@Override
	public void onHeroGainExp(float levelPercent, Hero hero) {
		if (!levelKnown && isEquipped(hero) && availableUsesToID <= USES_TO_ID/2f) {
			//gains enough uses to ID over 0.5 levels
			availableUsesToID = Math.min(USES_TO_ID/2f, availableUsesToID + levelPercent * USES_TO_ID);
		}
	}
	
	@Override
	public String name() {
		return getName() == null ? Glyph.getName(this, glyph, cursedKnown) : getName();
	}

	@Override
	public void curse() {
		super.curse();
		inscribe(Glyph.randomCurse());
	}

	@Override
	public void uncurse() {
		if (hasCurseGlyph()) {
			inscribe(null);
		}
	}

	@Override
	public String info() {
		String info = desc();
		
		if (levelKnown) {
			info += "\n\n" + Messages.get(Armor.class, "curr_absorb", tier, defense(), new DecimalFormat("#.##").format(poise()), STRReq());
			
			if (STRReq() > Dungeon.hero.STR()) {
				info += " " + Messages.get(Armor.class, "too_heavy");
			}
		} else {
			info += "\n\n" + Messages.get(Armor.class, "avg_absorb", tier, defense(0), new DecimalFormat("#.##").format(poise(0)), STRReq());

			if (STRReq() > Dungeon.hero.STR()) {
				info += " " + Messages.get(Armor.class, "probably_too_heavy");
			}
		}

		switch (augment) {
			case EVASION:
				info += "\n\n" + Messages.get(Armor.class, "evasion");
				break;
			case DEFENSE:
				info += "\n\n" + Messages.get(Armor.class, "defense");
				break;
			case NONE:
		}

		if (EVA != 1f || STE != 1f || speedFactor != 1f || magicDamageFactor != 1f || physicalDamageFactor != 1f) {

			info += "\n";

			if (magicDamageFactor > 1f) {
				info += "\n" + Messages.get(Armor.class, "magical_weak", Math.round((magicDamageFactor -1f)*100));
			} else if (magicDamageFactor < 1f) {
				info += "\n" + Messages.get(Armor.class, "magical_resist", Math.round((1f- magicDamageFactor)*100));
			}

			if (physicalDamageFactor > 1f) {
				info += "\n" + Messages.get(Armor.class, "physical_weak", Math.round((physicalDamageFactor -1f)*100));
			} else if (physicalDamageFactor < 1f) {
				info += "\n" + Messages.get(Armor.class, "physical_resist", Math.round((1f- physicalDamageFactor)*100));
			}

			if (EVA > 1f) {
				info += "\n" + Messages.get(Armor.class, "eva_increase", Math.round((EVA-1f)*100));
			} else if (EVA < 1f) {
				info += "\n" + Messages.get(Armor.class, "eva_decrease", Math.round((1f-EVA)*100));
			}

			if (STE > 1f) {
				info += "\n" + Messages.get(Armor.class, "ste_increase", Math.round((STE-1f)*100));
			} else if (STE < 1f) {
				info += "\n" + Messages.get(Armor.class, "ste_decrease", Math.round((1f-STE)*100));
			}

			if (speedFactor > 1f) {
				info += "\n" + Messages.get(Armor.class, "speed_increase", Math.round((speedFactor-1f)*100));
			} else if (speedFactor < 1f) {
				info += "\n" + Messages.get(Armor.class, "speed_decrease", Math.round((1f-speedFactor)*100));
			}
		}
		
		if (glyph != null  && (cursedKnown || !glyph.curse())) {
			info += "\n\n" +  Messages.get(Armor.class, "inscribed", glyph.name());
			info += " " + glyph.desc();
		}
		
		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Armor.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Armor.class, "cursed");
		} else if (seal != null) {
			info += "\n\n" + Messages.get(Armor.class, "seal_attached");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Armor.class, "not_cursed");
		}
		
		return info;
	}

	@Override
	public Emitter emitter() {
		Emitter emitter = super.emitter();
		if (seal == null) return emitter;
		emitter.pos(ItemSpriteSheet.film.width(image)/2f + 2f, ItemSpriteSheet.film.height(image)/3f);
		emitter.fillTarget = false;
		emitter.pour(Speck.factory( Speck.RED_LIGHT ), 0.6f);
		return emitter;
	}

	@Override
	public Item random() {
		//+0: 75% (3/4)
		//+1: 20% (4/20)
		//+2: 5%  (1/20)
		int n = 0;
		if (Random.Int(4) == 0) {
			n++;
			if (Random.Int(5) == 0) {
				n++;
			}
		}
		level(n);
		
		//30% chance to be cursed
		//15% chance to be inscribed
		float effectRoll = Random.Float();
		if (effectRoll < 0.3f) {
			inscribe(Glyph.randomCurse());
			cursed = true;
		} else if (effectRoll >= 0.85f){
			inscribe();
		}

		return this;
	}

	public int STRReq() {
		return (7 + Math.round(tier * 3));
	}
	
	@Override
	public int price() {
		if (seal != null) return 0;

		int price = 20 * tier;
		if (hasGoodGlyph()) {
			price *= 1.5;
		}
		if (cursedKnown && (cursed || hasCurseGlyph())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	public Armor inscribe( Glyph glyph ) {
		if (glyph == null || !glyph.curse()) curseInfusionBonus = false;
		this.glyph = glyph;
		updateQuickslot();
		return this;
	}

	public Armor inscribe() {

		Class<? extends Glyph> oldGlyphClass = glyph != null ? glyph.getClass() : null;
		Glyph gl = Glyph.random( oldGlyphClass );

		return inscribe( gl );
	}

	public boolean hasGlyph(Class<?extends Glyph> type, Char owner) {
		return glyph != null && glyph.getClass() == type && owner.buff(MagicImmune.class) == null;
	}

	//these are not used to process specific glyph effects, so magic immune doesn't affect them
	public boolean hasGoodGlyph(){
		return glyph != null && !glyph.curse();
	}

	public boolean hasCurseGlyph(){
		return glyph != null && glyph.curse();
	}
	
	@Override
	public ItemSprite.Glowing glowing() {
		return glyph != null && cursedKnown ? glyph.glowing() : null;
	}
	
	public static abstract class Glyph implements Bundlable {

		public static String getName(Armor armor, Glyph gly, boolean showGlyph) {
			String name = armor.name;
			if (gly != null && showGlyph) {
				name = gly.name(name);
			}
			return name;
		}
		
		private static final Class<?>[] common = new Class<?>[]{
				Obfuscation.class, Swiftness.class, Viscosity.class, Potential.class };
		
		private static final Class<?>[] uncommon = new Class<?>[]{
				Brimstone.class, Stone.class, Entanglement.class,
				Repulsion.class, Camouflage.class, Flow.class };
		
		private static final Class<?>[] rare = new Class<?>[]{
				Affection.class, AntiMagic.class, Thorns.class };
		
		private static final float[] typeChances = new float[]{
				50, //12.5% each
				40, //6.67% each
				10  //3.33% each
		};

		private static final Class<?>[] curses = new Class<?>[]{
				AntiEntropy.class, Corrosion.class, Displacement.class, Metabolism.class,
				Multiplicity.class, Stench.class, Overgrowth.class, Bulk.class
		};
		
		public abstract int proc( Armor armor, Char attacker, Char defender, int damage );

		public int magicalProc( Armor armor, Char attacker, Char defender, int damage ) {
			return damage;
		}
		
		public String name() {
			if (!curse())
				return name( Messages.get(this, "glyph") );
			else
				return name( Messages.get(Item.class, "curse"));
		}
		
		public String name( String armorName ) {
			return Messages.get(this, "name", armorName);
		}

		public String desc() {
			return Messages.get(this, "desc");
		}

		public boolean curse() {
			return false;
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
		}
		
		public abstract ItemSprite.Glowing glowing();

		@SuppressWarnings("unchecked")
		public static Glyph random( Class<? extends Glyph> ... toIgnore ) {
			switch(Random.chances(typeChances)){
				case 0: default:
					return randomCommon( toIgnore );
				case 1:
					return randomUncommon( toIgnore );
				case 2:
					return randomRare( toIgnore );
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomCommon( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(common));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomUncommon( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(uncommon));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomRare( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(rare));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomCurse( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(curses));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
	}
	public Armor setTier(int tier) {
		this.tier = tier;
		updateTier();
		return this;
	}

	public Armor upgradeTier(int tier) {
		this.tier += tier;
		updateTier();
		return this;
	}

	public Armor degradeTier(int tier) {
		this.tier -= tier;
		updateTier();
		return this;
	}

	public void updateTier() {

	}

	public static int processDamage(int attack, int defense) {
		//Speed up 0-def calculations and prevent division by zero in rare cases where attk and def are 0
		if (defense == 0) return attack;
		float factor = attack/(float)(attack+defense);
		return (int) (attack * factor);
	}
}
