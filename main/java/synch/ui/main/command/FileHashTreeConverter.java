package synch.ui.main.command;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.eriklievaart.toolkit.io.api.UrlTool;

import synch.domain.hash.FileHash;
import synch.domain.hash.HashCollection;

public class FileHashTreeConverter {

	public static JTree toTree(HashCollection hashes) {
		return new JTree(toNode(hashes));
	}

	static TreeNode toNode(HashCollection hashes) {
		List<FileHash> files = hashes.getHashes();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(".");
		for (FileHash file : files) {
			String path = UrlTool.getParent(file.getRelativePath());
			DefaultMutableTreeNode current = root;
			for (String part : path.split("[/\\\\]")) {
				current = getOrCreateChild(current, part);
			}
		}
		return root;
	}

	private static DefaultMutableTreeNode getOrCreateChild(DefaultMutableTreeNode current, String name) {
		for (int i = 0; i < current.getChildCount(); i++) {
			if (current.getChildAt(i).toString().equals(name)) {
				return (DefaultMutableTreeNode) current.getChildAt(i);
			}
		}
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(name);
		current.add(child);
		return child;
	}
}
