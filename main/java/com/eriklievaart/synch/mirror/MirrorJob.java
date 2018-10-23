package com.eriklievaart.synch.mirror;

import java.util.List;

import com.eriklievaart.toolkit.lang.api.str.Str;

public abstract class MirrorJob {

	public List<String> paths;
	public String title;
	public String autoAcceptProperty;
	private int total = 0;
	private int current = 0;

	public MirrorJob(String title, List<String> paths) {
		this.title = title;
		this.paths = paths;
	}

	public void consume(List<String> selectedPaths) {
		total = selectedPaths.size();
		for (String path : selectedPaths) {
			current++;
			accept(path);
		}
	}

	protected abstract void accept(String path);

	protected String getProgress() {
		long percentage = Math.round(100.0 * current / total);
		return Str.sub("$$ ($/$)", percentage, "%", current, total);
	}
}
