package waikato.ac.nz.hopara_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
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

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PaMapsActivity extends FragmentActivity
		implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
	public static int Z_INDEX_MAP_TILES = 1;
	public static int Z_INDEX_MARKER = 2;

	private boolean directionsRequested = false;

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
			getDirections();
		}
	}

	private void getDirections() {
		if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
				ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
					this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
		}

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				LatLng lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
				retrieveDirections(lastLocation, coords.get(selectedLocation));
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	private void retrieveDirections(final LatLng from, final LatLng to) {
		if (directionsRequested) {
			return;
		}

		directionsRequested = true;
		String string = getString(R.string.google_maps_key);
		GoogleDirection.withServerKey(string)
				.from(from)
				.to(to)
				.avoid(AvoidType.FERRIES)
				.avoid(AvoidType.HIGHWAYS)
				.execute(new DirectionCallback() {
					@Override
					public void onDirectionSuccess(Direction direction, String rawBody) {
						if (!direction.isOK()) {
							Log.e("Map", direction.getErrorMessage());
							return;
						}

						mMap.addMarker(new MarkerOptions().position(from));
						ArrayList<LatLng> directionPositionList =
								direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
						mMap.addPolyline(DirectionConverter.createPolyline(
								PaMapsActivity.this, directionPositionList, 5, Color.RED));
					}

					@Override
					public void onDirectionFailure(Throwable t) {
						// Do something
					}
				});
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
