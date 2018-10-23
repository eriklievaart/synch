package com.eriklievaart.synch.mirror;

import java.util.List;

import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class CopyJob extends MirrorJob {

	private VirtualFile fromRoot;
	private VirtualFile toRoot;

	public CopyJob(VirtualFile from, VirtualFile to, List<String> paths) {
		super("copy queued files to " + to.getPath(), paths);
		this.fromRoot = from;
		this.toRoot = to;
	}

	@Override
	protected void accept(String path) {
		System.out.println(getProgress() + " copying: " + path);
		fromRoot.resolve(path).copyTo(toRoot.resolve(path));
	}

}
