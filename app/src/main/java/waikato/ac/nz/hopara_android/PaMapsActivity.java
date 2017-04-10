package waikato.ac.nz.hopara_android;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Map<String, LatLng> coords;
    private ArrayList<Marker> markers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pa_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        setUpMarkers();
    }

    private void setUpMarkers(){

        // add markers
        coords = new HashMap<String, LatLng>();
        coords.put("Pukete Pa", new LatLng(-37.737051, 175.237474));
        coords.put("Kirikiriroa Pa", new LatLng(-37.782704, 175.28051));
        coords.put("Te Owhanga Pa", new LatLng(-37.737971, 175.251428));
        coords.put("Matakanoki Pa", new LatLng(-37.763188, 175.264215));

        markers = new ArrayList<Marker>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(String key : coords.keySet()) {
            LatLng lm = coords.get(key);
            Marker m = mMap.addMarker(new MarkerOptions().position(lm));
            m.setTitle(key);
            m.showInfoWindow();
            Assert.assertNotNull(m);
            markers.add(m);
            builder.include(m.getPosition());
        }
        // zoom in on marker group
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = null;
        switch(marker.getTitle()){
            case "Pukete Pa":  intent = new Intent(this, PuketeActivity.class); break;
            case "Kirikiriroa Pa":  intent = new Intent(this, KirikiriroaActivity.class); break;
            case "Te Owhanga Pa": break;
            case "Matakanoki Pa": break;
        }
        if (intent != null){
            this.startActivity(intent);
        }
        return false;
    }

}
