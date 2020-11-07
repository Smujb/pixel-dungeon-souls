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

import com.shatteredpixel.yasd.general.Badges;
import com.shatteredpixel.yasd.general.Constants;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.GamesInProgress;
import com.shatteredpixel.yasd.general.actors.Actor;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Berserk;
import com.shatteredpixel.yasd.general.actors.buffs.Burning;
import com.shatteredpixel.yasd.general.actors.buffs.Fury;
import com.shatteredpixel.yasd.general.actors.buffs.Invisibility;
import com.shatteredpixel.yasd.general.actors.buffs.Momentum;
import com.shatteredpixel.yasd.general.actors.mobs.Mob;
import com.shatteredpixel.yasd.general.items.EquipableItem;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.items.KindOfWeapon;
import com.shatteredpixel.yasd.general.items.KindofMisc;
import com.shatteredpixel.yasd.general.items.MainHandItem;
import com.shatteredpixel.yasd.general.items.armor.Armor;
import com.shatteredpixel.yasd.general.items.armor.curses.Bulk;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Brimstone;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Flow;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Obfuscation;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Stone;
import com.shatteredpixel.yasd.general.items.armor.glyphs.Swiftness;
import com.shatteredpixel.yasd.general.items.artifacts.CapeOfThorns;
import com.shatteredpixel.yasd.general.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.yasd.general.items.bags.Bag;
import com.shatteredpixel.yasd.general.items.keys.Key;
import com.shatteredpixel.yasd.general.items.rings.RingOfFuror;
import com.shatteredpixel.yasd.general.items.rings.RingOfHaste;
import com.shatteredpixel.yasd.general.items.rings.RingOfTenacity;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.yasd.general.items.wands.Wand;
import com.shatteredpixel.yasd.general.items.weapon.Weapon;
import com.shatteredpixel.yasd.general.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.yasd.general.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.yasd.general.levels.terrain.Terrain;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Iterator;

public class Belongings implements Iterable<Item> {

	public static final int BACKPACK_SIZE	= 24;
	
	private Char owner;
	
	public Bag backpack;

	public MainHandItem weapons[] = new MainHandItem[2];
	public Armor armor = null;
	public KindofMisc[] miscs = new KindofMisc[Constants.MISC_SLOTS];
	public int currentWeapon = 0;

	public Belongings( Char owner ) {
		this.owner = owner;

		backpack = new Bag() {
			{
				name = Messages.get(Bag.class, "name");
			}
			public int capacity(){
				int cap = super.capacity();
				for (Item item : items){
					if (item instanceof Bag){
						cap++;
					}
				}
				return cap;
			}
		};
		backpack.owner = owner;
	}

	//##############################################################################################
	//########################## Stuff for handling chars with belongings ##########################
	//##############################################################################################

	private ArrayList<Integer> availibleSlots(KindofMisc misc) {
		ArrayList<Integer> slots = new ArrayList<>();
		slots.add(0);
		slots.add(1);
		slots.add(2);
		slots.add(3);
		slots.add(4);
		return slots;
	}

	public boolean canEquip(KindofMisc misc, int slot) {//Use for setting specific properties for each slot.
		return miscs[slot] == null && availibleSlots(misc).contains(slot);
	}

	public boolean canEquip(KindofMisc misc) {//Use for setting specific properties for each slot.
		for (int i = 0; i < miscs.length; i++) {
			if (canEquip(misc, i)) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<KindOfWeapon> getWeapons() {
		ArrayList<KindOfWeapon> weapons = new ArrayList<>();
		for (MainHandItem item : this.weapons) {
			if (item instanceof MeleeWeapon) {
				weapons.add((KindOfWeapon) item);
			}
		}
		return weapons;

	}

	public ArrayList<KindofMisc> getMiscsOfType(Class type ) {//Find equipped items of a certain kind
		ArrayList<KindofMisc> items = new ArrayList<>();
		for (KindofMisc misc : miscs) {
			if (type.isInstance(misc)) {
				items.add(misc);
			}
		}
		return items;
	}

	public boolean shoot(Char enemy, MissileWeapon wep) {

		//temporarily set the hero's weapon to the missile weapon being used
		MainHandItem equipped = weapons[0];
		weapons[0] = wep;
		boolean hit = owner.attack(enemy);
		Invisibility.dispel();
		weapons[0] = equipped;

		return hit;
	}

	public int numberOfWeapons() {
		return getWeapons().size();
	}

	public void resetWeapon() {
		if (currentWeapon > (numberOfWeapons() - 1)) {
			currentWeapon = 0;
		}
	}

	public void nextWeapon() {
		currentWeapon += 1;
		resetWeapon();
	}

	public void doSwitchToNextWeapon() {
		owner.spend(attackDelay());
		owner.busy();
		nextWeapon();
		owner.sprite.operate( owner.pos );
		GLog.i(Messages.get(this, "weapon_switch"));
	}

	public KindOfWeapon getCurrentWeapon() {
	    if (getWeapons().size() == 0) {
	        return null;
        }
		resetWeapon();
		if (weapons[0] instanceof MissileWeapon) {
			return ((MissileWeapon) weapons[0]);
		}
		return getWeapons().get(currentWeapon);
	}

	public float accuracyFactor(float accuracy, Char target) {
		KindOfWeapon wep = getCurrentWeapon();
		//accuracy *= RingOfPerception.accuracyMultiplier(owner);
		if (wep instanceof MissileWeapon) {
			if (Dungeon.level.adjacent(owner.pos, target.pos)) {
				accuracy *= 0.5f;
			} else {
				accuracy *= 1.5f;
			}
		}

		if (wep != null) {
			accuracy *= wep.accuracyFactor(owner);
		}
		return accuracy;
	}

	public float physicalDamageFactor() {
		if (armor != null) return armor.physicalDamageFactor;
		return 1f;
	}

	public float magicalDamageFactor() {
		if (armor != null) return armor.magicDamageFactor;
		return 1f;
	}

	public float defenseRegen() {
		if (armor != null) return armor.defenseRegen();
		return 0;
	}

	public float poise() {
		if (armor != null) return armor.poise();
		return 0;
	}

	public float impact() {
		KindOfWeapon weapon = getCurrentWeapon();
		if (weapon instanceof MeleeWeapon) {
			return ((MeleeWeapon)weapon).impact();
		} else {
			return 0f;
		}
	}

	public int defense() {
		int defense = 0;
		if (armor != null) {
			int armDefense = armor.defense();
			if (owner.STR() < armor.STRReq()) {
				armDefense -= 2 * (armor.STRReq() - owner.STR());
			}
			if (armDefense > 0) defense += armDefense;
		}

		ArrayList<KindOfWeapon> Weapons = getWeapons();
		if (Weapons.size() > 0) {
			for (int i = 0; i < Weapons.size(); i++) {
				int wepDefense = Weapons.get(i).defenseFactor(owner);
				if (Weapons.get(i) instanceof MeleeWeapon & owner.STR() < ((MeleeWeapon) Weapons.get(i)).STRReq()) {
					wepDefense -= 2 * (((MeleeWeapon) Weapons.get(i)).STRReq()) - owner.STR();
				}
				if (wepDefense > 0) defense += wepDefense;
			}
		}
		return defense;
	}

	public int damageRoll() {
		int dmg;
		KindOfWeapon wep = getCurrentWeapon();
		if (wep != null) {
			if (wep instanceof MeleeWeapon) {
				wep.use();
			}
			dmg = wep.damageRoll(owner);
		} else {
			int level = 0;
			if (owner instanceof Mob) {
				level = ((Mob) owner).getLevel();
			} else if (owner instanceof Hero) {
				level = ((Hero)owner).lvl/2;
			}
			dmg = Random.Int(1, level);
		}
		if (dmg < 0) dmg = 0;

		Berserk berserk = owner.buff(Berserk.class);
		if (berserk != null) dmg = berserk.damageFactor(dmg);

		return owner.buff(Fury.class) != null ? (int) (dmg * 1.5f) : dmg;
	}

	public int defenseProc(Char enemy, int damage) {
		if (armor != null) damage = armor.proc(enemy, owner, damage);
		return damage;
	}

	public int attackProc(Char enemy, int damage) {
		KindOfWeapon wep = getCurrentWeapon();
		if (wep != null) damage = wep.proc(owner, enemy, damage);
		return damage;
	}

	public int magicalAttackProc(Char enemy, int damage) {
		return damage;
	}

	public int magicalDefenseProc(Char enemy, int damage) {
		if (armor != null) damage = armor.magicalProc(enemy, owner, damage);
		return damage;
	}

	public boolean canSurpriseAttack() {
		KindOfWeapon curWep = getCurrentWeapon();
		if (!(curWep instanceof Weapon)) return true;
		if (owner.STR() < ((Weapon) curWep).STRReq()) return false;
		return !curWep.properties.contains(KindOfWeapon.Property.CANT_SURPRISE_ATTK);
	}

	public int affectDamage(int damage, Object src) {
		if (owner.buff(TimekeepersHourglass.timeStasis.class) != null) {
			return 0;
		}
		CapeOfThorns.Thorns thorns = owner.buff(CapeOfThorns.Thorns.class);
		if (thorns != null) {
			damage = thorns.proc(damage, (src instanceof Char ? (Char) src : null), owner);
		}

		damage = (int) Math.ceil(damage * RingOfTenacity.damageMultiplier(owner));

		return damage;
	}

	public float attackDelay() {
		float multiplier = 1f;
		if (numberOfWeapons() > 0) {
			return getCurrentWeapon().speedFactor(owner) * multiplier;//Two weapons = 1/2 attack speed
		} else {
			//Normally putting furor speed on unarmed attacks would be unnecessary
			//But there's going to be that one guy who gets a furor+force ring combo
			//This is for that one guy, you shall get your fists of fury!
			return RingOfFuror.attackDelayMultiplier(owner);
		}
	}


	public float affectEvasion(float evasion) {
		if (armor != null) {
 			Armor CurArmour = armor;
			//evasion *= CurArmour.evasionMultiplier(ownerID);
			if (CurArmour.hasGlyph(Stone.class, owner) && !((Stone) CurArmour.glyph).testingEvasion()) {
				return 0;
			}
			int aEnc = CurArmour.STRReq() -  owner.STR();
			if (aEnc > 0) evasion /= Math.pow(1.5, aEnc);

			Momentum momentum = owner.buff(Momentum.class);
			if (momentum != null) {
				evasion += momentum.evasionBonus(Math.max(0, -aEnc));

			}
			evasion += CurArmour.augment.evasionFactor(CurArmour.level());

			evasion *= CurArmour.EVA;

		}
		return evasion;
	}

	public float affectSpeed(float speed) {
		speed *= RingOfHaste.speedMultiplier(owner);
		if (armor != null) {
			Armor curArmor = armor;
			//speed *= CurArmour.speedMultiplier(ownerID);
			int aEnc = curArmor.STRReq() - owner.STR();
			if (aEnc > 0) speed /= Math.pow(1.2, aEnc);

			if (curArmor.hasGlyph(Swiftness.class, owner)) {
				boolean enemyNear = false;
				PathFinder.buildDistanceMap(owner.pos, Dungeon.level.passable(), 2);
				for (Char ch : Actor.chars()){
					if ( PathFinder.distance[ch.pos] != Integer.MAX_VALUE && owner.alignment != ch.alignment){
						enemyNear = true;
						break;
					}
				}
				if (!enemyNear) speed *= (1.2f + 0.04f * curArmor.level());
			} else if (curArmor.hasGlyph(Flow.class, owner) && Dungeon.level.liquid(owner.pos)){
				speed *= (2f + 0.25f*curArmor.level());
			}

			if (curArmor.hasGlyph(Bulk.class, owner) &&
					(Dungeon.level.getTerrain(owner.pos) == Terrain.DOOR
							|| Dungeon.level.getTerrain(owner.pos) == Terrain.OPEN_DOOR )) {
				speed /= 3f;
			}
			speed *= curArmor.speedFactor;

		}
		return speed;
	}

	public float affectStealth(float stealth) {
		if (armor != null) {
			Armor CurArmour = armor;
			//sneakSkill *= CurArmour.stealthMultiplier(ownerID);

			if (CurArmour.hasGlyph(Obfuscation.class, owner)){
				stealth += 1 + CurArmour.level()/3f;
			}
			int penalty = CurArmour.tier;

			if (owner instanceof Hero) {
				if (((Hero) owner).heroClass == HeroClass.ROGUE) {
					penalty /= 2;
				}
			}
			stealth -= penalty;

			stealth *= CurArmour.STE;
		}
		return stealth;
	}

	public float affectPerception(float perception) {
		return perception;
	}

	public boolean isImmune(Class effect) {
		if (armor != null) {
			return effect == Burning.class && armor.hasGlyph(Brimstone.class, owner);
		}
		return false;
	}

	//##############################################################################################
	//####################### End of stuff for handling chars with belongings ######################
	//##############################################################################################

	private static final String ARMOR		= "armor";
	private static final String WEAPONS		= "weapon";
	private static final String MISC        = "misc";


	public void storeInBundle( Bundle bundle ) {
		
		backpack.storeInBundle( bundle );

		for (int i = 0; i < 2; i++) {
			bundle.put(WEAPONS + i, weapons[i]);
		}

		bundle.put(ARMOR, armor);

		for (int i = 0; i < Constants.MISC_SLOTS; i++) {//Store all miscs
			bundle.put( MISC + i, miscs[i]);
		}
	}
	
	public void restoreFromBundle( Bundle bundle ) {
		
		backpack.clear();
		backpack.restoreFromBundle( bundle );

		for (int i = 0; i < 2; i++) {
			weapons[i] = ((MainHandItem)bundle.get(WEAPONS + i));
			if (weapons[i] != null) weapons[i].activate(owner);
		}

		armor = (Armor) bundle.get(ARMOR);
		if (armor != null) armor.activate(owner);

		for (int i = 0; i < Constants.MISC_SLOTS; i++) {
			miscs[i] = (KindofMisc)bundle.get(MISC + i);
			if (miscs[i] != null) miscs[i].activate(owner);
		}
	}
	
	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		if (bundle.contains( ARMOR )){
			info.armorAppearance = ((Armor)bundle.get( ARMOR )).appearance;
		} else {
			info.armorAppearance = 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public<T extends Item> T getItem( Class<T> itemClass ) {

		for (Item item : this) {
			if (itemClass.isInstance( item )) {
				return (T)item;
			}
		}
		
		return null;
	}
	
	public boolean contains( Item contains ){
		
		for (Item item : this) {
			if (contains == item ) {
				return true;
			}
		}
		
		return false;
	}
	
	public Item getSimilar( Item similar ){
		
		for (Item item : this) {
			if (similar != item && similar.isSimilar(item)) {
				return item;
			}
		}
		
		return null;
	}
	
	public ArrayList<Item> getAllSimilar( Item similar ){
		ArrayList<Item> result = new ArrayList<>();
		
		for (Item item : this) {
			if (item != similar && similar.isSimilar(item)) {
				result.add(item);
			}
		}
		
		return result;
	}
	
	public void identify() {
		for (Item item : this) {
			item.identify();
		}
	}
	
	public void observe() {
		for (MainHandItem weapon : weapons) {//Restore all miscs
			if (weapon != null) {
				weapon.identify();
				Badges.validateItemLevelAquired(weapon);
			}
		}

		if (armor != null) {
			armor.identify();
			Badges.validateItemLevelAquired(armor);
		}

		for (int i = 0; i < Constants.MISC_SLOTS; i++) {//Restore all miscs
			if (miscs[i] != null) {
				miscs[i].identify();
				Badges.validateItemLevelAquired(miscs[i]);
			}
		}

		for (Item item : backpack) {
			if (item instanceof EquipableItem) {
				item.cursedKnown = true;
			}
		}
	}
	
	public void uncurseEquipped() {
		ScrollOfRemoveCurse.uncurse( owner,weapons[0], weapons[1], armor, miscs[0], miscs[1], miscs[2]);
	}
	
	public Item randomUnequipped() {
		return Random.element( backpack.items );
	}
	
	public void resurrect(String levelKey) {

		for (Item item : backpack.items.toArray( new Item[0])) {
			if (item instanceof Key) {
				if (((Key) item).levelKey.equals(levelKey)) {
					item.detachAll( backpack );
				}
			} else if (item.unique) {
				item.detachAll(backpack);
				//you keep the bag itself, not its contents.
				if (item instanceof Bag){
					((Bag)item).resurrect();
				}
				item.collect();
			} else if (!item.isEquipped( owner )) {
				item.detachAll( backpack );
			}
		}

		for (int i = 0; i < Constants.MISC_SLOTS; i++) {//Restore all miscs
			if (miscs[i] != null) {
				miscs[i].cursed = false;
				miscs[i].activate( owner );
			}
		}
	}
	
	public int charge( float charge ) {
		
		int count = 0;
		
		for (Wand.Charger charger : owner.buffs(Wand.Charger.class)){
			charger.gainCharge(charge);
			count++;
		}
		
		return count;
	}

	@Override
	public Iterator<Item> iterator() {
		return new ItemIterator();
	}
	
	private class ItemIterator implements Iterator<Item> {

		private int index = 0;
		
		private final Iterator<Item> backpackIterator = backpack.iterator();
		
		private final Item[] equipped = { weapons[0], weapons[1], armor, miscs[0], miscs[1],  miscs[2]};
		private final int backpackIndex = equipped.length;
		
		@Override
		public boolean hasNext() {
			
			for (int i=index; i < backpackIndex; i++) {
				if (equipped[i] != null) {
					return true;
				}
			}
			
			return backpackIterator.hasNext();
		}

		@Override
		public Item next() {
			
			while (index < backpackIndex) {
				Item item = equipped[index++];
				if (item != null) {
					return item;
				}
			}
			
			return backpackIterator.next();
		}

		@Override
		public void remove() {
			switch (index) {
				case 0:
					equipped[0] = weapons[0] = null;
					break;
				case 1:
					equipped[1] = weapons[1] = null;
					break;
				case 2:
					equipped[2] = armor = null;
					break;
				case 3:
					equipped[3] = miscs[0] = null;
					break;
				case 4:
					equipped[4] = miscs[1] = null;
					break;
				case 5:
					equipped[5] = miscs[2] = null;
					break;
				default:
					backpackIterator.remove();
			}
		}
	}
}
