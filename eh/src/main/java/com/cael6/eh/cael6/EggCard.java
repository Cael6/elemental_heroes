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

    public int getHatchTimer() {
        return hatchTimer;
    }

    @Override
    protected void setCardChildrenValues() {
        //Do nothing
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
            setOnClickListener(null);
        } else {
            setOnDragListener(new EggDragListener());
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
            if (card instanceof DragonCard) {
                EggCard egg = (EggCard) v;
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        // Dropped, reassign View to ViewGroup
                        if (context.dropDragonCard((DragonCard) card, egg, context.player)) {
                            context.player.dragonsOnBoard.add((DragonCard)card);
                        }
                        egg.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        egg.setVisibility(View.VISIBLE);
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
