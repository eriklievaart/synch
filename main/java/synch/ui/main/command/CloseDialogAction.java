package synch.ui.main.command;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

public class CloseDialogAction extends AbstractAction {
	private JDialog dialog;

	public CloseDialogAction(String name, JDialog dialog) {
		super(name);
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		dialog.setVisible(false);
		dialog.dispose();
	}
}