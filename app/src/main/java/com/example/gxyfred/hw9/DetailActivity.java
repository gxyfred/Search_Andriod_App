package com.example.gxyfred.hw9;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.gxyfred.hw9.fragment.FavoritesFragment;
import com.example.gxyfred.hw9.fragment.InfoFragment;
import com.example.gxyfred.hw9.fragment.MapFragment;
import com.example.gxyfred.hw9.fragment.NoPhotoFragment;
import com.example.gxyfred.hw9.fragment.PhotosFragment;
import com.example.gxyfred.hw9.fragment.ReviewsFragment;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends BaseActivity{
    //TabLayout
    private TabLayout DetailLayout;
    //ViewPager
    private ViewPager DetailViewPager;
    //Title
    private List<String> DetailTitle;
    //Fragment
    private List<Fragment> DetailFragment;

    private String detail_address;

    private ProgressDialog fetch;

    private ImageView heart;
    private ImageView share;
    private int position;
    private String place_name;
    private String address;
    private String thisWebsite;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start();
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        final String detail_url = bundle.getString("detail_url");
        place_name = bundle.getString("place_name");
        address = bundle.getString("address");
        position = Integer.valueOf(bundle.getString("position"));

        setActionBar();
        getSupportActionBar().setTitle(place_name);
        RxVolley.get(detail_url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                initData(t);
                setTwitter();
                initView();
                fetch.cancel();
            }
        });
    }

    private void start() {
        fetch = new ProgressDialog(DetailActivity.this);
        fetch.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        fetch.setIndeterminate(false);
        fetch.setMessage("fetching data...");
        fetch.show();
    }

    private void initData(String json) {
        DetailTitle = new ArrayList<>();
        DetailTitle.add("INFO");
        DetailTitle.add("PHOTOS");
        DetailTitle.add("MAP");
        DetailTitle.add("REVIEWS");
        int photo_size = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            photo_size = jsonObject.getJSONObject("result").getJSONArray("photos").length();
            thisWebsite = jsonObject.getJSONObject("result").getString("website");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Bundle bundle = new Bundle();
        bundle.putString("json", json);

        DetailFragment = new ArrayList<>();
        InfoFragment fragment_info = new InfoFragment();
        fragment_info.setArguments(bundle);
        DetailFragment.add(fragment_info);

        if(photo_size == 0) {
            NoPhotoFragment fragment_no_photo = new NoPhotoFragment();
            DetailFragment.add(fragment_no_photo);
        } else {
            PhotosFragment fragment_photos = new PhotosFragment();
            fragment_photos.setArguments(bundle);
            DetailFragment.add(fragment_photos);
        }

        MapFragment fragment_map = new MapFragment();
        fragment_map.setArguments(bundle);
        DetailFragment.add(fragment_map);

        ReviewsFragment fragment_reviews = new ReviewsFragment();
        fragment_reviews.setArguments(bundle);
        DetailFragment.add(fragment_reviews);
    }

    private void initView() {
        DetailLayout = findViewById(R.id.DetailTabLayout);
        DetailViewPager = findViewById(R.id.DetailViewPager);
        DetailViewPager.setOffscreenPageLimit(DetailFragment.size());
        //Set Adapter
        DetailViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return DetailFragment.get(position);
            }

            @Override
            public int getCount() {
                return DetailFragment.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return DetailTitle.get(position);
            }
        });
        DetailLayout.setupWithViewPager(DetailViewPager);
    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0f);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        View view = LayoutInflater.from(this).inflate(R.layout.action_bar, null);
        heart = (ImageView) view.findViewById(R.id.iv_heart);
        share = (ImageView) view.findViewById(R.id.iv_twitter);
        actionBar.setCustomView(view);

        if(ResultActivity.mList.get(position).getIsFavorite() != 0){
            heart.setImageResource(R.drawable.heart_fill_white);
        }else{
            heart.setImageResource(R.drawable.heart_outline_white);
        }
        final int pos = position;
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = (ResultActivity.mList.get(pos).getIsFavorite() == 0) ? 1 : 0;
                ResultActivity.mList.get(pos).setIsFavorite(value);
                if(value == 1){
                    heart.setImageResource(R.drawable.heart_fill_white);
                    FavoritesFragment.mFavoriteList.add(ResultActivity.mList.get(pos));
                }else{
                    heart.setImageResource(R.drawable.heart_outline_white);
                    FavoritesFragment.mFavoriteList.remove(ResultActivity.mList.get(pos));
                }
                FavoritesFragment.mListView.setAdapter(FavoritesFragment.mFavoriteAdapter);
            }
        });
    }

    private void setTwitter() {
        share.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                String url = "https://twitter.com/intent/tweet?text=Check out " + place_name + " located at " + address + ". Website: " + thisWebsite;
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }
}
