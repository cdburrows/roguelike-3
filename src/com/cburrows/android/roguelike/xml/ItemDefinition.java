package com.cburrows.android.roguelike.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item_definition")
public class ItemDefinition {
    
    @Attribute
    public String name;
    
    @Attribute
    public int index;
    
    @Element
    public int attack;
    
    @Element
    public int defense;
    
    @Element
    public int magic;
}
