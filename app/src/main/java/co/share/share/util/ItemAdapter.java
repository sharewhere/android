package co.share.share.util;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import co.share.share.R;
import co.share.share.models.Shareable;
import co.share.share.net.NetworkService;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private Context mContext;
    private List<Shareable>  mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public ImageView mImageViewMlg;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.item_text);
            mImageView = (ImageView) v.findViewById(R.id.list_item_image);
            mImageViewMlg = (ImageView) v.findViewById(R.id.list_item_image_mlg);
        }

        @Override
        public void onClick(View view) {
            Log.d("yay", "onClick " + getPosition() + " ");
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ItemAdapter(List<Shareable> list) {
        mDataset = list;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).shar_name);
        String pic = mDataset.get(position).shar_pic_name;

        if(pic != null)
            ImageLoader.getInstance().displayImage(NetworkService.getImageURL(pic), holder.mImageView);
        else
            holder.mImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.placeholder));

        holder.mImageViewMlg.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.placeholder_mlg));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
