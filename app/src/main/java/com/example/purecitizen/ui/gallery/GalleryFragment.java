package com.example.purecitizen.ui.gallery;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.purecitizen.R;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class GalleryFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView longitude_test =  root.findViewById(R.id.longitude_test);
        final TextView latitude_test =  root.findViewById(R.id.latitude_test);
        final Button button_test_location =  root.findViewById(R.id.button_test_location);
        button_test_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmartLocation.with(getContext()).location()
                        .start(new OnLocationUpdatedListener() {
                            @Override
                            public void onLocationUpdated(Location location) {
                                longitude_test.setText(location.getLongitude() + "");
                                latitude_test.setText(location.getLatitude() + "");
                                SmartLocation.with(getContext()).location().stop();
                            }
                        });
            }
        });

        return root;
    }
}
