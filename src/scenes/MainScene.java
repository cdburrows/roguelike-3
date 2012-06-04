package scenes;

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

import skills.Skill;
import skills.SkillDirection;

import base.AudioManager;
import base.Graphics;
import base.RoguelikeActivity;

import com.cburrows.android.roguelike.Direction;
import com.cburrows.android.roguelike.Dungeon;
import com.cburrows.android.roguelike.Event;
import com.cburrows.android.roguelike.MonsterFactory;
import com.cburrows.android.roguelike.Player;
import com.cburrows.android.roguelike.PlayerState;
import com.cburrows.android.roguelike.components.ProgressBar;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;

public class MainScene extends GameScene {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    public static final float FIGHT_CHANCE = 0.40f;
    private static final float TOUCH_SENSITIVITY = 32.0f;
    private static final int TEXTURE_ATLAS_WIDTH = 512;
    private static final int TEXTURE_ATLAS_HEIGHT = 512;
    //private static final int HUD_IMAGE_X = 0;
    //private static final int HUD_IMAGE_Y = 128;
    //private static final int HP_IMAGE_Y = 256;
    //private static final int HP_FILL_IMAGE_Y = 272;
    private static final float HUD_OPACITY = 0.5f;
    private static final int HP_BAR_WIDTH = 160;
    private static final int HP_BAR_HEIGHT = 16;
    private static final float HP_OFF_Y = 24;
    private static final int HP_BAR_COLOR = Color.RED;
    private static final float HP_BAR_ALPHA = HUD_OPACITY;    
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private static HUD sHud;
    private static Player sPlayer;
    private static Dungeon sDungeon;
    
    private static TiledSprite sStatusIcon;
    private static TiledSprite sMapIcon;
    private static TiledSprite sPotionIcon;
    private static ChangeableText sPotionText;
    private static ProgressBar sHPBar;
    
    private static float sTouchX;
    private static float sTouchY;
    private static float sTouchOffsetX;
    private static float sTouchOffsetY;
    private static float sTotalTouchOffsetX;
    private static float sTotalTouchOffsetY;
    
    private static Random sRand = new Random(System.currentTimeMillis());;
    
    private static MediaPlayer sBackgroundMusic;
    private static MediaPlayer sPotionEffect;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    public MainScene(RoguelikeActivity context) {
        super(context);
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
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
        attachChild(RoguelikeActivity.sDungeon.getSprite());

        
        sPlayer = RoguelikeActivity.getPlayer();
        sPlayer.setParentMap(sDungeon.getGameMap());
        sPlayer.setRoom(0, 0);
        sPlayer.setCurHP(80);
        
        sHPBar = new ProgressBar(
                (mCameraWidth / 2) - (HP_BAR_WIDTH * scaleX / 2),
                mCameraHeight - (HP_OFF_Y * scaleY), HP_BAR_WIDTH, HP_BAR_HEIGHT, 
                HP_BAR_COLOR, HP_BAR_ALPHA, sPlayer.getMaxHP());
        
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
        
        sHud = new HUD();
        sHud.attachChild(sStatusIcon);
        sHud.attachChild(sMapIcon);
        sHud.attachChild(sHPBar.getEntity());
        sHud.attachChild(sPotionIcon);
        sHud.attachChild(sPotionText);
        attachChild(sPlayer.getAnimatedSprite());
        
        mContext.getEngine().registerUpdateHandler(new UpdateHandler());
        setOnSceneTouchListener((IOnSceneTouchListener)mContext);
        
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
     // Load the map
        
        sStatusIcon.setCurrentTileIndex(0);
        sMapIcon.setCurrentTileIndex(2);
        sPotionIcon.setCurrentTileIndex(6);
        sPotionText.setText(String.format("%02d", sPlayer.getNumPotions()) + "x");
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
    
    public void suspend() {
        //AudioManager.pause();
    }
    
    @Override
    public void pause() {
        
    }
    
    @Override
    public boolean onSceneTouchEvent(TouchEvent pTouchEvent) {
        if (!mTransitionOver) return true;
        if(pTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            sTouchX = pTouchEvent.getMotionEvent().getX();
            sTouchY = pTouchEvent.getMotionEvent().getY();
            sTotalTouchOffsetX = 0;
            sTotalTouchOffsetY = 0;
            
            if (sStatusIcon.contains(sTouchX, sTouchY)) {
                sStatusIcon.setCurrentTileIndex(1);
                AudioManager.playClick();
                openStatus();
            }
            
            if (sMapIcon.contains(sTouchX, sTouchY)) {
                sMapIcon.setCurrentTileIndex(3);
                AudioManager.playClick();
                openMiniMap();
            }
            
            if (sPotionIcon.contains(sTouchX, sTouchY)) {
                if (RoguelikeActivity.sSoundEnabled) sPotionEffect.start();
                sPotionIcon.setCurrentTileIndex(7);
                usePotion();
            }
        }
        else if(pTouchEvent.getAction() == MotionEvent.ACTION_MOVE)
        {    
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
            sStatusIcon.setCurrentTileIndex(0);
            sMapIcon.setCurrentTileIndex(2);
            sPotionIcon.setCurrentTileIndex(6);
            
        }
        return true;
    }
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    private class UpdateHandler implements IUpdateHandler {
        public void onUpdate(float pSecondsElapsed) {
            Event event = sPlayer.update(pSecondsElapsed);
            
            switch (event) {
                case EVENT_NO_EVENT:
                    break;
                    
                case EVENT_NEW_ROOM:
                    if (sRand.nextFloat() <= sDungeon.getCurrentFloor().mMonsterSpawnRate) {
                        sPlayer.setPlayerState(PlayerState.FIGHTING);
                        mContext.startCombat();
                    }
                    break;
            }
        }

        public void reset() {               
        }
    }
    
    private void openStatus() {
        mContext.openStatus();
    }
    
    private void openMiniMap() {
    }
    
    private void updateHP() {
        sHPBar.setMaxValue(sPlayer.getMaxHP());
        sHPBar.setCurValue(sPlayer.getCurHP());
    }
    
    private void usePotion() {
        sPlayer.usePotion();
        sPotionText.setText(String.format("%02d", sPlayer.getNumPotions()) + "x");
        updateHP();
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
   
}
