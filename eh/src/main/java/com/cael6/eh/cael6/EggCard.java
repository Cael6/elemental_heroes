package com.cael6.eh.cael6;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cael6.eh.GameActivity;
import com.cael6.eh.R;

/**
 * Created by cael6 on 24/03/14.
 */
public class EggCard extends Card {
    private int hatchTimer;

    public EggCard(Context context) {
        super(context);
    }

    public EggCard(Context context, AttributeSet set) {
        super(context, set);
    }

    public EggCard(Context context, int cardStyle) {
        super(context, cardStyle);
    }

    public EggCard(Context context, Card card, Player owner) {
        super(context, card, owner);
    }

    public void setHatchTimer(int amount){
        hatchTimer = amount;
    }

    public int getHatchTimer() {
        return hatchTimer;
    }

    public void subtractHatchTimer(int amount) {
        hatchTimer = Math.max(0, hatchTimer - amount);
    }

    @Override
    public void setCardChildrenValues() {
        super.setCardChildrenValues();
        ((TextView) findViewWithTag("hatch_count")).setText(Integer.toString(hatchTimer));
    }

    @Override
    protected void generateCardForView(ViewGroup expectedParent) {
        Context context = getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layoutResource = 0;
        switch (cardSize) {
            case SMALL_CARD:
                layoutResource = R.layout.small_egg_card;
                break;
            case LARGE_CARD:
                layoutResource = R.layout.egg_card;
        }
        Card cardLayout = (Card) inflater.inflate(layoutResource, expectedParent, false);

        image = getContext().getResources().getDrawable(R.drawable.egg);
        active_image = getContext().getResources().getDrawable(R.drawable.egg_active);

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

    @Override
    public void setListeners() {
        if (inHand) {
            setOnTouchListener(new CTouchListener(CTouchListener.TYPE_PLAYABLE));
            setOnDragListener(null);
            setOnClickListener(null);
        } else {
            setOnDragListener(new EggDragListener());
            setOnTouchListener(null);
            setOnClickListener(null);
        }
    }

    public void increaseHatch() {
        hatchTimer++;
        TextView hatchCount = (TextView) findViewWithTag("hatch_count");
        hatchCount.setText(Integer.toString(hatchTimer));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class EggDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            Card card = (Card) event.getLocalState();
            GameActivity context = (GameActivity) v.getContext();
            int action = event.getAction();
            EggCard egg = (EggCard) v;
            if (card instanceof SpellCard) {
                return spellDrag((SpellCard) card, egg, action);
            } else if (card instanceof DragonCard && card.inHand && card.getOwner().equals(egg.getOwner())
                    && ((DragonCard)card).getHatchCost() <= egg.getHatchTimer()) {
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        card.getOwner().inactivateAllPlayables();
                        egg.isActive = true;
                        egg.setCardChildrenValues();
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        // Dropped, reassign View to ViewGroup
                        if (context.dropDragonCard((DragonCard) card, egg, context.player)) {
                            context.player.dragonsOnBoard.add((DragonCard) card);
                        }
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        egg.isActive = false;
                        egg.setCardChildrenValues();
                        card.getOwner().setPlayableCards();
                    default:
                        break;
                }
                return true;
            } else {
                return true;
            }
        }


        private boolean spellDrag(SpellCard spell, EggCard target, int action) {
            if (spell.isValidTarget(target)) {
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        spell.getOwner().inactivateAllPlayables();
                        target.isActive = true;
                        target.setCardChildrenValues();
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        //set background target shape
                        //context.setBackground(v, context.creatureZoneEnterShape);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        //set background back to default background
//                        context.setBackground(v, defaultBackground);
                        break;
                    case DragEvent.ACTION_DROP:
                        // Dropped, reassign View to ViewGroup

                        spell.getOwner().attemptToPlaySpellCard(spell, target, true);
//                        context.setBackground(v, null);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        target.isActive = false;
                        target.setCardChildrenValues();
                        spell.getOwner().setPlayableCards();
                    default:
                        break;
                }
                return true;
            } else {
                return true;
            }
        }
    }
}
