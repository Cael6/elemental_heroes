package com.cael6.eh.cael6;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cael6.eh.R;

/**
 * Created by cael6 on 23/02/14.
 */
public class HeroCard extends CharacterCard {

    public int dragonBreathDrawing;

    private int maxTurns = 2;

    public HeroCard(Context context, AttributeSet set){
        super(context, set);
    }

    public HeroCard(Context context, HeroCard card, Player owner){
        super(context, card, owner);
        init();
        this.dragonBreathDrawing = card.dragonBreathDrawing;
        this.setTag(card.getTag());
    }

    @Override
    protected void init(){
        super.init();
        turns = maxTurns;
    }

    protected void generateFromStyle(Context context, AttributeSet set, int style) {
        super.generateFromStyle(context, set, style);
        TypedArray ta = context.obtainStyledAttributes(set,
                R.styleable.Card,
                style,
                0);

        final int taCount;
        if (ta != null) {
            taCount = ta.getIndexCount();
            for (int i = 0; i < taCount; i++) {
                int attr = ta.getIndex(i);
                switch (attr) {
                    case R.styleable.Card_dragonBreathGen:
                        dragonBreathDrawing = ta.getInt(attr, -1);
                        break;
                }
            }
        }
    }

    @Override
    public void setListeners(){
        setOnDragListener(new CharacterCard.CharacterDragListener());
    }

    public void resetActions(){
        turns = maxTurns;
        invalidate();
    }

    @Override
    public void setCardChildrenValues() {
        super.setCardChildrenValues();
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
