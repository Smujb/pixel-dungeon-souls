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

package com.shatteredpixel.yasd.general.levels;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.mobs.Bat;
import com.shatteredpixel.yasd.general.actors.mobs.Brute;
import com.shatteredpixel.yasd.general.actors.mobs.DM200;
import com.shatteredpixel.yasd.general.actors.mobs.Shaman;
import com.shatteredpixel.yasd.general.actors.mobs.Spinner;
import com.shatteredpixel.yasd.general.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.yasd.general.levels.painters.CavesPainter;
import com.shatteredpixel.yasd.general.levels.painters.Painter;
import com.shatteredpixel.yasd.general.levels.rooms.Room;
import com.shatteredpixel.yasd.general.levels.terrain.Terrain;
import com.shatteredpixel.yasd.general.levels.traps.BurningTrap;
import com.shatteredpixel.yasd.general.levels.traps.ConfusionTrap;
import com.shatteredpixel.yasd.general.levels.traps.CorrosionTrap;
import com.shatteredpixel.yasd.general.levels.traps.FrostTrap;
import com.shatteredpixel.yasd.general.levels.traps.GrippingTrap;
import com.shatteredpixel.yasd.general.levels.traps.GuardianTrap;
import com.shatteredpixel.yasd.general.levels.traps.PitfallTrap;
import com.shatteredpixel.yasd.general.levels.traps.PoisonDartTrap;
import com.shatteredpixel.yasd.general.levels.traps.RockfallTrap;
import com.shatteredpixel.yasd.general.levels.traps.StormTrap;
import com.shatteredpixel.yasd.general.levels.traps.SummoningTrap;
import com.shatteredpixel.yasd.general.levels.traps.WarpingTrap;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.WALL_DECO;

public class CavesLevel extends RegularLevel {

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;

		viewDistance = Math.min(6, viewDistance);

		minScaleFactor = 13;
		maxScaleFactor = 18;
	}
	
	@Override
	protected ArrayList<Room> initRooms() {
		return Blacksmith.Quest.spawn(super.initRooms());
	}

	@Override
	protected int standardRooms() {
		//6 to 8, average 6.66
		return 23+Random.chances(new float[]{4, 2, 2})*2;
	}

	@Override
	protected int specialRooms() {
		//1 to 3, average 1.83
		return 4+Random.chances(new float[]{3, 4, 3})*2;
	}
	
	@Override
	protected Painter painter() {
		return new CavesPainter()
				.setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 6)
				.setGrass(feeling == Feeling.GRASS ? 0.65f : 0.15f, 3)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_CAVES;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.WATER_CAVES;
	}

	@Override
	public String loadImg() {
		return Assets.Interfaces.LOADING_CAVES;
	}

	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{
				BurningTrap.class, PoisonDartTrap.class, FrostTrap.class, StormTrap.class, CorrosionTrap.class,
				GrippingTrap.class, RockfallTrap.class,  GuardianTrap.class,
				ConfusionTrap.class, SummoningTrap.class, WarpingTrap.class, PitfallTrap.class };
	}

	@Override
	protected float[] trapChances() {
		return new float[]{
				4, 4, 4, 4, 4,
				2, 2, 2,
				1, 1, 1, 1};
	}

	@Override
	public Class<?>[] mobClasses() {
		return new Class[] {
				Bat.class,
				Brute.class,
				Spinner.class,
				Shaman.random(),
				DM200.class
		};
	}

	@Override
	public float[] mobChances() {
		return new float[] {
				5,
				3,
				2,
				4,
				3
		};
	}

	@Override
	protected float[] connectionRoomChances() {
		return new float[]{
				12,
				0,
				0,
				5,
				5,
				3,
				1};
	}

	@Override
	protected float[] standardRoomChances() {
		return new float[]{20,
				0,
				0,
				0,
				0,
				15,
				5,
				0,
				0,
				0,
				0,
				1,
				1,
				1,
				1,
				1,
				1,
				1,
				1,
				1,
				1};
	}

	@Override
	public String tileName( Terrain tile ) {
		switch (tile) {
			case GRASS:
				return Messages.get(CavesLevel.class, "grass_name");
			case HIGH_GRASS:
				return Messages.get(CavesLevel.class, "high_grass_name");
			case WATER:
				return Messages.get(CavesLevel.class, "water_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc( Terrain tile ) {
		switch (tile) {
			case ENTRANCE:
				return Messages.get(CavesLevel.class, "entrance_desc");
			case EXIT:
				return Messages.get(CavesLevel.class, "exit_desc");
			case HIGH_GRASS:
				return Messages.get(CavesLevel.class, "high_grass_desc");
			case WALL_DECO:
				return Messages.get(CavesLevel.class, "wall_deco_desc");
			case BOOKSHELF:
				return Messages.get(CavesLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals() {
		super.addVisuals();
		addCavesVisuals( this, visuals );
		return visuals;
	}
	
	public static void addCavesVisuals( Level level, Group group ) {
		for (int i=0; i < level.length(); i++) {
			if (level.getTerrain(i) == WALL_DECO) {
				group.add( new Vein( i ) );
			}
		}
	}
	
	private static class Vein extends Group {
		
		private int pos;
		
		private float delay;
		
		public Vein( int pos ) {
			super();
			
			this.pos = pos;
			
			delay = Random.Float( 2 );
		}
		
		@Override
		public void update() {
			
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				
				super.update();

				if ((delay -= Game.elapsed) <= 0) {

					//pickaxe can remove the ore, should remove the sparkling too.
					if (Dungeon.level.getTerrain(pos) != WALL_DECO){
						kill();
						return;
					}
					
					delay = Random.Float();
					
					PointF p = DungeonTilemap.tileToWorld( pos );
					((Sparkle)recycle( Sparkle.class )).reset(
						p.x + Random.Float( DungeonTilemap.SIZE ),
						p.y + Random.Float( DungeonTilemap.SIZE ) );
				}
			}
		}
	}
	
	public static final class Sparkle extends PixelParticle {
		
		public void reset( float x, float y ) {
			revive();
			
			this.x = x;
			this.y = y;
			
			left = lifespan = 0.5f;
		}
		
		@Override
		public void update() {
			super.update();
			
			float p = left / lifespan;
			size( (am = p < 0.5f ? p * 2 : (1 - p) * 2) * 2 );
		}
	}
}