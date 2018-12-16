package synch.ui.main;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LocationPanelComponents {

	private JPanel panel = new JPanel(new GridLayout(0, 2));
	private JLabel nameLabel = new JLabel("Name:");
	private JLabel locationLabel = new JLabel("Location:");
	private JTextField nameField = new JTextField();
	private JTextField locationField = new JTextField();

	public LocationPanelComponents() {
		panel.add(nameLabel);
		panel.add(nameField);
		panel.add(locationLabel);
		panel.add(locationField);

	}

	public JPanel getPanel() {
		return panel;
	}

	public JLabel getNameLabel() {
		return nameLabel;
	}

	public JLabel getLocationLabel() {
		return locationLabel;
	}

	public JTextField getNameField() {
		return nameField;
	}

	public JTextField getLocationField() {
		return locationField;
	}
}
