package co.share.share.util;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import co.share.share.ItemDetailActivity;
import co.share.share.R;
import co.share.share.models.Shareable;
import co.share.share.net.NetworkService;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private Context mContext;
    private List<Shareable>  mDataset;
    private View.OnClickListener clickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public CardView mCardView;
        public TextView mTextView;
        public ImageView mImageView;
        public ImageView mImageViewMlg;
        public ViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.card_view);
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getPosition();
                    Shareable s = mDataset.get(pos);
                    Intent d = new Intent(mContext, ItemDetailActivity.class);
                    d.putExtra("shar_name", s.shar_name);
                    d.putExtra("shar_pic_name", s.shar_pic_name);
                    d.putExtra("shar_desc", s.description);
                    d.putExtra("shar_creator", s.username);
                    mContext.startActivity(d);
                }
            });
            mTextView = (TextView) v.findViewById(R.id.item_text);
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
    public ItemAdapter(List<Shareable> list, Context context) {
        mDataset = list;
        mContext = context;
    //    this.clickListener = clickListener;

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
