package co.share.share.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

/**
 * Created by Grant on 3/22/15.
 */
public class ProgressSpinner {
    Context ctx;

    private View mProgressView;
    private View mToHide;
    private boolean mShown;

    public ProgressSpinner(Context ctx, View progressView, View toHide) {
        this.ctx = ctx;
        this.mProgressView = progressView;
        this.mToHide = toHide;
        this.mShown = false;
    }

    public void show() {
        showProgress(true);
    }

    public void hide() {
        showProgress(false);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = ctx.getResources().getInteger(android.R.integer.config_shortAnimTime);

            mToHide.setVisibility(show ? View.GONE : View.VISIBLE);
            mToHide.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mToHide.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mToHide.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
