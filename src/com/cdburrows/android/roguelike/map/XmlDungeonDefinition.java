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

package com.cdburrows.android.roguelike.map;

import java.io.InputStream;
import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.cdburrows.android.roguelike.tmx.TmxTileset;

@Root(name = "dungeon_definition")
public class XmlDungeonDefinition {

    @ElementList(name = "tileset", inline = true)
    private ArrayList<XmlTileset> mTilesets;

    @ElementList(name = "floor", inline = true)
    private ArrayList<XmlFloor> mFloors;

    public XmlDungeonDefinition() {
        mTilesets = new ArrayList<XmlTileset>();
        mFloors = new ArrayList<XmlFloor>();
    }

    public static XmlDungeonDefinition inflate(InputStream source) {
        Serializer serializer = new Persister();
        try {
            XmlDungeonDefinition dungeon = serializer.read(XmlDungeonDefinition.class, source);

            return dungeon;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<XmlTileset> getDungeonTilesets() {
        return mTilesets;
    }

    public XmlTileset getDungeonTileset(XmlFloor floor) {
        String id = floor.mTilesetName;
        for (XmlTileset t : mTilesets) {
            if (t.mName.equals(id))
                return t;
        }
        return null;
    }

    public TmxTileset getTmxTileset(XmlFloor floor) {
        String id = floor.mTilesetName;
        for (XmlTileset t : mTilesets) {
            if (t.mName.equals(id))
                return t.toTmx();
        }
        return null;
    }

    public XmlFloor getFloor(int floor) {
        return mFloors.get(floor);
    }

    public int getMaxDepth() {
        return mFloors.size();
    }
}
