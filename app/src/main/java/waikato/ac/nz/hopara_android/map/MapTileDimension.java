package waikato.ac.nz.hopara_android.map;

import android.graphics.Rect;
import android.util.SparseArray;

public class MapTileDimension {
	private final SparseArray<Rect> dimension;
	private static MapTileDimension instance;

	public MapTileDimension() {
		dimension = new SparseArray<>();

		addBoundingBox(14, 16168, 10051, 16169, 10052);
		addBoundingBox(15, 32337, 20103, 32339, 20105);
		addBoundingBox(16, 64675, 40207, 64678, 40210);
	}

	public static MapTileDimension getInstance() {
		if (instance == null) {
			instance = new MapTileDimension();
		}
		return instance;
	}

	public static void setInstance(MapTileDimension instance) {
		MapTileDimension.instance = instance;
	}

	/**
	 * Add the bounding box of the specified zoom level.
	 *
	 * @param zoom
	 * 		zoom level
	 * @param left
	 * 		The X coordinate of the left side of the rectangle
	 * @param top
	 * 		The Y coordinate of the top of the rectangle
	 * @param right
	 * 		The X coordinate of the right side of the rectangle
	 * @param bottom
	 * 		The Y coordinate of the bottom of the rectangle
	 */
	public void addBoundingBox(int zoom, int left, int top, int right, int bottom) {
		addBoundingBox(zoom, new Rect(left, top, right, bottom));
	}

	/**
	 * Add the bounding box of the specified zoom level.
	 *
	 * @param zoom
	 * 		zoom level
	 * @param boundingBox
	 * 		bounding box
	 */
	public void addBoundingBox(int zoom, Rect boundingBox) {
		dimension.append(zoom, boundingBox);
	}

	public Rect getBoundingBox(int zoom) {
		return null;
	}

	public boolean hasTile(int x, int y, int zoom) {
		Rect b = dimension.get(zoom);
		return b != null && (b.left <= x && x <= b.right && b.top <= y && y <= b.bottom);
	}
}
