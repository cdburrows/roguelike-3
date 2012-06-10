package com.cdburrows.android.roguelike.base;

public interface IAsyncCallback {
    // ===========================================================
    // Methods
    // ===========================================================
 
    public abstract void workToDo();
 
    public abstract void onComplete();
 
}
