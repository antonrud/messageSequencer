package de.tuberlin.tubit.gitlab.hagenanuth;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import de.tuberlin.tubit.gitlab.hagenanuth.messages.ExternalMessage;
import de.tuberlin.tubit.gitlab.hagenanuth.messages.Message;

public class Generator implements Runnable {

	private LinkedList<BlockingQueue<Message>> nodeQueues;
	private int numMessages;

	public Generator(int numMessages) {
		this.nodeQueues = new LinkedList<BlockingQueue<Message>>();
		this.numMessages = numMessages;
	}

	public void registerNode(BlockingQueue<Message> nodeQueue) {
		nodeQueues.add(nodeQueue);
	}

	@Override
	public void run() {
		App.log('i', "Generater started.");

		/* Sends messages to random Nodes with random payload */
		for (int i = 0; i < numMessages; i++) {
			try {
				Thread.sleep(200);
				nodeQueues.get((new Random()).nextInt(nodeQueues.size()))
						.put(new ExternalMessage((new Random()).nextInt()));
			} catch (InterruptedException e) {
				App.log('f', "Could not add message to node queue from Generater.");
			}
			App.log('i', "A message is sent to some Node.");
		}

		App.log('s', "Generater FINISHED.");
	}
}
