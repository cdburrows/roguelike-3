package com.cdburrows.android.roguelike.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.input.touch.TouchEvent;

import com.cdburrows.android.roguelike.Direction;
import com.cdburrows.android.roguelike.Event;
import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.audio.Audio;
import com.cdburrows.android.roguelike.component.Chest;
import com.cdburrows.android.roguelike.component.ProgressBar;
import com.cdburrows.android.roguelike.graphics.Graphics;
import com.cdburrows.android.roguelike.item.Item;
import com.cdburrows.android.roguelike.item.ItemFactory;
import com.cdburrows.android.roguelike.map.DungeonManager;
import com.cdburrows.android.roguelike.map.Minimap;
import com.cdburrows.android.roguelike.monster.MonsterFactory;
import com.cdburrows.android.roguelike.player.Player;
import com.cdburrows.android.roguelike.player.PlayerState;
import com.cdburrows.android.roguelike.skill.Skill;
import com.cdburrows.android.roguelike.skill.SkillDirection;


import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;

public class MainScene extends BaseScene {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    private static final float TOUCH_SENSITIVITY = 32.0f;
    private static final int TEXTURE_ATLAS_WIDTH = 512;
    private static final int TEXTURE_ATLAS_HEIGHT = 512;
    private static final float HUD_OPACITY = 0.5f;
    
    private static final int HP_BAR_WIDTH = 160;
    private static final int HP_BAR_HEIGHT = 16;
    private static final float HP_OFF_Y = 24;
    private static final int HP_BAR_COLOR = Color.RED;
    private static final float HP_BAR_ALPHA = HUD_OPACITY;
    
    private static final float MINIMAP_SCROLL_FACTOR = 1f;//1.5f;
    private static final int DOUBLE_TAP_DURATION = 300;
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private static BaseScene sScene;
    private static SceneState sSceneState;
    
    private static HUD sHud;
    private static Player sPlayer;
    private static Chest sChest;
    private static Event sEvent;
    
    private static TiledSprite sStatusIcon;
    private static TiledSprite sMapIcon;
    private static TiledSprite sPotionIcon;
    private static ChangeableText sPotionText;
    private static ProgressBar sHPBar;
    
    private static ChangeableText sPosition;
        
    // Input fields
    private static float sTouchX;
    private static float sTouchY;
    private static float sTouchOffsetX;
    private static float sTouchOffsetY;
    private static float sTotalTouchOffsetX;
    private static float sTotalTouchOffsetY;
    private static long sLastTapTime = System.currentTimeMillis();
    
    private static Random sRand = new Random(System.currentTimeMillis());;
    
    private static MediaPlayer sBackgroundMusic;
    private static MediaPlayer sPotionEffect;
    private static boolean sMinimapVisible;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    public MainScene() {
        super();
        sScene = this;
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    private static void setSceneState(SceneState stateTransition) {
        sTouchOffsetX = 0;
        sTouchOffsetY = 0;
        sTotalTouchOffsetX = 0;
        sTotalTouchOffsetY = 0;
        sSceneState = stateTransition;
    }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    @Override
    public void loadResources() {
        long timeStart = System.currentTimeMillis();
        
        loadPlayer();
        
        loadSkills();
        
        loadMonsters();
        
        loadGraphics();

        loadUI();
        
        loadHud();
        
        loadAudio();
        
        RoguelikeActivity.getContext().getEngine().registerUpdateHandler(new UpdateHandler());
        setOnSceneTouchListener((IOnSceneTouchListener)RoguelikeActivity.getContext());

        mLoaded = true;
        Log.d("MAIN", "Load time: " + (System.currentTimeMillis() - timeStart));
    }
    
    private void loadPlayer() {
        sPlayer = RoguelikeActivity.getPlayer();
        sPlayer.setParentMap(DungeonManager.getGameMap());
        sPlayer.setRoom(DungeonManager.getStartX(), DungeonManager.getStartY());
        sPlayer.setCurHP(80);
    }
    
    private void loadSkills() {
        ArrayList<Skill> skills = new ArrayList<Skill>();
        skills.add(new Skill("Stab", new ArrayList<SkillDirection>(
                Arrays.asList(SkillDirection.DIRECTION_UP, SkillDirection.DIRECTION_UP))));
        skills.add(new Skill("Slash", new ArrayList<SkillDirection>(
                Arrays.asList(SkillDirection.DIRECTION_DOWN_LEFT, SkillDirection.DIRECTION_DOWN_RIGHT))));
        skills.add(new Skill("Stab", new ArrayList<SkillDirection>(
                Arrays.asList(SkillDirection.DIRECTION_UP, SkillDirection.DIRECTION_UP, SkillDirection.DIRECTION_DOWN, SkillDirection.DIRECTION_UP_RIGHT))));
        skills.add(new Skill("Lunge", new ArrayList<SkillDirection>(
                Arrays.asList(SkillDirection.DIRECTION_UP, SkillDirection.DIRECTION_DOWN, SkillDirection.DIRECTION_RIGHT))));
        skills.add(new Skill("Flurry", new ArrayList<SkillDirection>(
                Arrays.asList(SkillDirection.DIRECTION_UP, SkillDirection.DIRECTION_DOWN, SkillDirection.DIRECTION_LEFT, SkillDirection.DIRECTION_RIGHT))));
        sPlayer.setSkills(skills);
    }
    
    private void loadMonsters() {
        MonsterFactory.initialize(DungeonManager.getMonsterList()); 
    }
    
    private void loadGraphics() {
        float scaleX = RoguelikeActivity.sScaleX;
        float scaleY = RoguelikeActivity.sScaleY;
        
        Graphics.beginLoad("gfx/", TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT);
        sStatusIcon = Graphics.createTiledSprite("icons.png", 4, 4, 
                scaleX, scaleY, HUD_OPACITY);
        
        sMapIcon = Graphics.createTiledSprite("icons.png", 4, 4, HUD_OPACITY);
        sMapIcon.setPosition(
                mCameraWidth - sMapIcon.getWidth(),
                scaleY); 
        sMapIcon.setCurrentTileIndex(RoguelikeActivity.ICON_MINIMAP_UP);
        
        sPotionIcon = Graphics.createTiledSprite("icons.png", 4, 4, HUD_OPACITY);
        sPotionIcon.setPosition(
                mCameraWidth - sPotionIcon.getWidth() - scaleX,
                mCameraHeight - sPotionIcon.getHeight() - scaleY);
   
        sPotionText = Graphics.createChangeableText(sPotionIcon.getX() - (28 * scaleX) , 
                sPotionIcon.getY() + (11 * scaleY), 
                Graphics.SmallFont, "88x", HUD_OPACITY);
        
        sPosition = Graphics.createChangeableText(16, 16, Graphics.SmallFont, "0,0");
       
        Graphics.endLoad("MAIN");
        
        sHPBar = new ProgressBar(
                (mCameraWidth / 2) - (HP_BAR_WIDTH * RoguelikeActivity.sScaleX / 2),
                mCameraHeight - (HP_OFF_Y * RoguelikeActivity.sScaleY), HP_BAR_WIDTH, HP_BAR_HEIGHT, 
                HP_BAR_COLOR, HP_BAR_ALPHA, sPlayer.getMaxHP());
        
        sChest = new Chest(mCameraWidth / 2, mCameraHeight / 2);
    }
    
    private void loadUI() {
        Minimap.initialize(RoguelikeActivity.getCurrentGameMap());
        Minimap.setCenter(sPlayer.getX(), sPlayer.getY());
        
        // Dungeon sprite
        attachChild(DungeonManager.getSprite(0));
        
        // Player sprite
        attachChild(sPlayer.getAnimatedSprite());
    }
    
    private void loadHud() {
        sHud = new HUD();
        sHud.attachChild(Minimap.getSprite());
        sHud.attachChild(sStatusIcon);
        sHud.attachChild(sMapIcon);
        sHud.attachChild(sHPBar.getEntity());
        sHud.attachChild(sPotionIcon);
        sHud.attachChild(sPotionText);
        sHud.attachChild(sChest.getSprite());
        sHud.attachChild(sPosition);
    }
    
    public void reloadUI() {
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
            public void run() {
                pause();
                sHud.detachChild(Minimap.getSprite());
                detachChildren();
                
                loadPlayer();
                loadMonsters();
                loadUI();
                sHud.attachChild(Minimap.getSprite());
                Minimap.setVisible(sMapIcon.getCurrentTileIndex() == RoguelikeActivity.ICON_MINIMAP_DOWN);
                
                resume();
            }
        });
    }
    
    private void loadAudio() {
        if (RoguelikeActivity.sMusicEnabled) {
            try {
                AssetFileDescriptor afd = RoguelikeActivity.getContext().getAssets().openFd("sfx/music/CornFields.aac");
                sBackgroundMusic = new MediaPlayer();
                sBackgroundMusic.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                Audio.pushMusic(sBackgroundMusic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
                
        if (RoguelikeActivity.sSoundEnabled) {
            try {
                AssetFileDescriptor afd = RoguelikeActivity.getContext().getAssets().openFd("sfx/power_up.mp3");
                sPotionEffect = new MediaPlayer();
                sPotionEffect.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                sPotionEffect.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void prepare(IEntityModifierListener preparedListener) {
        Log.d("MAIN", "Start prepare");
        setSceneState(SceneState.STATE_TRANSITION);
        
        sStatusIcon.setCurrentTileIndex(RoguelikeActivity.ICON_STATUS_UP);
        //sMapIcon.setCurrentTileIndex(2);
        sPotionIcon.setCurrentTileIndex(RoguelikeActivity.ICON_POTION_UP);
        sPotionText.setText(String.format("%02d", sPlayer.getNumPotions()) + "x");
        sChest.setVisible(false);
        
        updateHP();

        /* Create the sprite and add it to the scene. */
        mCamera.setChaseEntity(sPlayer.getAnimatedSprite());
        mCamera.updateChaseEntity();
        mCamera.setHUD(sHud);
        
        sPlayer.setPlayerState(PlayerState.IDLE);
        
        setTransitioning(false);
        mPrepared = true;
        preparedListener.onModifierFinished(null, this);
        Audio.play();
        
        Log.d("MAIN", "End prepare");
    }

    @Override
    public void pause() {
        mPaused = true;
    }
    
    @Override
    public void resume() {
        mPaused = false;        
    }
    
    @Override
    public void destroy() {        
    }
    
    @Override
    public boolean onSceneTouchEvent(TouchEvent pTouchEvent) {
        if (mPaused) return true;
        
        if (sMinimapVisible && pTouchEvent.getMotionEvent().getPointerCount() == 2) {
            sSceneState = SceneState.STAT_MINIMAP_SCROLL;
        }
        
        if (sSceneState == SceneState.STATE_TRANSITION) { 
            return true;
            
        } else if (sSceneState == SceneState.STATE_CHEST) {
            sTotalTouchOffsetX = 0;
            sTotalTouchOffsetY = 0;
            return sChest.handleTouchEvent(pTouchEvent);
            
        } else if (sSceneState == SceneState.STAT_MINIMAP_SCROLL) {
            if(pTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
                sTouchX = pTouchEvent.getMotionEvent().getX();
                sTouchY = pTouchEvent.getMotionEvent().getY();
                sTotalTouchOffsetX = 0;
                sTotalTouchOffsetY = 0;
            }
            else if(pTouchEvent.getAction() == MotionEvent.ACTION_MOVE) {    
                float newX = pTouchEvent.getMotionEvent().getX();
                float newY = pTouchEvent.getMotionEvent().getY();
               
                sTouchOffsetX = (newX - sTouchX);
                sTouchOffsetY = (newY - sTouchY);
                sTotalTouchOffsetX += sTouchOffsetX;
                sTotalTouchOffsetY += sTouchOffsetY;
                
                Minimap.scroll(sTotalTouchOffsetX * MINIMAP_SCROLL_FACTOR, 
                        sTotalTouchOffsetY * MINIMAP_SCROLL_FACTOR);
                
                sTouchX = newX;
                sTouchY = newY;
            } else if (pTouchEvent.getAction() == MotionEvent.ACTION_UP) {
                
                sTotalTouchOffsetX = 0;
                sTotalTouchOffsetY = 0;
                
                Minimap.scroll(sTotalTouchOffsetX, sTotalTouchOffsetY);
                sSceneState = SceneState.STATE_READY;
            }
            return true;
            
        } else if (sSceneState == SceneState.STATE_READY) {
            if(pTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
                sTouchX = pTouchEvent.getMotionEvent().getX();
                sTouchY = pTouchEvent.getMotionEvent().getY();
                sTotalTouchOffsetX = 0;
                sTotalTouchOffsetY = 0;
                
                if (System.currentTimeMillis() < sLastTapTime + DOUBLE_TAP_DURATION) {
                    interactStairs();
                }
                
                if (sStatusIcon.contains(sTouchX, sTouchY)) {
                    openStatus();
                } else if (sMapIcon.contains(sTouchX, sTouchY)) {
                    openMiniMap();
                } else if (sPotionIcon.contains(sTouchX, sTouchY)) {
                    usePotion();
                }
                
                sLastTapTime = System.currentTimeMillis();
            }
            else if(pTouchEvent.getAction() == MotionEvent.ACTION_MOVE) {    
                float newX = pTouchEvent.getMotionEvent().getX();
                float newY = pTouchEvent.getMotionEvent().getY();
               
                sTouchOffsetX = (newX - sTouchX);
                sTouchOffsetY = (newY - sTouchY);
                sTotalTouchOffsetX += sTouchOffsetX;
                sTotalTouchOffsetY += sTouchOffsetY;
                                               
                if (Math.abs(sTotalTouchOffsetX) >= TOUCH_SENSITIVITY) {
                    if (sTotalTouchOffsetX < 0) {
                        sPlayer.move(Direction.DIRECTION_RIGHT, sPlayer.getTileWidth() * DungeonManager.getRoomWidth());
                    } else if (sTotalTouchOffsetX > 0) {
                        sPlayer.move(Direction.DIRECTION_LEFT, sPlayer.getTileWidth() * DungeonManager.getRoomWidth());
                    }
                } else if (Math.abs(sTotalTouchOffsetY) >= TOUCH_SENSITIVITY) {
                    if (sTotalTouchOffsetY < 0) {
                        sPlayer.move(Direction.DIRECTION_DOWN, sPlayer.getTileHeight() * DungeonManager.getRoomHeight());
                    } else if (sTotalTouchOffsetY > 0) {
                        sPlayer.move(Direction.DIRECTION_UP, sPlayer.getTileHeight() * DungeonManager.getRoomHeight());
                    }
                }
                
                sTouchX = newX;
                sTouchY = newY;
            } else if (pTouchEvent.getAction() == MotionEvent.ACTION_UP) {
                sStatusIcon.setCurrentTileIndex(RoguelikeActivity.ICON_STATUS_UP);
                //sMapIcon.setCurrentTileIndex(RoguelikeActivity.ICON_STATUS_UP);
                sPotionIcon.setCurrentTileIndex(RoguelikeActivity.ICON_POTION_UP);
                
                sTotalTouchOffsetX = 0;
                sTotalTouchOffsetY = 0;
                
                
            }
            return true;
        }
        
        return false;
    }
    
    @Override
    protected void setTransitioning(boolean value) {
        mTransitioning = value;
        
        if (value) {
            setSceneState(SceneState.STATE_TRANSITION);
        } else {
            setSceneState(SceneState.STATE_READY);
        }
        
        Log.d("MAIN", "Transitioning to " + value);
    }
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    private static class UpdateHandler implements IUpdateHandler {
        public void onUpdate(float pSecondsElapsed) {
            if (mPaused) return;
            
            sEvent = sPlayer.update(pSecondsElapsed);
            
            switch (sEvent) {
                case EVENT_NO_EVENT:
                    break;
                    
                case EVENT_NEW_ROOM:
                    sPosition.setText(sPlayer.getRoomX() + ", " + sPlayer.getRoomY());
                    
                    if (DungeonManager.hasChest(sPlayer.getRoomX(), sPlayer.getRoomY())) {
                        
                        findChest();
                        
                    } else if (sRand.nextFloat() <= DungeonManager.getCurrentFloor().mMonsterSpawnRate) {
                        
                        sPlayer.setPlayerState(PlayerState.FIGHTING);
                        RoguelikeActivity.openCombat();
                        
                    }
                    break;
            }
        }

        public void reset() {               
        }
    }
    
    public static void fadeSceneOut(float f, IEntityModifierListener listener) {
        if (sScene == null) return;
        
        sScene.fadeOut(f, listener);
    }
    
    public static void fadeSceneIn(float f, IEntityModifierListener listener) {
        if (sScene == null) return;
        
        sScene.fadeIn(f, listener);
    }
    
    /*
    public static void fadeIn(float duration) {
        if (sScene == null) return;
        sScene.fadeIn(duration, null);
        Log.d("MAIN", "Fade in");
    }
    */
    
    private void interactStairs() {
        if (DungeonManager.interact(sPlayer.getRoomX(), sPlayer.getRoomY())) {
            reloadUI();
        }
    }
    
    private static void openStatus() {
        sStatusIcon.setCurrentTileIndex(RoguelikeActivity.ICON_STATUS_DOWN);
        Audio.playClick();
        RoguelikeActivity.openStatus();
    }
    
    private static void openMiniMap() {
        sMapIcon.setCurrentTileIndex(RoguelikeActivity.ICON_MINIMAP_DOWN);
        Audio.playClick();
        
        if (Minimap.isVisible()) {
            Minimap.setVisible(false);
            sMinimapVisible = false;
            sMapIcon.setCurrentTileIndex(RoguelikeActivity.ICON_MINIMAP_UP);
        } else {
            Minimap.setVisible(true);
            sMinimapVisible = true;
        }
    }
    
    private static void updateHP() {
        sHPBar.setMaxValue(sPlayer.getMaxHP());
        sHPBar.setCurValue(sPlayer.getCurHP());
    }
    
    private static void usePotion() {
        if (RoguelikeActivity.sSoundEnabled) sPotionEffect.start();
        sPotionIcon.setCurrentTileIndex(RoguelikeActivity.ICON_POTION_DOWN);
        sPlayer.usePotion();
        sPotionText.setText(String.format("%02d", sPlayer.getNumPotions()) + "x");
        updateHP();
    }
    
    private static void findChest() {
        setSceneState(SceneState.STATE_CHEST);
        
        sScene.fadeTo(0.5f, 0f, 0.5f);
        
        Item item = ItemFactory.createRandomItem(DungeonManager.getCurrentDepth());
        sPlayer.addItem(item);
        sChest.show(item.copySprite());
        
        DungeonManager.setChest(sPlayer.getRoomX(), sPlayer.getRoomY(), false);
    }
    
    public static void closeChest() {
        if (sSceneState == SceneState.STATE_CHEST) {
            sScene.fadeTo(0.5f, 0.5f, 0f);
            setSceneState(SceneState.STATE_READY);
        }
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
    private enum SceneState { STATE_TRANSITION, STATE_READY, STATE_CHEST, STAT_MINIMAP_SCROLL }
   
}
