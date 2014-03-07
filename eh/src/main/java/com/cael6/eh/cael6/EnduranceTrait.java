package com.cael6.eh.cael6;

import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Created by cael6 on 04/03/14.
 */
public class EnduranceTrait extends CardTrait implements ITrait{

    private final int trigger;

    public EnduranceTrait(){
        super(R.drawable.endurance, R.layout.endurance);
        this.trigger = ITrait.TRIGGER_AFTER_ATTACK;
    }

    @Override
    public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
        //TODO: make card defender
        return false;
    }

    @Override
    public boolean checkTrigger(int trigger) {
        return this.trigger == trigger;
    }
}
