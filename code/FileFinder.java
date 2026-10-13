// finds all folders on a drive and lists them by size
package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;

public class FileFinder {
	public static Map<Path, Long> fileSizes = new HashMap<Path, Long>();
	public static long comparisons = 0;

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		File startingFolder;

		do {
			Thread AFKThread = new AFKThread();
			AFKThread.start();
			System.out.println("Enter EXIT to exit.");
			System.out.print("Enter a file path to check (ex. C:\\, C:\\SteamLibrary, etc.): ");
			String file = input.nextLine();
			AFKThread.interrupt(); // user just gave an input, not AFK
			if (!file.equals("EXIT")) { // check if the user entered EXIT
				startingFolder = new File(file);
				if (startingFolder.exists()) {
					System.out.println("File found, searching will begin in a moment.");
					try {
						Thread.sleep(1000 * 3);
					} catch (InterruptedException e) {
						System.out.println("An error occured.");
						e.printStackTrace();
					}
				} else {
					System.out.println("Could not find the specified file path, please try again.");
					System.out.println();
				}
			} else {
				startingFolder = null; // program wont compile unless it knows that startingFolder will be defined once the loop ends
				System.exit(0);
			}
		} while (!startingFolder.exists());
		input.close();

		ArrayList<File> folders = new ArrayList<>();
		Stack<File> foldersToCheck = new Stack<>();
		foldersToCheck.push(startingFolder);
		while (!foldersToCheck.empty()) {
			File folder = foldersToCheck.pop(); // get the folder at the top of the list
			System.out.printf("Started checking %s.%n", folder.getName()); // just to give the user something to look at
			// check every file in the discovered folder to see if it is a folder and
			// add it to the list of folders to be checked if it is
			if (folder.listFiles() != null) { // check the folder has files in it
				for (File file : folder.listFiles()) {
					if (file.isDirectory()) {
						System.out.printf("Added %s to the list of discovered folders.%n", file.getName()); // just to give the user something to look at
						foldersToCheck.push(file);
					}
				}
			}
			folders.add(folder); // all the folders subfolders have been added, store it for sorting
			System.out.printf("Finished checking %s.%n", folder.getName()); // just to give the user something to look at
		}
		System.out.println();
		System.out.println("Finished finding all subfolders. The sorting process will begin in a moment.");
		System.out.println("Sorting takes time, expect the program to sort slowly at first and to slow intermittently.");

		try {
			Thread.sleep(1000 * 3);
		} catch (InterruptedException e) {
			System.out.println("An error occured.");
			e.printStackTrace();
		}

		folders.sort((f1, f2) -> { // use comparator to sort files by size
			long sizeF1 = size(f1.toPath()), sizeF2 = size(f2.toPath());
			System.out.printf("Sorting... Comparisons: %d%n", comparisons++); // just to give the user something to look at

			if (sizeF1 > sizeF2) {
				return 1;
			} else if (sizeF1 < sizeF2) {
				return -1;
			} else {
				return 0;
			}
		});

		File folder;
		// print all the folders in order from least size to greatest size
		for (int i = (1000 > folders.size()) ? 0 : folders.size() - 1000; i < folders.size(); i++) {
			folder = folders.get(i);
			System.out.printf("%s size: %.2f GB%n", folder.getAbsolutePath(), size(folder.toPath()) / 1_000_000_000.0);
		}
		
		try {
			Thread.sleep(1000 * 60 * 60 * 24);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// returns the size of a folder. not my code.
	public static long size(Path path) {

		if (fileSizes.containsKey(path)) { // this part is my code, sees if file size has already been cached
			return fileSizes.get(path);
		}

		final AtomicLong size = new AtomicLong(0);

		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

					size.addAndGet(attrs.size());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) {

					// System.out.println("skipped: " + file + " (" + exc + ")");
					// Skip folders that can't be traversed
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

					if (exc != null)
						System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
					// Ignore errors traversing a folder
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
		}

		long fileSize = size.get();
		fileSizes.put(path, fileSize); // this is my code too, caches file size for quicker lookup
		return fileSize;
	}

	public static class AFKThread extends Thread {
		public AFKThread() {
			super(() -> { // thread to kill the program if it waits for user input for too long, assume user closed the window without exiting
				try {
					Thread.sleep(1000 * 60 * 10); // 1000 milliseconds * 60 seconds * 10 minutes
					System.out.println("No user input detected, stopping program...");
					System.exit(1);
				} catch (InterruptedException e) { // interruption means the user entered something
				}
			});
		}
	}
}
