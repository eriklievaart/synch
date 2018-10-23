package com.eriklievaart.synch.mirror;

import java.awt.GridLayout;
import java.io.File;
import java.util.Map;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.eriklievaart.toolkit.io.api.JvmPaths;
import com.eriklievaart.toolkit.io.api.PropertiesIO;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class DriveSelector extends JPanel {

	private File file = new File(JvmPaths.getJarDirOrRunDir(getClass()), "data/settings.properties");
	public JTextField fromField = new JTextField();
	public JTextField toField = new JTextField();
	public JCheckBox metadataBox = new JCheckBox("store metadata", null);

	{
		setLayout(new GridLayout(0, 1));
		add(new JLabel("Copy from:"));
		add(fromField);
		add(new JLabel("Copy to (backup location with metadata):"));
		add(toField);
		add(metadataBox);
		load();
	}

	public void store() {
		Properties properties = new Properties();
		properties.put("from", fromField.getText());
		properties.put("to", toField.getText());
		properties.put("metadata", "" + metadataBox.isSelected());
		PropertiesIO.store(properties, file);
	}

	private void load() {
		Map<String, String> properties = file.exists() ? PropertiesIO.loadStrings(file) : NewCollection.map();

		fromField.setText(properties.getOrDefault("from", "/media"));
		toField.setText(properties.getOrDefault("to", "/media"));
		metadataBox.setSelected(new String("true").equalsIgnoreCase(properties.getOrDefault("metadata", "true")));
	}
}
