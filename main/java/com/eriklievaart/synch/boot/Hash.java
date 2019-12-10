package com.eriklievaart.synch.boot;

import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.swing.api.WindowSaver;
import com.google.inject.Guice;
import com.google.inject.Injector;

import synch.ui.main.MainController;

public class Hash {

	public static void main(String[] args) {
		SwingThread.invokeAndWaitUnchecked(new Runnable() {

			@Override
			public void run() {
				Injector injector = Guice.createInjector();
				WindowSaver.initialize();
				injector.getInstance(MainController.class).show();
			}
		});
	}
}