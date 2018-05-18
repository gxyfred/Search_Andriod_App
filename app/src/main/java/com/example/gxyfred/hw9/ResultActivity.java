package com.example.gxyfred.hw9;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gxyfred.hw9.adapter.ResultAdapter;
import com.example.gxyfred.hw9.entity.ResultData;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.http.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends BaseActivity implements View.OnClickListener{
    private ListView result_listview;
    public static int start_index = 0;
    public static int end_index = 0;
    public static List<ResultData> mList = new ArrayList<>();
    private List<String> mListPlaceName = new ArrayList<>();
    private List<String> mListDetailUrl = new ArrayList<>();
    private List<String> mListAddress = new ArrayList<>();
    private String place_api_key = "AIzaSyAQ_M84K8f7IN3EMmiLqXwm0R1q0xqEap8";
    private Button prev;
    private Button next;
    private String next_page_token;
    private ProgressDialog fetch;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        result_listview = findViewById(R.id.result_list);
        Bundle bundle = getIntent().getExtras();
        String jsonObj = bundle.getString("search_result");

        prev = findViewById(R.id.btn_prev);
        prev.setOnClickListener(this);
        prev.setEnabled(false);
        next = findViewById(R.id.btn_next);
        next.setOnClickListener(this);
        //Toast.makeText(ResultActivity.this, jsonObj, Toast.LENGTH_SHORT).show();
        parsingJson(jsonObj);
        if(next_page_token.equals("")) {
            next.setEnabled(false);
        } else {
            next.setEnabled(true);
        }
        result_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.i("TAG", "Position: " + position);
                Intent intent = new Intent(ResultActivity.this, DetailActivity.class);
                intent.putExtra("place_name", mListPlaceName.get(position));
                intent.putExtra("detail_url", mListDetailUrl.get(position));
                intent.putExtra("address", mListAddress.get(position));
                intent.putExtra("position", String.valueOf(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_prev:
                if(start_index != 0) {
                    next.setEnabled(true);
                    start_index -= 20;
                    if((end_index % 20) == 0) {
                        end_index -= 20;
                    } else {
                        end_index -= (end_index % 20);
                    }
                    ResultAdapter adapter = new ResultAdapter(this, mList.subList(start_index, end_index));
                    result_listview.setAdapter(adapter);
                    if(start_index == 0) {
                        prev.setEnabled(false);
                    }
                } else {

                }
                break;
            case R.id.btn_next:
                if(end_index != mList.size()) {
                    prev.setEnabled(true);
                    if(Math.min(20, mList.size() - end_index) == (mList.size() - end_index)) {
                        if(next_page_token == "") {
                            next.setEnabled(false);
                        }
                    }
                    end_index = end_index + Math.min(20, mList.size() - end_index);
                    start_index = start_index + 20;
                    ResultAdapter adapter = new ResultAdapter(this, mList.subList(start_index, end_index));
                    result_listview.setAdapter(adapter);
                } else if(next_page_token != "") {
                    start();
                    prev.setEnabled(true);
                    String next_url = "https://nodejs-sample-200920.appspot.com/searchNext?next_page_token=" + next_page_token;
                    RxVolley.get(next_url, new HttpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            start_index += 20;
                            parsingJson(t);
                            fetch.cancel();
                            if(next_page_token.equals("")) {
                                next.setEnabled(false);
                            }
                        }
                    });
                } else {

                }
                break;
        }
    }

    private void parsingJson(String jsonObj) {
        try {
            JSONObject jsonObject = new JSONObject(jsonObj);
            if(jsonObject.has("next_page_token")) {
                next_page_token = jsonObject.getString("next_page_token");
            } else {
                next_page_token = "";
            }
            JSONArray jsonResult = jsonObject.getJSONArray("results");
            String url;
            for (int i = 0; i < jsonResult.length(); i++) {
                JSONObject json = (JSONObject) jsonResult.get(i);
                ResultData data = new ResultData();

                String place_id = json.getString("place_id");
                url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place_id + "&key=" + place_api_key;
                data.setPlace_name(json.getString("name"));
                data.setPlace_address(json.getString("vicinity"));
                data.setCategory_image(json.getString("icon"));
                data.setDetail_url(url);

                mList.add(data);
                end_index++;
                mListPlaceName.add(json.getString("name"));
                mListDetailUrl.add(url);
                mListAddress.add(json.getString("vicinity"));
            }
            //Log.e("TAG", mList.get(0).getPlace_name());
            ResultAdapter adapter = new ResultAdapter(this, mList.subList(start_index, end_index));
            result_listview.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        fetch = new ProgressDialog(this);
        fetch.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        fetch.setIndeterminate(false);
        fetch.setMessage("fetching data...");
        fetch.show();
    }
}
