package co.share.share.util;


import android.graphics.Rect;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class TouchExtender implements Runnable {
    private WeakReference<View> mView;
    private int mAmount;

    public TouchExtender(View view, int amount)
    {
        mView = new WeakReference<>(view);
        mAmount = (amount < 0) ? 0 : amount;
    }

    public void run()
    {
        View view = mView.get();

        if(view == null) {
            return;
        }

        Rect delegateArea = extendSquare(view, mAmount);

        // Instantiate a TouchDelegate.
        // "delegateArea" is the bounds in local coordinates of
        // the containing view to be mapped to the delegate view.
        // "myButton" is the child view that should receive motion
        // events.
        TouchDelegate touchDelegate = new TouchDelegate(delegateArea,
                view);

        // Sets the TouchDelegate on the parent view, such that touches
        // within the touch delegate bounds are routed to the child.
        if (View.class.isInstance(view.getParent())) {
            ((View) view.getParent()).setTouchDelegate(touchDelegate);
        }
    }

    public static Rect extendSquare(View v, int amount)
    {
        if(v == null)
            return new Rect(); // zeroed rect

        amount = (amount < 0) ? 0 : amount;

        Rect newRect = new Rect();
        v.getHitRect(newRect);

        //Log.d(this.getClass().getSimpleName(), "Current bounds " +newRect.toString());

        // Extend the touch area of the ImageButton beyond its bounds
        // on the right and bottom.
        newRect.left = Math.max(newRect.left-amount, 0);
        newRect.top = Math.max(newRect.top-amount, 0);
        newRect.bottom = Math.max(newRect.bottom+amount, 0);
        newRect.right = Math.max(newRect.right+amount, 0);

        return newRect;
    }
}
