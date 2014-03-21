package com.cael6.eh.cael6;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.DragEvent;
import android.view.View;

import com.cael6.eh.GameActivity;
import com.cael6.eh.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TargetDragListener implements View.OnDragListener {

    private GameActivity context;
    private Drawable defaultBackground;

    public TargetDragListener(GameActivity context){
        super();
        this.context = context;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Card card = (Card)event.getLocalState();
        int action = event.getAction();
        Card targetCard = (Card) v;
        if(!card.movable && !card.equals(targetCard)){
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    defaultBackground = v.getBackground();
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //set background target shape
                    //context.setBackground(v, context.creatureZoneEnterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //set background back to default background
                    context.setBackground(v, defaultBackground);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    if(card.turns > 0){
                        context.attackCard(card, targetCard);
                    }
                    context.setBackground(v, null);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //set background back to default background
                    context.setBackground(v, defaultBackground);
                default:
                    break;
            }
            return true;
        }else{
            return true;
        }
    }
}

