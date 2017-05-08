package waikato.ac.nz.hopara_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import waikato.ac.nz.hopara_android.util.ActivityUtils;

public class PuketeActivity extends AppCompatActivity {
	public static final String KEY = "Pukete Pa";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pukete);
	}

	public void startMap(View view) {
		ActivityUtils.startActivity(this, PaMapsActivity.class, ActivityUtils.LOCATION_KEY, KEY);
	}

	public void goHome(View view) {
		ActivityUtils.startActivity(this, MainScreenActivity.class);
	}
}
