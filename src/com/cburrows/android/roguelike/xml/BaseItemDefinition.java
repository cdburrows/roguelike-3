package com.cburrows.android.roguelike.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "base_item")
public class BaseItemDefinition {
    
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
