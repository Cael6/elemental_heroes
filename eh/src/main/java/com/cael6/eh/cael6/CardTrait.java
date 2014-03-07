package com.cael6.eh.cael6;

import android.graphics.drawable.Drawable;

/**
 * Created by cael6 on 04/03/14.
 */
public class CardTrait {
    private final int imageResource;
    private final int layoutResource;

    public CardTrait(int imageResource, int layoutResource){
        this.imageResource = imageResource;
        this.layoutResource = layoutResource;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getLayoutResource() { return layoutResource;}
}
