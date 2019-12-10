package synch.ui.main.command;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.StringUtils;

import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.swing.api.menu.Command;
import com.google.inject.Inject;

import synch.domain.hash.HashCollection;
import synch.domain.hash.HashCompare;
import synch.domain.hash.HashIO;
import synch.domain.location.SynchLocation;
import synch.domain.location.SynchLocations;

public class CompareIndexesCommand {
	private LogTemplate log = new LogTemplate(getClass());

	@Inject
	private HashIO io;
	@Inject
	private SynchLocations synchLocations;

	@Command(name = "Compare Folders")
	public void compareTo() {
		SynchLocation fromLocation = getSynchLocation("Select compare from location");
		SynchLocation toLocation = getSynchLocation("Select compare to location");

		if (fromLocation == null || toLocation == null) {
			return;
		}

		try {
			Map<String, HashCollection> indexes = io.getIndexes();
			if (!indexes.containsKey(fromLocation.getName())) {
				log.info("% not found in $", fromLocation.getName(), indexes);
				JOptionPane.showMessageDialog(null, fromLocation.getName() + " has not been indexed!");
				return;
			}
			HashCollection fromHashes = filterByFolder(indexes.get(fromLocation.getName()));
			HashCollection toHashes = filterByFolder(indexes.get(toLocation.getName()));
			new HashCompare(fromHashes, toHashes);

		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	private HashCollection filterByFolder(HashCollection hashes) {
		DefaultMutableTreeNode selected = selectFolder(hashes);
		if (selected.getUserObject().toString().equals(".")) {
			return hashes;
		}
		List<String> path = new ArrayList<String>();
		DefaultMutableTreeNode current = selected;
		while (current.getParent() != null) {
			path.add(0, current.getUserObject().toString());
			current = (DefaultMutableTreeNode) current.getParent();
		}
		return hashes.filterOnPath(StringUtils.join(path, "/"));
	}

	private DefaultMutableTreeNode selectFolder(HashCollection fromHashes) {
		log.info("Conversion start " + new Date());
		long start = System.currentTimeMillis();
		JTree tree = FileHashTreeConverter.toTree(fromHashes);
		log.info("Filter time $ms", System.currentTimeMillis() - start);

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		final JDialog dialog = new JDialog();
		dialog.setTitle("Select a folder");
		dialog.getRootPane().add(new JScrollPane(tree), BorderLayout.CENTER);
		dialog.add(new JScrollPane(tree), BorderLayout.CENTER);

		JButton button = new JButton(new CloseDialogAction("Select", dialog));
		dialog.add(button, BorderLayout.SOUTH);
		dialog.setModal(true);
		dialog.setBounds(0, 0, 640, 480);
		dialog.setVisible(true);

		return (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
	}

	private SynchLocation getSynchLocation(String message) {
		SynchLocation[] locations = synchLocations.getSynchLocations();
		SynchLocation from = (SynchLocation) JOptionPane.showInputDialog(null, message, "Input required",
				JOptionPane.QUESTION_MESSAGE, null, locations, null);
		return from;
	}
}