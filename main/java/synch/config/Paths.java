package synch.config;

import java.io.File;

import com.eriklievaart.toolkit.io.api.JvmPaths;

public class Paths {

	private static final File RUN_DIR = new File(JvmPaths.getJarDirOrRunDir(Paths.class));
	private static final File LOCATIONS_CONFIG_FILE = new File(RUN_DIR, "locations.properties");
	private static final File STORE_HASH_DIR = new File(RUN_DIR, "hashes");

	public File getLocationsConfigFile() {
		return LOCATIONS_CONFIG_FILE;
	}

	public File getStoreHashDir() {
		return STORE_HASH_DIR;
	}
}
