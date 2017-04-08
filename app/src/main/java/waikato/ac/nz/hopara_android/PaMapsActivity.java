package waikato.ac.nz.hopara_android;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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

public class PaMapsActivity extends FragmentActivity implements OnMapReadyCallback {

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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //        LatLng sydney = new LatLng(-34, 151);
        //        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //
        setUpMap();

    }

    private void setUpMap(){

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
            Assert.assertNotNull(m);
            markers.add(m);
            builder.include(m.getPosition());
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(markers.get(0).getPosition()));
        // zoom in on marker group
        //LatLngBounds bounds = builder.build();
        //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));

    }
}
