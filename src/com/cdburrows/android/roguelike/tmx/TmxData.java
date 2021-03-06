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

package com.cdburrows.android.roguelike.tmx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.anddev.andengine.util.Base64;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

import android.util.Log;

public class TmxData {

    @SuppressWarnings("unused")
    @Attribute(required = false)
    private String encoding;

    @SuppressWarnings("unused")
    @Attribute(required = false)
    private String compression;

    @SuppressWarnings("unused")
    @Text
    private String value;

    public TmxData() {
    }

    public TmxData(int width, int height) {
        encoding = "base64";
        compression = "gzip";
    }

    public void setValue(int[] data) {
        byte[] array = new byte[data.length * 4];
        for (int i = 0; i < data.length; i++) {
            array[i * 4] = (byte)(data[i]);
            array[i * 4 + 1] = (byte)(data[i] >> 8);
            array[i * 4 + 2] = (byte)(data[i] >> 16);
            array[i * 4 + 3] = (byte)(data[i] >> 24);
        }

        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            GZIPOutputStream gzin = new GZIPOutputStream(bytesOut);
            gzin.write(array);
            gzin.finish();
            bytesOut.close();

            byte[] buffer = bytesOut.toByteArray();
            gzin.close();

            Log.d("MAP", "Encoding: " + Base64.encodeToString(buffer, Base64.DEFAULT));
            value = Base64.encodeToString(buffer, Base64.DEFAULT);

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void setValue(int[][] data) {
        Log.d("TMXDATA", "Flattening");
        int[] tmp = new int[data.length * data[0].length];
        int k = 0;
        for (int i = 0; i < data.length; i++) { // y
            for (int j = 0; j < data[0].length; j++) { // x
                tmp[k++] = data[i][j];
            }
        }
        setValue(tmp);
    }

}
