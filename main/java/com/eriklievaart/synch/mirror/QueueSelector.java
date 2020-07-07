package com.eriklievaart.synch.mirror;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import com.eriklievaart.synch.mirror.job.MirrorJob;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.swing.api.builder.JFrameBuilder;

public class QueueSelector {

	private JFrame frame = new JFrameBuilder("selector").exitOnClose().create();
	private JPanel listPanel = new JPanel(new GridLayout(1, 0));
	private JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
	private JList<String> availableList = new JList<>();
	private JList<String> queueList = new JList<>();
	private JButton queueButton = new JButton("Add to Queue");
	private JButton dequeueButton = new JButton("Remove from Queue");
	private JButton goButton = new JButton("Accept");

	private List<MirrorJob> jobs = NewCollection.list();
	private List<String> available = NewCollection.list();
	private List<String> queue = NewCollection.list();

	public QueueSelector() {
		initComponents();
	}

	public void addJob(MirrorJob job) {
		if (job.getAllPaths().isEmpty()) {
			return;
		}
		if (jobs.size() == 0) {
			initJob(job);
		}
		jobs.add(job);
	}

	private void initComponents() {
		frame.getContentPane().add(listPanel, BorderLayout.CENTER);
		frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		listPanel.add(new JScrollPane(availableList));
		listPanel.add(new JScrollPane(queueList));

		buttonPanel.add(queueButton);
		buttonPanel.add(goButton);
		buttonPanel.add(dequeueButton);

		queueButton.addActionListener(ae -> {
			available = removeIndices(available, availableList.getSelectedIndices());
			queue.addAll(availableList.getSelectedValuesList());
			updateLists();
		});

		dequeueButton.addActionListener(ae -> {
			queue = removeIndices(queue, queueList.getSelectedIndices());
			available.addAll(queueList.getSelectedValuesList());
			updateLists();
		});
	}

	private List<String> removeIndices(List<String> list, int[] remove) {
		Set<Integer> skip = new HashSet<>();
		for (int i : remove) {
			skip.add(i);
		}
		List<String> clone = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if (!skip.contains(i)) {
				clone.add(list.get(i));
			}
		}
		return clone;
	}

	private void initJob(MirrorJob job) {
		available.clear();
		queue.clear();
		available.addAll(job.filterValidPaths());

		if (available.isEmpty()) {
			nextJob();
			return;
		}

		updateLists();
		for (ActionListener listener : goButton.getActionListeners()) {
			goButton.removeActionListener(listener);
		}
		goButton.addActionListener(ae -> {
			try {
				frame.setVisible(false);
				job.consume(getQueuedPaths());
				nextJob();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		});
		frame.setTitle(job.title);
		frame.setVisible(true);
	}

	private void nextJob() {
		if (!jobs.isEmpty()) {
			jobs.remove(0);
		}
		if (jobs.isEmpty()) {
			System.exit(0);
		} else {
			initJob(jobs.get(0));
		}
	}

	private List<String> getQueuedPaths() {
		List<String> result = NewCollection.list();
		ListModel<String> model = queueList.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			result.add(model.getElementAt(i));
		}
		return result;
	}

	private void updateLists() {
		availableList.setSelectedIndex(-1);
		queueList.setSelectedIndex(-1);
		availableList.setListData(available.toArray(new String[] {}));
		queueList.setListData(queue.toArray(new String[] {}));
	}
}