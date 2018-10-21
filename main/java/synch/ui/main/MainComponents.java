package synch.ui.main;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import com.google.inject.Singleton;

import synch.domain.location.SynchLocation;

@Singleton
public class MainComponents {

	private JFrame frame = new JFrame();
	private JList<SynchLocation> locationList = new JList<SynchLocation>();
	private JPanel panel = new JPanel();
	private JButton addLocationButton = new JButton();
	private JButton indexLocationButton = new JButton();
	private JButton compareButton = new JButton();

	public JFrame getFrame() {
		return frame;
	}

	public JList<SynchLocation> getLocationList() {
		return locationList;
	}

	public JPanel getPanel() {
		return panel;
	}

	public JButton getAddLocationButton() {
		return addLocationButton;
	}

	public JButton getIndexLocationButton() {
		return indexLocationButton;
	}

	public JButton getCompareButton() {
		return compareButton;
	}

}
