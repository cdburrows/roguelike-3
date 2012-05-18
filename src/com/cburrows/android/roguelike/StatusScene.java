package com.cburrows.android.roguelike;

import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.HorizontalAlign;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.MotionEvent;

public class StatusScene extends GameScene {
    private static final int TEXTURE_ATLAS_WIDTH = 512;
    private static final int TEXTURE_ATLAS_HEIGHT = 256;
    private static final int TITLE_Y = 8;
    private static final int FONT_LARGE_SIZE = 20;
    private static final int FONT_SIZE = 16;
    
    private static final float ATTACK_TEXT_X = 56;
    private static final float ATTACK_TEXT_Y = 121;
    private static final float DEFENSE_TEXT_X = 56;
    private static final float DEFENSE_TEXT_Y = 160;
    private static final float MAGIC_TEXT_X = 56;
    private static final float MAGIC_TEXT_Y = 199;
    private static final float POTION_TEXT_X = 253;
    private static final float POTIOn_TEXT_Y = 199;

    private HUD mHud;
    
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion mBackgroundTextureRegion;
    private Sprite mBackgroundSprite;
    private BitmapTextureAtlas mFontTexture;
    private BitmapTextureAtlas mFontLargeTexture;
    
    private Font mFont;
    private Font mLargeFont;
    
    private Text mStatusText;
    private Text mAttackText;
    private Text mDefenseText;
    private Text mMagicText;
    private Text mPotionText;
    
    private float mTouchX;
    private float mTouchY;
    private float mTouchOffsetX;
    private float mTouchOffsetY;
    private float mTotalTouchOffsetX;
    private float mTotalTouchOffsetY;
        
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
        
        mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mFontLargeTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        mContext.getTextureManager().loadTextures(mBitmapTextureAtlas, mFontTexture, mFontLargeTexture);
        
        mHud = new HUD();
        
        Typeface t = Typeface.createFromAsset(mContext.getAssets(), "fonts/prstart.ttf");
        
        mFont = FontFactory.create( mFontTexture, t, FONT_SIZE * mContext.getGameScaleX(), true, Color.WHITE);
        mLargeFont = FontFactory.create( mFontLargeTexture, t, FONT_LARGE_SIZE * mContext.getGameScaleX(), true, Color.WHITE);
        mContext.getEngine().getFontManager().loadFonts(mFont, mLargeFont);
        
        mStatusText = new Text (0, 0, mLargeFont, "Status");
        mStatusText.setPosition((mCameraWidth / 2) - (mStatusText.getWidth() / 2), TITLE_Y * mContext.getGameScaleY());
        mAttackText = new Text(ATTACK_TEXT_X * mContext.getGameScaleX(), ATTACK_TEXT_Y * mContext.getGameScaleY(), mFont, "888");
        mDefenseText = new Text(DEFENSE_TEXT_X * mContext.getGameScaleX(), DEFENSE_TEXT_Y * mContext.getGameScaleY(), mFont, "888");
        mMagicText = new Text(MAGIC_TEXT_X * mContext.getGameScaleX(), MAGIC_TEXT_Y * mContext.getGameScaleY(), mFont, "888");
        mPotionText = new Text(POTION_TEXT_X * mContext.getGameScaleX(), MAGIC_TEXT_Y * mContext.getGameScaleY(), mFont, "x8");
        
        attachChild(mBackgroundSprite);
        attachChild(mStatusText);
        attachChild(mAttackText);
        attachChild(mDefenseText);
        attachChild(mMagicText);
        attachChild(mPotionText);
        
        mLoaded = true;
        
        Log.d("STATUS", "Load time: " + (System.currentTimeMillis() - timeStart));
    }

    public void initialize() {        
        mCamera.setHUD(mHud);
        mCamera.setChaseEntity(null);
        mCamera.setCenter(mCameraWidth / 2,  mCameraHeight / 2);
        
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
