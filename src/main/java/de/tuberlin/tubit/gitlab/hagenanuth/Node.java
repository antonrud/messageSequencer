package de.tuberlin.tubit.gitlab.hagenanuth;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.tuberlin.tubit.gitlab.hagenanuth.messages.InternalMessage;
import de.tuberlin.tubit.gitlab.hagenanuth.messages.Message;

public class Node implements Runnable {

	private BlockingQueue<InternalMessage> messageSequencerQueue;
	public BlockingQueue<Message> queue;
	private LinkedList<InternalMessage> storage;

	public Node(BlockingQueue<InternalMessage> messageSequencerQueue) {
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

	public BlockingQueue<Message> getQueue() {
		return queue;
	}

	@Override
	public void run() {
		App.log('i', "One Node thread started.");

		while (true) {

			Message message = null;
			try {
				message = queue.take();
			} catch (InterruptedException e) {
				App.log('f', "Node thread broke down somehow:/");
			}

			if (message.isInternal()) {
				storeMessage(new InternalMessage(message.getPayload()));
				App.log('i', "Node stored message.");
			} else {
				sendToMessageSequencer(new InternalMessage(message.getPayload()));
				App.log('i', "Node sent message to sequencer.");
			}
		}
	}
}
