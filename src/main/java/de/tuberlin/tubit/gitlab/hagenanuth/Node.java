package de.tuberlin.tubit.gitlab.hagenanuth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.tuberlin.tubit.gitlab.hagenanuth.messages.InternalMessage;
import de.tuberlin.tubit.gitlab.hagenanuth.messages.Message;

public class Node implements Runnable {

	private int id;
	private BlockingQueue<InternalMessage> messageSequencerQueue;
	public BlockingQueue<Message> queue;
	private LinkedList<InternalMessage> storage;

	public Node(int id, BlockingQueue<InternalMessage> messageSequencerQueue) {
		this.id = id;
		this.messageSequencerQueue = messageSequencerQueue;
		this.queue = new LinkedBlockingQueue<Message>();
		this.storage = new LinkedList<InternalMessage>();
	}

	public void addToQueue(Message message) {
		queue.add(message);
	}

	private void sendToMessageSequencer(InternalMessage message) {
		try {
			messageSequencerQueue.put(message);
		} catch (InterruptedException e) {
			App.log('f', "Node " + this.id + " could not add message to Sequencer queue.");
		}
	}

	private void storeMessage(InternalMessage message) {
		storage.add(message);
	}

	public void writeStorageToFile() {

		try {
			File file = new File("storage/node_" + this.id + ".txt");
			file.getParentFile().mkdirs();
			file.createNewFile();

			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			for (InternalMessage message : storage) {
				bufferedWriter.write("" + message.getPayload());
				bufferedWriter.newLine();
			}

			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException ioe) {
			App.log('f', "Node " + this.id + " could not write to file");
		}
	}

	public void retrieveStorage() {
		// System.out.println();
		System.out.print("\nNode " + this.id + ": ");
		storage.stream().forEach(message -> System.out.print(message.getPayload() + " "));
		// System.out.println();
	}

	public BlockingQueue<Message> getQueue() {
		return queue;
	}

	public int getId() {
		return id;
	}

	@Override
	public void run() {
		App.log('i', "Node " + this.id + " started.");

		while (!Thread.currentThread().isInterrupted()) {

			Message message = null;
			try {
				message = queue.take();
			} catch (InterruptedException e) {
				App.log('w', "Node " + this.id + " was interrupted");
			}

			try {
				if (message.isInternal()) {
					storeMessage(new InternalMessage(message.getPayload()));
					App.log('i', "Node " + this.id + " stored message.");
				} else {
					sendToMessageSequencer(new InternalMessage(message.getPayload()));
					App.log('i', "Node " + this.id + " sent message to sequencer.");
				}
			} catch (NullPointerException e) {
				App.log('w', "Expected NullPointerException after interrupting Node " + this.id);
			}
		}
	}
}
