package com.example.gxyfred.hw9.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxyfred.hw9.MainActivity;
import com.example.gxyfred.hw9.R;
import com.example.gxyfred.hw9.entity.ReviewData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<ReviewData> mList;
    private ReviewData data;

    public ReviewAdapter(Context mContext, List<ReviewData> mList) {
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
            convertView = inflater.inflate(R.layout.review_item, null);
            viewHolder.tv_review_author_name = convertView.findViewById(R.id.tv_review_author_name);
            viewHolder.tv_review_time = convertView.findViewById(R.id.tv_review_time);
            viewHolder.iv_review_author_photo = convertView.findViewById(R.id.iv_review_author_photo);
            viewHolder.rb_review_rating = convertView.findViewById(R.id.rb_review_rating);
            viewHolder.tv_review_text = convertView.findViewById(R.id.tv_review_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        data = mList.get(position);

        SpannableString ss = new SpannableString(data.getReview_author_name());
        ss.setSpan(new URLSpan(data.getReview_author_url()), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#48A88E")), 0 , ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.tv_review_author_name.setText(ss);
        viewHolder.tv_review_author_name.setMovementMethod(LinkMovementMethod.getInstance());

        viewHolder.tv_review_time.setText(data.getReview_time());
        viewHolder.rb_review_rating.setRating(data.getReview_rating());
        viewHolder.tv_review_text.setText(data.getReview_text());
        Picasso.get().load(data.getReview_profile_photo_url()).resize(100, 100).into(viewHolder.iv_review_author_photo);
        return convertView;
    }

    class ViewHolder {
        private TextView tv_review_author_name;
        private TextView tv_review_time;
        private ImageView iv_review_author_photo;
        private RatingBar rb_review_rating;
        private TextView tv_review_text;
    }
}
