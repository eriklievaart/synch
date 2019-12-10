package synch.domain.hash;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.collection.MultiMap;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class HashCollection {
	private LogTemplate log = new LogTemplate(getClass());

	private Map<String, FileHash> pathIndex = NewCollection.mapNotNull();
	private MultiMap<String, FileHash> hashIndex = new MultiMap<>();

	public HashCollection(List<FileHash> hashes) {
		for (FileHash hash : hashes) {
			pathIndex.put(hash.getRelativePath(), hash);
			hashIndex.add(hash.getHash(), hash);
		}
	}

	public List<FileHash> getHashes() {
		return new ArrayList<>(hashIndex.values());
	}

	public FileHash getHash(String relativePath) {
		return pathIndex.get(relativePath);
	}

	public List<FileHash> getHashes(String hash) {
		return hashIndex.get(hash);
	}

	public HashCollection filterOnPath(String filterPath) {
		String path = UrlTool.addSlash(filterPath);
		log.info("Filtering hash collection on path: $", path);
		List<FileHash> keep = NewCollection.list();

		Iterator<String> iterator = pathIndex.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (key.startsWith(path)) {
				FileHash original = pathIndex.get(key);
				String tail = original.getRelativePath().substring(path.length());
				keep.add(new FileHash(tail, original.getFileSize(), original.getHash()));
			}
		}
		log.info("Filtering complete, keeping $ of $ entries", keep.size(), pathIndex.size());
		return new HashCollection(keep);
	}
}