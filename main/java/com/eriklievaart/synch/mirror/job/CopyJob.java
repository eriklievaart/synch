package com.eriklievaart.synch.mirror.job;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.eriklievaart.toolkit.io.api.PropertiesIO;
import com.eriklievaart.toolkit.io.api.sha1.Sha1;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class CopyJob extends MirrorJob {

	protected VirtualFile fromRoot;
	protected VirtualFile toRoot;
	protected boolean metadata = true;

	public CopyJob(VirtualFile from, VirtualFile to, List<String> paths) {
		super(paths);
		this.fromRoot = from;
		this.toRoot = to;
		this.title = "copy queued files to " + to.getPath();
	}

	@Override
	protected void accept(String path) {
		System.out.println(getProgress() + " copying: " + path);
		VirtualFile source = fromRoot.resolve(path);
		VirtualFile destination = toRoot.resolve(path);

		destination.getParentFile().get().mkdir();
		InputStream is = source.getContent().getInputStream();
		String sha1 = Sha1.sha1CopyAndHash(is, destination.getContent().getOutputStream());
		destination.setLastModified(source.lastModified());

		createMetadata(source, sha1, path);
	}

	protected Map<String, String> createMetadata(VirtualFile source, String sha1, String path) {
		Map<String, String> properties = createMetadata(source, sha1);
		if (metadata) {
			PropertiesIO.storeStrings(properties, toRoot.resolve(path + ".smeta").getContent().getOutputStream());
		}
		return properties;
	}

	protected Map<String, String> createMetadata(VirtualFile source, String sha1) {
		Map<String, String> properties = NewCollection.map();
		properties.put("sha1", sha1);
		properties.put("size", "" + source.length());
		properties.put("modified", "" + source.lastModified());
		return properties;
	}

	public void setMetadata(boolean metadata) {
		this.metadata = metadata;
	}
}