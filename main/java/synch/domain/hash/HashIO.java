package synch.domain.hash;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.google.inject.Inject;

import synch.config.Paths;

public class HashIO {
	private LogTemplate log = new LogTemplate(getClass());

	@Inject
	private Paths paths;

	public void save(HashContext context) throws IOException {
		File hashFile = getHashFile(context);
		hashFile.getParentFile().mkdirs();
		log.info("Saving hash file $", hashFile);

		try (FileOutputStream os = new FileOutputStream(hashFile)) {
			CSVPrinter printer = CSVFormat.EXCEL.print(new OutputStreamWriter(new BufferedOutputStream(os)));

			for (FileHash hash : context.getHashes()) {
				printer.printRecord(hash.getRelativePath(), hash.getFileSize(), hash.getHash());
			}
			printer.close();
		}
	}

	public void load(HashContext context) throws IOException {
		for (FileHash hash : load(getHashFile(context))) {
			context.add(hash);
		}
	}

	private List<FileHash> load(File file) throws IOException, FileNotFoundException {
		List<FileHash> hashes = NewCollection.list();
		if (file.isFile()) {
			try (FileInputStream is = new FileInputStream(file)) {
				CSVParser parser = CSVFormat.EXCEL.parse(new InputStreamReader(new BufferedInputStream(is)));
				for (CSVRecord record : parser.getRecords()) {
					String prefix = file.getName().startsWith("oldelements") ? "Anime/" : "";
					hashes.add(new FileHash(prefix + record.get(0), Long.parseLong(record.get(1)), record.get(2)));
				}
			}
		}
		return hashes;
	}

	public Map<String, HashCollection> getIndexes() throws IOException {
		File root = paths.getStoreHashDir();

		Map<String, HashCollection> projectToHashes = NewCollection.mapNotNull();
		for (File file : root.listFiles()) {
			if (file.getName().endsWith(".csv")) {
				String base = StringUtils.substringBeforeLast(file.getName(), ".");
				projectToHashes.put(base, new HashCollection(load(file)));
			}
		}
		return projectToHashes;
	}

	private File getHashFile(HashContext context) {
		return new File(paths.getStoreHashDir(), context.getSynchLocationName() + ".csv");
	}

}
