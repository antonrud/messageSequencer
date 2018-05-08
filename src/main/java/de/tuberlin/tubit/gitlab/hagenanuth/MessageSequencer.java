package de.tuberlin.tubit.gitlab.hagenanuth;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.tuberlin.tubit.gitlab.hagenanuth.messages.InternalMessage;

public class MessageSequencer implements Runnable {

	private LinkedList<Node> nodes;
	private BlockingQueue<InternalMessage> queue;

	public MessageSequencer() {
		this.nodes = new LinkedList<Node>();
		this.queue = new LinkedBlockingQueue<InternalMessage>();
	}

	public void registerNode(Node node) {
		nodes.add(node);
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

		for (Node node : nodes) {
			node.addToQueue(internalMessage);
		}
		App.log('i', "Sequencer broadcasted message.");
	}

	public LinkedList<Node> getNodes() {
		return nodes;
	}

	@Override
	public void run() {
		App.log('i', "Sequencer thread started.");

		while (true) {
			broadcastMessage();
		}
	}
}
