package synch.domain.location;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.eriklievaart.toolkit.lang.api.ObservableDelegate;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SynchLocations extends ObservableDelegate implements Observer {

	private SynchLocationsIO io;

	private LogTemplate log = new LogTemplate(getClass());
	private List<SynchLocation> locations = NewCollection.list();

	@Inject
	public SynchLocations(SynchLocationsIO io) throws IOException {
		this.io = io;
		for (SynchLocation location : io.load()) {
			locations.add(location);
		}
		addObserver(this);
	}

	public void add(SynchLocation location) {
		locations.add(location);
		changeAndnotifyObservers();
	}

	public SynchLocation[] getSynchLocations() {
		return locations.toArray(new SynchLocation[] {});
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		try {
			io.save(locations);
		} catch (IOException e) {
			log.warn("Unable to save locations: " + e.getMessage(), e);
		}
	}
}
