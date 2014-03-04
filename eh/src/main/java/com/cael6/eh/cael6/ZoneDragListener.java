package com.cael6.eh.cael6;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.DragEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.cael6.eh.GameActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ZoneDragListener implements View.OnDragListener {

    private GameActivity context;

    public ZoneDragListener(GameActivity context){
        super();
        this.context = context;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Card card = (Card)event.getLocalState();
        int action = event.getAction();
        if(card.movable){
            RelativeLayout container = (RelativeLayout) v;
            int containerChildCount = container.getChildCount();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    if(1 > containerChildCount){
                        context.setBackground(v, context.enterShape);
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    if(1 > containerChildCount){
                        context.setBackground(v, context.normalShape);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    if(1 > containerChildCount){
                        // Dropped, reassign View to ViewGroup
                        if(context.dropCard(card, container, context.player)){
                            context.player.cardsOnBoard.add(card);
                        };
                        context.setBackground(v, null);
                        card.setVisibility(View.VISIBLE);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if(1 <= containerChildCount){
                        context.setBackground(v, null);
                    } else {
                        context.setBackground(v, context.normalShape);
                    }
                    card.setVisibility(View.VISIBLE);
                default:
                    break;
            }
            return true;
        }else{
            return true;
        }
    }
}

