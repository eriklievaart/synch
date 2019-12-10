package synch.domain.hash;

import java.util.Comparator;

public class FileHashComparator implements Comparator<FileHash> {

	@Override
	public int compare(FileHash o1, FileHash o2) {
		return o1.getRelativePath().toLowerCase().compareTo(o2.getRelativePath().toLowerCase());
	}
}