package com.example.gxyfred.hw9.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.gxyfred.hw9.DetailActivity;
import com.example.gxyfred.hw9.R;
import com.example.gxyfred.hw9.ResultActivity;
import com.example.gxyfred.hw9.adapter.ResultAdapter;
import com.example.gxyfred.hw9.entity.ResultData;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    public static ListView mListView;
    public static List<ResultData> mFavoriteList;
    public static ResultAdapter mFavoriteAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mListView = view.findViewById(R.id.lv_favorites);
        //
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.i("TAG", "Position: " + position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("place_name", mFavoriteList.get(position).getPlace_name());
                intent.putExtra("detail_url", mFavoriteList.get(position).getDetail_url());
                intent.putExtra("address", mFavoriteList.get(position).getPlace_address());
                intent.putExtra("position", String.valueOf(position));
                startActivity(intent);
            }
        });
        mFavoriteList = new ArrayList<>();
        mFavoriteAdapter = new ResultAdapter(getContext(), mFavoriteList);
        mListView.setAdapter(mFavoriteAdapter);
    }
}
