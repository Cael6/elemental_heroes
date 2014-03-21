package com.cael6.eh.cael6;

import android.content.Context;
import android.util.AttributeSet;


/**
 * Custom Text Views
 */
public class CTextView extends MagicTextView {
    public CTextView(Context context) {
        super(context);
        init(null);
    }
    public CTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }
    public CTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs){

    }
}
