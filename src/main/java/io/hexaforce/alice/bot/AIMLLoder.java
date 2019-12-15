package io.hexaforce.alice.bot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.w3c.dom.Node;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AIMLLoder {

	void saveAIML_IF(File aimlIFDirectory, ArrayList<Category> categories) {
		if (!aimlIFDirectory.exists()) {
			aimlIFDirectory.mkdir();
		}
		try {
			final String c = ",";
			final HashMap<String, BufferedWriter> fileBuffer = new HashMap<String, BufferedWriter>();
			for (final Category category : categories) {
				BufferedWriter bufferedWriter;
				if (fileBuffer.containsKey(category.getAimlFileName())) {
					bufferedWriter = fileBuffer.get(category.getAimlFileName());
				} else {
					File aimlFile = new File(aimlIFDirectory.getPath() + "/" + category.getAimlFileName() + ".csv");
					bufferedWriter = new BufferedWriter(new FileWriter(aimlFile));
					fileBuffer.put(category.getAimlFileName(), bufferedWriter);
				}
				String result = "";//category.getActivationCnt() + c + category.getPattern() + c + category.getThat() + c + category.getTopic() + c + templateToLine(category.getTemplate()) + c + category.getAimlFileName();
				
				bufferedWriter.write(result);
			}
			for (final BufferedWriter bufferedWriter : fileBuffer.values()) {
				bufferedWriter.flush();
				bufferedWriter.close();
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		aimlIFDirectory.setLastModified(new Date().getTime());
	}
	
	private final static IOFileFilter aimlFileExtension = FileFilterUtils.suffixFileFilter(".aiml");

	void saveAIML(File aimlDirectory, ArrayList<Category> categories) {
		try {
			final HashMap<String, BufferedWriter> fileBuffer = new HashMap<String, BufferedWriter>();
			for (final Category category : categories) {
				if ("null.aiml".equals(category.getAimlFileName())) {
					continue;
				}
				BufferedWriter bufferedWriter;
				if (fileBuffer.containsKey(category.getAimlFileName())) {
					bufferedWriter = fileBuffer.get(category.getAimlFileName());
				} else {
					File aimlFile = new File(aimlDirectory.getPath() + "/" + category.getAimlFileName());
					bufferedWriter = new BufferedWriter(new FileWriter(aimlFile));
					fileBuffer.put(category.getAimlFileName(), bufferedWriter);
					bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					bufferedWriter.newLine();
					bufferedWriter.write("<aiml version=\"2.1\">");
					bufferedWriter.newLine();
				}
				bufferedWriter.write(categoryToAIML(category));
			}
			for (final BufferedWriter bufferedWriter : fileBuffer.values()) {
				bufferedWriter.newLine();
				bufferedWriter.write("</aiml>");
				bufferedWriter.newLine();
				bufferedWriter.flush();
				bufferedWriter.close();
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	static String categoryToAIML(Category category) {

		String NL = System.getProperty("line.separator");
		String result = "";

		String pattern = category.getPattern();
		if (pattern.contains("<SET>") || pattern.contains("<BOT")) {
			final String[] splitPattern = pattern.split(" ");
			String rpattern = "";
			for (String w : splitPattern) {
				if (w.startsWith("<SET>") || w.startsWith("<BOT") || w.startsWith("NAME=")) {
					w = w.toLowerCase();
				}
				rpattern = rpattern + " " + w;
			}
			pattern = rpattern.trim();
		}
		try {
			if (category.getTopic().equals("*")) {
				String thatStatement = "";
				if (!category.getThat().equals("*")) {
					thatStatement = "      <that>" + category.getThat() + "</that>" + NL;
				}
				String[] templates = category.getTemplate().split(NL);
				String template = "";
				for (String t : templates) {
					template += "      " + t.trim() + NL;
				}
				result ="  <category>" + NL + //
						"    <pattern>" + NL + //
						"      " + pattern + NL + //
						"    </pattern>" + NL + //
						thatStatement + //
						"    <template>" + NL + //
						template + //
						"    </template>" + NL + //
						"  </category>" + NL;
			} else {
				String thatStatement = "";
				if (!category.getThat().equals("*")) {
					thatStatement = "      <that>" + category.getThat() + "</that>" + NL;
				}
				String[] templates = category.getTemplate().split(NL);
				String template = "";
				for (String t : templates) {
					template += "      " + t.trim() + NL;
				}
				result ="  <topic name=\"" + category.getTopic() + "\">" + NL + //
						"    <category>" + NL + //
						"      <pattern>" + NL + //
						"        " + pattern + NL + //
						"      </pattern>" + NL + //
						thatStatement + //
						"      <template>" + NL + //
						template + //
						"      </template>" + NL + //
						"    </category>" + NL + //
						"  </topic>" + NL;
			}
		} catch (final Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return result;
	}

	public static ArrayList<Category> load(File aimlDirectory) {
		final ArrayList<Category> categories = new ArrayList<Category>();
		final Collection<File> aimlFiles = FileUtils.listFiles(aimlDirectory, aimlFileExtension, FileFilterUtils.trueFileFilter());
		final Map<String, Node> rootNodes = aimlFiles.parallelStream().collect(Collectors.toMap(File::getName, DomUtils::parseFile));
		for (final Entry<String, Node> x : rootNodes.entrySet()) {
			categories.addAll(toCategories(x.getKey(), x.getValue()));
		}
		return categories;
	}

	public static ArrayList<Category> toCategories(String aimlFileName, Node node) {
		final ArrayList<Category> categories = new ArrayList<Category>();
		for (Node child : new IterableNodeList(node.getChildNodes())) {
			if ("category".equals(child.getNodeName())) {
				categories.add(categoryProcessor(aimlFileName, child));
			} else if ("topic".equals(child.getNodeName())) {
				final String topic = child.getAttributes().getNamedItem("name").getTextContent();
				for (Node grandchild : new IterableNodeList(child.getChildNodes())) {
					categories.add(categoryProcessor(aimlFileName, grandchild, topic));
				}
			}
		}
		return categories;
	}

	private static Category categoryProcessor(String aimlFileName, Node node) {
		return categoryProcessor(aimlFileName, node, "*");
	}

	private static Category categoryProcessor(String aimlFileName, Node node, String topic) {

		String pattern = "*";
		String that = "*";
		String template = "";

		for (Node child : new IterableNodeList(node.getChildNodes())) {
			if ("#text".equals(child.getNodeName())) {
				continue;
			}
			String nodeToString = DomUtils.nodeToString(child);
			if ("pattern".equals(child.getNodeName())) {
				nodeToString = trimTag(nodeToString, "pattern");
				nodeToString = cleanPattern(nodeToString);
				pattern = JapaneseUtils.tokenizeSentence(nodeToString);
			} else if ("that".equals(child.getNodeName())) {
				nodeToString = trimTag(nodeToString, "that");
				nodeToString = cleanPattern(nodeToString);
				that = JapaneseUtils.tokenizeSentence(nodeToString);
			} else if ("topic".equals(child.getNodeName())) {
				nodeToString = trimTag(nodeToString, "topic");
				nodeToString = cleanPattern(nodeToString);
				topic = JapaneseUtils.tokenizeSentence(nodeToString);
			} else if ("template".equals(child.getNodeName())) {
				template = nodeToString;
			} else {
				log.warn("categoryProcessor: unexpected {} in {}", child.getNodeName(), nodeToString);
			}
		}
		return Category.builder().aimlFileName(aimlFileName).topic(topic).pattern(pattern).that(that).template(template).build();
	}

	private final static String cleanPattern(String pattern) {
		pattern = pattern.replaceAll("(\r\n|\n\r|\r|\n)", " ");
		pattern = pattern.replaceAll("　", " ");
		return pattern.trim();
	}

	private final static String trimTag(String s, String tagName) {
		final String stag = "<" + tagName + ">";
		final String etag = "</" + tagName + ">";
		if (s.startsWith(stag) && s.endsWith(etag)) {
			s = s.substring(stag.length());
			s = s.substring(0, s.length() - etag.length());
		}
		return s.trim();
	}

}
