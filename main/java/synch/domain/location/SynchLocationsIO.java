package synch.domain.location;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.eriklievaart.toolkit.convert.api.ConversionException;
import com.eriklievaart.toolkit.io.api.properties.PropertiesIO;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.google.inject.Inject;

import synch.config.Paths;

public class SynchLocationsIO {

	@Inject
	private Paths paths;

	public void save(Collection<SynchLocation> locations) throws IOException {
		Map<String, String> map = NewCollection.mapNotNull();
		for (SynchLocation location : locations) {
			map.put(location.getName(), location.getLocation());
		}
		PropertiesIO.storeStrings(map, paths.getLocationsConfigFile());
	}

	public List<SynchLocation> load() throws IOException {
		File file = paths.getLocationsConfigFile();
		List<SynchLocation> locations = NewCollection.list();

		Map<String, String> map = PropertiesIO.loadStrings(file);
		for (String key : map.keySet()) {
			try {
				locations.add(new SynchLocation(key, map.get(key)));
			} catch (ConversionException e) {
				throw new IOException(Str.sub("Config file $ corrupt; $", file, e.getMessage()), e);
			}
		}
		return locations;
	}
}