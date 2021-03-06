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

package com.shatteredpixel.yasd.general;

import com.shatteredpixel.yasd.general.actors.Actor;
import com.shatteredpixel.yasd.general.actors.buffs.Buff;
import com.shatteredpixel.yasd.general.actors.mobs.Mob;
import com.shatteredpixel.yasd.general.actors.mobs.Phantom;
import com.shatteredpixel.yasd.general.levels.DeadEndLevel;
import com.shatteredpixel.yasd.general.levels.Level;
import com.shatteredpixel.yasd.general.levels.UnderwaterLevel;
import com.shatteredpixel.yasd.general.levels.features.Chasm;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.shatteredpixel.yasd.general.scenes.TextScene;
import com.shatteredpixel.yasd.general.ui.GameLog;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Point;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class LevelHandler {

	private static Point pos;
	private static boolean fallIntoPit;
	public static int depth;
	public static String key;
	private static Callback callback;


	public static String filename(String key, int slot) {
		return GamesInProgress.slotFolder(slot) + "/" + key + ".dat";
	}

	static Level getLevel(String key) {
		return getLevel(key, GamesInProgress.curSlot);
	}


	@Nullable
	static Level getLevel(String key, int save) {

		try {
			Bundle bundle = FileUtils.bundleFromFile(filename(key, save));
			return (Level) bundle.get(Dungeon.LEVEL);
		} catch (IOException e) {
			return null;
		}
	}

	public enum Mode {
		DESCEND, ASCEND, CONTINUE, RESURRECT, RETURN, FALL, RESET, MOVE, INIT, DIVE, LAST_BONFIRE
	}
	private static Mode mode;
	

	@Contract(" -> new")
	private static Thread getThread() {
		return new Thread() {
			@Override
			public void run() {

				try {

					if (Dungeon.hero != null){
						Dungeon.hero.spendToWhole();
					}
					Actor.fixTime();

					if (mode == Mode.CONTINUE) {
						restore();
					} else {
						switchDepth(depth, mode, key );
					}

				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	@Contract(pure = true)
	public static float getSpeed() {
		float scrollSpeed;
		switch (mode) {
			default:
				scrollSpeed = 0;
				break;
			case CONTINUE:
			case DESCEND:
				scrollSpeed = 5;
				break;
			case FALL:
				scrollSpeed = 50;
				break;
			case ASCEND:
				scrollSpeed = -5;
				break;
			case RETURN:
				scrollSpeed = depth > Dungeon.depth ? 15 : -15;
				break;
		}
		return scrollSpeed;
	}

	@Contract(pure = true)
	public static Mode mode() {
		return mode;
	}

	public static void descend() {
		GameScene.messageOnEnter = Messages.get(LevelHandler.class, "descend", Dungeon.depth + 1);
		move(Dungeon.keyForDepth(Dungeon.depth +1), Messages.get(Mode.class, Mode.DESCEND.name()), Mode.DESCEND, Dungeon.depth + 1, null);
	}

	public static void ascend() {
		GameScene.messageOnEnter = Messages.get(LevelHandler.class, "ascend", Dungeon.depth + 1);
		move(Dungeon.keyForDepth(Dungeon.depth -1), Messages.get(Mode.class, Mode.ASCEND.name()), Mode.ASCEND, Dungeon.depth - 1, null);
	}

	public static void fall(boolean fallIntoPit, boolean bossLevel) {
		LevelHandler.fallIntoPit = fallIntoPit;
		if (bossLevel) {//If falling from a boss level, the hero will fall back onto the same floor and it will reset.
			Dungeon.level.reset();
			move(Dungeon.keyForDepth(), Messages.get(Mode.class, Mode.FALL.name()), Mode.FALL, Dungeon.depth, null);
		} else {
			move(Dungeon.keyForDepth(Dungeon.depth +1), Messages.get(Mode.class, Mode.FALL.name()), Mode.FALL, Dungeon.depth + 1, null);
		}
	}

	public static void reset() {
		move(Dungeon.keyForDepth(), Messages.get(Mode.class, Mode.RESET.name()), Mode.RESET, Dungeon.depth, null);
	}

	public static void lastBonfire(Callback callback) {
		int depth = Statistics.lastBonfireDepth;
		if (depth == -1) depth = 1;
		move(Dungeon.keyForDepth(depth), Messages.get(Mode.class, Mode.RESURRECT.name()), Mode.LAST_BONFIRE, depth, null, callback);
	}

	public static void resurrect() {
		move(Dungeon.keyForDepth(), Messages.get(Mode.class, Mode.RESURRECT.name()), Mode.RESURRECT, Dungeon.depth, null);
	}

	public static void returnTo(int depth, int pos) {
		GameScene.messageOnEnter = Messages.get(LevelHandler.class, "return", depth);
		returnTo(depth, Dungeon.level.cellToPoint(pos));
	}

	public static void returnTo(int depth, Point pos) {
		move(Dungeon.keyForDepth(depth), Messages.get(Mode.class, Mode.RETURN.name()), Mode.RETURN, depth, pos);
	}

	public static void dive(int pos) {
		dive(Dungeon.level.cellToPoint(pos));
	}

	public static void dive(Point pos) {
		Dungeon.underwater = !Dungeon.underwater();
		if (Dungeon.underwater()) {
			GameScene.messageOnEnter = Messages.get(LevelHandler.class, "dive");
		} else {
			GameScene.messageOnEnter = Messages.get(LevelHandler.class, "resurface");
		}
		move(Dungeon.keyForDepth(), Messages.get(Mode.class, Mode.DIVE.name()), Mode.DIVE, Dungeon.depth, pos);
	}

	public static void doRestore() {
		mode = Mode.CONTINUE;
		TextScene.init(Messages.get(Mode.class, Mode.CONTINUE.name()), Messages.get(LevelHandler.class, "continue"), Dungeon.newLevel( Dungeon.keyForDepth(), false).loadImg(), getSpeed(), 0.67f, new Callback() {
			@Override
			public void call() {
				GameScene.messageOnEnter = Messages.get(LevelHandler.class, "return", depth);
				PDSGame.switchScene(GameScene.class);
			}
		}, getThread(), PPDSettings.fastInterlevelScene());
	}

	public static void doInit(long seed) {
		mode = Mode.DESCEND;
		depth = 1;
		pos = null;
		key = Dungeon.keyForDepth();
		GameScene.messageOnEnter = Messages.get(LevelHandler.class, "start", depth);
		TextScene.init(Messages.get(Mode.class, Mode.DESCEND.name()), Messages.get(LevelHandler.class, "continue"), Dungeon.newLevel( Dungeon.keyForDepth(), false).loadImg(), getSpeed(), 0.67f, new Callback() {
			@Override
			public void call() {
				PDSGame.switchScene(GameScene.class);
			}
		}, new Thread() {
			@Override
			public void run() {
				try {
					initGame(seed);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}, PPDSettings.fastInterlevelScene());
	}

	public static void move(String key, String msg, Mode mode, int depth, Point pos) {
		move(key, msg, mode, depth, pos, null);
	}

	public static void move(String key, String msg, Mode mode, int depth, Point pos, Callback callback) {
		LevelHandler.depth = depth;
		LevelHandler.mode = mode;
		LevelHandler.key = key;
		LevelHandler.pos = pos;
		LevelHandler.callback = callback;
		TextScene.init(msg, Messages.get(LevelHandler.class, "continue"), Dungeon.newLevel( Dungeon.keyForDepth(), false).loadImg(), getSpeed(), 0.67f, new Callback() {
			@Override
			public void call() {
				PDSGame.switchScene(GameScene.class);
			}
		}, getThread(), PPDSettings.fastInterlevelScene());
	}

	public static void resetMode() {
		mode = Mode.MOVE;
	}

	private static void initGame(long seed) throws IOException {
		Mob.clearHeldAllies();
		Dungeon.level = null;
		Dungeon.hero = null;
		Dungeon.init(seed);
		GameLog.wipe();
		LevelHandler.key = Dungeon.keyForDepth();
		switchDepth(1, Mode.DESCEND, Dungeon.keyForDepth());
	}

	private static void switchDepth(int depth, final Mode mode, String key) throws IOException {
		if (Dungeon.hero != null && Dungeon.level != null) {
			Mob.holdMobs( Dungeon.level );
			Dungeon.saveAll();
		}
		Dungeon.depth = depth;
		if (mode.equals(Mode.RESURRECT) & Dungeon.hero != null & Dungeon.level != null) {
			if (Dungeon.level.locked) {
				Dungeon.hero.resurrect( Dungeon.key);
			} else {
				Dungeon.hero.resurrect( null );
				Dungeon.resetLevel();
				return;
			}
		}


		Level level = null;
		Dungeon.key = key;
		if (mode != Mode.RESET) {
			level = getLevel(key);
		}

		Actor.clear();
		if (level == null) {

			if (depth > Statistics.deepestFloor) {
				Statistics.deepestFloor = depth;

				Statistics.completedWithNoKilling = Statistics.qualifiedForNoKilling;
			}

			level = Dungeon.newLevel( LevelHandler.key, true );

			Statistics.qualifiedForNoKilling = !Dungeon.bossLevel();
			if (level instanceof DeadEndLevel) {
				Statistics.deepestFloor--;
			}

		}
		Point pos = LevelHandler.pos;
		if (pos == null) {
			switch (mode) {
				case ASCEND:
					pos = level.cellToPoint(level.getExitPos());
					break;
				case FALL:
					pos = level.cellToPoint(level.fallCell(fallIntoPit));
					break;
				case LAST_BONFIRE:
					int cell = level.heroSpawnCell();
					if (cell == -1) {
						cell = level.getEntrancePos();
					}
					pos = level.cellToPoint(cell);
					break;
				default:
					pos = level.cellToPoint(level.getEntrancePos());
					break;
			}
		}
		switch (mode) {
			case FALL:
				Buff.affect(Dungeon.hero, Chasm.Falling.class);
				break;
			case DIVE:
				if (Dungeon.underwater()) {
					pos.scale(UnderwaterLevel.SIZE_FACTOR);
				} else {
					pos.scale(1 / UnderwaterLevel.SIZE_FACTOR);
				}
				break;
			case LAST_BONFIRE:
				level.reset();
				level.mobs.add(Phantom.createFromHero(Dungeon.hero, level));
				break;
		}
		if (callback == null) {
			Dungeon.switchLevel(level, pos);
		} else {
			callback.call();
			callback = null;
		}
	}

	private static void restore() throws IOException {

		Mob.clearHeldAllies();

		GameLog.wipe();
		Level level;
		Dungeon.loadGame( GamesInProgress.curSlot );
		Dungeon.level = null;
		Actor.clear();
		if (Dungeon.key == null) {
			Dungeon.depth = Statistics.deepestFloor;
			Dungeon.switchLevel( getLevel(Dungeon.key = Dungeon.keyForDepth()), null );
		} else {
			level = getLevel( Dungeon.key, GamesInProgress.curSlot );
			if (level == null) {
				throw new RuntimeException("Tried to load a level that doesn't exist or is corrupt");
			}
			Dungeon.switchLevel( level, level.cellToPoint(Dungeon.hero.pos) );
		}
	}
}
