package com.cael6.eh.cael6;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import com.cael6.eh.GameActivity;
import com.cael6.eh.R;

/**
 * Created by cael6 on 24/02/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StaticCardOnTouchListener implements View.OnTouchListener{
    long minMillis = 50, millisStart;
    private GameActivity context;

    public StaticCardOnTouchListener(GameActivity context){
        super();
        this.context = context;
    }

    public boolean onTouch(View view, MotionEvent motionEvent){
        if(context.previewOpen){
            return true;
        }
        Card card = (Card) view;
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                millisStart = System.currentTimeMillis();
                return false;
            case MotionEvent.ACTION_UP:
                if(System.currentTimeMillis() - millisStart <= minMillis){
                    context.previewCard((Card) view);
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if(System.currentTimeMillis() - millisStart > minMillis){
                    ClipData data = ClipData.newPlainText("", "");
                    card.movable = false;
                    View.DragShadowBuilder shadowBuilder = ImageDragShadowBuilder.fromResource(card.getContext(), R.drawable.target);
                    card.startDrag(data, shadowBuilder, view,0);
                    return true;
                }
                return false;
            default:
                return false;
        }
    }
}
