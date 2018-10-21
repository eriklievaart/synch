package synch.ui.main.command;

import javax.swing.JOptionPane;

import com.eriklievaart.toolkit.convert.api.ConversionException;
import com.eriklievaart.toolkit.convert.api.validate.ExistingDirectoryValidator;
import com.eriklievaart.toolkit.swing.api.menu.Command;
import com.google.inject.Inject;

import synch.domain.location.SynchLocation;
import synch.domain.location.SynchLocations;
import synch.ui.main.LocationPanelComponents;

public class AddLocationCommand {

	@Inject
	private LocationPanelComponents components;
	@Inject
	private SynchLocations locations;

	@Command(name = "add location")
	public void add() {
		int option = JOptionPane.OK_CANCEL_OPTION;
		int result = JOptionPane.showConfirmDialog(null, components.getPanel(), "Input required", option);
		if (result != JOptionPane.OK_OPTION) {
			return;
		}
		String name = components.getNameField().getText();
		String location = components.getLocationField().getText();
		try {
			new ExistingDirectoryValidator().check(location);
			locations.add(new SynchLocation(name, location));

		} catch (ConversionException e) {
			JOptionPane.showMessageDialog(null, "Invalid input: " + e.getMessage());
			add();
		}
	}

}
