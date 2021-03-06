package co.share.share.util;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import co.share.share.ItemDetailActivity;
import co.share.share.R;
import co.share.share.models.Shareable;
import co.share.share.net.NetworkService;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private Context mContext;
    private List<Shareable>  mDataset;
    private View.OnClickListener clickListener;
    private boolean isProfileView;

    public int itemHeight;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public FrameLayout mCardView;
        public TextView mTextView;
        public TextView mSubtitle;
        public ImageView mImageView;
        public ImageView mImageViewMlg;
        public ViewHolder(View v) {
            super(v);
            mCardView = (FrameLayout) v.findViewById(R.id.card_view);
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getPosition();
                    Shareable s = mDataset.get(pos);

                    Intent d = new Intent(mContext, ItemDetailActivity.class);
                    d.putExtra(Constants.SHAREABLE, s);
                    mContext.startActivity(d);
                }
            });
            mTextView = (TextView) v.findViewById(R.id.item_text);
            mSubtitle = (TextView) v.findViewById(R.id.item_subtitle);
            mTextView.setSelected(true);
            mImageView = (ImageView) v.findViewById(R.id.list_item_image);
            mImageViewMlg = (ImageView) v.findViewById(R.id.list_item_image_mlg);
        }

        @Override
        public void onClick(View view) {
            Log.d("yay", "onClick " + getPosition() + " ");
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ItemAdapter(List<Shareable> list, Context context, boolean isProfileView) {
        mDataset = list;
        mContext = context;
        this.isProfileView = isProfileView;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v;
        if (isProfileView)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // to prevent stale images from being displayed, unset the image initially
        holder.mImageView.setImageBitmap(null);

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Shareable s = mDataset.get(position);
        holder.mTextView.setText(s.shar_name);

        if(isProfileView) {
            String username = UserProfile.getInstance().getUserName();
            if (username.equals(s.username)) {
                if (s.getSharableType() == Constants.CreateType.OFFER)
                    holder.mSubtitle.setText(s.responses + " Requests");
                else
                    holder.mSubtitle.setText(s.responses + " Offers");
            } else {
                if (s.getSharableType() == Constants.CreateType.OFFER) {
                    holder.mSubtitle.setText("from " + s.username);
                }
                else {
                    holder.mSubtitle.setText("to " + s.username);
                }
            }
        }
        // image displaying
        String pic = mDataset.get(position).shar_pic_name;
        final ImageView img = holder.mImageView;

        // nasty, nasty hack
        float height = mContext.getResources().getDimension(R.dimen.item_image_height);
        ImageSize size = new ImageSize((int)(height*2), (int)height);

        /* TODO: profile view circle image view is not being set. fix this */

        if(pic != null)
            ImageLoader.getInstance().loadImage(NetworkService.getImageURL(pic), size, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    img.setImageBitmap(loadedImage);
                }
            });

            //ImageLoader.getInstance().displayImage(NetworkService.getImageURL(pic), holder.mImageView);
        else
            holder.mImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.placeholder));

        if (!isProfileView)
            holder.mImageViewMlg.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.placeholder_mlg));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
