package de.tuberlin.tubit.gitlab.hagenanuth;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import de.tuberlin.tubit.gitlab.hagenanuth.messages.InternalMessage;

public class MessageSequencer implements Runnable {

	private LinkedList<Node> nodes;
	private Queue<InternalMessage> queue;

	public MessageSequencer() {
		this.nodes = new LinkedList<Node>();
		this.queue = new LinkedBlockingQueue<InternalMessage>();
	}

	public void registerNode(Node node) {
		nodes.add(node);
	}

	public void addToQueue(InternalMessage message) {
		queue.add(message);

		// TODO Notify thread about new message
	}

	private void broadcastMessage() {

		for (Node node : nodes) {
			node.addToQueue(queue.peek());
		}
		queue.poll();
	}

	public LinkedList<Node> getNodes() {
		return nodes;
	}

	@Override
	public void run() {

		// TODO Must be notified here (maybe wait() - notify() purpose??)

		broadcastMessage();

	}
}
