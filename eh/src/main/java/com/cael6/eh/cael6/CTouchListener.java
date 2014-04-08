package com.cael6.eh.cael6;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

import com.cael6.eh.GameActivity;
import com.cael6.eh.R;

/**
 * Created by cael6 on 08/04/14.
 */
public class CTouchListener implements View.OnTouchListener {
    long minMillis = 50, millisStart;
    int type;
    public static final int TYPE_PLAYABLE = 0;
    public static final int TYPE_STATIC = 1;

    public CTouchListener(int type){
        this.type = type;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (type) {
            case TYPE_PLAYABLE:
                return playableOnTouch(view, motionEvent);
            case TYPE_STATIC:
                return staticOnTouch(view, motionEvent);
            default:
                return true;
        }
    }

    private boolean staticOnTouch(View view, MotionEvent motionEvent) {
        GameActivity context = (GameActivity)view.getContext();
        if (context.previewOpen) {
            return true;
        }
        Card card = (Card) view;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                millisStart = System.currentTimeMillis();
                return false;
            case MotionEvent.ACTION_UP:
                if (System.currentTimeMillis() - millisStart <= minMillis) {
                    context.previewCard((Card) view);
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if (System.currentTimeMillis() - millisStart > minMillis) {
                    ClipData data = ClipData.newPlainText("", "");
                    card.movable = false;
                    View.DragShadowBuilder shadowBuilder = ImageDragShadowBuilder.fromResource(card.getContext(), R.drawable.target);
                    card.startDrag(data, shadowBuilder, view, 0);
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    public boolean playableOnTouch(View view, MotionEvent motionEvent){

        GameActivity context = (GameActivity)view.getContext();
        if (context.previewOpen) {
            return true;
        }
        Card card = (Card) view;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                millisStart = System.currentTimeMillis();
                return false;
            case MotionEvent.ACTION_UP:
                if (System.currentTimeMillis() - millisStart <= minMillis) {
                    context.previewCard((Card) view);
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if (System.currentTimeMillis() - millisStart > minMillis) {
                    ClipData data = ClipData.newPlainText("", "");
                    card.movable = false;
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(card);
                    card.startDrag(data, shadowBuilder, view, 0);
                   return true;
                }
                return false;
            default:
                return false;
        }
    }
}
