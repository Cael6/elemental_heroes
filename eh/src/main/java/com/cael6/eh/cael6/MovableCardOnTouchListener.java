package com.cael6.eh.cael6;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import com.cael6.eh.GameActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MovableCardOnTouchListener implements View.OnTouchListener {

    float xStart, yStart, yThreshold;
    long minMillis = 50, millisStart;
    boolean yThresholdReached = false;
    private GameActivity context;

    public MovableCardOnTouchListener(GameActivity context){
        super();
        this.context = context;
        yThreshold = context.pxFromDp(10);
    }
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(context.previewOpen){
            return true;
        }
        Card card = (Card) view;
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                xStart = motionEvent.getX();
                yStart = motionEvent.getY();
                millisStart = System.currentTimeMillis();
                return false;
            case MotionEvent.ACTION_UP:
                if(System.currentTimeMillis() - millisStart <= minMillis){
                    context.previewCard((Card)view);
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                yThresholdReached = yThreshold < Math.abs(yStart - motionEvent.getY());
                if(System.currentTimeMillis() - millisStart > minMillis){
                    //Enough time to move the card.
                    if(yThresholdReached){
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                        card.movable = true;
                        card.startDrag(data, shadowBuilder, view, 0);
                        card.setVisibility(View.INVISIBLE);
                        return true;
                    }else{
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }
}
