package de.tuberlin.tubit.gitlab.hagenanuth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class App {

	static private LinkedList<Node> nodes = new LinkedList<Node>();
	static private LinkedList<Thread> nodeThreads = new LinkedList<Thread>();
	static private Thread messageSequencerThread;
	static private Thread generatorThread;

	public static void main(String[] args) {

		int numNodes = 0;
		int numMessages = 0;

		try {
			numNodes = Integer.parseInt(args[0]);
			numMessages = Integer.parseInt(args[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			App.log('e', "USAGE: Provide number of Nodes and number of Messages as arguments.");
			System.exit(0);
		} catch (NumberFormatException e) {
			App.log('e', "NOT A NUMBER!");
			System.exit(0);
		}

		App.log('i', "Yay! App started!");

		MessageSequencer messageSequencer = new MessageSequencer();
		Generator generator = new Generator(numMessages);

		/* Create and register Nodes */
		int nodeId = 0;
		for (int i = 0; i < numNodes; i++) {
			nodeId++;
			Node node = new Node(nodeId, messageSequencer.getQueue());
			messageSequencer.registerNode(node.getQueue());
			generator.registerNode(node.getQueue());
			nodes.add(node);
		}
		App.log('i', "Nodes registered.");

		messageSequencerThread = new Thread(messageSequencer);
		messageSequencerThread.start();

		for (Node node : nodes) {
			Thread nodeThread = new Thread(node);
			nodeThreads.add(nodeThread);
			nodeThread.start();
		}

		generatorThread = new Thread(generator);
		generatorThread.start();

		App.log('s', "App.main() finished.");
	}

	public static void prepareShutdown() {

		/* Just for some cool shutdown animation */
		System.out.print("\nPrepare to shutdown");
		for (int i = 0; i < 4; i++) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				App.log('f', "Shutdown failed!");
				e.printStackTrace();
			}
			System.out.print(" .");
		}
		System.out.println("\n");

		/* Interrupt threads */
		messageSequencerThread.interrupt();
		for (Thread nodeThread : nodeThreads) {
			nodeThread.interrupt();
		}

		/* Clean previous storage before saving to disk */
		File[] storageDirectory = (new File("storage")).listFiles();
		if (storageDirectory != null) {
			for (File file : storageDirectory) {
				file.delete();
			}
		}

		/* Saves to disc stored messages of every node */
		nodes.stream().forEach(node -> node.writeStorageToFile());
		App.log('i', "All data is written to disc");

		/* Prints out stored messages of every node */
		nodes.stream().forEach(node -> node.retrieveStorage());

		System.out.println("\n\nSHUTDOWN!");
		System.exit(0);
	}

	public static void log(char type, String message) {

		String logEvent = "";

		switch (type) {
		case 'i':
			logEvent = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [INFO] " + message;
			break;
		case 'w':
			logEvent = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [WARNING] " + message;
			break;
		case 's':
			logEvent = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [SUCCESS] " + message;
			break;
		case 'f':
			logEvent = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [FAIL] " + message;
			break;
		case 'e':
			logEvent = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [ERROR] " + message;
			break;
		default:
			logEvent = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " " + message;
		}

		try {
			File file = new File("log.txt");
			file.createNewFile();

			FileWriter fileWriter = new FileWriter(file, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write(logEvent);
			bufferedWriter.newLine();

			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException ioe) {
			App.log('f', "Could not write log event to file");
		}

		System.out.println(logEvent);
	}
}
