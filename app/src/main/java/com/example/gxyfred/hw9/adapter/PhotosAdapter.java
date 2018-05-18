package com.example.gxyfred.hw9.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.gxyfred.hw9.R;

import java.util.List;

public class PhotosAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<Bitmap> mList;

    public PhotosAdapter(Context mContext, List<Bitmap>mList) {
        this.mContext = mContext;
        this.mList = mList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.photos_item, null);
            viewHolder.iv_photos = convertView.findViewById(R.id.iv_photos);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.iv_photos.setImageBitmap(mList.get(position));
        return convertView;
    }

    class ViewHolder{
        private ImageView iv_photos;
    }
}
