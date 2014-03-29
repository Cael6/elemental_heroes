package com.cael6.eh.cael6;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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
public class Card extends FrameLayout {

    //<editor-fold desc="Private variables">
    private boolean hidden = false;
    private Player owner;
    //</editor-fold>

    protected int cardSize;

    //<editor-fold desc="Public variables">
    public final static int SMALL_CARD = 0;
    public final static int LARGE_CARD = 1;
    public Drawable image;
    public float mRotation = 0;
    public boolean inHand;

    public boolean movable;
    //</editor-fold>


    Paint mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float mShadowDepth;
    Bitmap mShadowBitmap;
    static final int BLUR_RADIUS = 4;
    static final RectF sShadowRectF = new RectF(0, 0, 200, 200);
    static final Rect sShadowRect = new Rect(0, 0, 200 + 2 * BLUR_RADIUS, 200 + 2 * BLUR_RADIUS);
    static RectF tempShadowRectF = new RectF(0, 0, 0, 0);

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
    }

    private void init() {
        mShadowPaint.setColor(0x80fcfc33);
        mShadowPaint.setStyle(Paint.Style.FILL);
        setWillNotDraw(false);
        mShadowBitmap = Bitmap.createBitmap(sShadowRect.width(),
                sShadowRect.height(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(mShadowBitmap);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(BLUR_RADIUS, BlurMaskFilter.Blur.NORMAL));
        c.translate(BLUR_RADIUS, BLUR_RADIUS);
        c.drawRoundRect(sShadowRectF, sShadowRectF.width() / 40,
                sShadowRectF.height() / 40, mShadowPaint);

        this.inHand = false;
    }

    protected void generateFromStyle(Context context, AttributeSet set, int style) {
        TypedArray ta = context.obtainStyledAttributes(set,
                R.styleable.Card,
                style,
                0);

        final int taCount = ta.getIndexCount();
        for (int i = 0; i < taCount; i++) {
            int attr = ta.getIndex(i);
            switch (attr) {
                case R.styleable.Card_image:
                    int srcId = ta.getResourceId(attr, -1);
                    image = getResources().getDrawable(srcId);
                    break;
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected void setCardChildrenValues() {
        ((ImageView)this.findViewWithTag("cardImage")).setImageDrawable(image);
    }

    private void generateCardForView(ViewGroup expectedParent) {
        Context context = getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layoutResource = 0;
        switch(cardSize){
            case SMALL_CARD:
                layoutResource = R.layout.small_card;
                break;
            case LARGE_CARD:
                layoutResource = R.layout.card;
        }
        Card cardLayout = (Card)inflater.inflate(layoutResource, expectedParent, false);

        this.setLayoutParams(cardLayout.getLayoutParams());
        while(0 < cardLayout.getChildCount()){
            View cardChild = cardLayout.getChildAt(0);
            if(cardChild != null){
                ((ViewGroup)cardChild.getParent()).removeView(cardChild);
                this.addView(cardChild);
            }
        }
        setCardChildrenValues();
    }

    public void hideCard(){
        this.hidden = true;
        for(int i = 0; i < getChildCount(); i++){
            getChildAt(i).setVisibility(INVISIBLE);
        }
        ((GameActivity)getContext()).setBackground(this, getResources().getDrawable(R.drawable.card_back));
    }

    public void showCard(){
        this.hidden = false;
        for(int i = 0; i < getChildCount(); i++){
            getChildAt(i).setVisibility(VISIBLE);
        }
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
        if(null != expectedParent){
            
        }
    }

    /**
     * removes the view from the game and puts into graveyard
     */
    public void destroyCard(){
        ((ViewGroup)this.getParent()).removeView(this);
        this.owner.graveyard.addCard(this);
    }

}
