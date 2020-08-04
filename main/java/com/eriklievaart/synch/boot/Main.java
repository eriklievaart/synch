package com.eriklievaart.synch.boot;

import java.io.File;

import javax.swing.JOptionPane;

import com.eriklievaart.synch.mirror.DriveSelector;
import com.eriklievaart.synch.mirror.Mirror;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.swing.api.WindowSaver;
import com.eriklievaart.toolkit.swing.api.laf.LookAndFeel;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;

public class Main {

	public static void main(String[] args) {
		SwingThread.invokeAndWaitUnchecked(() -> {
			LookAndFeel.instance().load();

			DriveSelector drives = new DriveSelector();
			if (args.length == 2) {
				drives.fromField.setText(args[0]);
				drives.toField.setText(args[1]);
			} else {
				int result = JOptionPane.showConfirmDialog(null, drives, "select drives", JOptionPane.OK_CANCEL_OPTION);
				if (result != JOptionPane.OK_OPTION) {
					System.out.println("exiting, user abort");
					return;
				}
			}
			SystemFile from = new SystemFile(new File(drives.fromField.getText()));
			SystemFile to = new SystemFile(new File(drives.toField.getText()));
			drives.store();

			WindowSaver.initialize();
			drives.setAutoCopyProperties();
			Mirror.synch(from, to, drives.metadataBox.isSelected());
			System.out.println("complete!");
		});
	}
}