package com.cael6.eh.cael6;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by cael6 on 24/03/14.
 */
public class SpellCard extends Card{

    public int dragonBreathCost;

    public SpellCard(Context context) {
        super(context);
    }

    public SpellCard(Context context, AttributeSet set) {
        super(context, set);
    }

    public SpellCard(Context context, int cardStyle) {
        super(context, cardStyle);
    }

    public SpellCard(Context context, Card card, Player owner) {
        super(context, card, owner);
    }
}
