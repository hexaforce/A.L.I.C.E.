package org.alicebot.ab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.java.sen.SenFactory;
import net.java.sen.StringTagger;
import net.java.sen.dictionary.Token;

public class JapaneseTokenizer {

	static final Pattern tagPattern = Pattern.compile("(<.*>.*</.*>)|(<.*/>)");

	static Set<Character.UnicodeBlock> japaneseUnicodeBlocks = new HashSet<Character.UnicodeBlock>() {
		private static final long serialVersionUID = 1L;
	};

	public static String buildFragment(String fragment) {
		String result = "";

		StringTagger tagger = SenFactory.getStringTagger(null, false);
		List<Token> tokens = new ArrayList<Token>();
		try {
			tagger.analyze(fragment, tokens);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//		for (Morpheme e : Tagger.parse(fragment)) {
//			result = result + e.surface + " ";
//		}
		for (Token e : tokens) {
			result = result + e.getSurface() + " ";
		}
		return result.trim();
	}

	public static String morphSentence(String sentence) {

		if (!MagicBooleans.jp_morphological_analysis)
			return sentence;

		String result = "";
		Matcher matcher = tagPattern.matcher(sentence);
		while (matcher.find()) {
			String prefix;
			int i = matcher.start();
			int j = matcher.end();

			if (i > 0) {
				prefix = sentence.substring(0, i - 1);
			} else {
				prefix = "";
			}
			String tag = sentence.substring(i, j);
			result = result + " " + buildFragment(prefix) + " " + tag;
			if (j < sentence.length()) {
				sentence = sentence.substring(j, sentence.length());
				continue;
			}
			sentence = "";
		}

		result = result + " " + buildFragment(sentence);
		for (; result.contains("$ "); result = result.replace("$ ", "$"))
			;
		for (; result.contains("  "); result = result.replace("  ", " "))
			;
		return result.trim();
	}
}
