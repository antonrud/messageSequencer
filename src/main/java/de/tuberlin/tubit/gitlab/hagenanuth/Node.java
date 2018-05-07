package de.tuberlin.tubit.gitlab.hagenanuth;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import de.tuberlin.tubit.gitlab.hagenanuth.messages.InternalMessage;
import de.tuberlin.tubit.gitlab.hagenanuth.messages.Message;

public class Node implements Runnable {

	MessageSequencer messageSequencer;
	private Queue<Message> queue;
	private LinkedList<InternalMessage> history;

	public Node(MessageSequencer messageSequencer) {
		this.messageSequencer = messageSequencer;
		this.queue = new LinkedBlockingQueue<Message>();
		this.history = new LinkedList<InternalMessage>();
	}

	public void addToQueue(Message message) {
		queue.add(message);

		// TODO Notify thread about new message
	}

	private void sendToMessageSequencer(InternalMessage message) {
		messageSequencer.addToQueue(message);
	}

	private void storeMessage(InternalMessage message) {
		history.add(message);
	}

	@Override
	public void run() {

		// TODO Must be notified here (maybe wait() - notify() purpose??)

		Message message = queue.poll();
		if (message.isInternal()) {
			storeMessage((InternalMessage) message);
		} else {
			message.setInternal();
			sendToMessageSequencer((InternalMessage) message);
		}

	}
}
