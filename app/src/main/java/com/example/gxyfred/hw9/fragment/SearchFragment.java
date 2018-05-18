package com.example.gxyfred.hw9.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gxyfred.hw9.NoResultActivity;
import com.example.gxyfred.hw9.R;
import com.example.gxyfred.hw9.ResultActivity;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.http.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchFragment extends Fragment implements View.OnClickListener{
    public static String category = "default";
    public static String radio_location = "cur_location";
    private Button search;
    private Button clear;
    private Spinner sp;
    private EditText get_distance;
    private EditText get_keyword;
    private EditText get_location;
    private String distance;
    private String keyword;
    private String location;
    private String url = "https://nodejs-sample-200920.appspot.com/";
    private ProgressDialog fetch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null);
        category = "default";
        get_keyword = view.findViewById(R.id.keyword);
        get_distance = view.findViewById(R.id.distance);
        get_location = view.findViewById(R.id.other_location_text);
        sp = view.findViewById(R.id.category_spinner);
        search = view.findViewById(R.id.btn_search);
        search.setOnClickListener(this);
        clear = view.findViewById(R.id.btn_clear);
        clear.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getItemAtPosition(i).toString();
                category = category.replace(" ", "_").toLowerCase();
                //Log.e("TAG", category);
                //Toast.makeText(getContext(), category, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                category = "default";
            }
        });

        //Set Radiobutton listener (location)
        RadioButton mCur = view.findViewById(R.id.current_location);
        mCur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radio_location = "cur_location";
                    //Toast.makeText(getContext(), test, Toast.LENGTH_LONG).show();
                } else {
                    radio_location = "other_location";
                    //Toast.makeText(getContext(), test, Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    //Button click listener
    @Override
    public void onClick(View view) {
        distance = get_distance.getText().toString().trim();
        keyword = get_keyword.getText().toString().trim();
        location = get_location.getText().toString().trim();
        switch (view.getId()){
            case R.id.btn_search:
                //Get distance
                if(TextUtils.isEmpty(distance)) {
                    distance = "16090";
                } else {
                    int dis = Integer.parseInt(distance) * 1609;
                    distance = Integer.toString(dis);
                }
                //Log.i("TAG", radio_location);
                //Get location
                if(radio_location.equals("cur_location")) {
                    if(keyword.length() == 0) {
                        Toast.makeText(getContext(), "Please enter mandatory field!", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        start();
                        RxVolley.get("http://ip-api.com/json", new HttpCallback() {
                            //Toast.makeText(getContext(), test, Toast.LENGTH_LONG).show();
                            @Override
                            public void onSuccess(String t) {
                                parse_json_current_location(t);
                            }
                            @Override
                            public void onFailure(VolleyError error) {
                                super.onFailure(error);
                                Log.i("TAG", "Fail");
                            }
                        });
                    }
                } else {
                    if(keyword.length() == 0 || location.length() == 0) {
                        Toast.makeText(getContext(), "Please enter mandatory field!", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        start();
                        String geocoding_url = "https://nodejs-sample-200920.appspot.com/geocode?address=" + location;
                        RxVolley.get(geocoding_url, new HttpCallback() {
                            @Override
                            public void onSuccess(String t) {
                                parse_json_other_location(t);
                            }
                            @Override
                            public void onFailure(VolleyError error) {
                                super.onFailure(error);
                                Log.i("TAG", "Fail");
                            }
                        });
                    }
                }
                break;

            case R.id.btn_clear:
                clear();
                break;
        }
    }

    private void clear() {
        ResultActivity.start_index = 0;
        ResultActivity.end_index = 0;
        ResultActivity.mList.clear();
        get_distance.setText(null);
        get_keyword.setText(null);
        get_location.setText(null);
    }

    private void parse_json_current_location(String t) {
        try {
            JSONObject jsonObj = new JSONObject(t);
            double Lat = jsonObj.getDouble("lat");
            double Lon = jsonObj.getDouble("lon");
            String myLat = String.valueOf(Lat);
            String myLon = String.valueOf(Lon);
            String myUrl = url + "search?lat=" + myLat + "&lon=" + myLon + "&radius=" + distance + "&type=" + category + "&keyword=" + keyword;
            start_result_activity(myUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parse_json_other_location(String t) {
        try {
            JSONObject jsonObject = new JSONObject(t);
            double Lat = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            double Lon = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            String myLat = String.valueOf(Lat);
            String myLon = String.valueOf(Lon);
            String myUrl = url + "search?lat=" + myLat + "&lon=" + myLon + "&radius=" + distance + "&type=" + category + "&keyword=" + keyword;
            start_result_activity(myUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void start_result_activity(String search_url) {
        //Log.e("TAG", search_url);
        RxVolley.get(search_url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                fetch.cancel();
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    String status = jsonObject.getString("status");
                    if(status.equals("ZERO_RESULTS")) {
                        Intent intent = new Intent(getActivity(), NoResultActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), ResultActivity.class);
                        intent.putExtra("search_result", t);
                        ResultActivity.mList.clear();
                        ResultActivity.start_index = 0;
                        ResultActivity.end_index = 0;
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Log.i("TAG", t);
            }
        });
    }

    private void start() {
        fetch = new ProgressDialog(getContext());
        fetch.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        fetch.setIndeterminate(false);
        fetch.setMessage("fetching data...");
        fetch.show();
    }
}
