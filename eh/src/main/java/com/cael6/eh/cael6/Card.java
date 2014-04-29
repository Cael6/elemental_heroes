package com.cael6.eh.cael6;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cael6.eh.GameActivity;
import com.cael6.eh.R;

/**
 * Represents a Card that can be played
 */
public class Card extends FrameLayout implements ICard {

    //<editor-fold desc="Public variables">
    public final static int SMALL_CARD = 0;
    public final static int LARGE_CARD = 1;
    //</editor-fold>
    public Drawable image;
    public Drawable active_image;
    public boolean isActive;
    public float mRotation = 0;
    public boolean inHand;
    public boolean movable;
    protected int cardSize;
    //<editor-fold desc="Private variables">
    private boolean hidden = false;
    private Player owner;
    //</editor-fold>

    public Card(Context context) {
        super(context);
        init();
    }

    public Card(Context context, AttributeSet set) {
        super(context, set);
        init();
        int style = set.getStyleAttribute();
        if (0 != style) {
            generateFromStyle(context, set, style);
        }
    }

    public Card(Context context, int cardStyle) {
        super(context);
        init();
        generateFromStyle(context, null, cardStyle);
    }

    public Card(Context context, Card card, Player owner) {
        super(context);
        setOwner(owner);
        init();
        this.inHand = card.inHand;
        this.mRotation = card.mRotation;
        this.image = card.image;
        this.active_image = card.active_image;
    }

    protected void init() {
        this.isActive = false;
        this.inHand = false;
    }

    protected void generateFromStyle(Context context, AttributeSet set, int style) {
        TypedArray ta = context.obtainStyledAttributes(set,
                R.styleable.Card,
                style,
                0);

        if (ta != null) {
            final int taCount = ta.getIndexCount();
            for (int i = 0; i < taCount; i++) {
                int attr = ta.getIndex(i);
                switch (attr) {
                    case R.styleable.Card_image:
                        int srcId = ta.getResourceId(attr, -1);
                        image = getResources().getDrawable(srcId);
                        break;
                    case R.styleable.Card_activeImage:
                        int activeImageId = ta.getResourceId(attr, -1);
                        active_image = getResources().getDrawable(activeImageId);
                        break;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setCardChildrenValues() {
        Drawable img;
        if(isActive){
            img = active_image;
        } else {
            img = image;
        }
        ((ImageView) this.findViewWithTag("cardImage")).setImageDrawable(img);
    }

    protected void generateCardForView(ViewGroup expectedParent) {
        Context context = getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layoutResource = 0;
        switch (cardSize) {
            case SMALL_CARD:
                layoutResource = R.layout.small_card;
                break;
            case LARGE_CARD:
                layoutResource = R.layout.card;
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

    public void hideCard() {
        this.hidden = true;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(INVISIBLE);
        }
        ((GameActivity) getContext()).setBackground(this, getResources().getDrawable(R.drawable.card_back));
    }

    public void showCard() {
        this.hidden = false;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(VISIBLE);
        }
        GameActivity.setBackground(this, null);
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void setCardForView(int size, ViewGroup expectedParent) {
        cardSize = size;
        generateCardForView(expectedParent);
        if (null != expectedParent) {

        }
    }

    public void regenerateView() {
        setCardChildrenValues();
    }

    /**
     * removes the view from the game and puts into graveyard
     */
    public void destroyCard() {
//        this.owner.graveyard.addCard(this);
        ((ViewGroup) this.getParent()).removeView(this);
    }

    @Override
    public void setListeners() {

    }

    @Override
    public boolean checkResources(Object extraResources) {
        return false;
    }

}
