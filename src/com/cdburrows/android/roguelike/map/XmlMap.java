package com.cdburrows.android.roguelike.map;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

@Root(name="map")
public class XmlMap {
    @Attribute(name="room_rows")
    int mRoomRows;
    
    @Attribute(name="room_cols")
    int mRoomCols;
    
    @ElementArray(name="rooms")
    Room[][] mRooms;

    public XmlMap(Room[][] rooms) {
        mRoomRows = rooms.length;
        mRoomCols = rooms[0].length;
        mRooms = rooms;
                /*new Room[rooms.length * rooms[0].length];
        int k = 0;
        for (int i = 0; i < rooms.length; i++)
            for (int j = 0; j < rooms[0].length; j++) {
                mRooms[k++] = rooms[i][j];
            }
            */
    }
    /*
    public static Room[][] inflate(InputStream source) {
        Serializer serializer = new Persister();
        try {
            XmlMap map = serializer.read(XmlMap.class, source);
            
            Room[][] rooms = new Room[map.mRoomRows][map.mRoomCols];
            int k = 0;
            for (int y = 0; y < map.mRoomRows; y++)
                for (int x = 0; x < map.mRoomCols; x++) {
                    rooms[y][x] = map.mRooms[y * map.mRoomCols + x];
                    Log.d("XMLMAP", "room state: " + map.mRooms[y * map.mRoomCols + x].getRoomState());
                }
            
            return rooms;
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        return null;        
    }
    
    public static void deflate(Room[][] rooms, OutputStream dest) {
        Serializer serializer = new Persister();
        try {
            XmlMap map = new XmlMap(rooms);
            PrintWriter writer = new PrintWriter(dest);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.flush();
            serializer.write(map, dest);
            writer.close();
            dest.close();
            Log.d("XmlMAP", "DEFLATED");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
}

