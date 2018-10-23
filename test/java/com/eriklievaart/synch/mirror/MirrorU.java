package com.eriklievaart.synch.mirror;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.test.api.SandboxTest;

public class MirrorU extends SandboxTest {

	// TODO
	// copy  metadata created
	// copy timestamp changed
	// copy hash changed

	@Before
	public void init() {
		System.setProperty("autodelete", "true");
		System.setProperty("autocopy", "true");
	}

	@Test
	public void listPathsSkipTrash() {
		memoryFile("/from/a").writeString("aaaa");
		memoryFile("/.Trash-1000/files/b").writeString("bbbb");

		Set<String> result = Mirror.listPaths(memoryFile("/"));
		Assertions.assertThat(result).containsExactly("/from/a");
	}

	@Test
	public void deleteMissingInSource() {
		memoryFile("from/a").writeString("aaaa");
		memoryFile("to/a").writeString("aaaa");
		memoryFile("to/b").writeString("bbbb");

		Mirror.synch(memoryFile("from"), memoryFile("to"));

		Check.isEqual(memoryFile("from/a").readString(), "aaaa");
		Check.isEqual(memoryFile("to/a").readString(), "aaaa");
		Check.isFalse(memoryFile("from/b").exists());
		Check.isFalse(memoryFile("to/b").exists());
	}

	@Test
	public void deleteEmptyDirectory() {
		memoryFile("from/a").writeString("aaaa");
		memoryFile("to/a").writeString("aaaa");
		memoryFile("to/deletedir/b").writeString("bbbb");

		Mirror.synch(memoryFile("from"), memoryFile("to"));

		Check.isEqual(memoryFile("from/a").readString(), "aaaa");
		Check.isEqual(memoryFile("to/a").readString(), "aaaa");
		Check.isFalse(memoryFile("to/deletedir").exists());
	}

	@Test
	public void copyMissingInDestination() {
		memoryFile("from/a").writeString("aaaa");
		memoryFile("from/b").writeString("bbbb");
		memoryFile("to/a").writeString("aaaa");

		Mirror.synch(memoryFile("from"), memoryFile("to"));

		Check.isEqual(memoryFile("from/a").readString(), "aaaa");
		Check.isEqual(memoryFile("to/a").readString(), "aaaa");
		Check.isEqual(memoryFile("from/b").readString(), "bbbb");
		Check.isEqual(memoryFile("to/b").readString(), "bbbb");
	}

	@Test
	public void copyAndDelete() {
		memoryFile("from/a").writeString("aaaa");
		memoryFile("from/b").writeString("bbbb");
		memoryFile("to/b").writeString("bbbb");
		memoryFile("to/c").writeString("bbbb");

		Mirror.synch(memoryFile("from"), memoryFile("to"));

		Check.isEqual(memoryFile("from/a").readString(), "aaaa");
		Check.isEqual(memoryFile("to/a").readString(), "aaaa");
		Check.isEqual(memoryFile("from/b").readString(), "bbbb");
		Check.isEqual(memoryFile("to/b").readString(), "bbbb");
		Check.isFalse(memoryFile("from/c").exists());
		Check.isFalse(memoryFile("to/c").exists());
	}
}
