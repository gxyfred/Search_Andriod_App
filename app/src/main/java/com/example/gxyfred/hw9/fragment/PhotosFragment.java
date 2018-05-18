package com.example.gxyfred.hw9.fragment;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.gxyfred.hw9.R;
import com.example.gxyfred.hw9.adapter.PhotosAdapter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PhotosFragment extends Fragment {

    private List<Bitmap> mList;
    protected GeoDataClient mGeoDataClient;
    private int photo_number;
    private ListView lv_photos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, null);
        lv_photos = view.findViewById(R.id.lv_photos);
        mList = new ArrayList<Bitmap>();
        mGeoDataClient = Places.getGeoDataClient(getContext());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String json = bundle.getString("json");
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                JSONObject jsonRes = jsonObject.getJSONObject("result");
                String id = jsonRes.getString("place_id");
                getPhotos(id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    private void getPhotos(String id) {
        final String placeId = id;
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                photo_number = photoMetadataBuffer.getCount();
                for(int i = 0; i < photo_number; i++) {
                    // Get the first photo in the list.
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            int width = bitmap.getWidth();
                            int height = bitmap.getHeight();
                            float scale = ((float) height) / width;
                            int newHeight = (int) (990 * scale);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 990, newHeight, false);
                            mList.add(bitmap);
                            if(mList.size() == photo_number) {
                                PhotosAdapter adapter = new PhotosAdapter(getContext(), mList);
                                lv_photos.setAdapter(adapter);
                            }
                        }
                    });
                }
            }
        });
    }
}