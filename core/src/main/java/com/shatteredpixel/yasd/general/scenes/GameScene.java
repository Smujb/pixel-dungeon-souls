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

package com.shatteredpixel.yasd.general.scenes;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Badges;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.LevelHandler;
import com.shatteredpixel.yasd.general.Lore;
import com.shatteredpixel.yasd.general.PDSGame;
import com.shatteredpixel.yasd.general.Statistics;
import com.shatteredpixel.yasd.general.PPDSettings;
import com.shatteredpixel.yasd.general.actors.Actor;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.blobs.Blob;
import com.shatteredpixel.yasd.general.actors.mobs.DemonSpawner;
import com.shatteredpixel.yasd.general.actors.mobs.Mob;
import com.shatteredpixel.yasd.general.effects.BannerSprites;
import com.shatteredpixel.yasd.general.effects.BlobEmitter;
import com.shatteredpixel.yasd.general.effects.CircleArc;
import com.shatteredpixel.yasd.general.effects.EmoIcon;
import com.shatteredpixel.yasd.general.effects.Flare;
import com.shatteredpixel.yasd.general.effects.FloatingText;
import com.shatteredpixel.yasd.general.effects.Ripple;
import com.shatteredpixel.yasd.general.effects.RippleShock;
import com.shatteredpixel.yasd.general.effects.SpellSprite;
import com.shatteredpixel.yasd.general.items.Heap;
import com.shatteredpixel.yasd.general.items.Honeypot;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.items.artifacts.DriedRose;
import com.shatteredpixel.yasd.general.items.bags.MagicalHolster;
import com.shatteredpixel.yasd.general.items.bags.PotionBandolier;
import com.shatteredpixel.yasd.general.items.bags.ScrollHolder;
import com.shatteredpixel.yasd.general.items.bags.VelvetPouch;
import com.shatteredpixel.yasd.general.items.potions.Potion;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.yasd.general.journal.Journal;
import com.shatteredpixel.yasd.general.levels.RegularLevel;
import com.shatteredpixel.yasd.general.levels.terrain.KindOfTerrain;
import com.shatteredpixel.yasd.general.levels.traps.Trap;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.plants.Plant;
import com.shatteredpixel.yasd.general.sprites.CharSprite;
import com.shatteredpixel.yasd.general.sprites.DiscardedItemSprite;
import com.shatteredpixel.yasd.general.sprites.HeroSprite;
import com.shatteredpixel.yasd.general.sprites.ItemSprite;
import com.shatteredpixel.yasd.general.tiles.CustomTilemap;
import com.shatteredpixel.yasd.general.tiles.DungeonTerrainTilemap;
import com.shatteredpixel.yasd.general.tiles.DungeonTileSheet;
import com.shatteredpixel.yasd.general.tiles.DungeonTilemap;
import com.shatteredpixel.yasd.general.tiles.DungeonWallsTilemap;
import com.shatteredpixel.yasd.general.tiles.FogOfWar;
import com.shatteredpixel.yasd.general.tiles.GridTileMap;
import com.shatteredpixel.yasd.general.tiles.RaisedTerrainTilemap;
import com.shatteredpixel.yasd.general.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.yasd.general.tiles.WallBlockingTilemap;
import com.shatteredpixel.yasd.general.ui.ActionIndicator;
import com.shatteredpixel.yasd.general.ui.Banner;
import com.shatteredpixel.yasd.general.ui.BusyIndicator;
import com.shatteredpixel.yasd.general.ui.CharHealthIndicator;
import com.shatteredpixel.yasd.general.ui.DiveIndicator;
import com.shatteredpixel.yasd.general.ui.GameLog;
import com.shatteredpixel.yasd.general.ui.LootIndicator;
import com.shatteredpixel.yasd.general.ui.QuickSlotButton;
import com.shatteredpixel.yasd.general.ui.ResumeIndicator;
import com.shatteredpixel.yasd.general.ui.StatusPane;
import com.shatteredpixel.yasd.general.ui.TargetHealthIndicator;
import com.shatteredpixel.yasd.general.ui.Toast;
import com.shatteredpixel.yasd.general.ui.Toolbar;
import com.shatteredpixel.yasd.general.ui.Window;
import com.shatteredpixel.yasd.general.ui.attack.ActionIcon;
import com.shatteredpixel.yasd.general.ui.attack.ActionFrame;
import com.shatteredpixel.yasd.general.ui.attack.AttackIcon;
import com.shatteredpixel.yasd.general.ui.attack.RollIcon;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.shatteredpixel.yasd.general.windows.WndBag;
import com.shatteredpixel.yasd.general.windows.WndBag.Mode;
import com.shatteredpixel.yasd.general.windows.WndGame;
import com.shatteredpixel.yasd.general.windows.WndHero;
import com.shatteredpixel.yasd.general.windows.WndInfoCell;
import com.shatteredpixel.yasd.general.windows.WndInfoItem;
import com.shatteredpixel.yasd.general.windows.WndInfoMob;
import com.shatteredpixel.yasd.general.windows.WndInfoPlant;
import com.shatteredpixel.yasd.general.windows.WndInfoTrap;
import com.shatteredpixel.yasd.general.windows.WndMessage;
import com.shatteredpixel.yasd.general.windows.WndOptions;
import com.shatteredpixel.yasd.general.windows.WndRetry;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class GameScene extends PixelScene {

	static GameScene scene;

	private SkinnedBlock water;
	private DungeonTerrainTilemap tiles;
	private GridTileMap visualGrid;
	private TerrainFeaturesTilemap terrainFeatures;
	private RaisedTerrainTilemap raisedTerrain;
	private DungeonWallsTilemap walls;
	private WallBlockingTilemap wallBlocking;
	private FogOfWar fog;
	private HeroSprite hero;

	private StatusPane pane;
	
	private GameLog log;
	
	private BusyIndicator busy;
	private CircleArc counter;
	
	private static CellSelector cellSelector;
	
	private Group terrain;
	private Group customTiles;
	private Group levelVisuals;
	private Group customWalls;
	private Group ripples;
	private Group heaps;
	private Group mobs;
	private Group floorEmitters;
	private Group emitters;
	private Group effects;
	private Group gases;
	private Group spells;
	private Group statuses;
	private Group emoicons;
	private Group overFogEffects;
	private Group healthIndicators;
	
	private Toolbar toolbar;
	private Toast prompt;

	private LootIndicator loot;
	private ActionIndicator action;
	private ResumeIndicator resume;
	private DiveIndicator dive;

	public static String messageOnEnter = null;

	@Override
	public void create() {
		
		if (Dungeon.hero == null){
			PDSGame.switchNoFade(TitleScene.class);
			return;
		}

		Music.INSTANCE.play( Dungeon.level.music(), true );

		PPDSettings.lastClass(Dungeon.hero.heroClass.ordinal());
		
		super.create();
		Camera.main.zoom( GameMath.gate(minZoom, defaultZoom + PPDSettings.zoom(), maxZoom));

		scene = this;

		terrain = new Group();
		add( terrain );
		String waterTex = Dungeon.level.waterTex();
		water = new SkinnedBlock(
			Dungeon.level.width() * DungeonTilemap.SIZE,
			Dungeon.level.height() * DungeonTilemap.SIZE,
			waterTex ) {
		};
		terrain.add( water );


		ripples = new Group();
		terrain.add( ripples );

		DungeonTileSheet.setupVariance(Dungeon.level.getMap().length, Dungeon.seedCurDepth());
		
		tiles = new DungeonTerrainTilemap();
		terrain.add( tiles );

		customTiles = new Group();
		terrain.add(customTiles);

		for( CustomTilemap visual : Dungeon.level.customTiles){
			addCustomTile(visual);
		}

		visualGrid = new GridTileMap();
		terrain.add( visualGrid );

		terrainFeatures = new TerrainFeaturesTilemap(Dungeon.level.plants, Dungeon.level.getTraps());
		terrain.add(terrainFeatures);
		
		levelVisuals = Dungeon.level.addVisuals();
		add(levelVisuals);

		floorEmitters = new Group();
		add(floorEmitters);
		
		heaps = new Group();
		add( heaps );
		
		for ( Heap heap : Dungeon.level.heaps.valueList() ) {
			addHeapSprite( heap );
		}
		
		emitters = new Group();
		effects = new Group();
		healthIndicators = new Group();
		emoicons = new Group();
		overFogEffects = new Group();

		mobs = new Group();
		add( mobs );

		hero = new HeroSprite();
		hero.place( Dungeon.hero.pos );
		hero.updateArmor();
		mobs.add( hero );
		
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			addMobSprite( mob );
			if (Statistics.amuletObtained) {
				mob.beckon( Dungeon.hero.pos );
			}
		}
		
		raisedTerrain = new RaisedTerrainTilemap();
		add( raisedTerrain );

		walls = new DungeonWallsTilemap();
		add(walls);

		customWalls = new Group();
		add(customWalls);

		for( CustomTilemap visual : Dungeon.level.customWalls){
			addCustomWall(visual);
		}

		wallBlocking = new WallBlockingTilemap();
		add (wallBlocking);

		add( emitters );
		add( effects );

		gases = new Group();
		add( gases );

		for (Blob blob : Dungeon.level.blobs.values()) {
			blob.emitter = null;
			addBlobSprite( blob );
		}

		if (Dungeon.underwater()) {
			water.alpha(0.5f);
			addToFront(water);
		}

		fog = new FogOfWar( Dungeon.level.width(), Dungeon.level.height() );
		add( fog );

		spells = new Group();
		add( spells );

		add(overFogEffects);
		
		statuses = new Group();
		add( statuses );
		
		add( healthIndicators );
		//always appears ontop of other health indicators
		add( new TargetHealthIndicator() );
		
		add( emoicons );
		
		add( cellSelector = new CellSelector( tiles ) );

		pane = new StatusPane();
		pane.camera = uiCamera;
		pane.setSize( uiCamera.width, 0 );
		add( pane );
		
		toolbar = new Toolbar();
		toolbar.camera = uiCamera;
		toolbar.setRect( 0,uiCamera.height - toolbar.height(), uiCamera.width, toolbar.height() );
		add( toolbar );

		loot = new LootIndicator();
		loot.camera = uiCamera;
		add( loot );

		action = new ActionIndicator();
		action.camera = uiCamera;
		add( action );

		resume = new ResumeIndicator();
		resume.camera = uiCamera;
		add( resume );

		dive = new DiveIndicator();
		dive.camera = uiCamera;
		add( dive );

		float top = uiCamera.height/3f;

		ActionIcon[] leftIcons = new ActionIcon[2];
		leftIcons[0] = new AttackIcon(0, true);
		leftIcons[1] = new AttackIcon(0, false);
		ActionFrame left = new ActionFrame(0, top, leftIcons);
		left.camera = uiCamera;
		add(left);

		ActionIcon[] rightIcons = new ActionIcon[3];
		rightIcons[0] = new AttackIcon(1, true);
		rightIcons[1] = new AttackIcon(1, false);
		rightIcons[2] = new RollIcon();
		ActionFrame right = new ActionFrame(uiCamera.width-24, top, rightIcons);
		right.camera = uiCamera;
		add(right);

		log = new GameLog();
		log.camera = uiCamera;
		log.newLine();
		add( log );

		layoutTags();

		busy = new BusyIndicator();
		busy.camera = uiCamera;
		busy.x = 1;
		busy.y = pane.bottom() + 1;
		add( busy );
		
		counter = new CircleArc(18, 4.25f);
		counter.color( 0x808080, true );
		counter.camera = uiCamera;
		counter.show(this, busy.center(), 0f);
		
		switch (LevelHandler.mode()) {
			case RESURRECT:
				ScrollOfTeleportation.appear(Dungeon.hero, Dungeon.level.getEntrancePos());
				new Flare(8, 32).color(0xFFFF66, true).show(hero, 2f);
				break;
			case RETURN:
				ScrollOfTeleportation.appear(Dungeon.hero, Dungeon.hero.pos);
				break;
			case DESCEND:
				if (Dungeon.bossLevel(Dungeon.depth -1) && PPDSettings.cutscenes()) {
					Lore.showChapter(Dungeon.level);
				}
				/*
				if (Dungeon.hero.isAlive() && Dungeon.depth != 22) {
					Badges.validateNoKilling();
				}*/
				break;
			default:
		}

		ArrayList<Item> dropped = Dungeon.droppedItems.get( Dungeon.depth);
		if (dropped != null) {
			for (Item item : dropped) {
				int pos = Dungeon.level.randomRespawnCell();
				if (item instanceof Potion) {
					((Potion)item).shatter( pos );
				} else if (item instanceof Plant.Seed) {
					Dungeon.level.plant( (Plant.Seed)item, pos );
				} else if (item instanceof Honeypot) {
					Dungeon.level.drop(((Honeypot) item).shatter(null, pos), pos);
				} else {
					Dungeon.level.drop( item, pos );
				}
			}
			Dungeon.droppedItems.remove( Dungeon.depth);
		}
		
		ArrayList<Item> ported = Dungeon.portedItems.get( Dungeon.depth);
		if (ported != null){
			//TODO currently items are only ported to boss rooms, so this works well
			//might want to have a 'near entrance' function if items can be ported elsewhere
			int pos;
			//try to find a tile with no heap, otherwise just stick items onto a heap.
			int tries = 100;
			do {
				pos = Dungeon.level.randomRespawnCell();
				tries--;
			} while (tries > 0 && Dungeon.level.heaps.get(pos) != null);
			for (Item item : ported) {
				Dungeon.level.drop( item, pos ).type = Heap.Type.CHEST;
			}
			Dungeon.level.heaps.get(pos).type = Heap.Type.CHEST;
			Dungeon.level.heaps.get(pos).sprite.link(); //sprite reset to show chest
			Dungeon.portedItems.remove( Dungeon.depth);
		}

		Dungeon.hero.next();

		switch (LevelHandler.mode()){
			case FALL: case DESCEND: case CONTINUE:
				Camera.main.snapTo(hero.center().x, hero.center().y - DungeonTilemap.SIZE * (defaultZoom/Camera.main.zoom));
				break;
			case ASCEND:
				Camera.main.snapTo(hero.center().x, hero.center().y + DungeonTilemap.SIZE * (defaultZoom/Camera.main.zoom));
				break;
			default:
				Camera.main.snapTo(hero.center().x, hero.center().y);
		}
		Camera.main.panTo(hero.center(), 2.5f);

		if (LevelHandler.mode() != LevelHandler.Mode.MOVE) {
			if (Dungeon.depth == Statistics.deepestFloor
					&& (LevelHandler.mode() == LevelHandler.Mode.DESCEND || LevelHandler.mode() == LevelHandler.Mode.FALL)) {
				Sample.INSTANCE.play(Assets.Sounds.DESCEND);
				
				for (Char ch : Actor.chars()){
					if (ch instanceof DriedRose.GhostHero){
						((DriedRose.GhostHero) ch).sayAppeared();
					}
				}

				int spawnersAbove = Statistics.spawnersAlive;
				if (spawnersAbove > 0 && Dungeon.depth <= 25) {
					for (Mob m : Dungeon.level.mobs) {
						if (m instanceof DemonSpawner && ((DemonSpawner) m).spawnRecorded) {
							spawnersAbove--;
						}
					}

					if (spawnersAbove > 0) {
						if (Dungeon.bossLevel()) {
							GLog.n(Messages.get(this, "spawner_warn_final"));
						} else {
							GLog.n(Messages.get(this, "spawner_warn"));
						}
					}
				}
				
			}

			switch (Dungeon.level.feeling) {
				case CHASM:
					GLog.w(Messages.get(this, "chasm"));
					break;
				case WATER:
					GLog.w(Messages.get(this, "water"));
					break;
				case GRASS:
					GLog.w(Messages.get(this, "grass"));
					break;
				case DARK:
					GLog.w(Messages.get(this, "dark"));
					break;
				case EVIL:
					GLog.w(Messages.get(this, "evil"));
					break;
				case DANGER:
					GLog.w(Messages.get(this, "danger"));
					break;
				case EMBER:
					GLog.w(Messages.get(this, "ember"));
					break;
				case OPEN:
					GLog.w(Messages.get(this, "open"));
					break;
				default:
			}
			if (Dungeon.level instanceof RegularLevel &&
					((RegularLevel) Dungeon.level).secretDoors > Random.IntRange(3, 4)) {
				GLog.w(Messages.get(this, "secrets"));
			}

			LevelHandler.resetMode();

			
		}

		if (messageOnEnter != null) {
			GLog.h(messageOnEnter);
			messageOnEnter = null;
		}
		
		fadeIn();

		if (!Dungeon.hero.isAlive()) {
			addToFront(new WndRetry());
		}

		layoutTags();
	}

	public static void checkKeyHold(){
		cellSelector.processKeyHold();
	}

	public static void resetKeyHold(){
		cellSelector.resetKeyHold();
	}
	
	public void destroy() {
		
		//tell the actor thread to finish, then wait for it to complete any actions it may be doing.
		if (actorThread != null && actorThread.isAlive()){
			synchronized (GameScene.class){
				synchronized (actorThread) {
					actorThread.interrupt();
				}
				try {
					GameScene.class.wait(5000);
				} catch (InterruptedException e) {
					PDSGame.reportException(e);
				}
				synchronized (actorThread) {
					if (Actor.processing()) {
						Throwable t = new Throwable();
						t.setStackTrace(actorThread.getStackTrace());
						throw new RuntimeException("timeout waiting for actor thread! ", t);
					}
				}
			}
		}
		
		freezeEmitters = false;
		
		scene = null;
		Badges.saveGlobal();
		Journal.saveGlobal();
		
		super.destroy();
	}

	public static void endActorThread(){
		if (actorThread != null && actorThread.isAlive()){
			Actor.keepActorThreadAlive = false;
			actorThread.interrupt();
		}
	}
	
	@Override
	public synchronized void onPause() {
		try {
			Dungeon.saveAll();
			Badges.saveGlobal();
			Journal.saveGlobal();
		} catch (IOException e) {
			PDSGame.reportException(e);
		}
	}

	private static Thread actorThread;

	//sometimes UI changes can be prompted by the actor thread.
	// We queue any removed element destruction, rather than destroying them in the actor thread.
	private ArrayList<Gizmo> toDestroy = new ArrayList<>();

	@Override
	public synchronized void update() {
		if (Dungeon.hero == null || scene == null) {
			return;
		}

		super.update();
		
		if (!freezeEmitters) water.offset( 0, -5 * Game.elapsed );

		if (!Actor.processing() && Dungeon.hero.isAlive()) {
			if (actorThread == null || !actorThread.isAlive()) {

				actorThread = new Thread() {
					@Override
					public void run() {
						Actor.process();
					}
				};
				//if cpu cores are limited, game should prefer drawing the current frame
				if (Runtime.getRuntime().availableProcessors() == 1) {
					actorThread.setPriority(Thread.NORM_PRIORITY - 1);
				}
				actorThread.setName("SHPD Actor Thread");
				Thread.currentThread().setName("SHPD Render Thread");
				Actor.keepActorThreadAlive = true;
				actorThread.start();
			} else {
				synchronized (actorThread) {
					actorThread.notify();
				}
			}
		}

		counter.setSweep((1f - Actor.now()%1f)%1f);
		
		if (Dungeon.hero.ready && Dungeon.hero.paralysed == 0) {
			log.newLine();
		}

		if ( tagLoot != loot.visible ||
				tagAction != action.visible ||
				tagDive != dive.visible ||
				tagResume != resume.visible) {

			//we only want to change the layout when new tags pop in, not when existing ones leave.
			boolean tagAppearing = 	(loot.visible && !tagLoot) ||
									(action.visible && !tagAction) ||
									(dive.visible && !tagDive) ||
									(resume.visible && !tagResume);

			tagLoot = loot.visible;
			tagAction = action.visible;
			tagResume = resume.visible;
			tagDive = dive.visible;

			if (tagAppearing) layoutTags();
		}

		cellSelector.enable(Dungeon.hero.ready);

		for (Gizmo g : toDestroy){
			g.destroy();
		}
		toDestroy.clear();
	}

	private boolean tagLoot      = false;
	private boolean tagAction    = false;
	private boolean tagResume    = false;
	private boolean tagDive      = false;

	public static void layoutTags() {

		if (scene == null) return;

		float tagLeft = PPDSettings.flipTags() ? 0 : uiCamera.width - scene.loot.width();

		if (PPDSettings.flipTags()) {
			scene.log.setRect(scene.loot.width(), scene.toolbar.top()-2, uiCamera.width - scene.loot.width(), 0);
		} else {
			scene.log.setRect(0, scene.toolbar.top()-2, uiCamera.width - scene.loot.width(),  0 );
		}

		float pos = scene.toolbar.top();

		if (scene.tagDive){
			scene.dive.setPos( tagLeft, pos - scene.dive.height());
			scene.dive.flip(tagLeft == 0);
			pos = scene.dive.top();
		}

		if (scene.tagLoot) {
			scene.loot.setPos( tagLeft, pos - scene.loot.height() );
			scene.loot.flip(tagLeft == 0);
			pos = scene.loot.top();
		}

		if (scene.tagAction) {
			scene.action.setPos( tagLeft, pos - scene.action.height() );
			scene.action.flip(tagLeft == 0);
			pos = scene.action.top();
		}

		if (scene.tagResume) {
			scene.resume.setPos( tagLeft, pos - scene.resume.height() );
			scene.resume.flip(tagLeft == 0);
			pos = scene.resume.top();
		}
	}
	
	@Override
	protected void onBackPressed() {
		if (!cancel()) {
			add( new WndGame() );
		}
	}

	public void addCustomTile( CustomTilemap visual){
		customTiles.add( visual.create() );
	}

	public void addCustomWall( CustomTilemap visual){
		customWalls.add( visual.create() );
	}
	
	private void addHeapSprite( Heap heap ) {
		ItemSprite sprite = heap.sprite = (ItemSprite)heaps.recycle( ItemSprite.class );
		sprite.revive();
		sprite.link( heap );
		heaps.add( sprite );
	}
	
	private void addDiscardedSprite( Heap heap ) {
		heap.sprite = (DiscardedItemSprite)heaps.recycle( DiscardedItemSprite.class );
		heap.sprite.revive();
		heap.sprite.link( heap );
		heaps.add( heap.sprite );
	}
	
	private void addPlantSprite( Plant plant ) {

	}

	private void addTrapSprite( Trap trap ) {

	}
	
	private void addBlobSprite( final Blob gas ) {
		if (gas.emitter == null) {
			gases.add( new BlobEmitter( gas ) );
		}
	}
	
	private void addMobSprite( Mob mob ) {
		CharSprite sprite = mob.sprite();
		sprite.visible = Dungeon.level.heroFOV[mob.pos];
		mobs.add( sprite );
		sprite.link( mob );
	}
	
	private synchronized void prompt( String text ) {
		
		if (prompt != null) {
			prompt.killAndErase();
			toDestroy.add( prompt );
			prompt = null;
		}
		
		if (text != null) {
			prompt = new Toast( text ) {
				@Override
				protected void onClose() {
					cancel();
				}
			};
			prompt.camera = uiCamera;
			prompt.setPos( (uiCamera.width - prompt.width()) / 2, uiCamera.height - 60 );
			add( prompt );
		}
	}
	
	private void showBanner( Banner banner ) {
		banner.camera = uiCamera;
		banner.x = align( uiCamera, (uiCamera.width - banner.width) / 2 );
		banner.y = align( uiCamera, (uiCamera.height - banner.height) / 3 );
		addToFront( banner );
	}
	
	// -------------------------------------------------------

	public static void add( Plant plant ) {
		if (scene != null) {
			scene.addPlantSprite( plant );
		}
	}

	public static void add( Trap trap ) {
		if (scene != null) {
			scene.addTrapSprite( trap );
		}
	}
	
	public static void add( Blob gas ) {
		Actor.add( gas );
		if (scene != null) {
			scene.addBlobSprite( gas );
		}
	}
	
	public static void add( Heap heap ) {
		if (scene != null) {
			scene.addHeapSprite( heap );
		}
	}
	
	public static void discard( Heap heap ) {
		if (scene != null) {
			scene.addDiscardedSprite( heap );
		}
	}
	
	public static void add( Mob mob ) {
		Dungeon.level.mobs.add( mob );
		Actor.add( mob );
		scene.addMobSprite( mob );
	}

	public static void addSprite( Mob mob ) {
		scene.addMobSprite( mob );
	}
	
	public static void add( Mob mob, float delay ) {
		Dungeon.level.mobs.add( mob );
		Actor.addDelayed( mob, delay );
		scene.addMobSprite( mob );
	}
	
	public static void add( EmoIcon icon ) {
		scene.emoicons.add( icon );
	}
	
	public static void add( CharHealthIndicator indicator ){
		if (scene != null) scene.healthIndicators.add(indicator);
	}
	
	public static void add( CustomTilemap t, boolean wall ){
		if (scene == null) return;
		if (wall){
			scene.addCustomWall(t);
		} else {
			scene.addCustomTile(t);
		}
	}
	
	public static void effect( Visual effect ) {
		scene.effects.add( effect );
	}

	public static void effectOverFog( Visual effect ) {
		scene.overFogEffects.add( effect );
	}
	
	public static Ripple ripple( int pos ) {
		if (scene != null) {
			Ripple ripple = (Ripple) scene.ripples.recycle(Ripple.class);
			ripple.reset(pos);
			return ripple;
		} else {
			return null;
		}
	}
	
	public static SpellSprite spellSprite() {
		return (SpellSprite)scene.spells.recycle( SpellSprite.class );
	}

	public static RippleShock electrify( int pos ) {
		RippleShock ripple = (RippleShock)scene.ripples.recycle( RippleShock.class );
		ripple.reset(pos);
		return ripple;
	}
	
	public static Emitter emitter() {
		if (scene != null) {
			Emitter emitter = (Emitter)scene.emitters.recycle( Emitter.class );
			emitter.revive();
			return emitter;
		} else {
			return null;
		}
	}

	public static Emitter floorEmitter() {
		if (scene != null) {
			Emitter emitter = (Emitter)scene.floorEmitters.recycle( Emitter.class );
			emitter.revive();
			return emitter;
		} else {
			return null;
		}
	}
	
	public static FloatingText status() {
		return scene != null ? (FloatingText)scene.statuses.recycle( FloatingText.class ) : null;
	}
	
	public static void pickUp( Item item, int pos ) {
		if (scene != null) scene.toolbar.pickup( item, pos );
	}

	public static void pickUpJournal( Item item, int pos ) {
		if (scene != null) scene.pane.pickup( item, pos );
	}
	
	public static void flashJournal(){
		if (scene != null) scene.pane.flash();
	}
	
	public static void updateKeyDisplay(){
		if (scene != null) scene.pane.updateKeys();
	}

	public static void resetMap() {
		if (scene != null) {
			scene.tiles.map(Dungeon.level.getMap(), Dungeon.level.width() );
			scene.visualGrid.map(Dungeon.level.getMap(), Dungeon.level.width() );
			scene.terrainFeatures.map(Dungeon.level.getMap(), Dungeon.level.width() );
			scene.raisedTerrain.map(Dungeon.level.getMap(), Dungeon.level.width() );
			scene.walls.map(Dungeon.level.getMap(), Dungeon.level.width() );
		}
		updateFog();
	}

	//updates the whole map
	public static void updateMap() {
		if (scene != null) {
			scene.tiles.updateMap();
			scene.visualGrid.updateMap();
			scene.terrainFeatures.updateMap();
			scene.raisedTerrain.updateMap();
			scene.walls.updateMap();
			updateFog();
		}
	}
	
	public static void updateMap( int cell ) {
		if (scene != null) {
			scene.tiles.updateMapCell( cell );
			scene.visualGrid.updateMapCell( cell );
			scene.terrainFeatures.updateMapCell( cell );
			scene.raisedTerrain.updateMapCell( cell );
			scene.walls.updateMapCell( cell );
			//update adjacent cells too
			updateFog( cell, 1 );
		}
	}

	public static void plantSeed( int cell ) {
		if (scene != null) {
			scene.terrainFeatures.growPlant( cell );
		}
	}
	
	//todo this doesn't account for walls right now
	public static void discoverTile( int pos, KindOfTerrain oldValue ) {
		if (scene != null) {
			scene.tiles.discover( pos, oldValue );
		}
	}
	
	public static void show( Window wnd ) {
		if (scene != null) {
			cancelCellSelector();
			scene.addToFront(wnd);
		}
	}

	public static void updateFog(){
		if (scene != null) {
			scene.fog.updateFog();
			scene.wallBlocking.updateMap();
		}
	}

	public static void updateFog(int x, int y, int w, int h){
		if (scene != null) {
			scene.fog.updateFogArea(x, y, w, h);
			scene.wallBlocking.updateArea(x, y, w, h);
		}
	}
	
	public static void updateFog( int cell, int radius ){
		if (scene != null) {
			scene.fog.updateFog( cell, radius );
			scene.wallBlocking.updateArea( cell, radius );
		}
	}
	
	public static void afterObserve() {
		if (scene != null) {
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob.sprite != null)
					mob.sprite.visible = Dungeon.level.heroFOV[mob.pos];
			}
		}
	}
	
	public static void flash( int color ) {
		flash( color, true, PixelScene.DEFAULT_FADE_TIME);
	}

	public static void flash( int color, boolean lightmode, float time ) {
		scene.fadeIn( 0xFF000000 | color, lightmode, time );
	}
	
	public static void gameOver() {
		Banner gameOver = new Banner( BannerSprites.get( BannerSprites.Type.GAME_OVER ) );
		gameOver.show( 0x000000, 1f );
		scene.showBanner( gameOver );
		
		Sample.INSTANCE.play( Assets.Sounds.DEATH );
	}
	
	public static void bossSlain() {
		if (Dungeon.hero.isAlive()) {
			Banner bossSlain = new Banner( BannerSprites.get( BannerSprites.Type.BOSS_SLAIN ) );
			bossSlain.show( 0xFFFFFF, 0.3f, 5f );
			scene.showBanner( bossSlain );
			
			Sample.INSTANCE.play( Assets.Sounds.BOSS );
		}
	}
	
	public static void handleCell( int cell ) {
		cellSelector.select( cell );
	}
	
	public static void selectCell( CellSelector.Listener listener ) {
		if (cellSelector.listener != null && cellSelector.listener != defaultCellListener){
			cellSelector.listener.onSelect(null);
		}
		cellSelector.listener = listener;
		if (scene != null)
			scene.prompt( listener.prompt() );
	}
	
	private static boolean cancelCellSelector() {
		cellSelector.resetKeyHold();
		if (cellSelector.listener != null && cellSelector.listener != defaultCellListener) {
			cellSelector.cancel();
			return true;
		} else {
			return false;
		}
	}
	
	public static WndBag selectItem( WndBag.Listener listener, WndBag.Mode mode, String title ) {
		cancelCellSelector();
		
		WndBag wnd =
				mode == Mode.SANDALS ?
					WndBag.getBag( VelvetPouch.class, listener, mode, title ) :
				mode == Mode.SCROLL ?
					WndBag.getBag( ScrollHolder.class, listener, mode, title ) :
				mode == Mode.POTION ?
					WndBag.getBag( PotionBandolier.class, listener, mode, title ) :
				mode == Mode.WAND ?
					WndBag.getBag( MagicalHolster.class, listener, mode, title ) :
				WndBag.lastBag( listener, mode, title );
		
		if (scene != null) scene.addToFront( wnd );
		
		return wnd;
	}
	
	static boolean cancel() {
		if (Dungeon.hero != null && (Dungeon.hero.curAction != null || Dungeon.hero.resting)) {
			
			Dungeon.hero.curAction = null;
			Dungeon.hero.resting = false;
			return true;
			
		} else {
			
			return cancelCellSelector();
			
		}
	}
	
	public static void ready() {
		selectCell( defaultCellListener );
		QuickSlotButton.cancel();
		if (scene != null && scene.toolbar != null) scene.toolbar.examining = false;
	}

	public static void examineCell( Integer cell ) {
		if (cell == null
				|| cell < 0
				|| cell > Dungeon.level.length()
				|| (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell])) {
			return;
		}

		ArrayList<String> names = new ArrayList<>();
		final ArrayList<Object> objects = new ArrayList<>();

		if (cell == Dungeon.hero.pos) {
			objects.add(Dungeon.hero);
			names.add(Dungeon.hero.className().toUpperCase(Locale.ENGLISH));
		} else {
			if (Dungeon.level.heroFOV[cell]) {
				Mob mob = (Mob) Actor.findChar(cell);
				if (mob != null) {
					objects.add(mob);
					names.add(Messages.titleCase( mob.name() ));
				}
			}
		}

		Heap heap = Dungeon.level.heaps.get(cell);
		if (heap != null && heap.seen) {
			objects.add(heap);
			names.add(Messages.titleCase( heap.toString() ));
		}

		Plant plant = Dungeon.level.plants.get( cell );
		if (plant != null) {
			objects.add(plant);
			names.add(Messages.titleCase( plant.plantName ));
		}

		Trap trap = Dungeon.level.trap( cell );
		if (trap != null && trap.visible) {
			objects.add(trap);
			names.add(Messages.titleCase( trap.name ));
		}

		if (objects.isEmpty()) {
			GameScene.show(new WndInfoCell(cell));
		} else if (objects.size() == 1){
			examineObject(objects.get(0));
		} else {
			GameScene.show(new WndOptions(Messages.get(GameScene.class, "choose_examine"),
					Messages.get(GameScene.class, "multiple_examine"), names.toArray(new String[names.size()])){
				@Override
				protected void onSelect(int index) {
					examineObject(objects.get(index));
				}
			});

		}
	}

	public static void examineObject(Object o){
		if (o == Dungeon.hero){
			GameScene.show( new WndHero() );
		} else if ( o instanceof Mob ){
			GameScene.show(new WndInfoMob((Mob) o));
		} else if ( o instanceof Heap ){
			GameScene.show(new WndInfoItem((Heap)o));
		} else if ( o instanceof Plant ){
			GameScene.show( new WndInfoPlant((Plant) o) );
		} else if ( o instanceof Trap ){
			GameScene.show( new WndInfoTrap((Trap) o));
		} else {
			GameScene.show( new WndMessage( Messages.get(GameScene.class, "dont_know") ) ) ;
		}
	}

	
	private static final CellSelector.Listener defaultCellListener = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer cell ) {
			if (Dungeon.hero.handle( cell )) {
				Dungeon.hero.next();
			}
		}
		@Override
		public String prompt() {
			return null;
		}
	};
}
