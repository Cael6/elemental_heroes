package com.cael6.eh.cael6;

import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Created by cael6 on 04/03/14.
 */
public class DrawDragonBreathTrait extends CardTrait implements ITrait{
    int trigger;
    int dragonBreathDrawing;
    public DrawDragonBreathTrait(int dragonBreathDrawing){
        super(R.drawable.fire, R.layout.draw_dragon_breath_trait);
        this.dragonBreathDrawing = dragonBreathDrawing;
        this.trigger = ITrait.TRIGGER_START_TURN;
    }

    @Override
    public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
        card.getOwner().drawDragonBreath(dragonBreathDrawing);
        return true;
    }

    @Override
    public boolean checkTrigger(int trigger) {
        return this.trigger == trigger;
    }
}
