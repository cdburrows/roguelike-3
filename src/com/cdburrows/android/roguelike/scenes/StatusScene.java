package com.cdburrows.android.roguelike.scenes;

import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ScrollDetector;
import org.anddev.andengine.input.touch.detector.SurfaceScrollDetector;
import org.anddev.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;


import com.cburrows.android.roguelike.Item;
import com.cburrows.android.roguelike.Player;
import com.cburrows.android.roguelike.components.ScrollList;
import com.cburrows.android.roguelike.components.ScrollList.ISelectListener;
import com.cdburrows.android.roguelike.base.AudioManager;
import com.cdburrows.android.roguelike.base.Graphics;
import com.cdburrows.android.roguelike.base.RoguelikeActivity;

import android.util.Log;
import android.view.MotionEvent;

public class StatusScene extends BaseScene implements IScrollDetectorListener {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    //private static final float EQUIPMENT_WIDTH = 184;
    //private static final float EQUIPMENT_HEIGHT = 32;
    
    private static final int TEXTURE_ATLAS_WIDTH = 512;
    private static final int TEXTURE_ATLAS_HEIGHT = 1024;
    private static final float HUD_OPACITY = 0.3f;
    private static final float TITLE_X = 36;
    private static final int TITLE_Y = 10;
    private static final int TITLE_WIDTH = 152;
    //private static final int TITLE_HEIGHT = 11;
        
    private static final float STATUS_ICON_X = 32;
    private static final float WEAPON_ICON_X = 64;
    private static final float ARMOUR_ICON_X = 96;
    private static final float SKILL_ICON_X = 128;
    
    // Status panel values
    private static final int NAME_TEXT_X = 16;
    private static final int NAME_TEXT_Y = 41;
    private static final int LEVEL_TEXT_X = 16;
    private static final int LEVEL_TEXT_Y = 61;
    private static final int HP_TEXT_X = 121;
    private static final int HP_TEXT_Y  = 41;
    private static final int XP_TEXT_X = 121;
    private static final int XP_TEXT_Y = 61;
    private static final int ATTACK_TEXT_X = 117;
    private static final int ATTACK_TEXT_Y = 90;
    private static final int DEFENSE_TEXT_X = 117;
    private static final int DEFENSE_TEXT_Y = 116;
    private static final int MAGIC_TEXT_X = 117;
    private static final int MAGIC_TEXT_Y = 142;
    private static final int POTION_TEXT_X = 253;
    //private static final int POTION_TEXT_Y = 199;
    private static final float WEAPON_SPRITE_X = 4;
    private static final float WEAPON_SPRITE_Y = 166;
    private static final float ARMOUR_SPRITE_X = 4;
    private static final float ARMOUR_SPRITE_Y = 202;
    
    // Weapon / armour panel values
    private static final float EQUIPPED_ITEM_X = 18;
    private static final float EQUIPPED_ITEM_Y = 40;
    private static final float ITEM_SCROLL_X = 16;
    private static final float ITEM_SCROLL_Y = 84;
    private static final float ITEM_SCROLL_WIDTH = 292;
    private static final float ITEM_SCROLL_HEIGHT = 148;  
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private static HUD sHud;
    private static float sScaleX;
    private static float sScaleY;
    
    private static Sprite sStatusBackgroundSprite;
    private static Sprite sWeaponsBackgroundSprite;
    private static Sprite sArmourBackgroundSprite;
    private static Sprite sSkillsBackgroundSprite;
    private static Sprite sCurrentPanel;
    
    private static TiledSprite sBackIcon;
    private static TiledSprite sStatusIcon;
    private static TiledSprite sWeaponIcon;
    private static TiledSprite sArmourIcon;
    private static TiledSprite sSkillIcon;
    
    private static TiledSprite sWeaponSprite;
    private static TiledSprite sArmourSprite;
    private static TiledSprite sEquippedWeaponSprite;
    private static TiledSprite sEquippedArmourSprite;
    private static ScrollList sWeaponsScrollList;
    private static ScrollList sArmourScrollList;
        
    private static ChangeableText sTitleText;
    private static ChangeableText sNameText;
    private static ChangeableText sLevelText;
    private static ChangeableText sHPText;
    private static ChangeableText sXPText;
    private static ChangeableText sAttackText;
    private static ChangeableText sDefenseText;
    private static ChangeableText sMagicText;
    private static ChangeableText sPotionText;
    
    private static SurfaceScrollDetector sScrollDetector;
    private static float sTouchX;
    private static float sTouchY;
    //private float mTouchOffsetX;
    //private float mTouchOffsetY;
    //private float mTotalTouchOffsetX;
    //private float mTotalTouchOffsetY;
    
    private static Player sPlayer;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    public StatusScene() {
        super();
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    public void loadResources() {
        long timeStart = System.currentTimeMillis();
        
        sScaleX = RoguelikeActivity.sScaleX;
        sScaleY = RoguelikeActivity.sScaleY;
        sScrollDetector = new SurfaceScrollDetector(this);
        
        // Cross panel icons
        Graphics.beginLoad("gfx/", TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT);
        
        sStatusBackgroundSprite = Graphics.createSprite("panels/status_panel.png");
        sWeaponsBackgroundSprite = Graphics.createSprite("panels/item_panel.png");
        sArmourBackgroundSprite = Graphics.createSprite("panels/item_panel.png");
        sSkillsBackgroundSprite = Graphics.createSprite("panels/skill_panel.png");
        sCurrentPanel = sStatusBackgroundSprite;
        
        sBackIcon = Graphics.createTiledSprite("icons.png", 4, 4, 
               sScaleX, sScaleY, HUD_OPACITY);
        sStatusIcon = Graphics.createTiledSprite("icons.png", 4, 4, 
               sScaleX, sScaleY, HUD_OPACITY);
        sStatusIcon.setPosition(mCameraWidth - (STATUS_ICON_X *sScaleX), sScaleY);
        sWeaponIcon = Graphics.createTiledSprite("icons.png", 4, 4, 
               sScaleX, sScaleY, HUD_OPACITY);
        sWeaponIcon.setPosition(mCameraWidth - (WEAPON_ICON_X *sScaleX), sScaleY);
        sArmourIcon = Graphics.createTiledSprite("icons.png", 4, 4, 
               sScaleX, sScaleY, HUD_OPACITY);
        sArmourIcon.setPosition(mCameraWidth - (ARMOUR_ICON_X *sScaleX), sScaleY);
        sSkillIcon = Graphics.createTiledSprite("icons.png", 4, 4, 
               sScaleX, sScaleY, HUD_OPACITY);
        sSkillIcon.setPosition(mCameraWidth - (SKILL_ICON_X *sScaleX), sScaleY);
        
        Graphics.endLoad("STATUS");
        
        sHud = new HUD();
        
        // Status panel
        sTitleText = Graphics.createChangeableText (32 *sScaleX, (float)TITLE_Y, Graphics.Font, "Status");
        sNameText = Graphics.createChangeableText(NAME_TEXT_X *sScaleX, NAME_TEXT_Y * sScaleY, Graphics.SmallFont, "XXXXXX");
        sLevelText= Graphics.createChangeableText(LEVEL_TEXT_X *sScaleX, LEVEL_TEXT_Y * sScaleY, Graphics.SmallFont, "Lvl 88");
        sHPText = Graphics.createChangeableText(HP_TEXT_X *sScaleX, HP_TEXT_Y * sScaleY, Graphics.SmallFont, "HP XXXX/XXXX");
        sXPText = Graphics.createChangeableText(XP_TEXT_X *sScaleX, XP_TEXT_Y * sScaleY, Graphics.SmallFont, "XP XXXX/XXXX");
        sAttackText = Graphics.createChangeableText(ATTACK_TEXT_X *sScaleX, ATTACK_TEXT_Y * sScaleY, Graphics.SmallFont, "888");
        sDefenseText = Graphics.createChangeableText(DEFENSE_TEXT_X *sScaleX, DEFENSE_TEXT_Y * sScaleY, Graphics.SmallFont, "888");
        sMagicText = Graphics.createChangeableText(MAGIC_TEXT_X *sScaleX, MAGIC_TEXT_Y * sScaleY, Graphics.SmallFont, "888");
        sPotionText = Graphics.createChangeableText(POTION_TEXT_X *sScaleX, MAGIC_TEXT_Y * sScaleY, Graphics.SmallFont, "x08");
        
        attachChild(sStatusBackgroundSprite);
        attachChild(sWeaponsBackgroundSprite);
        attachChild(sArmourBackgroundSprite);
        attachChild(sSkillsBackgroundSprite);
        attachChild(sTitleText);
        attachChild(sBackIcon);
        attachChild(sStatusIcon);
        attachChild(sWeaponIcon);
        attachChild(sArmourIcon);
        attachChild(sSkillIcon);
        
        sStatusBackgroundSprite.attachChild(sNameText);
        sStatusBackgroundSprite.attachChild(sLevelText);
        sStatusBackgroundSprite.attachChild(sHPText);
        sStatusBackgroundSprite.attachChild(sXPText);
        sStatusBackgroundSprite.attachChild(sAttackText);
        sStatusBackgroundSprite.attachChild(sDefenseText);
        sStatusBackgroundSprite.attachChild(sMagicText);
        sStatusBackgroundSprite.attachChild(sPotionText);
        
        // Weapon panel
        sWeaponsScrollList = new ScrollList(ITEM_SCROLL_X * sScaleX, ITEM_SCROLL_Y * sScaleY, ITEM_SCROLL_WIDTH * sScaleX, ITEM_SCROLL_HEIGHT * sScaleY,
                new ISelectListener() {
                    public void itemSelected(Item item) {
                        equip(item);
                        sWeaponsScrollList.setData(sPlayer.getWeaponList());
                        updatePlayerData();
                    }
                });
        
        sWeaponsBackgroundSprite.attachChild(sWeaponsScrollList.getSprite());
        
        // Armour panel
        sArmourScrollList = new ScrollList(ITEM_SCROLL_X * sScaleX, ITEM_SCROLL_Y * sScaleY, ITEM_SCROLL_WIDTH * sScaleX, ITEM_SCROLL_HEIGHT * sScaleY,
                new ISelectListener() {
                    public void itemSelected(Item item) {
                        equip(item);
                        sArmourScrollList.setData(sPlayer.getArmourList());
                        updatePlayerData();
                    }
                });
        sArmourBackgroundSprite.attachChild(sArmourScrollList.getSprite());
        
        mLoaded = true;
        
        Log.d("STATUS", "Load time: " + (System.currentTimeMillis() - timeStart));
    }

    public void prepare(IEntityModifierListener preparedListener) {        
        mCamera.setHUD(sHud);
        mCamera.setChaseEntity(null);
        mCamera.setCenter(mCameraWidth / 2,  mCameraHeight / 2);
        
        sPlayer = RoguelikeActivity.getPlayer();
        
        updatePlayerData();
        resetIcons();
        
        if (sPlayer.getWeaponList() != null) sWeaponsScrollList.setData(sPlayer.getWeaponList());
        if (sPlayer.getArmourList() != null) sArmourScrollList.setData(sPlayer.getArmourList());

        sStatusBackgroundSprite.setVisible(false);
        sWeaponsBackgroundSprite.setVisible(false);
        sArmourBackgroundSprite.setVisible(false);
        sSkillsBackgroundSprite.setVisible(false);
        sCurrentPanel.setVisible(true);
        mPrepared = true;
        preparedListener.onModifierFinished(null, this);
    }
    
    public void pause() {
        
    }
    
    @Override
    public void destroy() {
        
    }
    
    @Override
    public boolean onSceneTouchEvent(TouchEvent pTouchEvent) {
        if(pTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            sTouchX = pTouchEvent.getMotionEvent().getX();
            sTouchY = pTouchEvent.getMotionEvent().getY();   
            
            // Check navagation icons
            if (sBackIcon.contains(sTouchX, sTouchY)) {
                sBackIcon.setCurrentTileIndex(5);
                AudioManager.playClick();
                RoguelikeActivity.closeStatus();
                resetIcons();
            } else if (sStatusIcon.contains(sTouchX, sTouchY)) {
                AudioManager.playClick();
                showStatusPanel();
                sStatusIcon.setCurrentTileIndex(1);
            } else if (sWeaponIcon.contains(sTouchX, sTouchY)) {
                AudioManager.playClick();
                showWeaponsPanel();
                sWeaponIcon.setCurrentTileIndex(9);
            } else if (sArmourIcon.contains(sTouchX, sTouchY) ) {
                AudioManager.playClick();
                showArmourPanel();
            } else if (sSkillIcon.contains(sTouchX, sTouchY)) {
                AudioManager.playClick();
                showSkillsPanel();
            } 
        }
        else if(pTouchEvent.getAction() == MotionEvent.ACTION_MOVE)
        {    
            
        } else if (pTouchEvent.getAction() == MotionEvent.ACTION_UP) {
            resetIcons();
        }
        
        if (sCurrentPanel == sWeaponsBackgroundSprite) {
            sWeaponsScrollList.handleTouchEvent(pTouchEvent);
            sScrollDetector.onTouchEvent(pTouchEvent);
        } else if (sCurrentPanel == sArmourBackgroundSprite) {
            sArmourScrollList.handleTouchEvent(pTouchEvent);
            sScrollDetector.onTouchEvent(pTouchEvent);
        }
        
        return true;
    }
    
    public void onScroll(final ScrollDetector pScollDetector, final TouchEvent pTouchEvent, final float pDistanceX, final float pDistanceY) {
        if (sWeaponsBackgroundSprite.isVisible()) {
            sWeaponsScrollList.handleScrollEvent(pScollDetector, pTouchEvent, pDistanceX, pDistanceY);
        } else if (sArmourBackgroundSprite.isVisible()) {
            sArmourScrollList.handleScrollEvent(pScollDetector, pTouchEvent, pDistanceX, pDistanceY);
        }
    }
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    private void showStatusPanel() {
        sStatusIcon.setAlpha(1.0f);
        sCurrentPanel.setVisible(false);
        updatePlayerData();
        sStatusBackgroundSprite.setVisible(true);
        sCurrentPanel = sStatusBackgroundSprite;
        resetIcons();  
    }
    
    private void showWeaponsPanel() {
        sWeaponIcon.setAlpha(1.0f);
        sCurrentPanel.setVisible(false);
        updatePlayerData();
        sWeaponsBackgroundSprite.setVisible(true);
        sCurrentPanel = sWeaponsBackgroundSprite;
        resetIcons();
    }
    
    private void showArmourPanel() {
        sArmourIcon.setAlpha(1.0f);
        sCurrentPanel.setVisible(false);
        updatePlayerData();
        sArmourBackgroundSprite.setVisible(true);
        sCurrentPanel = sArmourBackgroundSprite;
        resetIcons();
    }
    
    private void showSkillsPanel() {
        sSkillIcon.setAlpha(1.0f);
        sCurrentPanel.setVisible(false);
        sSkillsBackgroundSprite.setVisible(true);
        sCurrentPanel = sSkillsBackgroundSprite;
        resetIcons();
        sSkillIcon.setCurrentTileIndex(13);
    }
    
    private void resetIcons() {
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {

            public void run() {
                sBackIcon.setCurrentTileIndex(4);
                sStatusIcon.setCurrentTileIndex(0);
                sStatusIcon.setAlpha(HUD_OPACITY);
                sWeaponIcon.setCurrentTileIndex(8);
                sWeaponIcon.setAlpha(HUD_OPACITY);
                sArmourIcon.setCurrentTileIndex(10);
                sArmourIcon.setAlpha(HUD_OPACITY);
                sSkillIcon.setCurrentTileIndex(12);
                sSkillIcon.setAlpha(HUD_OPACITY);
                
                //if (sWeaponSprite != null) sWeaponSprite.handleTouchUp();
                //if (sArmourItem != null) sArmourItem.handleTouchUp();
                
                if (sCurrentPanel == sStatusBackgroundSprite) {
                    sStatusIcon.setAlpha(1f);
                    sTitleText.setText("Status");
                } else if (sCurrentPanel == sWeaponsBackgroundSprite) {
                    sWeaponIcon.setAlpha(1f);
                    sTitleText.setText("Weapons");
                } else if (sCurrentPanel == sArmourBackgroundSprite) {
                    sArmourIcon.setAlpha(1f);
                    sTitleText.setText("Armour");
                } else if (sCurrentPanel == sSkillsBackgroundSprite) {
                    sSkillIcon.setAlpha(1f);
                    sTitleText.setText("Skills");
                }
                sTitleText.setPosition((TITLE_X *sScaleX) + ((TITLE_WIDTH *sScaleX / 2) - sTitleText.getWidth() / 2), 
                        TITLE_Y * sScaleY);
            }
            
        });
        
    }
    
    private void updatePlayerData() {
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {

            public void run() {
                // Status panel
                sNameText.setText(sPlayer.getName());
                sLevelText.setText("Level " + sPlayer.getLevel()); //String.format("%02d", ));
                sHPText.setText("HP " + String.format("%04d", sPlayer.getCurHP()) + "/" + String.format("%04d", sPlayer.getMaxHP()));
                sXPText.setText("XP "+ String.format("%04d", sPlayer.getCurXP()) + "/" + String.format("%04d", sPlayer.getNextXP()));
                sAttackText.setText("Attack " + String.format("%03d",  sPlayer.getTotalAttack()));
                sDefenseText.setText("Defense " + String.format("%03d", sPlayer.getTotalDefense()));
                sMagicText.setText("Magic " + String.format("%03d",  sPlayer.getTotalMagic()));
                sPotionText.setText("x" + String.format("%02d", sPlayer.getNumPotions()));
                
                if (sWeaponSprite != null) sWeaponSprite.detachSelf();
                sWeaponSprite = sPlayer.getWeapon().copySprite();
                sWeaponSprite.setPosition((int)(WEAPON_SPRITE_X *sScaleX), (int)(WEAPON_SPRITE_Y * sScaleY));
                sStatusBackgroundSprite.attachChild(sWeaponSprite);
                
                if (sArmourSprite != null) sArmourSprite.detachSelf();
                sArmourSprite = sPlayer.getArmour().copySprite();
                sArmourSprite.setPosition(ARMOUR_SPRITE_X *sScaleX, ARMOUR_SPRITE_Y * sScaleY);
                sStatusBackgroundSprite.attachChild(sArmourSprite);
                
                // Weapons panel    
                if (sEquippedWeaponSprite != null) sEquippedWeaponSprite.detachSelf();
                sEquippedWeaponSprite = sPlayer.getWeapon().copySprite();
                sEquippedWeaponSprite.setPosition((int)(EQUIPPED_ITEM_X *sScaleX), (int)(EQUIPPED_ITEM_Y * sScaleY));
                if (!sEquippedWeaponSprite.hasParent()) sWeaponsBackgroundSprite.attachChild(sEquippedWeaponSprite);
                
                // Armour panel    
                if (sEquippedArmourSprite != null) sEquippedArmourSprite.detachSelf();
                sEquippedArmourSprite = sPlayer.getArmour().copySprite();
                sEquippedArmourSprite.setPosition((int)(EQUIPPED_ITEM_X *sScaleX), (int)(EQUIPPED_ITEM_Y * sScaleY));
                if (!sEquippedArmourSprite.hasParent()) sArmourBackgroundSprite.attachChild(sEquippedArmourSprite);
            }});
    }
    
    private void equip(Item item) {
        if (item.getItemType() == Item.ITEM_TYPE_WEAPON) {
            sPlayer.equipWeapon(item);
        } if (item.getItemType() == Item.ITEM_TYPE_ARMOUR) {
            sPlayer.equipArmour(item);
        }
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
}
