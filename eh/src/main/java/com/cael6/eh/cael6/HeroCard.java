package com.cael6.eh.cael6;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Created by cael6 on 23/02/14.
 */
public class HeroCard extends CharacterCard {

    public int dragonBreathDrawing;

    private int maxTurns = 2;

    public HeroCard(Context context, AttributeSet set){
        super(context, set);
        init();
    }

    public HeroCard(Context context, HeroCard card, Player owner){
        super(context, card, owner);
        init();
        this.dragonBreathDrawing = card.dragonBreathDrawing;
    }

    @Override
    protected void init(){
        turns = maxTurns;
    }

    public void resetActions(){
        turns = maxTurns;
        invalidate();
    }

    @Override
    protected void setCardChildrenValues() {
        ((ImageView) this.findViewWithTag("cardImage")).setImageDrawable(image);
    }

    @Override
    protected void generateCardForView(ViewGroup expectedParent) {
        Context context = getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layoutResource = 0;
        switch (cardSize) {
            case SMALL_CARD:
                layoutResource = R.layout.small_hero_card;
                break;
            case LARGE_CARD:
                layoutResource = R.layout.hero_card;
        }
        Card cardLayout = (Card) inflater.inflate(layoutResource, expectedParent, false);

        this.setLayoutParams(cardLayout.getLayoutParams());
        while (0 < cardLayout.getChildCount()) {
            View cardChild = cardLayout.getChildAt(0);
            if (cardChild != null) {
                ((ViewGroup) cardChild.getParent()).removeView(cardChild);
                this.addView(cardChild);
            }
        }
        setCardChildrenValues();
    }
}
