package com.cael6.eh.cael6;

import java.util.ArrayList;

/**
 * Created by cael6 on 04/03/14.
 */
public class GenerateTrait extends CardTrait implements ITrait{
    int trigger;
    Energy energy;
    public GenerateTrait(Energy energy){
        super(energy.getImageResourceById(), energy.getLayoutResourceById());
        this.energy = energy;
        this.trigger = ITrait.TRIGGER_START_TURN;
    }

    @Override
    public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
        card.getOwner().generateElement(this.energy);
        return true;
    }

    @Override
    public boolean checkTrigger(int trigger) {
        return this.trigger == trigger;
    }
}
