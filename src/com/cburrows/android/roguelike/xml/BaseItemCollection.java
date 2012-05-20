package com.cburrows.android.roguelike.xml;

import java.io.InputStream;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;


import android.util.Log;

@Root
public class BaseItemCollection {

    @Attribute
    public String image_path;
    
    @ElementList(inline = true)
    public List<BaseItemDefinition> base_item;
    
    public static BaseItemCollection inflate(InputStream stream) {
        Serializer serializer = new Persister();

        try {
            BaseItemCollection col = serializer.read(BaseItemCollection.class, stream);
                        
            for (BaseItemDefinition b : col.base_item) {
                Log.d("ITEM", b.name + ", " + b.index + ", " + b.attack);
            }
            
            
            return col;
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        return null;  
    }
    
}
