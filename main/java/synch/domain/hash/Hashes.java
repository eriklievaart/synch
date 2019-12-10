package synch.domain.hash;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.eriklievaart.toolkit.io.api.Console;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.io.api.sha1.Sha1;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.google.inject.Inject;

public class Hashes {

	@Inject
	private HashIO io;

	private LogTemplate log = new LogTemplate(getClass());

	public void hash(HashContext context) throws IOException {
		io.load(context);

		while (context.hasNext()) {
			File file = context.next();

			if (context.isCheckpoint()) {
				context.timestampStartSave();
				saveHashes(context);
				context.timestampEndSave();
			}
			logProgress(context);
			hashFile(file, context);
		}
		saveHashes(context);
		Console.println("hashing complete");
	}

	private void saveHashes(HashContext context) throws IOException {
		io.save(context);
	}

	private void hashFile(File file, HashContext context) {
		if (!file.isFile()) {
			log.warn("not a file: $", file);
			return;
		}
		String rootPath = context.getRootPath().replace('\\', '/');
		String path = file.getAbsolutePath().replace('\\', '/');
		Check.isTrue(path.startsWith(rootPath), "error: $ does not start with $", path, rootPath);

		String relativePath = path.substring(rootPath.length());
		long fileSize = file.length();
		if (context.contains(relativePath, fileSize)) {
			log.info("skipping " + relativePath);
			context.incrementSkip();
			return;
		}

		try {
			String hash = Sha1.hash(file);
			context.add(new FileHash(relativePath, fileSize, hash));

		} catch (RuntimeIOException e) {
			log.warn("Cannot hash file %", e, file);
			context.add(e);
		}
	}

	private void logProgress(HashContext context) {
		int skipped = context.getSkipCount();
		int total = context.getFileCount();
		int index = context.getIndex();
		int remaining = total - index;
		int percent = 100 * index / total;

		long spent = System.currentTimeMillis() - context.getStartTimestamp();
		long eta = index == 0 ? Long.MAX_VALUE : remaining * spent / (index - skipped);

		String etaHuman = DurationFormatUtils.formatDurationHMS(eta);
		Console.println("hashing $ ($/$)  eta $ms => $", percent + "%", index, total, etaHuman, context.getFile());
	}
}