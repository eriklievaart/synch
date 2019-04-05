package com.eriklievaart.synch.mirror.job;

import java.util.List;

import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.str.Str;

public abstract class MirrorJob {

	private List<String> paths;
	public String title;
	public String autoAcceptProperty;
	private int total = 0;
	private int current = 0;

	public MirrorJob(List<String> paths) {
		this.paths = ListTool.sortedCopy(paths);
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

	public List<String> getAllPaths() {
		return paths;
	}

	/**
	 * Override this method to filter a subset of all paths to process
	 */
	public List<String> filterValidPaths() {
		return paths;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
