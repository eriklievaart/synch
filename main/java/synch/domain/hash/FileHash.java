package synch.domain.hash;

import java.util.Arrays;
import java.util.List;

import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;

public class FileHash {

	private String relativePath;
	private long fileSize;
	private String hash;

	public FileHash(String relativePath, long fileSize, String hash) {
		this.relativePath = relativePath;
		this.fileSize = fileSize;
		this.hash = hash;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public long getFileSize() {
		return fileSize;
	}

	public String getHash() {
		return hash;
	}

	public List<String> toCsv() {
		return Arrays.asList(relativePath, "" + fileSize, hash);
	}

	public static FileHash fromCsv(List<String> fields) {
		CheckCollection.isSize(fields, 3);
		return new FileHash(fields.get(0), Long.valueOf(fields.get(1)), fields.get(2));
	}

	@Override
	public String toString() {
		return ToString.simple(this, "${$}[$,$]", relativePath, fileSize, hash);
	}
}