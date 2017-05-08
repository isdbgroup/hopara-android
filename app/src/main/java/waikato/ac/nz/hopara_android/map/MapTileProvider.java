package waikato.ac.nz.hopara_android.map;

import android.content.res.AssetManager;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapTileProvider implements TileProvider {
	private static final int TILE_WIDTH = 256;
	private static final int TILE_HEIGHT = 256;
	private static final int BUFFER_SIZE = 16 * 1024;
	private static final String MAP_DIR = "map";

	/** asset manager for retrieving the tiles from disk */
	private final AssetManager assetManager;

	/** whether the folder map exists */
	private final boolean mapFolderExists;

	/** bounding boxes of the available map tiles */
	private final MapTileDimension boundingBox;

	public MapTileProvider(AssetManager assets, MapTileDimension boundingBox) {
		this.assetManager = assets;
		this.boundingBox = boundingBox;

		// check for the existence of the folder containing the map tiles
		boolean exists;
		try {
			List files = new ArrayList();
			Collections.addAll(files, assetManager.list(""));
			exists = files.contains(MAP_DIR);
		} catch (IOException e) {
			exists = false;
		}
		mapFolderExists = exists;
	}

	@Override
	public Tile getTile(int x, int y, int zoom) {
		if (mapFolderExists && boundingBox != null && boundingBox.hasTile(x, y, zoom)) {
			byte[] image = readTileImage(x, y, zoom);
			return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
		}
		return NO_TILE;
	}

	/**
	 * @param x
	 * 		x coordinate
	 * @param y
	 * 		y coordinate
	 * @param zoom
	 * 		zoom level
	 * @return read the bites of the tile image from the disk
	 */
	private byte[] readTileImage(int x, int y, int zoom) {
		InputStream in = null;
		ByteArrayOutputStream buffer = null;

		try {
			in = assetManager.open(getTileFilename(x, y, zoom));
			buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[BUFFER_SIZE];

			while ((nRead = in.read(data, 0, BUFFER_SIZE)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();

			return buffer.toByteArray();
		} catch (IOException ignored) {
			return null;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception ignored) {
				}
			}
			if (buffer != null) {
				try {
					buffer.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	/**
	 * @param x
	 * 		x coordinate
	 * @param y
	 * 		y coordinate
	 * @param zoom
	 * 		zoom level
	 * @return filename of the tile image
	 */
	private String getTileFilename(int x, int y, int zoom) {
		return MAP_DIR + "/" + zoom + '/' + x + '/' + y + ".png";
	}
}