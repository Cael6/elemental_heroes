package com.cael6.eh.cael6;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.DragEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.cael6.eh.GameActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CreatureZoneDragListener implements View.OnDragListener {

    private GameActivity context;

    public CreatureZoneDragListener(GameActivity context){
        super();
        this.context = context;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Card card = (Card)event.getLocalState();
        int action = event.getAction();
        if(card instanceof EggCard){
            RelativeLayout container = (RelativeLayout) v;
            int containerChildCount = container.getChildCount();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    if(1 > containerChildCount){
                        GameActivity.setBackground(v, context.creatureZoneEnterShape);
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    if(1 > containerChildCount){
                        GameActivity.setBackground(v, context.creatureZoneNormalShape);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    if(1 > containerChildCount){
                        // Dropped, reassign View to ViewGroup
                        if(context.dropEggCard((EggCard)card, container, context.player)){
                            context.player.eggsOnBoard.add((EggCard)card);
                        };
                        GameActivity.setBackground(v, null);
                        card.setVisibility(View.VISIBLE);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if(1 <= containerChildCount){
                        GameActivity.setBackground(v, null);
                    } else {
                        GameActivity.setBackground(v, context.creatureZoneNormalShape);
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
