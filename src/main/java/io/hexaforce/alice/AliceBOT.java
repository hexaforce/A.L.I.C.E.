package io.hexaforce.alice;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicNumbers;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Verbs;
import org.alicebot.ab.utils.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliceBOT {

	public static void main(String[] args) {
		mainFunction();
	}

	public static void mainFunction() {

		AIMLProcessor.extension = new PCAIMLProcessorExtension();
		String botName = "alice2";
		MagicBooleans.jp_tokenize = true;
		log.info(MagicStrings.program_name_version);
		
			log.trace("Working Directory = " + MagicStrings.root_path);

		Graphmaster.enableShortCuts = true;
		Bot bot = new Bot(botName, MagicStrings.root_path);

		if (MagicBooleans.make_verbs_sets_maps)
			Verbs.makeVerbSetsMaps(bot);

		if (bot.brain.getCategories().size() < MagicNumbers.brain_print_size)
			bot.brain.printgraph();

		boolean doWrites = true;
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

	}

}
