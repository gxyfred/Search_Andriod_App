package com.example.gxyfred.hw9.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.gxyfred.hw9.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;

public class InfoFragment extends Fragment {

    private String detail_address;
    private String phone_number;
    private String price_level;
    private Float rating;
    private String google_page;
    private String website;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info, null);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            String json = bundle.getString("json");
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject jsonRes = jsonObject.getJSONObject("result");
                detail_address = jsonRes.getString("formatted_address");
                phone_number = jsonRes.getString("formatted_phone_number");
                price_level = "";
                int price = jsonRes.getInt("price_level");
                for(int i = 0; i < price; i++) {
                    price_level += "$";
                }
                rating = (float) jsonRes.getDouble("price_level");
                google_page = jsonRes.getString("url");
                website = jsonRes.getString("website");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        TextView tv_detail_address = view.findViewById(R.id.tv_detail_address);
        TextView tv_detail_phone_number = view.findViewById(R.id.tv_detail_phone_number);
        TextView tv_detail_price_level = view.findViewById(R.id.tv_detail_price_level);
        RatingBar tv_detail_rating = view.findViewById(R.id.rb_info_rating);
        TextView tv_detail_google_page = view.findViewById(R.id.tv_detail_google_page);
        TextView tv_detail_website = view.findViewById(R.id.tv_detail_website);

        TextView maddress = view.findViewById(R.id.address);
        TextView mphone_number = view.findViewById(R.id.phone_number);
        TextView mprice_level = view.findViewById(R.id.price_level);
        TextView mrating = view.findViewById(R.id.rating);
        TextView mgoogle_page = view.findViewById(R.id.google_page);
        TextView mwebsite = view.findViewById(R.id.website);

        if(detail_address != null) {
            tv_detail_address.setText(detail_address);
        } else {
            tv_detail_address.setText("");
            maddress.setText("");
        }
        if(phone_number != null) {
            tv_detail_phone_number.setText(phone_number);
        } else {
            tv_detail_phone_number.setText("");
            mphone_number.setText("");
        }
        if(price_level != null && price_level != "") {
            tv_detail_price_level.setText(price_level);
        } else {
            tv_detail_price_level.setText("");
            mprice_level.setText("");
        }
        if(rating != null) {
            tv_detail_rating.setRating(rating);
        } else {
            tv_detail_rating.setVisibility(GONE);
            mrating.setText("");
        }
        if(google_page != null) {
            tv_detail_google_page.setText(google_page);
        } else {
            tv_detail_google_page.setText("");
            mgoogle_page.setText("");
        }
        if(website != null) {
            tv_detail_website.setText(website);
        } else {
            tv_detail_website.setText("");
            mwebsite.setText("");
        }
        return view;
    }
}