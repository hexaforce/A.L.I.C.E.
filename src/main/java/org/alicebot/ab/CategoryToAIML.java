package org.alicebot.ab;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategoryToAIML {

	final static String NL = System.getProperty("line.separator");

	/**
	 * convert a Category object to AIML syntax
	 *
	 * @param category Category object
	 * @return AIML Category
	 */
	static String toAIML(Category category) {

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
					thatStatement = "    <that>" + category.getThat() + "</that>" + NL;
				}
				String[] templates = category.getTemplate().split(NL);
				String template = "";
				for (String t : templates) {
					String tt = t.trim();
					if ("<template>".equals(tt) || "</template>".equals(tt)) {
						template += "    " + tt + NL;
					} else if (tt.startsWith("<template>") || tt.endsWith("</template>")) {
						template += tt.replace("<template>", "<template>" + NL).replace("</template>", NL + "</template>");
					} else {
						template += "      " + tt + NL;
					}
				}
				result = "  <category>" + NL + //
						"    <pattern>" + pattern + "</pattern>" + NL + //
						thatStatement + //
						template + //
						"  </category>" + NL;

			} else {

				String thatStatement = "";
				if (!category.getThat().equals("*")) {
					thatStatement = "      <that>" + category.getThat() + "</that>" + NL;
				}
				String[] templates = category.getTemplate().split(NL);
				String template = "";
				for (String t : templates) {
					String tt = t.trim();
					if ("<template>".equals(tt) || "</template>".equals(tt)) {
						template += "      " + tt + NL;
					} else if (tt.startsWith("<template>") || tt.endsWith("</template>")) {
						template += tt.replace("<template>", "<template>" + NL).replace("</template>", NL + "</template>");
					} else {
						template += "        " + tt + NL;
					}
				}
				result = "  <topic name=\"" + category.getTopic() + "\">" + NL + //
						"    <category>" + NL + //
						"      <pattern>" + pattern + "</pattern>" + NL + //
						thatStatement + //
						template + //
						"    </category>" + NL + //
						"  </topic>" + NL;
			}

		} catch (final Exception ex) {
			log.error(ex.getMessage(), ex);
		}

		return result;

	}
	
}
