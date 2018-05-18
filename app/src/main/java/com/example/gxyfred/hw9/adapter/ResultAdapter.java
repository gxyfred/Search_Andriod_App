package com.example.gxyfred.hw9.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxyfred.hw9.R;
import com.example.gxyfred.hw9.entity.ResultData;
import com.example.gxyfred.hw9.fragment.FavoritesFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ResultAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<ResultData> mList;

    public ResultAdapter(Context mContext, List<ResultData>mList) {
        this.mContext = mContext;
        this.mList = mList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.result_item, null);
            viewHolder.iv_category = view.findViewById(R.id.iv_category);
            viewHolder.tv_place_name = view.findViewById(R.id.tv_place_name);
            viewHolder.tv_place_address = view.findViewById(R.id.tv_place_address);
            viewHolder.iv_favorite = view.findViewById(R.id.iv_favorite);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ResultData data = mList.get(i);
        viewHolder.tv_place_name.setText(data.getPlace_name());
        viewHolder.tv_place_address.setText(data.getPlace_address());
        Picasso.get().load(data.getCategory_image()).resize(50, 50).into(viewHolder.iv_category);
        if(data.getIsFavorite() == 0) {
            viewHolder.iv_favorite.setImageResource(R.drawable.heart_outline_black);
        } else {
            viewHolder.iv_favorite.setImageResource(R.drawable.heart_fill_red);
        }
        viewHolder.iv_favorite.setOnClickListener(new myClick(data));
        return view;
    }

    class ViewHolder{
        private ImageView iv_category;
        private TextView tv_place_name;
        private TextView tv_place_address;
        private ImageView iv_favorite;
    }

    class myClick implements View.OnClickListener {
        private ResultData data;

        myClick(ResultData data) {
            this.data = data;
        }

        @Override
        public void onClick(View v) {
            ImageView h = (ImageView)v;
            int value = (data.getIsFavorite() == 0) ? 1 : 0;
            //Log.v("TAG", String.valueOf(FavoritesFragment.mFavoriteList.size()));
            data.setIsFavorite(value);
            if(value == 0) {
                h.setImageResource(R.drawable.heart_outline_black);
                //Toast.makeText(getContext(), data.getPlace_name() + "was removed from favorites", Toast.LENGTH_SHORT).show();
                FavoritesFragment.mFavoriteList.remove(data);
            } else {
                h.setImageResource(R.drawable.heart_fill_red);
                FavoritesFragment.mFavoriteList.add(data);
                //Toast.makeText(getContext(), data.getPlace_name() + "was added to favorites", Toast.LENGTH_SHORT).show();
            }
            FavoritesFragment.mListView.setAdapter(FavoritesFragment.mFavoriteAdapter);
            notifyDataSetChanged();
        }
    }
}
