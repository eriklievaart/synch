package synch.ui.main.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.check.Check;

import synch.domain.hash.FileHash;
import synch.domain.hash.HashCollection;

public class FileHashTreeConverterU {

	@Test
	public void toNodeSingle() {
		String path = "Anime/Full Moon Wo/Full moon wo sagashite - Episode 039 [A.F.K.].avi";
		String hash = "950b28ec4d06db3a5556d096cbf94b099d6a7613";
		HashCollection hashes = new HashCollection(Arrays.asList(new FileHash(path, 178794496, hash)));

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) FileHashTreeConverter.toNode(hashes);
		validatePath(path, node);
	}

	@Test
	public void toNodeMultiple() {
		String path39 = "Anime/Full Moon Wo/Full moon wo sagashite - Episode 039 [A.F.K.].avi";
		String hash39 = "950b28ec4d06db3a5556d096cbf94b099d6a7613";
		FileHash file39 = new FileHash(path39, 178794496, hash39);

		String path08 = "Anime/Full Moon Wo/Full moon wo sagashite - Episode 008 [A.F.K.].avi";
		String hash08 = "77523be72339aa9c15da31f22f4dfbbf8789b344";
		FileHash file08 = new FileHash(path08, 165949440, hash08);

		HashCollection hashes = new HashCollection(Arrays.asList(file08, file39));

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) FileHashTreeConverter.toNode(hashes);
		validatePath(path08, node);
	}

	private void validatePath(String path, DefaultMutableTreeNode node) {
		String parent = UrlTool.removeSlash(UrlTool.getParent(path)); // exclude file name
		String actual = toPath(node);
		Check.isEqual(actual, parent);
	}

	private String toPath(DefaultMutableTreeNode node) {
		boolean isRoot = node.getUserObject().toString().equals(".");
		if (isRoot && node.getChildCount() == 0) {
			return ".";
		}
		List<String> parts = new ArrayList<>();
		DefaultMutableTreeNode current = node;
		while (current.getChildCount() == 1) {
			current = (DefaultMutableTreeNode) current.getChildAt(0);
			parts.add(current.getUserObject().toString());
		}
		if (node.getChildCount() > 1) {
			throw new RuntimeException("expecting only one child for node " + current);
		}
		return StringUtils.join(parts, '/');
	}
}
