/*
 * Copyright (c) 2012-2013, Christopher Burrows
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cdburrows.android.roguelike.audio;

import java.io.IOException;
import java.util.ArrayList;

import org.anddev.andengine.engine.handler.IUpdateHandler;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.cdburrows.android.roguelike.RoguelikeActivity;

public class Audio {

    // TODO: Add enabled here

    private static final float FADE_DURATION = 0.5f;

    private static ArrayList<MediaPlayer> sMediaPlayers;

    private static float sVolume = 0;

    private static MediaPlayer sClick;

    public static void initialize() {
        if (RoguelikeActivity.sSoundEnabled) {
            sMediaPlayers = new ArrayList<MediaPlayer>();

            try {
                AssetFileDescriptor afd = RoguelikeActivity.getContext().getAssets()
                        .openFd("sfx/click.mp3");
                sClick = new MediaPlayer();
                sClick.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                sClick.prepare();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void pushMusic(String path) {
        pause();
        if (RoguelikeActivity.sMusicEnabled) {
            try {
                AssetFileDescriptor afd = RoguelikeActivity.getContext().getAssets().openFd(path);
                MediaPlayer m = new MediaPlayer();
                m.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                m.prepare();
                m.setLooping(true);
                sMediaPlayers.add(m);
            } catch (IllegalArgumentException e) {
            } catch (IllegalStateException e) {
            } catch (IOException e) {
            }
        }
    }

    public static void pushMusic(MediaPlayer mediaPlayer) {
        pause();
        if (RoguelikeActivity.sMusicEnabled) {
            try {
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true);
                sMediaPlayers.add(mediaPlayer);
                play();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void popMusic() {
        if (RoguelikeActivity.sMusicEnabled && sMediaPlayers.size() > 0) {
            sMediaPlayers.get(sMediaPlayers.size() - 1).stop();
            sMediaPlayers.remove(sMediaPlayers.size() - 1);
            play();
        }
    }

    public static void play() {
        if (RoguelikeActivity.sMusicEnabled && sMediaPlayers.size() > 0) {
            sMediaPlayers.get(sMediaPlayers.size() - 1).start();

            // Entity e = new Entity();
            // e.registerUpdateHandler(fadeIn);
        }
    }

    public static void pause() {
        if (sMediaPlayers == null)
            return;

        if (sMediaPlayers.size() > 0) {
            sMediaPlayers.get(sMediaPlayers.size() - 1).pause();
        }
    }

    public static void stop() {
        if (sMediaPlayers == null)
            return;

        if (sMediaPlayers.size() > 0) {
            sMediaPlayers.get(sMediaPlayers.size() - 1).stop();
        }
    }

    public static void playClick() {
        if (RoguelikeActivity.sSoundEnabled) {
            sClick.seekTo(0);
            sClick.start();
        }
    }

    private static IUpdateHandler fadeIn = new IUpdateHandler() {

        public void onUpdate(float pSecondsElapsed) {
            sVolume += (pSecondsElapsed / FADE_DURATION);
            if (sVolume >= 1) {
                sVolume = 1;
            }
            sMediaPlayers.get(sMediaPlayers.size() - 1).setVolume(sVolume, sVolume);
        }

        public void reset() {
        }
    };

    public static void end() {
        sMediaPlayers = null;
        sClick = null;
    }

    public static MediaPlayer createAudio(String path) {
        AssetFileDescriptor afd;
        try {
            afd = RoguelikeActivity.getContext().getAssets()
                    .openFd(path);
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                    afd.getLength());
            return mp;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

    public static MediaPlayer prepareAudio(String path) {
        try {
            MediaPlayer mp = createAudio(path);
            mp.prepare();
            return mp;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

}
