package co.share.share.util;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class TouchDelegateComposite extends TouchDelegate {

    private final List<TouchDelegate> delegates = new ArrayList<>();
    private static final Rect emptyRect = new Rect();

    public TouchDelegateComposite(View view) {
        super(emptyRect, view);
    }

    public void newDelegate(View smallView, int amount)
    {
        int mAmount = (amount < 0) ? 0 : amount;

        // The hit rectangle for the ImageButton
        Rect newRect = TouchExtender.extendSquare(smallView, mAmount);

        //Log.d(this.getClass().getSimpleName(), "New bounds " +newRect.toString());

        // Instantiate a TouchDelegate.
        // "delegateArea" is the bounds in local coordinates of
        // the containing view to be mapped to the delegate view.
        // "myButton" is the child view that should receive motion
        // events.
        TouchDelegate touchDelegate = new TouchDelegate(newRect,
                smallView);

        addDelegate(touchDelegate);
    }

    private void addDelegate(TouchDelegate delegate) {
        if (delegate != null) {
            delegates.add(delegate);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean res = false;
        float x = event.getX();
        float y = event.getY();

        //Log.d("TouchDelegate", String.format("touch X %f Y %f", x, y));

        for (TouchDelegate delegate : delegates) {
            event.setLocation(x, y);
            res = delegate.onTouchEvent(event) || res;
        }
        return res;
    }

}