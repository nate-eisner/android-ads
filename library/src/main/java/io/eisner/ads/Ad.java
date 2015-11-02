package io.eisner.ads;

import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * An ad view that is added into the root view
 * Created by nathan eisner on 10/5/15.
 */
public class Ad extends ImageView {
    public Ad(Context context) {
        super(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.alignWithParent = true;
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        setScaleType(ScaleType.FIT_CENTER);
        setLayoutParams(layoutParams);
//        setBackgroundColor(Color.CYAN);
        setVisibility(VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setImageDrawable(context.getDrawable(R.drawable.placeholder));
        }
        //// TODO: 11/1/15 set drawable in lower SDKs
//        addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
//            @Override
//            public void onViewAttachedToWindow(View v) {
//                View viewParent = (View) getParent();
//                View grandParent = (View) viewParent.getParent();
//                if (grandParent instanceof RelativeLayout) {
//                    Log.i("AdView", "GrandParent is RelativeLayout");
//                } else if (viewParent instanceof LinearLayout) {
//                    Log.i("AdView", "GrandParent is LinearLayout");
//                }
//            }
//
//            @Override
//            public void onViewDetachedFromWindow(View v) {
//
//            }
//        });
    }

}
