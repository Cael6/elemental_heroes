package com.cael6.eh.cael6;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cael6.eh.GameActivity;
import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Class that represents a dragon card.
 */
public class DragonCard extends CharacterCard{

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
        init();
        this.hatchCost = card.getHatchCost();
    }
    @Override
    protected void generateFromStyle(Context context, AttributeSet set, int style) {
        super.generateFromStyle(context, set, style);
        TypedArray ta = context.obtainStyledAttributes(set,
                R.styleable.Card,
                style,
                0);

        traits = new ArrayList<ITrait>();

        final int taCount;
        if (ta != null) {
            taCount = ta.getIndexCount();
            for (int i = 0; i < taCount; i++) {
                int attr = ta.getIndex(i);
                switch (attr) {
                    case R.styleable.Card_hatchCost:
                        hatchCost = ta.getInt(attr, -1);
                        break;
                }
            }

        }
    }

    public void init(){

    }

    public void enterBattleField() {
        loopTraits(null, ITrait.TRIGGER_DRAGON_ENTER_BATTLEFIELD);
        inHand = false;
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

    public void setCardChildrenValues(){
        super.setCardChildrenValues();
        LayoutInflater inflater = ((GameActivity) getContext()).getLayoutInflater();
        LinearLayout costLL = (LinearLayout)findViewWithTag("costLL");
        TextView hatchCostTV = null;
        switch(cardSize){
            case LARGE_CARD:
                LinearLayout traitLL = (LinearLayout)this.findViewWithTag("traitLL");
                for(ITrait trait : traits){
                    RelativeLayout traitLayout = (RelativeLayout)inflater.inflate(trait.getLayoutResource(), traitLL, false);
                    traitLL.addView(traitLayout);
                }
                hatchCostTV = (TextView)inflater.inflate(R.layout.hatch_cost, costLL, false);
                break;
            case SMALL_CARD:
                hatchCostTV = (TextView)inflater.inflate(R.layout.small_hatch_cost, costLL, false);
                break;
        }

        hatchCostTV.setText(Integer.toString(hatchCost));
        costLL.removeAllViews();
        costLL.addView(hatchCostTV);
        TextView attackText = (TextView)this.findViewWithTag("attack");
        attackText.setText(String.valueOf(attack));
    }

    @Override
    public void setListeners() {
        if(inHand){
            setOnTouchListener(new CTouchListener(CTouchListener.TYPE_PLAYABLE));
            setOnDragListener(null);
        } else{
            setOnTouchListener(new CTouchListener(CTouchListener.TYPE_STATIC));
            setOnDragListener(new CharacterDragListener());
        }
        final DragonCard thisCard = this;
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)getContext()).previewCard(thisCard);
            }
        });
    }
}
