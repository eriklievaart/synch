package com.eriklievaart.synch.mirror;

import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.toolkit.io.api.properties.PropertiesIO;
import com.eriklievaart.toolkit.io.api.sha1.Sha1;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.mock.SandboxTest;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFile;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;

public class MirrorU extends SandboxTest {
	private static final boolean METADATA_ON = true;
	private static final boolean METADATA_OFF = false;

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
	public void listPathsSkipMetadata() {
		memoryFile("/from/a").writeString("aaaa");
		memoryFile("/from/a.smeta").writeString("metadata");

		Set<String> result = Mirror.listPaths(memoryFile("/"));
		Assertions.assertThat(result).containsExactly("/from/a");
	}

	@Test
	public void deleteMissingInSource() {
		memoryFile("from/a").writeString("aaaa");
		memoryFile("to/a").writeString("aaaa");
		memoryFile("to/b").writeString("bbbb");
		memoryFile("to/b.smeta").writeString("metadata");

		Mirror.synch(memoryFile("from"), memoryFile("to"), METADATA_OFF);

		Check.isEqual(memoryFile("from/a").readString(), "aaaa");
		Check.isEqual(memoryFile("to/a").readString(), "aaaa");
		Check.isFalse(memoryFile("from/b").exists());
		Check.isFalse(memoryFile("to/b").exists());
		Check.isFalse(memoryFile("to/b.smeta").exists());
	}

	@Test
	public void deleteEmptyDirectory() {
		memoryFile("from/a").writeString("aaaa");
		memoryFile("to/a").writeString("aaaa");
		memoryFile("to/deletedir/b").writeString("bbbb");

		Mirror.synch(memoryFile("from"), memoryFile("to"), METADATA_OFF);

		Check.isEqual(memoryFile("from/a").readString(), "aaaa");
		Check.isEqual(memoryFile("to/a").readString(), "aaaa");
		Check.isFalse(memoryFile("to/deletedir").exists());
	}

	@Test
	public void deleteFileAndDirectory() {
		memoryFile("from/c").writeString("c");
		memoryFile("to/a/b").writeString("b");

		Mirror.synch(memoryFile("from"), memoryFile("to"), METADATA_OFF);

		Check.isFalse(memoryFile("to/a").exists());
		Check.isTrue(memoryFile("to/c").exists());
	}

	@Test
	public void deleteFileAndDirectoryNested() {
		memoryFile("from/d").writeString("d");
		memoryFile("to/a/b/c").writeString("c");

		Mirror.synch(memoryFile("from"), memoryFile("to"), METADATA_OFF);

		Check.isFalse(memoryFile("to/a").exists());
		Check.isTrue(memoryFile("to/d").exists());
	}

	@Test
	public void copyTimestamp() {
		MemoryFile original = memoryFile("from/a");
		original.writeString("aaaa");
		original.setLastModified(1547067136323l);
		MemoryFile copy = memoryFile("to/a");

		Mirror.synch(memoryFile("from"), memoryFile("to"), METADATA_OFF);

		Check.isEqual(original.readString(), "aaaa");
		Check.isEqual(copy.readString(), "aaaa");
		Check.isEqual(original.lastModified(), copy.lastModified());
	}

	@Test
	public void copyEmptyDir() {
		SystemFile original = systemFile("from/a");
		original.writeString("aaaa");
		systemFile("from/b").mkdir();
		systemFile("to").mkdir();

		Mirror.synch(systemFile("from"), systemFile("to"), METADATA_OFF);

		Check.isTrue(systemFile("to/b").isDirectory());
		Check.isTrue(systemFile("to/b").exists());
	}

	@Test
	public void copyEmptyDirSkipIfParentIsMissing() {
		SystemFile original = systemFile("from/a");
		original.writeString("aaaa");
		systemFile("from/b/c").mkdir();
		systemFile("to").mkdir();

		Mirror.synch(systemFile("from"), systemFile("to"), METADATA_OFF);

		Check.isFalse(systemFile("to/b/c").isDirectory());
		Check.isFalse(systemFile("to/b/c").exists());
	}

	@Test
	public void copyMissingInDestination() {
		memoryFile("from/a").writeString("aaaa");
		memoryFile("from/b").writeString("bbbb");
		memoryFile("to/a").writeString("aaaa");

		Mirror.synch(memoryFile("from"), memoryFile("to"), METADATA_OFF);

		Check.isEqual(memoryFile("from/a").readString(), "aaaa");
		Check.isEqual(memoryFile("to/a").readString(), "aaaa");
		Check.isEqual(memoryFile("from/b").readString(), "bbbb");
		Check.isEqual(memoryFile("to/b").readString(), "bbbb");
		Check.isFalse(memoryFile("to/a.smeta").exists());
		Check.isFalse(memoryFile("to/b.smeta").exists());
	}

	@Test
	public void copyCreateMetadata() {
		memoryFile("from/a").writeString("aaaa");
		memoryFile("to").mkdir();

		Mirror.synch(memoryFile("from"), memoryFile("to"), METADATA_ON);

		Check.isEqual(memoryFile("to/a").readString(), "aaaa");
		Check.isEqual(memoryFile("from/a").readString(), "aaaa");
		Check.isFalse(memoryFile("from/a.smeta").exists());

		Assertions.assertThat(memoryFile("to/a.smeta").readString()).contains(Sha1.hash("aaaa"));
	}

	@Test
	public void copyWithoutMetadata() {
		memoryFile("from/a").writeString("aaaa");
		memoryFile("from/a.smeta").writeString("metadata");

		Mirror.synch(memoryFile("from"), memoryFile("to"), METADATA_OFF);

		Check.isEqual(memoryFile("from/a").readString(), "aaaa");
		Check.isEqual(memoryFile("from/a.smeta").readString(), "metadata");
		Check.isEqual(memoryFile("to/a").readString(), "aaaa");
		Check.isFalse(memoryFile("to/a.smeta").exists());
	}

	@Test
	public void copyOlder() throws InterruptedException {
		memoryFile("from/a").writeString("data");
		Thread.sleep(10);
		memoryFile("to/a").writeString("newer");

		Mirror.synch(memoryFile("from"), memoryFile("to"), METADATA_OFF);

		Check.isEqual(memoryFile("from/a").readString(), "data");
		Check.isEqual(memoryFile("to/a").readString(), "newer");
	}

	@Test
	public void copyNewer() throws InterruptedException {
		memoryFile("to/a").writeString("older");
		Thread.sleep(10);
		memoryFile("from/a").writeString("newer");

		Mirror.synch(memoryFile("from"), memoryFile("to"), METADATA_ON);

		Check.isEqual(memoryFile("from/a").readString(), "newer");
		Check.isEqual(memoryFile("to/a").readString(), "newer");
		Check.isTrue(memoryFile("to/a.smeta").exists());

		Map<String, String> properties = PropertiesIO.loadStrings(memoryFile("to/a.smeta").getInputStream());
		Check.isEqual(properties.get("sha1"), Sha1.hash("newer"));
	}

	@Test
	public void copyAndDelete() {
		memoryFile("from/a").writeString("aaaa");
		memoryFile("from/b").writeString("bbbb");
		memoryFile("to/b").writeString("bbbb");
		memoryFile("to/c").writeString("bbbb");

		Mirror.synch(memoryFile("from"), memoryFile("to"), METADATA_OFF);

		Check.isEqual(memoryFile("from/a").readString(), "aaaa");
		Check.isEqual(memoryFile("to/a").readString(), "aaaa");
		Check.isEqual(memoryFile("from/b").readString(), "bbbb");
		Check.isEqual(memoryFile("to/b").readString(), "bbbb");
		Check.isFalse(memoryFile("from/c").exists());
		Check.isFalse(memoryFile("to/c").exists());
	}
}