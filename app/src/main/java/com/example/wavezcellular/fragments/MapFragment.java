package com.example.wavezcellular.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wavezcellular.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment {
    private GoogleMap myGoogleMap;

    private OnMapReadyCallback callback = googleMap ->  { myGoogleMap = googleMap; };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        return inflater.inflate(R.layout.fragment_map,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Initialize map fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        //Async map
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void addMarkerToMap(double lon, double lat, String addressName){
        LatLng location = new LatLng(lon, lat);
        myGoogleMap.addMarker(new MarkerOptions().position(location).title(addressName));
        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}