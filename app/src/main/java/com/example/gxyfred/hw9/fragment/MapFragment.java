package com.example.gxyfred.hw9.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.gxyfred.hw9.PolyUtil.Decoder;
import com.example.gxyfred.hw9.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private static String travel_mode = "Driving";
    private Spinner travel_mode_spinner;
    private EditText get_start_location;
    private GoogleMap googleMap;
    private double detail_location_lat;
    private double detail_location_lon;
    private double start_location_lat;
    private double start_location_lon;
    private String start_location;
    private String destination_location;
    private String dest_name;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);
        travel_mode_spinner = view.findViewById(R.id.travel_mode_spinner);
        get_start_location = view.findViewById(R.id.start_location);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String json = bundle.getString("json");
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                detail_location_lat = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                detail_location_lon = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                destination_location = jsonObject.getJSONObject("result").getString("formatted_address");
                dest_name = jsonObject.getJSONObject("result").getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //String start_location = get_start_location.getText().toString().trim();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.travel_mode_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travel_mode_spinner.setAdapter(adapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googlemap) {
                googleMap = googlemap;
                LatLng detail_location = new LatLng(detail_location_lat, detail_location_lon);
                googleMap.addMarker(new MarkerOptions().position(detail_location).title(dest_name));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(detail_location));
                googleMap.setMinZoomPreference(10f);
            }
        });

        travel_mode_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                travel_mode = adapterView.getItemAtPosition(i).toString();
                //Toast.makeText(getContext(), category, Toast.LENGTH_LONG).show();
                if(start_location != null && start_location != "") {
                    getSearchResult();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                travel_mode = "Driving";
            }
        });

        get_start_location.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(delayRun!=null){
                    handler.removeCallbacks(delayRun);
                }
                start_location = s.toString();
                handler.postDelayed(delayRun, 800);

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        return view;
    }

    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            getSearchResult();
        }
    };

    private void getSearchResult() {
        String direction_url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + start_location + "&destination=" + destination_location + "&mode=" + travel_mode.toLowerCase() + "&key=AIzaSyCPkarqGvXQyFcKNQkL2mXqLRnhTgBGfL0";
        RxVolley.get(direction_url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                googleMap.clear();
                LatLng detail_location = new LatLng(detail_location_lat, detail_location_lon);
                googleMap.addMarker(new MarkerOptions().position(detail_location).title(dest_name));
                String[] directionsList = parseDirections(t);
                displayDirection(directionsList);
                LatLng start_location = new LatLng(start_location_lat, start_location_lon);
                googleMap.addMarker(new MarkerOptions().position(start_location));
            }
        });
    }

    private String[] parseDirections(String t) {
        JSONObject jsonObject;
        JSONArray jsonArray = null;
        try {
            jsonObject = new JSONObject(t);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            start_location_lat = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getDouble("lat");
            start_location_lon = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }

    private String[] getPaths(JSONArray googleStepsJson) {
        int count = googleStepsJson.length();
        String[] polylines = new String[count];
        for(int i = 0; i < count; i++) {
            try {
                polylines[i] = getPath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polylines;
    }

    private String getPath(JSONObject googlePathJson) {
        String polyline = "";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }

    private void displayDirection(String[] directionsList) {
        int count = directionsList.length;
        PolylineOptions options = null;
        for(int i = 0; i < count; i++) {
            options = new PolylineOptions();
            options.color(Color.BLUE);
            List<LatLng> mList = Decoder.decode(directionsList[i]);
            options.addAll(mList);
            googleMap.addPolyline(options);
        }
    }
}