package de.tuberlin.tubit.gitlab.hagenanuth;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.tuberlin.tubit.gitlab.hagenanuth.messages.InternalMessage;
import de.tuberlin.tubit.gitlab.hagenanuth.messages.Message;

public class Node implements Runnable {

	MessageSequencer messageSequencer;
	private BlockingQueue<Message> queue;
	private LinkedList<InternalMessage> history;

	public Node(MessageSequencer messageSequencer) {
		this.messageSequencer = messageSequencer;
		this.queue = new LinkedBlockingQueue<Message>();
		this.history = new LinkedList<InternalMessage>();
	}

	public void addToQueue(Message message) {
		queue.add(message);
	}

	private void sendToMessageSequencer(InternalMessage message) {
		messageSequencer.addToQueue(message);
	}

	private void storeMessage(InternalMessage message) {
		history.add(message);
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
				storeMessage((InternalMessage) message);
				App.log('i', "Node stored message.");
			} else {
				message.setInternal();
				sendToMessageSequencer((InternalMessage) message);
				App.log('i', "Node sent message to sequencer.");
			}
		}
	}
}
