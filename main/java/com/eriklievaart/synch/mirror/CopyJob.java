package com.eriklievaart.synch.mirror;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.eriklievaart.toolkit.io.api.PropertiesIO;
import com.eriklievaart.toolkit.io.api.sha1.Sha1;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class CopyJob extends MirrorJob {

	private VirtualFile fromRoot;
	private VirtualFile toRoot;
	private boolean metadata = true;

	public CopyJob(VirtualFile from, VirtualFile to, List<String> paths) {
		super("copy queued files to " + to.getPath(), paths);
		this.fromRoot = from;
		this.toRoot = to;
	}

	@Override
	protected void accept(String path) {
		System.out.println(getProgress() + " copying: " + path);
		VirtualFile source = fromRoot.resolve(path);
		VirtualFile destination = toRoot.resolve(path);

		destination.getParentFile().get().mkdir();
		InputStream is = source.getContent().getInputStream();
		String sha1 = Sha1.sha1CopyAndHash(is, destination.getContent().getOutputStream());
		if (metadata) {
			Properties properties = new Properties();
			properties.put("sha1", sha1);
			properties.put("modified", "" + source.lastModified());
			PropertiesIO.store(properties, toRoot.resolve(path + ".smeta").getContent().getOutputStream());
		}
	}

	public void setMetadata(boolean metadata) {
		this.metadata = metadata;
	}
}