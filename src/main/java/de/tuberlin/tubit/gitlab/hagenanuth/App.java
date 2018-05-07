package de.tuberlin.tubit.gitlab.hagenanuth;

public class App {

	public static void main(String[] args) {

		int numThreads = 0;
		int numMessages = 0;

		try {
			numThreads = Integer.parseInt(args[0]);
			numMessages = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out.println("NOT A NUMBER!");
		} catch (NullPointerException e) {
			System.out.println("USAGE: Provide number of threads and number of messages as arguments.");
		}

		MessageSequencer messageSequencer = new MessageSequencer();
		Generator generator = new Generator(numMessages);

		/* Create and register Nodes */
		for (int i = 0; i < numThreads; i++) {
			Node node = new Node(messageSequencer);
			messageSequencer.registerNode(node);
			generator.registerNode(node);
		}

		Thread messageSequencerThread = new Thread(messageSequencer);
		messageSequencerThread.start();

		for (Node node : messageSequencer.getNodes()) {
			Thread nodeThread = new Thread(node);
			nodeThread.start();
		}

		Thread generatorThread = new Thread(generator);
		generatorThread.start();
	}
}
