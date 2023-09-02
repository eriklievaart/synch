package com.eriklievaart.synch.mirror.job;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import com.eriklievaart.toolkit.io.api.Console;
import com.eriklievaart.toolkit.io.api.properties.PropertiesIO;
import com.eriklievaart.toolkit.io.api.sha1.Sha1;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class ChangedJob extends CopyJob {

	public ChangedJob(VirtualFile from, VirtualFile to, List<String> paths) {
		super(from, to, paths);
		title = "update queued files in " + to.getPath() + " from source";
	}

	@Override
	public List<String> filterValidPaths() {
		List<String> filtered = NewCollection.list();

		for (String path : getAllPaths()) {
			VirtualFile from = fromRoot.resolve(path);
			VirtualFile to = toRoot.resolve(path);
			if (!to.exists()) {
				continue; // was not copied as part of CopyJob, so should not copy now
			}
			Map<String, String> meta = getMetadata(path);
			long size = meta.containsKey("size") ? Long.parseLong(meta.get("size")) : to.length();
			long stamp = meta.containsKey("timestamp") ? Long.parseLong(meta.get("timestamp")) : to.lastModified();

			if (from.lastModified() > stamp) {
				filtered.add(path);

			} else if (from.length() != size) {
				Console.println("*warning*: file sizes do not match for %", path);
			}
		}
		System.out.println();
		for (VirtualFile child : toRoot.getChildren()) {
			System.out.println(child.getPath());
		}
		System.out.println();

		return filtered;
	}

	private Map<String, String> getMetadata(String path) {
		VirtualFile source = fromRoot.resolve(path);
		VirtualFile destination = toRoot.resolve(path);
		VirtualFile smeta = toRoot.resolve(path + ".smeta");

		if (smeta.exists()) {
			return PropertiesIO.loadStrings(smeta.getContent().getInputStream());
		}
		System.out.println("hashing " + path.replaceAll("[^\\p{Print}]", ""));
		String sha1 = metadata ? Sha1.hash(destination.getContent().getInputStream()) : "";
		return createMetadata(source, sha1, path);
	}

	@Override
	public Color getButtonColor() {
		return Color.ORANGE;
	}
}
