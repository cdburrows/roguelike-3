package com.cdburrows.android.roguelike.scenes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.input.touch.TouchEvent;
import com.cburrows.android.roguelike.Direction;
import com.cburrows.android.roguelike.Dungeon;
import com.cburrows.android.roguelike.Event;
import com.cburrows.android.roguelike.Item;
import com.cburrows.android.roguelike.ItemFactory;
import com.cburrows.android.roguelike.MonsterFactory;
import com.cburrows.android.roguelike.Player;
import com.cburrows.android.roguelike.PlayerState;
import com.cburrows.android.roguelike.components.Chest;
import com.cburrows.android.roguelike.components.Minimap;
import com.cburrows.android.roguelike.components.ProgressBar;
import com.cburrows.android.roguelike.components.TextPanel;
import com.cdburrows.android.roguelike.base.AudioManager;
import com.cdburrows.android.roguelike.base.Graphics;
import com.cdburrows.android.roguelike.base.RoguelikeActivity;
import com.cdburrows.android.roguelike.skills.Skill;
import com.cdburrows.android.roguelike.skills.SkillDirection;

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
    
    private static final float MINIMAP_SCROLL_FACTOR = 1.5f;
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private static BaseScene sScene;
    private static SceneState sSceneState;
    
    private static HUD sHud;
    private static Player sPlayer;
    private static Dungeon sDungeon;
    private static Chest sChest;
    private static Event sEvent;
    
    private static TiledSprite sStatusIcon;
    private static TiledSprite sMapIcon;
    private static TiledSprite sPotionIcon;
    private static ChangeableText sPotionText;
    private static ProgressBar sHPBar;
        
    // Input fields
    private static float sTouchX;
    private static float sTouchY;
    private static float sTouchOffsetX;
    private static float sTouchOffsetY;
    private static float sTotalTouchOffsetX;
    private static float sTotalTouchOffsetY;
    
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
       
        Graphics.endLoad("MAIN");
              
        sDungeon = RoguelikeActivity.sDungeon;
        MonsterFactory.initialize(sDungeon.getMonsterList());
        attachChild(RoguelikeActivity.sDungeon.getSprite(0));
        
        sPlayer = RoguelikeActivity.getPlayer();
        sPlayer.setParentMap(sDungeon.getGameMap());
        sPlayer.setRoom(0, 0);
        sPlayer.setCurHP(80);
        attachChild(sPlayer.getAnimatedSprite());
        
        attachChild(RoguelikeActivity.sDungeon.getSprite(1));
        
        sHPBar = new ProgressBar(
                (mCameraWidth / 2) - (HP_BAR_WIDTH * scaleX / 2),
                mCameraHeight - (HP_OFF_Y * scaleY), HP_BAR_WIDTH, HP_BAR_HEIGHT, 
                HP_BAR_COLOR, HP_BAR_ALPHA, sPlayer.getMaxHP());
        
        sChest = new Chest(mCameraWidth / 2, mCameraHeight / 2);
        
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

        Minimap.initialize(RoguelikeActivity.getDungeon());
        Minimap.setCenter(sPlayer.getX(), sPlayer.getY());

        sHud = new HUD();
        sHud.attachChild(Minimap.getSprite());
        sHud.attachChild(sStatusIcon);
        sHud.attachChild(sMapIcon);
        sHud.attachChild(sHPBar.getEntity());
        sHud.attachChild(sPotionIcon);
        sHud.attachChild(sPotionText);
        sHud.attachChild(sChest.getSprite());
        
        RoguelikeActivity.getContext().getEngine().registerUpdateHandler(new UpdateHandler());
        setOnSceneTouchListener((IOnSceneTouchListener)RoguelikeActivity.getContext());
        
        if (RoguelikeActivity.sMusicEnabled) {
            try {
                AssetFileDescriptor afd = RoguelikeActivity.getContext().getAssets().openFd("sfx/music/CornFields.aac");
                sBackgroundMusic = new MediaPlayer();
                sBackgroundMusic.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                AudioManager.pushMusic(sBackgroundMusic);
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
        
        mLoaded = true;
        Log.d("MAIN", "Load time: " + (System.currentTimeMillis() - timeStart));
    }
    
    @Override
    public void prepare(IEntityModifierListener preparedListener) {
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
        
        mTransitionOver = false;
        mPrepared = true;
        preparedListener.onModifierFinished(null, this);
        AudioManager.play();
    }

    @Override
    public void pause() {
        
    }
    
    @Override
    public void destroy() {        
    }
    
    @Override
    public boolean onSceneTouchEvent(TouchEvent pTouchEvent) {
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
                
                if (sStatusIcon.contains(sTouchX, sTouchY)) {
                    openStatus();
                } else if (sMapIcon.contains(sTouchX, sTouchY)) {
                    openMiniMap();
                } else if (sPotionIcon.contains(sTouchX, sTouchY)) {
                    usePotion();
                }
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
                        sPlayer.move(Direction.DIRECTION_RIGHT, sPlayer.getTileWidth() * Dungeon.getRoomWidth());
                    } else if (sTotalTouchOffsetX > 0) {
                        sPlayer.move(Direction.DIRECTION_LEFT, sPlayer.getTileWidth() * Dungeon.getRoomWidth());
                    }
                } else if (Math.abs(sTotalTouchOffsetY) >= TOUCH_SENSITIVITY) {
                    if (sTotalTouchOffsetY < 0) {
                        sPlayer.move(Direction.DIRECTION_DOWN, sPlayer.getTileHeight() * Dungeon.getRoomHeight());
                    } else if (sTotalTouchOffsetY > 0) {
                        sPlayer.move(Direction.DIRECTION_UP, sPlayer.getTileHeight() * Dungeon.getRoomHeight());
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
        mTransitionOver = value;
        setSceneState(SceneState.STATE_READY);
    }
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    private static class UpdateHandler implements IUpdateHandler {
        public void onUpdate(float pSecondsElapsed) {
            sEvent = sPlayer.update(pSecondsElapsed);
            
            switch (sEvent) {
                case EVENT_NO_EVENT:
                    break;
                    
                case EVENT_NEW_ROOM:
                    if (sDungeon.hasChest(sPlayer.getRoomX(), sPlayer.getRoomY())) {
                        
                        findChest();
                        
                    } else if (sRand.nextFloat() <= sDungeon.getCurrentFloor().mMonsterSpawnRate) {
                        
                        sPlayer.setPlayerState(PlayerState.FIGHTING);
                        RoguelikeActivity.startCombat();
                        
                    }
                    break;
            }
        }

        public void reset() {               
        }
    }
    
    private static void openStatus() {
        sStatusIcon.setCurrentTileIndex(RoguelikeActivity.ICON_STATUS_DOWN);
        AudioManager.playClick();
        RoguelikeActivity.openStatus();
    }
    
    private static void openMiniMap() {
        sMapIcon.setCurrentTileIndex(RoguelikeActivity.ICON_MINIMAP_DOWN);
        AudioManager.playClick();
        
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
        
        Item item = ItemFactory.createRandomItem(sDungeon.getCurrentFloorLevel());
        sPlayer.addItem(item);
        sChest.show(item.copySprite());
        
        sDungeon.setChest(sPlayer.getRoomX(), sPlayer.getRoomY(), false);
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
