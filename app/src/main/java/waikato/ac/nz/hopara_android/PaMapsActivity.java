package waikato.ac.nz.hopara_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import waikato.ac.nz.hopara_android.map.MapTileDimension;
import waikato.ac.nz.hopara_android.map.MapTileProvider;
import waikato.ac.nz.hopara_android.util.ActivityUtils;

public class PaMapsActivity extends FragmentActivity
		implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
	public static int Z_INDEX_MAP_TILES = 1;
	public static int Z_INDEX_MARKER = 2;

	private GoogleMap mMap;
	private Map<String, LatLng> coords;
	private ArrayList<Marker> markers;

	/** Key of the selected location, e.g., user gets here from a location-specific activity */
	private String selectedLocation = null;

	public PaMapsActivity() {
		// add markers
		coords = new HashMap<>();
		coords.put(PuketeActivity.KEY, new LatLng(-37.737051, 175.237474));
		coords.put(KirikiriroaActivity.KEY, new LatLng(-37.782704, 175.28051));
		coords.put("Te Owhanga Pa", new LatLng(-37.737971, 175.251428));
		coords.put("Matakanoki Pa", new LatLng(-37.763188, 175.264215));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pa_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		Bundle data = getIntent().getExtras();
		Serializable serializable = data != null ? data.getSerializable(ActivityUtils.LOCATION_KEY) : null;
		if (serializable != null && serializable instanceof String) {
			selectedLocation = (String) serializable;
		}
	}


	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.setOnMarkerClickListener(this);
		setupMarkers();
		setupMapTiles();
	}

	private void setupMapTiles() {
		mMap.addTileOverlay(new TileOverlayOptions()
				.tileProvider(new MapTileProvider(getResources().getAssets(), MapTileDimension.getInstance()))
				.zIndex(Z_INDEX_MAP_TILES));
	}

	private void setupMarkers() {
		markers = new ArrayList<>();

		LatLng position = null;

		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (String key : coords.keySet()) {
			LatLng latLng = coords.get(key);
			Marker marker = mMap.addMarker(getMarkerOptions(key, latLng));
			marker.setTitle(key);
			marker.showInfoWindow();
			markers.add(marker);
			builder.include(marker.getPosition());
			if (isSelectedLocation(key)) {
				position = latLng;
			}
		}

		// zoom in on marker group if no location was selected
		if (position == null) {
			position = builder.build().getCenter();
		}
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(position)
				.zoom(14).build();
		mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	private MarkerOptions getMarkerOptions(String key, LatLng lm) {
		int icon = isSelectedLocation(key) ? R.drawable.pin_red : R.drawable.pin_black;
		BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(icon);
		return new MarkerOptions()
				.position(lm)
				.zIndex(Z_INDEX_MARKER)
				.icon(markerIcon);
	}

	private boolean isSelectedLocation(String key) {
		return selectedLocation != null && selectedLocation.equals(key);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Intent intent = null;
		switch (marker.getTitle()) {
			case "Pukete Pa":
				intent = new Intent(this, PuketeActivity.class);
				break;
			case "Kirikiriroa Pa":
				intent = new Intent(this, KirikiriroaActivity.class);
				break;
			case "Te Owhanga Pa":
				break;
			case "Matakanoki Pa":
				break;
		}
		if (intent != null) {
			this.startActivity(intent);
		}
		return false;
	}
}
