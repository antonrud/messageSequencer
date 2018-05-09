package de.tuberlin.tubit.gitlab.hagenanuth;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.tuberlin.tubit.gitlab.hagenanuth.messages.InternalMessage;
import de.tuberlin.tubit.gitlab.hagenanuth.messages.Message;

public class MessageSequencer implements Runnable {

	private LinkedList<BlockingQueue<Message>> nodeQueues;
	public BlockingQueue<InternalMessage> queue;

	public MessageSequencer() {
		this.nodeQueues = new LinkedList<BlockingQueue<Message>>();
		this.queue = new LinkedBlockingQueue<InternalMessage>();
	}

	public void registerNode(BlockingQueue<Message> nodeQueue) {
		nodeQueues.add(nodeQueue);
	}

	public void addToQueue(InternalMessage message) {
		queue.add(message);
	}

	private void broadcastMessage() {

		InternalMessage internalMessage = null;
		try {
			internalMessage = queue.take();
		} catch (InterruptedException e) {
			App.log('f', "Sequencer thread broke down somehow :/");
		}

		for (BlockingQueue<Message> nodeQueue : nodeQueues) {
			try {
				nodeQueue.put(internalMessage);
			} catch (InterruptedException e) {
				App.log('f', "Could not add message to Node queue from Sequencer.");
			}
		}
		App.log('i', "Sequencer broadcasted message with payload " + internalMessage.getPayload());
	}

	public BlockingQueue<InternalMessage> getQueue() {
		return queue;
	}

	@Override
	public void run() {
		App.log('i', "Sequencer thread started.");

		while (true) {
			broadcastMessage();
		}
	}
}
