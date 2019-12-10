package synch.domain.hash;

import java.util.Collections;
import java.util.List;

import com.eriklievaart.toolkit.lang.api.Obj;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class HashCompare {

	private LogTemplate log = new LogTemplate(getClass());

	private List<FileHash> valid = NewCollection.list();
	private List<FileHash> moved = NewCollection.list();
	private List<FileHash> missing = NewCollection.list();
	private List<FileHash> corrupt = NewCollection.list();

	public HashCompare(HashCollection from, HashCollection to) {
		CheckCollection.notEmpty(from.getHashes());
		CheckCollection.notEmpty(to.getHashes());

		for (FileHash expected : from.getHashes()) {
			FileHash toHash = to.getHash(expected.getRelativePath());

			if (toHash != null) {
				indexFileInSameLocation(expected, toHash);
			} else {
				indexMissingFile(to, expected);
			}
		}
		sort();
		dump();
	}

	private void sort() {
		Collections.sort(valid, new FileHashComparator());
		Collections.sort(moved, new FileHashComparator());
		Collections.sort(corrupt, new FileHashComparator());
		Collections.sort(missing, new FileHashComparator());
	}

	private void dump() {
		log.info("## valid file count: $ ##", valid.size());
		log.info("## missing file count: $ ##", missing.size());
		for (FileHash hash : missing) {
			log.info("missing: " + hash.getRelativePath());
		}
		log.info("## corrupt file count: $ ##", corrupt.size());
		for (FileHash hash : corrupt) {
			log.info("corrupt: " + hash.getRelativePath());
		}
		log.info("## end of comparison ##");
	}

	private void indexFileInSameLocation(FileHash expected, FileHash toHash) {
		if (Obj.equals(expected.getHash(), toHash.getHash())) {
			valid.add(expected);
		} else {
			corrupt.add(expected);
		}
	}

	private void indexMissingFile(HashCollection to, FileHash expected) {
		if (isMoved(expected, to)) {
			moved.add(expected);
		} else {
			missing.add(expected);
		}
	}

	private boolean isMoved(FileHash expected, HashCollection to) {
		for (FileHash match : to.getHashes(expected.getHash())) {
			if (match.getFileSize() == expected.getFileSize()) {
				log.info("$ appears to have moved to $", expected.getRelativePath(), match.getRelativePath());
				return true;
			}
		}
		return false;
	}
}