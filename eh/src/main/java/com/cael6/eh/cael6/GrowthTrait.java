package com.cael6.eh.cael6;

import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Created by cael6 on 04/03/14.
 */
public class GrowthTrait extends CardTrait implements ITrait {
    private final int maxGrowth;
    private final int trigger;
    private int growthCount = 0;

    public GrowthTrait(int growth) {
        super(R.drawable.growth, R.layout.growth);
        this.trigger = ITrait.TRIGGER_START_TURN;
        this.maxGrowth = growth;
    }

    @Override
    public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
        if(growthCount < maxGrowth){
            card.health++;
            card.defense++;
            growthCount++;
            return true;
        }
        return false;
    }

    @Override
    public boolean checkTrigger(int trigger) {
        return this.trigger == trigger;
    }
}
