package com.eriklievaart.synch.mirror;

import java.util.List;
import java.util.Optional;

import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class DeleteJob extends MirrorJob {

	private VirtualFile root;

	public DeleteJob(VirtualFile root, List<String> paths) {
		super("delete queued files from " + root.getPath(), paths);
		this.root = root;
	}

	@Override
	protected void accept(String path) {
		System.out.println(getProgress() + " deleting: " + path);
		VirtualFile file = root.resolve(path);
		delete(file);
	}

	private static void delete(VirtualFile file) {
		file.getParentFile().get().resolve(file.getName() + ".smeta").delete();
		file.delete();

		Optional<? extends VirtualFile> optional = file.getParentFile();
		if (optional.isPresent()) {
			VirtualFile parent = optional.get();
			if (parent.isDirectory() && parent.getChildren().isEmpty()) {
				delete(parent);
			}
		}
	}

}
