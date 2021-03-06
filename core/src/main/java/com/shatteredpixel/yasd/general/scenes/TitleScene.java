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
import com.shatteredpixel.yasd.general.Chrome;
import com.shatteredpixel.yasd.general.GamesInProgress;
import com.shatteredpixel.yasd.general.PDSGame;
import com.shatteredpixel.yasd.general.effects.BannerSprites;
import com.shatteredpixel.yasd.general.effects.Fireball;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.ui.Archs;
import com.shatteredpixel.yasd.general.ui.DevSettingsButton;
import com.shatteredpixel.yasd.general.ui.DiscordButton;
import com.shatteredpixel.yasd.general.ui.ExitButton;
import com.shatteredpixel.yasd.general.ui.Icons;
import com.shatteredpixel.yasd.general.ui.StyledButton;
import com.shatteredpixel.yasd.general.ui.UpdateNotification;
import com.shatteredpixel.yasd.general.windows.WndSettings;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PlatformSupport;

public class TitleScene extends PixelScene {
	
	@Override
	public void create() {
		
		super.create();

		Music.INSTANCE.play( Assets.Music.TITLE_THEME, true );

		uiCamera.visible = false;
		
		int w = Camera.main.width;
		int h = Camera.main.height;
		
		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );
		
		Image title = BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON );
		add( title );

		float topRegion = Math.max(title.height, h*0.45f);
		title.x = (w - title.width()) / 2f;

		if (landscape()) {
			title.y = (topRegion - title.height()) / 2f;
		} else {
			title.y = 20 + (topRegion - title.height() - 20) / 2f;
		}

		align(title);

		placeTorch(title.x + 22, title.y + 60);
		placeTorch(title.x + title.width - 22, title.y + 60);

		Image signs = new Image( BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON_SIGNS ) ) {
			private float time = 0;
			@Override
			public void update() {
				super.update();
				am = Math.max(0f, (float)Math.sin( time += Game.elapsed ));
				if (time >= 1.5f*Math.PI) time = 0;
			}
			@Override
			public void draw() {
				Blending.setLightMode();
				super.draw();
				Blending.setNormalMode();
			}
		};
		signs.x = title.x + (title.width() - signs.width())/2f;
		signs.y = title.y;
		add( signs );
		
		TitleButton btnPlay = new TitleButton(Messages.get(this, "enter")){
			@Override
			protected void onClick() {
				if (GamesInProgress.checkAll().size() == 0){
					GamesInProgress.selectedClass = null;
					GamesInProgress.curSlot = 1;
					PDSGame.switchScene(HeroSelectScene.class);
				} else {
					PDSGame.switchScene( StartScene.class );
				}
			}
			
			@Override
			protected boolean onLongClick() {
				//making it easier to start runs quickly while debugging
				if (DeviceCompat.isDebug()) {
					GamesInProgress.selectedClass = null;
					GamesInProgress.curSlot = 1;
					HeroSelectScene.testing = true;
					PDSGame.platform.promptTextInput("Enter a seed", "-1", Integer.MAX_VALUE, false, "Confirm", "Cancel", new PlatformSupport.TextCallback() {
						@Override
						public void onSelect(boolean positive, String text) {
							if (positive) {
								try {
									HeroSelectScene.seed = Long.parseLong(text);
								} catch (NumberFormatException e) {
									PDSGame.reportException(e);
								}
							}
							PDSGame.switchScene(HeroSelectScene.class);
						}
					});
					return true;
				}
				return super.onLongClick();
			}
		};
		btnPlay.icon(Icons.get(Icons.ENTER));
		add(btnPlay);
		
		TitleButton btnSettings = new TitleButton(Messages.get(this, "settings")){
			@Override
			protected void onClick() {
				parent.add( new WndSettings() );
			}
		};
		btnSettings.icon(Icons.get(Icons.PREFS));
		add(btnSettings);
		
		TitleButton btnRankings = new TitleButton(Messages.get(this, "rankings")){
			@Override
			protected void onClick() {
				PDSGame.switchScene( RankingsScene.class );
			}
		};
		btnRankings.icon(Icons.get(Icons.RANKINGS));
		add(btnRankings);
		
		TitleButton btnBadges = new TitleButton(Messages.get(this, "badges")){
			@Override
			protected void onClick() {
				PDSGame.switchScene( BadgesScene.class );
			}
		};
		btnBadges.icon(Icons.get(Icons.BADGES));
		add(btnBadges);
		
		TitleButton btnChanges = new TitleButton(Messages.get(this, "changes")){
			@Override
			protected void onClick() {
				ChangesScene.changesSelected = 0;
				PDSGame.switchScene( ChangesScene.class );
			}
		};
		btnChanges.icon(Icons.get(Icons.CHANGES));
		add(btnChanges);
		
		TitleButton btnAbout = new TitleButton(Messages.get(this, "about")){
			@Override
			protected void onClick() {
				PDSGame.switchScene( AboutScene.class );
			}
		};
		btnAbout.icon(Icons.get(Icons.YENDOR));
		add(btnAbout);
		
		final int BTN_HEIGHT = 21;
		int GAP = (int)(h - topRegion - (landscape() ? 3 : 4)*BTN_HEIGHT)/3;
		GAP /= landscape() ? 3 : 4;
		GAP = Math.max(GAP, 2);

		if (landscape()) {
			btnPlay.setRect(title.x-50, topRegion+GAP, ((title.width()+100)/2)-1, BTN_HEIGHT);
			align(btnPlay);
			btnSettings.setRect(btnPlay.right()+2, btnPlay.top(), btnPlay.width(), BTN_HEIGHT);
			btnRankings.setRect(btnPlay.left() + (btnPlay.width()*.33f)+1, btnPlay.bottom()+ GAP, (btnPlay.width()*.67f)-1, BTN_HEIGHT);
			btnBadges.setRect(btnRankings.right()+2, btnRankings.top(), btnRankings.width(), BTN_HEIGHT);
			btnChanges.setRect(btnRankings.left(), btnRankings.bottom() + GAP, btnRankings.width(), BTN_HEIGHT);
			btnAbout.setRect(btnChanges.right()+2, btnChanges.top(), btnRankings.width(), BTN_HEIGHT);
		} else {
			btnPlay.setRect(title.x, topRegion+GAP, title.width(), BTN_HEIGHT);
			align(btnPlay);
			btnRankings.setRect(btnPlay.left(), btnPlay.bottom()+ GAP, (btnPlay.width()/2)-1, BTN_HEIGHT);
			btnBadges.setRect(btnRankings.right()+2, btnRankings.top(), btnRankings.width(), BTN_HEIGHT);
			btnChanges.setRect(btnRankings.left(), btnRankings.bottom()+ GAP, btnRankings.width(), BTN_HEIGHT);
			btnAbout.setRect(btnChanges.right()+2, btnChanges.top(), btnChanges.width(), BTN_HEIGHT);
			btnSettings.setRect(btnPlay.left(), btnAbout.bottom()+ GAP, btnPlay.width(), BTN_HEIGHT);
		}

		BitmapText version = new BitmapText( "v" + Game.version, pixelFont);
		version.measure();
		version.hardlight( 0x888888 );
		version.x = w - version.width() - 4;
		version.y = h - version.height() - 2;
		add( version );
		
		int pos = 2;
		
		DiscordButton btnDiscord = new DiscordButton();
		btnDiscord.setRect( pos, 0, 16, 20 );
		add( btnDiscord );
		
		pos += btnDiscord.width();
		if (DeviceCompat.isDebug()) {
			DevSettingsButton devSettingsButton = new DevSettingsButton();
			devSettingsButton.setRect(pos, 0, 16, 20);
			add(devSettingsButton);
		}

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( w - btnExit.width(), 0 );
		add( btnExit );

		UpdateNotification updInfo = new UpdateNotification();
		updInfo.setRect(4, h-BTN_HEIGHT, updInfo.reqWidth() + 6, BTN_HEIGHT-4);
		add(updInfo);


		fadeIn();
	}
	
	private void placeTorch( float x, float y ) {
		Fireball fb = new Fireball();
		fb.setPos( x, y );
		add( fb );
	}
	
	private static class TitleButton extends StyledButton {
		
		public TitleButton( String label ){
			this(label, 9);
		}
		
		public TitleButton( String label, int size ){
			super(Chrome.Type.GREY_BUTTON_TR, label, size);
		}
		
	}
}
