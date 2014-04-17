package com.cael6.eh.cael6;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cael6.eh.GameActivity;
import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Created by cael6 on 24/03/14.
 */
public class SpellCard extends Card{

    public int dragonBreathCost;
    private int functionId;
    private String text;

    private final static int EFFECT_POISON_BLAST = 1;

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

    protected void generateFromStyle(Context context, AttributeSet set, int style) {
        super.generateFromStyle(context, set, style);
        TypedArray ta = context.obtainStyledAttributes(set,
                R.styleable.SpellCard,
                style,
                0);

        final int taCount;
        if (ta != null) {
            taCount = ta.getIndexCount();
            for (int i = 0; i < taCount; i++) {
                int attr = ta.getIndex(i);
                switch (attr) {
                    case R.styleable.SpellCard_breathCost:
                        dragonBreathCost = ta.getInt(attr, -1);
                        break;
                    case R.styleable.SpellCard_function:
                        functionId = ta.getInt(attr, -1);
                        break;
                    case R.styleable.SpellCard_text:
                        text = ta.getString(attr);
                        break;
                }
            }

        }
    }

    @Override
    protected void setCardChildrenValues(){
        super.setCardChildrenValues();
        LayoutInflater inflater = ((GameActivity) getContext()).getLayoutInflater();
        LinearLayout costLL = (LinearLayout)findViewWithTag("costLL");
        TextView breathCostText = null;
        switch(cardSize){
            case LARGE_CARD:
                TextView effectText = (TextView)this.findViewWithTag("effect");
                effectText.setText(text);
                breathCostText = (TextView)inflater.inflate(R.layout.breath_cost, costLL, false);
                break;
            case SMALL_CARD:
                breathCostText = (TextView)inflater.inflate(R.layout.small_breath_cost, costLL, false);
                break;
        }

        breathCostText.setText(Integer.toString(dragonBreathCost));
        costLL.addView(breathCostText);
    }

    public void executeEffect(Card target){
        switch (functionId){
            case EFFECT_POISON_BLAST:
                poisonBlast((CharacterCard) target);
                break;
        }
    }

    @Override
    public void setListeners() {
        setOnTouchListener(new SpellOnTouchListener());
    }

    public class SpellOnTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    }

    private void poisonBlast(CharacterCard target){
        target.health--;
        if(target.health <= 0){
            if(target instanceof DragonCard){
                target.getOwner().dragonsOnBoard.remove(target);
                ((DragonCard)target).killed();
            } else if(target instanceof HeroCard) {
                target.getOwner().lose((GameActivity)target.getContext());
            }
        }
    }
}
