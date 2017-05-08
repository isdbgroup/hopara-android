package waikato.ac.nz.hopara_android.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

public class ActivityUtils {
	public static final String LOCATION_KEY = "LOCATION_KEY";

	private ActivityUtils() {
	}

	/**
	 * Start the given activity class.
	 *
	 * @param context
	 * 		source context
	 * @param clazz
	 * 		class of the activity to be started
	 * @param key
	 * 		key for passing {@code data} in a {@link Bundle} to the activity (optional)
	 * @param data
	 * 		data to be passed (only if {@code key} is not null
	 */
	public static void startActivity(Context context, Class clazz, String key, Serializable data) {
		Intent intent = new Intent(context, clazz);
		if (key != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable(key, data);
			intent.putExtras(bundle);
		}
		context.startActivity(intent);
	}

	public static void startActivity(Context context, Class clazz) {
		startActivity(context, clazz, null, null);
	}
}
