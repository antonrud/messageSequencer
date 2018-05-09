package de.tuberlin.tubit.gitlab.hagenanuth;

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
			App.log('f', "Could not add message to Sequencer queue.");
		}
	}

	private void storeMessage(InternalMessage message) {
		storage.add(message);
	}

	public void saveStorage() {
		// TODO FileOutput
	}

	public void retrieveStorage() {
		System.out.print("Node " + id + ": ");
		storage.stream().forEach(message -> System.out.print(message.getPayload() + " "));
		System.out.println();
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

		while (true) {

			Message message = null;
			try {
				message = queue.take();
			} catch (InterruptedException e) {
				App.log('f', "Node thread broke down somehow:/");
			}

			if (message.isInternal()) {
				storeMessage(new InternalMessage(message.getPayload()));
				App.log('i', "Node " + this.id + " stored message.");
			} else {
				sendToMessageSequencer(new InternalMessage(message.getPayload()));
				App.log('i', "Node " + this.id + " sent message to sequencer.");
			}
		}
	}
}
