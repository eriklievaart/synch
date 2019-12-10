package com.eriklievaart.synch.mirror;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.eriklievaart.synch.mirror.job.ChangedJob;
import com.eriklievaart.synch.mirror.job.CopyJob;
import com.eriklievaart.synch.mirror.job.DeleteJob;
import com.eriklievaart.synch.mirror.job.EmptyDirJob;
import com.eriklievaart.synch.mirror.job.MirrorJob;
import com.eriklievaart.toolkit.io.api.Console;
import com.eriklievaart.toolkit.io.api.SystemProperties;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.vfs.api.VirtualFileScanner;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFileChildren;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFileType;

public class Mirror {

	public static void synch(VirtualFile from, VirtualFile to, boolean metadata) {
		Check.isTrue(from.isDirectory(), "not a directory: " + from);
		Check.isTrue(to.isDirectory(), "not a directory: " + to);
		Check.isFalse(from.getPath().equals(to.getPath()), "cannot compare to itself $", from.getPath());

		Set<String> sourcePaths = listPaths(from);
		Collection<String> emptyDirPaths = listEmptyDirPaths(from, from);
		Set<String> destinationPaths = listPaths(to);
		removeExistingPaths(to, emptyDirPaths);

		List<String> delete = entriesMissingInFirstSet(sourcePaths, destinationPaths);
		List<String> copy = entriesMissingInFirstSet(destinationPaths, sourcePaths);
		Console.println("$ files to delete $ files to copy", delete.size(), copy.size());

		CopyJob copyJob = new CopyJob(from, to, copy);
		copyJob.setMetadata(metadata);
		ChangedJob changedJob = new ChangedJob(from, to, new ArrayList<>(sourcePaths));
		changedJob.setMetadata(metadata);
		EmptyDirJob emptyDirJob = new EmptyDirJob(to, emptyDirPaths);

		QueueSelector selector = new QueueSelector();
		executeOrQueue(selector, new DeleteJob(to, delete), "autodelete");
		executeOrQueue(selector, copyJob, "autocopy");
		executeOrQueue(selector, changedJob, "autocopy");
		executeOrQueue(selector, emptyDirJob, "autocopy");
	}

	private static void removeExistingPaths(VirtualFile to, Collection<String> paths) {
		Iterator<String> iterator = paths.iterator();
		while (iterator.hasNext()) {
			String path = iterator.next();
			if (to.resolve(path).exists()) {
				iterator.remove();
			}
		}
	}

	private static Collection<String> listEmptyDirPaths(VirtualFile root, VirtualFile directory) {
		VirtualFileChildren children = directory.getChildrenAdvanced().noError();
		if (children.get().isEmpty()) {
			Check.isFalse(root.equals(directory), "Cannot mirror from empty directory, just delete the files");
			return ListTool.of(root.getRelativePathOf(directory));
		}
		Set<String> paths = NewCollection.set();
		if (!directory.getName().toLowerCase().startsWith(".trash")) {
			for (VirtualFile child : children.get(VirtualFileType.DIRECTORY)) {
				paths.addAll(listEmptyDirPaths(root, child));
			}
		}
		return paths;
	}

	private static void executeOrQueue(QueueSelector selector, MirrorJob job, String autoProperty) {
		System.out.println("starting job: " + job.getClass().getName());

		if (SystemProperties.isSet(autoProperty, "true")) {
			job.consume(job.filterValidPaths());

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
		Console.println("$ listing paths", directory.getPath());
		Set<String> paths = new TreeSet<>();
		VirtualFileScanner scanner = new VirtualFileScanner(directory);
		scanner.addDirectoryFilter(file -> !file.getName().toLowerCase().startsWith(".trash"));
		scanner.addFileFilter(file -> !file.getUrl().getExtension().equalsIgnoreCase("smeta"));

		for (VirtualFile child : scanner) {
			String path = directory.getRelativePathOf(child);
			if (Str.isBlank(path)) {
				Check.notBlank(path, "Relative path is empty: $", child.getPath());
			}
			paths.add(path);
		}
		Console.println("$ indexed $ files", directory.getPath(), paths.size());
		return paths;
	}
}