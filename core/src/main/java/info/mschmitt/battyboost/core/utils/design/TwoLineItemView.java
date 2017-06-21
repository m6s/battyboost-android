package info.mschmitt.battyboost.core.utils.design;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import info.mschmitt.battyboost.core.R;
import info.mschmitt.battyboost.core.utils.ObjectsBackport;

/**
 * @author Matthias Schmitt
 */
public class TwoLineItemView extends LinearLayout {
    private final TextView textView1;
    private final TextView textView2;
    private String text1;
    private String text2;

    public TwoLineItemView(Context context) {
        this(context, null, 0);
    }

    public TwoLineItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.two_line_item_view, this);
        initAttributes(attrs, defStyleAttr);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setVisibility(text1 == null ? GONE : VISIBLE);
        textView1.setText(text1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setVisibility(text2 == null ? GONE : VISIBLE);
        textView2.setText(text2);
        setOrientation(LinearLayout.VERTICAL);
        int padding =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        setPadding(padding, padding, padding, padding);
        int minHeight =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics());
        setMinimumHeight(minHeight);
    }

    private void initAttributes(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray =
                getContext().obtainStyledAttributes(attrs, R.styleable.MultiLineItemView, defStyleAttr, 0);
        text1 = typedArray.getString(R.styleable.MultiLineItemView_text1);
        text2 = typedArray.getString(R.styleable.MultiLineItemView_text2);
        typedArray.recycle();
    }

    public TwoLineItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setText1(String text1) {
        if (ObjectsBackport.equals(text1, this.text1)) {
            return;
        }
        if (text1 == null || this.text1 == null) {
            textView1.setVisibility(text1 == null ? GONE : VISIBLE);
        }
        textView1.setText(text1);
    }

    public void setText2(String text2) {
        if (ObjectsBackport.equals(text2, this.text2)) {
            return;
        }
        if (text2 == null || this.text2 == null) {
            textView2.setVisibility(text2 == null ? GONE : VISIBLE);
        }
        textView2.setText(text2);
    }
}
