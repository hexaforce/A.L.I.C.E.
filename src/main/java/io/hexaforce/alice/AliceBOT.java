package io.hexaforce.alice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Category;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicNumbers;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.Nodemapper;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Verbs;
import org.alicebot.ab.utils.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliceBOT {

	public static void main(String[] args) {
		AIMLProcessor.extension = new PCAIMLProcessorExtension();
		mainFunction(args);
	}

	public static void mainFunction(String[] args) {
		
		String botName = "alice2";
		MagicBooleans.jp_tokenize = false;
		MagicBooleans.trace_mode = true;
		String action = "chat";
		log.info(MagicStrings.program_name_version);
		
//		for (String s : args) {
//			String[] splitArg = s.split("=");
//			if (splitArg.length >= 2) {
//				String option = splitArg[0];
//				String value = splitArg[1];
//				if (option.equals("bot"))
//					botName = value;
//				if (option.equals("action"))
//					action = value;
//				if (option.equals("trace")) {
//					if (value.equals("true"))
//						MagicBooleans.trace_mode = true;
//					else
//						MagicBooleans.trace_mode = false;
//				}
//				if (option.equals("morph")) {
//					if (value.equals("true"))
//						MagicBooleans.jp_tokenize = true;
//					else {
//						MagicBooleans.jp_tokenize = false;
//					}
//				}
//			}
//		}
		
		if (MagicBooleans.trace_mode)
			log.info("Working Directory = " + MagicStrings.root_path);
		
		Graphmaster.enableShortCuts = true;
		Bot bot = new Bot(botName, MagicStrings.root_path, action);
		
		if (MagicBooleans.make_verbs_sets_maps)
			Verbs.makeVerbSetsMaps(bot);
		
		if (bot.brain.getCategories().size() < MagicNumbers.brain_print_size)
			bot.brain.printgraph();
		
		if (MagicBooleans.trace_mode)
			log.info("Action = '" + action + "'");

//		if (action.equals("chat") || action.equals("chat-app")) {
//			boolean doWrites = !action.equals("chat-app");
//			TestAB.testChat(bot, doWrites, MagicBooleans.trace_mode);
//		}
		boolean doWrites = !action.equals("chat-app");
		Chat chatSession = new Chat(bot, doWrites);
		bot.brain.nodeStats();
		while (true) {

			String textLine = IOUtils.readInputTextLine("Human");
			
			String response = chatSession.multisentenceRespond(textLine);
			while (response.contains("&lt;"))
				response = response.replace("&lt;", "<");
			while (response.contains("&gt;"))
				response = response.replace("&gt;", ">");
			
			IOUtils.writeOutputTextLine("Robot", response);
		}

//		else if (action.equals("ab"))
//			TestAB.testAB(bot, TestAB.sample_file);
//		else if (action.equals("aiml2csv") || action.equals("csv2aiml"))
//			convert(bot, action);
//		else if (action.equals("abwq")) {
//			AB ab = new AB(bot, TestAB.sample_file);
//			ab.abwq();
//		} else if (action.equals("test")) {
//			TestAB.runTests(bot, MagicBooleans.trace_mode);
//		} else if (action.equals("shadow")) {
//			MagicBooleans.trace_mode = false;
//			bot.shadowChecker();
//		} else if (action.equals("iqtest")) {
//			ChatTest ct = new ChatTest(bot);
//			try {
//				ct.testMultisentenceRespond();
//			} catch (Exception ex) {
//				log.error(ex.getMessage(), ex);
//			}
//		} else
//			log.info("Unrecognized action " + action);

	}

	public static void convert(Bot bot, String action) {
		if (action.equals("aiml2csv"))
			bot.writeAIMLIFFiles();
		else if (action.equals("csv2aiml"))
			bot.writeAIMLFiles();
	}

	public static void getGloss(Bot bot, String filename) {
		log.info("getGloss");
		try {
			// Open the file that is the first command line parameter
			File file = new File(filename);
			if (file.exists()) {
				FileInputStream fstream = new FileInputStream(filename);
				// Get the object
				getGlossFromInputStream(bot, fstream);
				fstream.close();
			}
		} catch (Exception e) {
			// Catch exception if any
			log.error("Error: " + e.getMessage());
		}
	}

	public static void getGlossFromInputStream(Bot bot, InputStream in) {
		log.info("getGlossFromInputStream");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int cnt = 0;
		int filecnt = 0;
		HashMap<String, String> def = new HashMap<String, String>();
		try {
			// Read File Line By Line
			String word;
			String gloss;
			word = null;
			gloss = null;
			while ((strLine = br.readLine()) != null) {
				if (strLine.contains("<entry word")) {
					int start = strLine.indexOf("<entry word=\"") + "<entry word=\"".length();
					int end = strLine.indexOf("#");
					word = strLine.substring(start, end);
					word = word.replaceAll("_", " ");
					log.info(word);
				} else if (strLine.contains("<gloss>")) {
					gloss = strLine.replaceAll("<gloss>", "");
					gloss = gloss.replaceAll("</gloss>", "");
					gloss = gloss.trim();
					log.info(gloss);
				}
				if (word != null && gloss != null) {
					word = word.toLowerCase().trim();
					if (gloss.length() > 2)
						gloss = gloss.substring(0, 1).toUpperCase() + gloss.substring(1, gloss.length());
					String definition;
					if (def.keySet().contains(word)) {
						definition = def.get(word);
						definition = definition + "; " + gloss;
					} else
						definition = gloss;
					def.put(word, definition);
					word = null;
					gloss = null;
				}
			}
			Category d = new Category(0, "WNDEF *", "*", "*", "unknown", "wndefs" + filecnt + ".aiml");
			bot.brain.addCategory(d);
			for (String x : def.keySet()) {
				word = x;
				gloss = def.get(word) + ".";
				cnt++;
				if (cnt % 5000 == 0)
					filecnt++;
				Category c = new Category(0, "WNDEF " + word, "*", "*", gloss, "wndefs" + filecnt + ".aiml");
				log.info(cnt + " " + filecnt + " " + c.inputThatTopic() + ":" + c.getTemplate() + ":" + c.getFilename());
				Nodemapper node;
				if ((node = bot.brain.findNode(c)) != null)
					node.category.setTemplate(node.category.getTemplate() + "," + gloss);
				bot.brain.addCategory(c);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	public static void sraixCache(String filename, Chat chatSession) {
		int limit = 1000;
		try {
			FileInputStream fstream = new FileInputStream(filename);
			// Get the object
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			// Read File Line By Line
			int count = 0;
			while ((strLine = br.readLine()) != null && count++ < limit) {
				System.out.print("Human: " + strLine);
				String response = chatSession.multisentenceRespond(strLine);
				System.out.print("Robot: " + response);
			}
			br.close();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

}
