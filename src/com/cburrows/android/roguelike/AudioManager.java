package com.cburrows.android.roguelike;

import java.io.IOException;
import java.util.ArrayList;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.Entity;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class AudioManager {
    
    private static final float FADE_DURATION = 0.5f;
    
    private static ArrayList<MediaPlayer> sMediaPlayers = new ArrayList<MediaPlayer>();
    private static float sVolume = 0;
    
    private static MediaPlayer sClick;
    
    public static void initialize() {
        try {
            AssetFileDescriptor afd = RoguelikeActivity.getContext().getAssets().openFd("sfx/click.mp3");
            sClick = new MediaPlayer();
            sClick.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            sClick.prepare();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void pushMusic(String path) {
        pause();
        try {
                AssetFileDescriptor afd = RoguelikeActivity.getContext().getAssets().openFd(path);
                MediaPlayer m = new MediaPlayer();
                m.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                m.prepare();
                m.setLooping(true);
                sMediaPlayers.add(m);
            } 
        catch (IllegalArgumentException e) {    } 
        catch (IllegalStateException e) { } 
        catch (IOException e) { } 
    }
    
    public static void pushMusic(MediaPlayer mediaPlayer) {
        pause();
        try {
            if (RoguelikeActivity.sMusic) {
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true);
                sMediaPlayers.add(mediaPlayer);
                play();
            }
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void popMusic() {
        if (sMediaPlayers.size() > 0) {
            sMediaPlayers.get(sMediaPlayers.size()-1).stop();
            sMediaPlayers.remove(sMediaPlayers.size()-1);
            play();
        }
    }
    
    public static void play() {
        if (RoguelikeActivity.sMusic && sMediaPlayers.size() > 0) {
            sMediaPlayers.get(sMediaPlayers.size()-1).start();
            
           // Entity e = new Entity();
            //e.registerUpdateHandler(fadeIn);
        }
    }
    
    public static void pause() {
        if (sMediaPlayers.size() > 0) {
            sMediaPlayers.get(sMediaPlayers.size()-1).pause();
        }
    }
    
    public static void stop() {
        if (sMediaPlayers.size() > 0) {
            sMediaPlayers.get(sMediaPlayers.size()-1).stop();
        }
    }
    
    public static void playClick() {
        sClick.seekTo(0);
        sClick.start();
    }
    
    private static IUpdateHandler fadeIn = new IUpdateHandler() {

        public void onUpdate(float pSecondsElapsed) {
            sVolume += (pSecondsElapsed / FADE_DURATION);
            if (sVolume >= 1) {
                sVolume = 1;
            }
            sMediaPlayers.get(sMediaPlayers.size()-1).setVolume(sVolume, sVolume);
        }
        public void reset() {}
    };
    

}
