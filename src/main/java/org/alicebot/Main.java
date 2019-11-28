package org.alicebot;

import java.io.*;
import java.util.*;
import org.alicebot.ab.*;
import org.alicebot.ab.Timer;
import org.alicebot.ab.utils.IOUtils;

public class Main {

	public Main() {
	}

	public static void main(String args[]) {
		MagicStrings.root_path = System.getProperty("user.dir");
		System.out.println((new StringBuilder()).append("Working Directory = ").append(MagicStrings.root_path).toString());
		AIMLProcessor.extension = new PCAIMLProcessorExtension();
		mainFunction(args);
	}

	public static void mainFunction(String args[]) {
		String botName = "super";
		String action = "chat";
		System.out.println(MagicStrings.programNameVersion);
		String arr$[] = args;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++) {
			String s = arr$[i$];
			System.out.println(s);
			String splitArg[] = s.split("=");
			if (splitArg.length < 2)
				continue;
			String option = splitArg[0];
			String value = splitArg[1];
			if (option.equals("bot"))
				botName = value;
			if (option.equals("action"))
				action = value;
			if (option.equals("trace") && value.equals("true"))
				MagicBooleans.trace_mode = true;
			else
				MagicBooleans.trace_mode = false;
		}

		System.out.println((new StringBuilder()).append("trace mode = ").append(MagicBooleans.trace_mode).toString());
		Graphmaster.enableShortCuts = true;
		Timer timer = new Timer();
		Bot bot = new Bot(botName, MagicStrings.root_path, action);
		if (bot.brain.getCategories().size() < 100)
			bot.brain.printgraph();
		if (action.equals("chat"))
			testChat(bot, MagicBooleans.trace_mode);
		else if (action.equals("test"))
			testSuite(bot, (new StringBuilder()).append(MagicStrings.root_path).append("/data/find.txt").toString());
		else if (action.equals("ab"))
			testAB(bot);
		else if (action.equals("aiml2csv") || action.equals("csv2aiml"))
			convert(bot, action);
		else if (action.equals("abwq"))
			AB.abwq(bot);
	}

	public static void convert(Bot bot, String action) {
		if (action.equals("aiml2csv"))
			bot.writeAIMLIFFiles();
		else if (action.equals("csv2aiml"))
			bot.writeAIMLFiles();
	}

	public static void testAB(Bot bot) {
		MagicBooleans.trace_mode = true;
		AB.ab(bot);
		AB.terminalInteraction(bot);
	}

	public static void testShortCuts() {
	}

	public static void testChat(Bot bot, boolean traceMode) {
		Chat chatSession = new Chat(bot);
		bot.brain.nodeStats();
		MagicBooleans.trace_mode = traceMode;
		String textLine = "";
		do {
			System.out.print("Human: ");
			textLine = IOUtils.readInputTextLine();
			if (textLine == null || textLine.length() < 1)
				textLine = MagicStrings.null_input;
			if (textLine.equals("q"))
				System.exit(0);
			else if (textLine.equals("wq")) {
				bot.writeQuit();
				System.exit(0);
			} else if (textLine.equals("ab")) {
				testAB(bot);
			} else {
				String request = textLine;
				if (MagicBooleans.trace_mode)
					System.out.println((new StringBuilder()).append("STATE=").append(request).append(":THAT=").append(((History) chatSession.thatHistory.get(0)).get(0)).append(":TOPIC=").append(chatSession.predicates.get("topic")).toString());
				String response;
				for (response = chatSession.multisentenceRespond(request); response.contains("&lt;"); response = response.replace("&lt;", "<"))
					;
				for (; response.contains("&gt;"); response = response.replace("&gt;", ">"))
					;
				System.out.println((new StringBuilder()).append("Robot: ").append(response).toString());
			}
		} while (true);
	}

	public static void testBotChat() {
		Bot bot = new Bot("alice");
		System.out.println((new StringBuilder()).append(bot.brain.upgradeCnt).append(" brain upgrades").toString());
		bot.brain.nodeStats();
		Chat chatSession = new Chat(bot);
		String request = "Hello.  How are you?  What is your name?  Tell me about yourself.";
		String response = chatSession.multisentenceRespond(request);
		System.out.println((new StringBuilder()).append("Human: ").append(request).toString());
		System.out.println((new StringBuilder()).append("Robot: ").append(response).toString());
	}

	public static void testSuite(Bot bot, String filename) {
		try {
			AB.passed.readAIMLSet(bot);
			AB.testSet.readAIMLSet(bot);
			System.out.println((new StringBuilder()).append("Passed ").append(AB.passed.size()).append(" samples.").toString());
			String textLine = "";
			Chat chatSession = new Chat(bot);
			FileInputStream fstream = new FileInputStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			int count = 0;
			HashSet samples = new HashSet();
			String strLine;
			while ((strLine = br.readLine()) != null)
				samples.add(strLine);
			ArrayList sampleArray = new ArrayList(samples);
			Collections.sort(sampleArray);
			Iterator i$ = sampleArray.iterator();
			do {
				if (!i$.hasNext())
					break;
				String request = (String) i$.next();
				if (request.startsWith("Human: "))
					request = request.substring("Human: ".length(), request.length());
				Category c = new Category(0, bot.preProcessor.normalize(request), "*", "*", MagicStrings.blank_template, MagicStrings.null_aiml_file);
				if (AB.passed.contains(request))
					System.out.println((new StringBuilder()).append("--> Already passed ").append(request).toString());
				else if (!bot.deletedGraph.existsCategory(c) && !AB.passed.contains(request)) {
					String response = chatSession.multisentenceRespond(request);
					System.out.println((new StringBuilder()).append(count).append(". Human: ").append(request).toString());
					System.out.println((new StringBuilder()).append(count).append(". Robot: ").append(response).toString());
					textLine = IOUtils.readInputTextLine();
					AB.terminalInteractionStep(bot, request, textLine, c);
					count++;
				}
			} while (true);
			br.close();
		} catch (Exception e) {
			System.err.println((new StringBuilder()).append("Error: ").append(e.getMessage()).toString());
		}
	}
}
