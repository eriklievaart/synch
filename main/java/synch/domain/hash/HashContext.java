package synch.domain.hash;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.collection.FromCollection;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

import synch.domain.location.SynchLocation;

public class HashContext {
	private static final int MINUTE = 60 * 1000;
	private static final long CHECKPOINT_TIME = 5 * MINUTE;

	private SynchLocation location;
	private List<RuntimeIOException> ioExceptions = NewCollection.list();
	private Map<String, FileHash> pathToHash = NewCollection.mapNotNull();
	private List<File> files;
	private long startTimestamp;

	private int index = 0;
	private int skipped = 0;
	private long timestampStartSave = System.currentTimeMillis();
	private long timestampEndSave = System.currentTimeMillis();

	public HashContext(SynchLocation location) {
		this.location = location;
		File root = new File(location.getLocation());
		files = new ArrayList<>(FileUtils.listFiles(root, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));
		startTimestamp = System.currentTimeMillis();
	}

	public File getRoot() {
		return new File(location.getLocation());
	}

	public String getRootPath() {
		return location.getLocation();
	}

	public void add(FileHash hash) {
		pathToHash.put(hash.getRelativePath(), hash);
	}

	public void add(RuntimeIOException e) {
		ioExceptions.add(e);
	}

	public List<FileHash> getHashes() {
		return FromCollection.toList(pathToHash.values());
	}

	public String getSynchLocationName() {
		return location.getName();
	}

	public int getFileCount() {
		return files.size();
	}

	public File getFile() {
		return files.get(index - 1);
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public boolean contains(String relativePath, long fileSize) {
		return pathToHash.containsKey(relativePath) && pathToHash.get(relativePath).getFileSize() == fileSize;
	}

	public void incrementSkip() {
		skipped++;
	}

	public File next() {
		return files.get(index++);
	}

	public int getSkipCount() {
		return skipped;
	}

	public int getIndex() {
		return index;
	}

	public boolean hasNext() {
		return index < files.size();
	}

	public boolean isCheckpoint() {
		long noSaveTime = System.currentTimeMillis() - timestampEndSave;
		long saveDuration = timestampEndSave - timestampStartSave;
		return noSaveTime > CHECKPOINT_TIME + 20 * saveDuration;
	}

	public void timestampStartSave() {
		timestampStartSave = System.currentTimeMillis();
	}

	public void timestampEndSave() {
		timestampEndSave = System.currentTimeMillis();
	}
}
