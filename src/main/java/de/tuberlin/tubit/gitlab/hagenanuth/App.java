package de.tuberlin.tubit.gitlab.hagenanuth;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class App {

	public static void main(String[] args) {

		int numThreads = 0;
		int numMessages = 0;

		try {
			numThreads = 2;
			numMessages = 5;
			//numThreads = Integer.parseInt(args[0]);
			//numMessages = Integer.parseInt(args[1]);
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
		App.log('i', "Nodes registered.");

		Thread messageSequencerThread = new Thread(messageSequencer);
		messageSequencerThread.start();

		for (Node node : messageSequencer.getNodes()) {
			Thread nodeThread = new Thread(node);
			nodeThread.start();
		}

		Thread generatorThread = new Thread(generator);
		generatorThread.start();

		App.log('s', "All threads STARTED!");
	}

	public static void log(char type, String message) {

		switch (type) {
		case 'i':
			System.out.println(
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [INFO] " + message);
			break;
		case 'f':
			System.out.println(
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [FAIL] " + message);
			break;
		case 'w':
			System.out.println(
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [WARNING] " + message);
			break;
		case 's':
			System.out.println(
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [SUCCESS] " + message);
			break;
		case 'e':
			System.out.println(
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [ERROR] " + message);
			break;
		default:
			System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " " + message);
		}
	}
}
