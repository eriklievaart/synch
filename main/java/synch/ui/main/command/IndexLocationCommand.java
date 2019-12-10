package synch.ui.main.command;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.eriklievaart.toolkit.swing.api.menu.Command;
import com.google.inject.Inject;

import synch.domain.hash.HashContext;
import synch.domain.hash.Hashes;
import synch.domain.location.SynchLocation;
import synch.ui.main.MainComponents;

public class IndexLocationCommand {

	@Inject
	private MainComponents components;
	@Inject
	private Hashes hashes;

	@Command(name = "index location")
	public void index() {
		SynchLocation location = components.getLocationList().getSelectedValue();
		if (location == null) {
			JOptionPane.showMessageDialog(null, "Select a location first!");
			return;
		}
		try {
			HashContext context = new HashContext(location);
			hashes.hash(context);
			JOptionPane.showMessageDialog(null, "Indexing complete!");

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to store hash file: " + e.getMessage());
			e.printStackTrace();
		}
	}
}