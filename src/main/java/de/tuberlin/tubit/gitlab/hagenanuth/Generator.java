package de.tuberlin.tubit.gitlab.hagenanuth;

import java.util.LinkedList;
import java.util.Random;

import de.tuberlin.tubit.gitlab.hagenanuth.messages.ExternalMessage;

public class Generator implements Runnable {

	private LinkedList<Node> nodes;
	private int numMessages;

	public Generator(int numMessages) {
		this.nodes = new LinkedList<Node>();
		this.numMessages = numMessages;
	}

	public void registerNode(Node node) {
		nodes.add(node);
	}

	@Override
	public void run() {
		App.log('i', "Generater started.");

		/* Sends messages to random Nodes with random payload */
		for (int i = 0; i < numMessages; i++) {
			nodes.get((new Random()).nextInt(nodes.size())).addToQueue(new ExternalMessage((new Random()).nextInt()));
			App.log('i', "A message is sent to some Node.");
		}

		App.log('s', "Generater FINISHED.");
	}
}
