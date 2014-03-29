package com.cael6.eh.cael6;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

import com.cael6.eh.GameActivity;

/**
 * Generic Card Touch listener.
 */
public class CardOnTouchListener implements View.OnTouchListener {

    public boolean enabled;


    protected float xStart, yStart, yThreshold;
    protected long minMillis = 50, millisStart;
    protected boolean yThresholdReached = false;

    public CardOnTouchListener(){

        yThreshold = 20;
        enabled = true;
    }


    public boolean onTouch(View view, MotionEvent motionEvent) {
        return !(view instanceof Card) || onTouch((Card) view, motionEvent);
    }

    public boolean onTouch(Card card, MotionEvent motionEvent) {
        if(!enabled){
            return true;
        }
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                return actionDown(card, motionEvent);
            case MotionEvent.ACTION_UP:
                return actionUp(card, motionEvent);
            case MotionEvent.ACTION_MOVE:
                return actionMove(card, motionEvent);
            default:
                return actionDefault(card, motionEvent);
        }
    }
    protected void previewCard(Card card){
        GameActivity context = (GameActivity)card.getContext();
        if (context != null) {
            context.previewCard(card);
        }
    }

    protected boolean actionDown(Card card, MotionEvent motionEvent){
        xStart = motionEvent.getX();
        yStart = motionEvent.getY();
        millisStart = System.currentTimeMillis();
        return false;
    }

    protected boolean actionUp(Card card, MotionEvent motionEvent) {
        if(System.currentTimeMillis() - millisStart <= minMillis){
            previewCard(card);
            return true;
        }
        return false;
    }

    protected boolean actionMove(Card card, MotionEvent motionEvent){

        yThresholdReached = yThreshold < Math.abs(yStart - motionEvent.getY());
        if(System.currentTimeMillis() - millisStart > minMillis){
            //Enough time to move the card.
            if(yThresholdReached){
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(card);
                card.movable = true;
                card.startDrag(data, shadowBuilder, card, 0);
                card.setVisibility(View.INVISIBLE);
                return true;
            }else{
                return true;
            }
        }
        return false;
    }

    protected boolean actionDefault(Card card, MotionEvent motionEvent){
        return false;
    }
}
