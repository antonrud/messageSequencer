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
			int nodeQueueIndex = (new Random()).nextInt(nodeQueues.size());
			ExternalMessage externalMessage = new ExternalMessage((new Random()).nextInt(100));
			try {
				// Uncomment this to generate messages gradually
				// Thread.sleep(300);
				nodeQueues.get(nodeQueueIndex).put(externalMessage);
			} catch (InterruptedException e) {
				App.log('f', "Generater thread broke down :/");
			}
			App.log('i', "Message with payload " + externalMessage.getPayload() + " is sent to Node "
					+ (nodeQueueIndex + 1));
		}

		App.log('s', "Generater FINISHED.");

		/* Give other threads some time to finish their tasks */
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			App.log('f', "Generator broke just before shutdown");
		}

		App.prepareShutdown();
	}
}
