package com.cael6.eh.cael6;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by cael6 on 24/03/14.
 */
public class EggCard extends Card{
    private int hatchTimer;

    public EggCard(Context context) {
        super(context);
    }

    public EggCard(Context context, AttributeSet set) {
        super(context, set);
    }

    public EggCard(Context context, int cardStyle) {
        super(context, cardStyle);
    }

    public EggCard(Context context, Card card, Player owner) {
        super(context, card, owner);
    }

    public int getHatchTimer() {
        return hatchTimer;
    }
}
