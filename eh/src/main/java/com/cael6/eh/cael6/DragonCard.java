package com.cael6.eh.cael6;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cael6.eh.GameActivity;

/**
 * Class that represents a dragon card.
 */
public class DragonCard extends CharacterCard {

    private int hatchCost;

    public DragonCard(Context context, AttributeSet set){
        super(context, set);
        init();
    }

    public DragonCard(Context context, int cardStyle){
        super(context, cardStyle);
        init();
    }

    public DragonCard(Context context, DragonCard card, Player owner) {
        super(context, card, owner);
        setOwner(owner);
        init();
        this.attack = card.attack;
        this.defense = card.defense;
        this.health = card.health;
        this.turns = card.turns;
        this.traits = card.traits;
    }


    public void init(){

    }


    public int getHatchCost() {
        return hatchCost;
    }

    /**
     * this card has been killed;
     */
    public void killed() {
        loopTraits(null, ITrait.TRIGGER_BEFORE_KILLED);
        ((GameActivity)getContext()).setBackground((ViewGroup) getParent(), ((GameActivity) getContext()).creatureZoneNormalShape);
        destroyCard();
        loopTraits(null, ITrait.TRIGGER_AFTER_KILLED);
    }

    protected void setCardChildrenValues(){
        super.setCardChildrenValues();
        switch(cardSize){
            case LARGE_CARD:
                LinearLayout traitLL = (LinearLayout)this.findViewWithTag("traitLL");
                for(ITrait trait : traits){
                    LayoutInflater inflater = ((GameActivity) getContext()).getLayoutInflater();
                    RelativeLayout traitLayout = (RelativeLayout)inflater.inflate(trait.getLayoutResource(), traitLL, false);
                    traitLL.addView(traitLayout);
                }
                break;
        }

        TextView attackText = (TextView)this.findViewWithTag("attack");
        attackText.setText(String.valueOf(attack));
    }
}
