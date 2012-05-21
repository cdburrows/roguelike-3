package com.cburrows.android.roguelike;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.HorizontalAlign;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.MotionEvent;

public class StatusScene extends GameScene {
    private static final float EQUIPMENT_WIDTH = 184;
    private static final float EQUIPMENT_HEIGHT = 32;
    
    private static final int TEXTURE_ATLAS_WIDTH = 512;
    private static final int TEXTURE_ATLAS_HEIGHT = 512;
    private static final int HUD_IMAGE_X = 0;
    private static final int HUD_IMAGE_Y = 320;
    private static final int TITLE_Y = 10;
        
    private static final int NAME_TEXT_X = 16;
    private static final int NAME_TEXT_Y = 41;
    private static final int LEVEL_TEXT_X = 16;
    private static final int LEVEL_TEXT_Y = 61;
    private static final int HP_TEXT_X = 121;
    private static final int HP_TEXT_Y  = 41;
    private static final int XP_TEXT_X = 121;
    private static final int XP_TEXT_Y = 61;
    private static final int ATTACK_TEXT_X = 28;
    private static final int ATTACK_TEXT_Y = 90;
    private static final int DEFENSE_TEXT_X = 28;
    private static final int DEFENSE_TEXT_Y = 116;
    private static final int MAGIC_TEXT_X = 28;
    private static final int MAGIC_TEXT_Y = 142;
    private static final int POTION_TEXT_X = 253;
    private static final int POTION_TEXT_Y = 199;
    private static final float WEAPON_SPRITE_X = 4;
    private static final float WEAPON_SPRITE_Y = 166;
    private static final float ARMOUR_SPRITE_X = 4;
    private static final float ARMOUR_SPRITE_Y = 202;

    private HUD mHud;
    
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion mBackgroundTextureRegion;
    private Sprite mBackgroundSprite;
    
    private Item mWeaponSprite;
    private Item mArmourSprite;
    
    private TiledTextureRegion mBackIconTextureRegion;
    private AnimatedSprite mBackIcon;
    
    private ChangeableText mStatusText;
    private ChangeableText mNameText;
    private ChangeableText mLevelText;
    private ChangeableText mHPText;
    private ChangeableText mXPText;
    private ChangeableText mAttackText;
    private ChangeableText mDefenseText;
    private ChangeableText mMagicText;
    private ChangeableText mPotionText;
    
    private float mTouchX;
    private float mTouchY;
    private float mTouchOffsetX;
    private float mTouchOffsetY;
    private float mTotalTouchOffsetX;
    private float mTotalTouchOffsetY;
    private Object mIconTextureRegion;
            
    public StatusScene(RoguelikeActivity context) {
        super(context);
    }
    
    public void loadResources() {
        long timeStart = System.currentTimeMillis();
        
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        mBitmapTextureAtlas = new BitmapTextureAtlas(TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        
        mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, mContext, "panels/status.png", 0, 0);
        mBackgroundSprite = new Sprite(0, 0, mCameraWidth, mCameraHeight, mBackgroundTextureRegion);

        mContext.getTextureManager().loadTexture(mBitmapTextureAtlas);
        
        mHud = new HUD();
        
        mStatusText = new ChangeableText (0, 0, mContext.Font, "Status");
        mStatusText.setPosition((mCameraWidth / 2) - (mStatusText.getWidth() / 2), TITLE_Y * mContext.getGameScaleY());
        
        mBackIconTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(mBitmapTextureAtlas, mContext, "icons.png", HUD_IMAGE_X, HUD_IMAGE_Y, 2, 4);
        mBackIcon = new AnimatedSprite (mContext.getGameScaleX(), mContext.getGameScaleY(), 32 * mContext.getGameScaleX(), 32 * mContext.getGameScaleY(), mBackIconTextureRegion);
        mBackIcon.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mBackIcon.setAlpha(0.50f);
        
        /*
        mWeaponSprite = new Sprite(WEAPON_SPRITE_X* mContext.getGameScaleX(), WEAPON_SPRITE_Y * mContext.getGameScaleY(), EQUIPMENT_WIDTH * mContext.getGameScaleX(), 
                EQUIPMENT_HEIGHT * mContext.getGameScaleY(), mContext.getEquipmentBackgroundTextureRegion());
        mArmourSprite = new Sprite(ARMOUR_SPRITE_X* mContext.getGameScaleX(), ARMOUR_SPRITE_Y * mContext.getGameScaleY(), EQUIPMENT_WIDTH * mContext.getGameScaleX(), 
                EQUIPMENT_HEIGHT * mContext.getGameScaleY(),  mContext.getEquipmentBackgroundTextureRegion());
                */
        //mWeaponSprite = new Item(mContext, "Weapon", 0);
        //mArmourSprite = new Item(mContext, "Armour", 1);
        
        mNameText = new ChangeableText(NAME_TEXT_X * mContext.getGameScaleX(), NAME_TEXT_Y * mContext.getGameScaleY(), mContext.SmallFont, "XXXXXX");
        mLevelText= new ChangeableText(LEVEL_TEXT_X * mContext.getGameScaleX(), LEVEL_TEXT_Y * mContext.getGameScaleY(), mContext.SmallFont, "Lvl 88");
        mHPText = new ChangeableText(HP_TEXT_X * mContext.getGameScaleX(), HP_TEXT_Y * mContext.getGameScaleY(), mContext.SmallFont, "HP XXXX/XXXX");
        mXPText = new ChangeableText(XP_TEXT_X * mContext.getGameScaleX(), XP_TEXT_Y * mContext.getGameScaleY(), mContext.SmallFont, "XP XXXX/XXXX");
        mAttackText = new ChangeableText(ATTACK_TEXT_X * mContext.getGameScaleX(), ATTACK_TEXT_Y * mContext.getGameScaleY(), mContext.SmallFont, "888");
        mDefenseText = new ChangeableText(DEFENSE_TEXT_X * mContext.getGameScaleX(), DEFENSE_TEXT_Y * mContext.getGameScaleY(), mContext.SmallFont, "888");
        mMagicText = new ChangeableText(MAGIC_TEXT_X * mContext.getGameScaleX(), MAGIC_TEXT_Y * mContext.getGameScaleY(), mContext.SmallFont, "888");
        mPotionText = new ChangeableText(POTION_TEXT_X * mContext.getGameScaleX(), MAGIC_TEXT_Y * mContext.getGameScaleY(), mContext.SmallFont, "x08");
        
        attachChild(mBackgroundSprite);
        attachChild(mBackIcon);
        attachChild(mNameText);
        attachChild(mLevelText);
        attachChild(mHPText);
        attachChild(mXPText);
        attachChild(mStatusText);
        attachChild(mAttackText);
        attachChild(mDefenseText);
        attachChild(mMagicText);
        attachChild(mPotionText);
        
        /*
        attachChild(new Sprite(WEAPON_SPRITE_X* mContext.getGameScaleX(), WEAPON_SPRITE_Y * mContext.getGameScaleY(), EQUIPMENT_WIDTH * mContext.getGameScaleX(), 
                EQUIPMENT_HEIGHT * mContext.getGameScaleY(), mContext.getEquipmentBackgroundSprite()));
        attachChild(mArmourSprite.getSprite());
        */
        
        mLoaded = true;
        
        Log.d("STATUS", "Load time: " + (System.currentTimeMillis() - timeStart));
    }

    public void initialize() {        
        mCamera.setHUD(mHud);
        mCamera.setChaseEntity(null);
        mCamera.setCenter(mCameraWidth / 2,  mCameraHeight / 2);
        
        mBackIcon.setCurrentTileIndex(4);
        
        Player player = mContext.getPlayer();
        mNameText.setText(player.getName());
        mLevelText.setText("Lvl " + String.format("%02d", player.getLevel()));
        mHPText.setText("HP " + String.format("%04d", player.getCurHP()) + "/" + String.format("%04d", player.getMaxHP()));
        mXPText.setText("XP "+ String.format("%04d", player.getCurXP()) + "/" + String.format("%04d", player.getNextXP()));
        mAttackText.setText(String.format("%03d", player.getTotalAttack()));
        mDefenseText.setText(String.format("%03d", player.getTotalDefense()));
        mMagicText.setText(String.format("%03d", player.getTotalMagic()));
        mPotionText.setText("x" + String.format("%02d", player.getNumPotions()));
        
        mWeaponSprite = player.getWeapon();
        mWeaponSprite.setPosition((int)(WEAPON_SPRITE_X * mContext.getGameScaleX()), (int)(WEAPON_SPRITE_Y * mContext.getGameScaleY()));
        if (!mWeaponSprite.getSprite().hasParent()) attachChild(mWeaponSprite.getSprite());
        
        mArmourSprite = player.getArmour();
        mArmourSprite.getSprite().setPosition(ARMOUR_SPRITE_X * mContext.getGameScaleX(), ARMOUR_SPRITE_Y * mContext.getGameScaleY());
        if (!mArmourSprite.getSprite().hasParent()) attachChild(mArmourSprite.getSprite());
        
        mInitialized = true;
    }
    
    public void pause() {
        
    }
    
    public boolean onSceneTouchEvent(TouchEvent pTouchEvent) {
        if(pTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            mTouchX = pTouchEvent.getMotionEvent().getX();
            mTouchY = pTouchEvent.getMotionEvent().getY();
            mTotalTouchOffsetX = 0;
            mTotalTouchOffsetY = 0;     
            
            if (mBackIcon.contains(mTouchX, mTouchY)) {
                mBackIcon.setCurrentTileIndex(5);
                mContext.closeStatus();
            }
        }
        else if(pTouchEvent.getAction() == MotionEvent.ACTION_MOVE)
        {    
            float newX = pTouchEvent.getMotionEvent().getX();
            float newY = pTouchEvent.getMotionEvent().getY();
           
            mTouchOffsetX = (newX - mTouchX);
            mTouchOffsetY = (newY - mTouchY);
            mTotalTouchOffsetX += mTouchOffsetX;
            mTotalTouchOffsetY += mTouchOffsetY;     
            
            //mCamera.setCenter(mCamera.getCenterX() - mTouchOffsetX, mCamera.getCenterY());
            
        } else if (pTouchEvent.getAction() == MotionEvent.ACTION_UP) {
            float mTouchUpX = pTouchEvent.getMotionEvent().getX();
            float mTouchUpY = pTouchEvent.getMotionEvent().getY();
        }
        return true;
    }
    
}
