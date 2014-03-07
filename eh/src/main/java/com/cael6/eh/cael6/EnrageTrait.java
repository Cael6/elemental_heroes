package com.cael6.eh.cael6;

import android.widget.TextView;

import com.cael6.eh.R;

import java.util.ArrayList;

public class EnrageTrait extends CardTrait implements ITrait{

    private int trigger;

    public EnrageTrait(){
        super(R.drawable.enrage, R.layout.enrage);
        trigger = ITrait.TRIGGER_KILL;
    }

    @Override
    public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger){
        card.attack++;
        TextView attackTV = (TextView)card.findViewWithTag("attack");
        attackTV.setText(String.valueOf(card.attack));
        attackTV.setTextColor(0xFF22CC22);
        return true;
    }

    @Override
    public boolean checkTrigger(int trigger) {
        return this.trigger == trigger;
    }
}
