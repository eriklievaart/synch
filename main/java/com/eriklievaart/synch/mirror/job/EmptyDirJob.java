package com.eriklievaart.synch.mirror.job;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class EmptyDirJob extends MirrorJob {

	private VirtualFile to;

	public EmptyDirJob(VirtualFile to, Collection<String> paths) {
		super(new ArrayList<>(paths));
		this.to = to;
		this.title = "create empty dirs in " + to.getPath();
	}

	@Override
	protected void accept(String path) {
		VirtualFile file = to.resolve(path);
		Optional<? extends VirtualFile> optional = file.getParentFile();
		if (optional.isPresent()) {
			VirtualFile parent = optional.get();
			if (parent.exists()) {
				file.mkdir();
			}
		}
	}

	@Override
	public Color getButtonColor() {
		return null;
	}
}
