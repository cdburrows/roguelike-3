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

package com.cdburrows.android.roguelike.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.cdburrows.android.roguelike.item.Item;
import com.cdburrows.android.roguelike.item.ItemFactory;

public class PlayerInventory {

    // ===========================================================
    // Constants
    // ===========================================================
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private int mPotions;

    private Item mEquippedWeapon;

    private Item mEquippedArmour;

    private ArrayList<Item> mWeaponList;

    private ArrayList<Item> mArmourList;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    public PlayerInventory() {
        mPotions = 10;
        mWeaponList = new ArrayList<Item>();
        mArmourList = new ArrayList<Item>();

        for (int i = 0; i < 3; i++) {
            mWeaponList.add(ItemFactory.createRandomWeapon(5));
            mArmourList.add(ItemFactory.createRandomArmour(5));
        }
        sort();
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public int getNumPotions() { return mPotions; } 
    public void setNumPotions(int potions) { mPotions = potions; }    
    public void changePotions(int i) { mPotions += i; }
    
    public Item getEquippedWeapon() {
        return mEquippedWeapon;
    }
    
    public Item getEquippedArmour() {
        return mEquippedArmour;
    }
    
    public ArrayList<Item> getArmourList() {
        return mArmourList;
    }
    
    public ArrayList<Item> getWeaponList() {
        return mWeaponList;
    }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    private void sort() {
        sortWeapons();
        sortArmour();
    }
    
    /**
     * Sorts weapons in inventory
     */
    private void sortWeapons() {
        Collections.sort(mWeaponList, new Comparator<Item>() {
            public int compare(Item lhs, Item rhs) {
                if (lhs.getAttack() < rhs.getAttack())
                    return 1;
                if (lhs.getAttack() == rhs.getAttack())
                    return 0;
                return -1;
            }

        });
    }

    /**
     * Sorts armour in inventory
     */
    private void sortArmour() {
        Collections.sort(mArmourList, new Comparator<Item>() {
            public int compare(Item lhs, Item rhs) {
                if (lhs.getDefense() < rhs.getDefense())
                    return 1;
                if (lhs.getDefense() == rhs.getDefense())
                    return 0;
                return -1;
            }

        });
    }

    /**
     * 
     * 
     * @param item
     */
    public void unequip(Item item) {
        if (item.getItemType() == Item.ITEM_TYPE_WEAPON) {
            mWeaponList.add(item);
            sortWeapons();
        } else if (item.getItemType() == Item.ITEM_TYPE_ARMOUR) {
            mArmourList.add(item);
            sortArmour();
        }
    }

    /**
     * 
     * 
     * @param item
     */
    public void equipWeapon(Item item) {
        if (mEquippedWeapon != null)
            unequip(mEquippedWeapon);

        mWeaponList.remove(item);
        mEquippedWeapon = item;
    }

    /**
     * 
     * 
     * @param item
     */
    public void equipArmour(Item item) {
        if (mEquippedArmour != null)
            unequip(mEquippedArmour);

        mArmourList.remove(item);
        mEquippedArmour = item;
    }

    /**
     * 
     * 
     * @param item
     */
    public void addItem(Item item) {
        if (item.getItemType() == Item.ITEM_TYPE_WEAPON) {
            mWeaponList.add(item);
            sortWeapons();
        } else if (item.getItemType() == Item.ITEM_TYPE_ARMOUR) {
            mArmourList.add(item);
            sortArmour();
        }
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
}
