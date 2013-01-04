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

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

@Root(name = "map")
public class XmlMap {
    @Attribute(name = "room_rows")
    int mRoomRows;

    @Attribute(name = "room_cols")
    int mRoomCols;

    @ElementArray(name = "rooms")
    Room[][] mRooms;

    public XmlMap(Room[][] rooms) {
        mRoomRows = rooms.length;
        mRoomCols = rooms[0].length;
        mRooms = rooms;
        /*
         * new Room[rooms.length * rooms[0].length]; int k = 0; for (int i = 0;
         * i < rooms.length; i++) for (int j = 0; j < rooms[0].length; j++) {
         * mRooms[k++] = rooms[i][j]; }
         */
    }
    /*
     * public static Room[][] inflate(InputStream source) { Serializer
     * serializer = new Persister(); try { XmlMap map =
     * serializer.read(XmlMap.class, source); Room[][] rooms = new
     * Room[map.mRoomRows][map.mRoomCols]; int k = 0; for (int y = 0; y <
     * map.mRoomRows; y++) for (int x = 0; x < map.mRoomCols; x++) { rooms[y][x]
     * = map.mRooms[y * map.mRoomCols + x]; Log.d("XMLMAP", "room state: " +
     * map.mRooms[y * map.mRoomCols + x].getRoomState()); } return rooms; }
     * catch (Exception e) { e.printStackTrace(); } return null; } public static
     * void deflate(Room[][] rooms, OutputStream dest) { Serializer serializer =
     * new Persister(); try { XmlMap map = new XmlMap(rooms); PrintWriter writer
     * = new PrintWriter(dest);
     * writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
     * writer.flush(); serializer.write(map, dest); writer.close();
     * dest.close(); Log.d("XmlMAP", "DEFLATED"); } catch (Exception e) {
     * e.printStackTrace(); } }
     */
}
