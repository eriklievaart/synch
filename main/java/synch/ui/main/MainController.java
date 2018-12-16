package synch.ui.main;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.eriklievaart.toolkit.swing.api.menu.ReflectionActionBuilder;
import com.google.inject.Inject;

import synch.domain.location.SynchLocations;
import synch.ui.main.command.AddLocationCommand;
import synch.ui.main.command.CompareIndexesCommand;
import synch.ui.main.command.IndexLocationCommand;

public class MainController {

	private MainComponents components;

	@Inject
	public MainController(MainComponents components) {
		this.components = components;

		JFrame frame = components.getFrame();
		frame.setName("main");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = components.getPanel();
		panel.add(components.getAddLocationButton());
		panel.add(components.getIndexLocationButton());
		panel.add(components.getCompareButton());

		frame.getContentPane().add(new JScrollPane(components.getLocationList()));
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
	}

	@Inject
	public void addLocationCommand(AddLocationCommand command) {
		components.getAddLocationButton().setAction(ReflectionActionBuilder.createSingleAction(command));
	}

	@Inject
	public void indexLocationCommand(IndexLocationCommand command) {
		components.getIndexLocationButton().setAction(ReflectionActionBuilder.createSingleAction(command));
	}

	@Inject
	public void indexLocationCommand(CompareIndexesCommand command) {
		components.getCompareButton().setAction(ReflectionActionBuilder.createSingleAction(command));
	}

	@Inject
	public void registerObserver(final SynchLocations locations) {
		locations.addObserver(new Observer() {
			@Override
			public void update(Observable arg0, Object arg1) {
				updateList(locations);
			}
		});
		updateList(locations);
	}

	private void updateList(final SynchLocations locations) {
		components.getLocationList().setListData(locations.getSynchLocations());
	}

	public void show() {
		components.getFrame().setVisible(true);
	}
}
