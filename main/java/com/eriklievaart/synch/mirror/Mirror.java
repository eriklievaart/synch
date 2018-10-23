package com.eriklievaart.synch.mirror;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.eriklievaart.toolkit.io.api.Console;
import com.eriklievaart.toolkit.io.api.SystemProperties;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.vfs.api.VirtualFileScanner;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class Mirror {

	public static void synch(VirtualFile from, VirtualFile to) {
		Check.isTrue(from.isDirectory(), "not a directory: " + from);
		Check.isTrue(to.isDirectory(), "not a directory: " + to);

		Set<String> sourcePaths = listPaths(from);
		Set<String> destinationPaths = listPaths(to);

		List<String> delete = entriesMissingInFirstSet(sourcePaths, destinationPaths);
		List<String> copy = entriesMissingInFirstSet(destinationPaths, sourcePaths);
		Console.println("$ files to delete $ files to copy", delete.size(), copy.size());

		QueueSelector selector = new QueueSelector();
		executeOrQueue(selector, new DeleteJob(to, delete), "autodelete");
		executeOrQueue(selector, new CopyJob(from, to, copy), "autocopy");
	}

	private static void executeOrQueue(QueueSelector selector, MirrorJob job, String autoProperty) {
		if (job.paths.isEmpty()) {
			return;
		}
		if (SystemProperties.isSet(autoProperty, "true")) {
			job.consume(job.paths);
		} else {
			selector.addJob(job);
		}
	}

	private static List<String> entriesMissingInFirstSet(Set<String> sourcePaths, Set<String> destinationPaths) {
		List<String> missingInSource = NewCollection.list();
		for (String destination : destinationPaths) {
			if (!sourcePaths.contains(destination)) {
				missingInSource.add(destination);
			}
		}
		return missingInSource;
	}

	static Set<String> listPaths(VirtualFile directory) {
		Set<String> paths = new TreeSet<>();
		VirtualFileScanner scanner = new VirtualFileScanner(directory);
		scanner.addDirectoryFilter(file -> !file.getName().toLowerCase().startsWith(".trash"));

		for (VirtualFile child : scanner) {
			String path = directory.getRelativePathOf(child);
			if (Str.isBlank(path)) {
				Check.notBlank(path, "Relative path is empty: $", child.getPath());
			}
			paths.add(path);
		}
		Console.println("indexed $ files in $", paths.size(), directory.getPath());
		return paths;
	}
}
