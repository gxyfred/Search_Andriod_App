package com.example.gxyfred.hw9.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gxyfred.hw9.R;
import com.example.gxyfred.hw9.adapter.ReviewAdapter;
import com.example.gxyfred.hw9.entity.ReviewData;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.http.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;
import java.util.Comparator;

public class ReviewsFragment extends Fragment {

    private ListView review_listview;
    private List<ReviewData> mList = new ArrayList<>();
    public static String source = "Google reviews";
    public static String sort = "Default order";
    private Spinner review_source_spinner;
    private Spinner review_sort_spinner;
    private Inflater inflater;

    private List<String> mListAuthorName = new ArrayList<>();
    private List<String> mListAuthorUrl = new ArrayList<>();
    private List<String> mListPhotoUrl = new ArrayList<>();
    private List<String> mListText = new ArrayList<>();
    private List<String> mListTime = new ArrayList<>();
    private List<Integer> mListRating = new ArrayList<>();
    private List<Integer> mListDefaultOrder = new ArrayList<>();

    private ListView lv_reviews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, null);
        lv_reviews = view.findViewById(R.id.lv_reviews);
        review_source_spinner = view.findViewById(R.id.review_source_spinner);
        review_sort_spinner = view.findViewById(R.id.review_sort_spinner);
        final Bundle bundle = this.getArguments();

        ArrayAdapter<CharSequence> adapter_source = ArrayAdapter.createFromResource(getContext(), R.array.review_source_array, android.R.layout.simple_spinner_item);
        adapter_source.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        review_source_spinner.setAdapter(adapter_source);
        review_source_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                source = adapterView.getItemAtPosition(i).toString();
                if(source.equals("Google reviews")) {
                    setGoogleReview(bundle);
                    //Toast.makeText(getContext(), source, Toast.LENGTH_LONG).show();
                } else {
                    setYelpReview(bundle);
                    //Toast.makeText(getContext(), source, Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(getContext(), category, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                source = "Google reviews";
            }
        });

        ArrayAdapter<CharSequence> adapter_sort = ArrayAdapter.createFromResource(getContext(), R.array.review_sort_array, android.R.layout.simple_spinner_item);
        adapter_sort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        review_sort_spinner.setAdapter(adapter_sort);
        review_sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sort = adapterView.getItemAtPosition(i).toString();
                if(sort.equals("Default order")) {
                    Collections.sort(mList, new Comparator<ReviewData>() {
                        @Override
                        public int compare(ReviewData o1, ReviewData o2) {
                            return o1.getReview_default_order() >= o2.getReview_default_order() ? -1 : 1;
                        }
                    });
                } else if(sort.equals("Highest rating")) {
                    Collections.sort(mList, new Comparator<ReviewData>() {
                        @Override
                        public int compare(ReviewData o1, ReviewData o2) {
                            return o1.getReview_rating() >= o2.getReview_rating() ? -1 : 1;
                        }
                    });
                } else if(sort.equals("Lowest rating")) {
                    Collections.sort(mList, new Comparator<ReviewData>() {
                        @Override
                        public int compare(ReviewData o1, ReviewData o2) {
                            return o1.getReview_rating() < o2.getReview_rating() ? -1 : 1;
                        }
                    });
                } else if(sort.equals("Most recent")) {
                    Collections.sort(mList, new Comparator<ReviewData>() {
                        @Override
                        public int compare(ReviewData o1, ReviewData o2) {
                            Date date1 = stringToDate(o1.getReview_time());
                            Date date2 = stringToDate(o2.getReview_time());
                            return date1.after(date2)?-1:1;
                        }
                    });
                } else {
                    Collections.sort(mList, new Comparator<ReviewData>() {
                        @Override
                        public int compare(ReviewData o1, ReviewData o2) {
                            Date date1 = stringToDate(o1.getReview_time());
                            Date date2 = stringToDate(o2.getReview_time());
                            return date1.before(date2)?-1:1;
                        }
                    });
                }
                ReviewAdapter adapter1 = new ReviewAdapter(getContext(), mList);
                lv_reviews.setAdapter(adapter1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sort = "Default order";
            }
        });

        return view;
    }

    private Date stringToDate(String s) {
        Date date = null;
        SimpleDateFormat a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = a.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private void setGoogleReview(Bundle bundle) {
        if (bundle != null) {
            String json = bundle.getString("json");
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                JSONArray jsonRev = jsonObject.getJSONObject("result").getJSONArray("reviews");
                mList.clear();
                for (int i = 0; i < jsonRev.length(); i++) {
                    JSONObject jsonReview = (JSONObject) jsonRev.get(i);
                    ReviewData data = new ReviewData();

                    String author_name = jsonReview.getString("author_name");
                    String author_url = jsonReview.getString("author_url");
                    String photo_url = jsonReview.getString("profile_photo_url");
                    String text = jsonReview.getString("text");
                    long t = jsonReview.getLong("time");
                    t = t * 1000;
                    Date date = new Date(t);
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    String time = fmt.format(date);
                    int rating = jsonReview.getInt("rating");

                    data.setReview_author_name(author_name);
                    data.setReview_author_url(author_url);
                    data.setReview_profile_photo_url(photo_url);
                    data.setReview_text(text);
                    data.setReview_time(time);
                    data.setReview_rating(rating);
                    data.setReview_default_order(i);
                    mList.add(data);
                    /* Set bundle content
                    mListPlaceName.add(json.getString("name"));
                    mListDetailUrl.add(url);
                    mListLat.add(String.valueOf(json.getJSONObject("geometry").getJSONObject("location").getDouble("lat")));
                    mListLon.add(String.valueOf(json.getJSONObject("geometry").getJSONObject("location").getDouble("lng")));*/
                }
                ReviewAdapter adapter = new ReviewAdapter(getContext(), mList);
                lv_reviews.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setYelpReview(Bundle bundle) {
        if (bundle != null) {
            String json = bundle.getString("json");
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                JSONObject jsonRes = jsonObject.getJSONObject("result");
                String name = jsonRes.getString("name");
                String address = jsonRes.getString("formatted_address");
                JSONArray address_components = jsonRes.getJSONArray("address_components");

                String city = "";
                String country = "";
                String state = "";
                for(int i = 0; i < address_components.length(); i++) {
                    JSONObject address_level = address_components.getJSONObject(i);
                    if(address_level.getJSONArray("types").getString(0).equals("locality")) {
                        city = address_level.getString("short_name");
                    } else if(address_level.getJSONArray("types").getString(0).equals("administrative_area_level_1")) {
                        state = address_level.getString("short_name");
                    } else if(address_level.getJSONArray("types").getString(0).equals("country")) {
                        country = address_level.getString("short_name");
                    } else {
                        continue;
                    }
                }
                String yelpBest_url = "https://nodejs-sample-200920.appspot.com/yelpBest?name=" + name + "&address1=" + address + "&city=" + city + "&state=" + state + "&country=" + country;
                Log.e("TAG", yelpBest_url);
                getYelpBest(yelpBest_url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getYelpBest(String yelpBest_url) {
        RxVolley.get(yelpBest_url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                Log.e("TAG", "Yelp Success Root");
                try {
                    JSONObject yelpBest_json = new JSONObject(t);
                    String yelp_id = yelpBest_json.getJSONArray("businesses").getJSONObject(0).getString("id");
                    String yelpReview_url = "https://nodejs-sample-200920.appspot.com/yelpReview?id=" + yelp_id;
                    Log.e("TAG", yelpReview_url);
                    getYelpReview(yelpReview_url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                //Toast.makeText(getContext(), "Yelp_error", Toast.LENGTH_LONG).show();
                Log.e("TAG", "Yelp Error Root");
            }

        });
    }

    private void getYelpReview(String yelpReview_url) {
        RxVolley.get(yelpReview_url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                Log.e("TAG", "Yelp Success Child");
                try {
                    JSONObject yelpReview_json = new JSONObject(t);
                    JSONArray yelpReview = yelpReview_json.getJSONArray("reviews");
                    mList.clear();
                    for (int i = 0; i < yelpReview.length(); i++) {
                        JSONObject jsonReview = (JSONObject) yelpReview.get(i);
                        ReviewData data = new ReviewData();

                        String author_name = jsonReview.getJSONObject("user").getString("name");
                        String author_url = jsonReview.getString("url");
                        String photo_url = jsonReview.getJSONObject("user").getString("image_url");
                        String text = jsonReview.getString("text");
                        String time = jsonReview.getString("time_created");
                        int rating = jsonReview.getInt("rating");

                        data.setReview_author_name(author_name);
                        data.setReview_author_url(author_url);
                        data.setReview_profile_photo_url(photo_url);
                        data.setReview_text(text);
                        data.setReview_time(time);
                        data.setReview_rating(rating);
                        data.setReview_default_order(i);
                        mList.add(data);
                    }
                    ReviewAdapter adapter = new ReviewAdapter(getContext(), mList);
                    lv_reviews.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(VolleyError error) {
                //Toast.makeText(getContext(), "Yelp_error", Toast.LENGTH_LONG).show();
                Log.e("TAG", "Yelp Error Child");
            }
        });
    }
}