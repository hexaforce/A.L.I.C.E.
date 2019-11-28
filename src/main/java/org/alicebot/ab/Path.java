package org.alicebot.ab;

import java.util.ArrayList;

public class Path extends ArrayList<String> {
	private static final long serialVersionUID = 1L;

	public Path next = null;
	public String word = null;
	public int length = 0;

	public static Path sentenceToPath(String sentence) {
		sentence = sentence.trim();
		return arrayToPath(sentence.split(" "));
	}

	public static String pathToSentence(Path path) {
		String result = "";
		for (Path p = path; p != null; p = p.next) {
			result = result + " " + path.word;
		}
		return result.trim();
	}

	private static Path arrayToPath(String[] array) {
		Path tail = null;
		Path head = null;
		for (int i = array.length - 1; i >= 0; i--) {
			head = new Path();
			head.word = array[i];
			head.next = tail;
			if (tail == null) {
				head.length = 1;
			} else {
				tail.length++;
			}
			tail = head;
		}
		return head;
	}

	@SuppressWarnings("unused")
	private static Path arrayToPath(String[] array, int index) {
		if (index >= array.length)
			return null;
		Path newPath = new Path();
		newPath.word = array[index];
		newPath.next = arrayToPath(array, index + 1);
		if (newPath.next == null) {
			newPath.length = 1;
		} else {
			newPath.next.length++;
		}
		return newPath;
	}

	public void print() {
		String result = "";
		for (Path p = this; p != null; p = p.next) {
			result = result + p.word + ",";
		}
		if (result.endsWith(","))
			result = result.substring(0, result.length() - 1);
		System.out.println(result);
	}
}
