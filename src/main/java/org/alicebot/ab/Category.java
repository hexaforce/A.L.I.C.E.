package org.alicebot.ab;

/* 
	Program AB Reference AIML 2.1 implementation

	Copyright (C) 2013 ALICE A.I. Foundation
	Contact: info@alicebot.org

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Library General Public
	License as published by the Free Software Foundation; either
	version 2 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Library General License for more details.

	You should have received a copy of the GNU Library General Public
	License along with this library; if not, write to the
	Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
	Boston, MA  02110-1301, USA.
*/

import java.util.Comparator;

/**
 * structure representing an AIML category and operations on Category
 */
class Category {

	private String pattern;
	private String that;
	private String topic;
	private String template;
	private String filename;
	private int activationCnt;
	private final int categoryNumber; // for loading order
	static int categoryCnt = 0;
	private AIMLSet matches;

	/**
	 * number of times a category was activated by inputs
	 *
	 * @return integer number of activations
	 */
	private int getActivationCnt() {
		return activationCnt;
	}

	/**
	 * get the index number of this category
	 *
	 * @return unique integer identifying this category
	 */
	private int getCategoryNumber() {
		return categoryNumber;
	}

	/**
	 * get category pattern
	 *
	 * @return pattern
	 */
	String getPattern() {
		if (pattern == null) {
			return "*";
		} else {
			return pattern;
		}
	}

	/**
	 * get category that pattern
	 *
	 * @return that pattern
	 */
	String getThat() {
		if (that == null) {
			return "*";
		} else {
			return that;
		}
	}

	/**
	 * get category topic pattern
	 *
	 * @return topic pattern
	 */
	String getTopic() {
		if (topic == null) {
			return "*";
		} else {
			return topic;
		}
	}

	/**
	 * get category template
	 *
	 * @return template
	 */
	String getTemplate() {
		if (template == null) {
			return "";
		} else {
			return template;
		}
	}

	/**
	 * get name of AIML file for this category
	 *
	 * @return file name
	 */
	String getFilename() {
		if (filename == null) {
			return Properties.unknown_aiml_file;
		} else {
			return filename;
		}
	}

	/**
	 * return a string represeting the full pattern path as
	 * "{@code input pattern <THAT> that pattern <TOPIC> topic pattern}"
	 * 
	 * @return string
	 */
	String inputThatTopic() {
		return Graphmaster.inputThatTopic(pattern, that, topic);
	}

	/**
	 * add a matching input to the matching input set
	 *
	 * @param input matching input
	 * @param bot   bot
	 */
	void addMatch(String input, Bot bot) {
		if (matches == null) {
			final String setName = this.inputThatTopic().replace("*", "STAR").replace("_", "UNDERSCORE").replace(" ", "-").replace("<THAT>", "THAT").replace("<TOPIC>", "TOPIC");
			matches = new AIMLSet(setName, bot);
		}
		matches.add(input);
	}

	public final static String Newline = "\\#Newline";
	public final static String Comma = "\\#Comma";
	
	/**
	 * convert a template to a single-line representation by replacing "," with
	 * #Comma and newline with #Newline
	 * 
	 * @param template original template
	 * @return template on a single line of text
	 */
	static String templateToLine(String template) {
		String result = template;
		result = result.replaceAll("(\r\n|\n\r|\r|\n)", Newline);
		result = result.replaceAll(",", Comma);
		return result;
	}

	/**
	 * restore a template to its original form by replacing #Comma with "," and
	 * #Newline with newline.
	 * 
	 * @param line template on a single line of text
	 * @return original multi-line template
	 */
	private static String lineToTemplate(String line) {
		String result = line.replaceAll(Newline, System.getProperty("line.separator"));
		result = result.replaceAll(Comma, ",");
		return result;
	}

	/**
	 * convert a category from AIMLIF format to a Category object
	 *
	 * @param IF Category in AIMLIF format
	 * @return Category object
	 */
	static Category IFToCategory(String IF) {
		final String[] split = IF.split(",");
		return new Category(Integer.parseInt(split[0]), split[1], split[2], split[3], lineToTemplate(split[4]), split[5]);
	}

	/**
	 * convert a Category object to AIMLIF format
	 * 
	 * @param category Category object
	 * @return category in AIML format
	 */
	static String categoryToIF(Category category) {
		final String c = ",";
		return category.getActivationCnt() + c + category.getPattern() + c + category.getThat() + c + category.getTopic() + c + templateToLine(category.getTemplate()) + c + category.getFilename();
	}

	/**
	 * Constructor
	 *
	 * @param activationCnt category activation count
	 * @param pattern       input pattern
	 * @param that          that pattern
	 * @param topic         topic pattern
	 * @param template      AIML template
	 * @param filename      AIML file name
	 */

	Category(int activationCnt, String pattern, String that, String topic, String template, String filename) {
		if (Properties.fix_excel_csv) {
			pattern = Utilities.fixCSV(pattern);
			that = Utilities.fixCSV(that);
			topic = Utilities.fixCSV(topic);
			template = Utilities.fixCSV(template);
			filename = Utilities.fixCSV(filename);
		}
		this.pattern = pattern.trim().toUpperCase();
		this.that = that.trim().toUpperCase();
		this.topic = topic.trim().toUpperCase();
		this.template = template.replace("& ", " and "); // XML parser treats & badly
		this.filename = filename;
		this.activationCnt = activationCnt;
		matches = null;
		this.categoryNumber = categoryCnt++;
	}

	/**
	 * compare two categories for sorting purposes based on category index number
	 */
	static Comparator<Category> CATEGORY_NUMBER_COMPARATOR = new Comparator<Category>() {
		@Override
		public int compare(Category c1, Category c2) {
			return c1.getCategoryNumber() - c2.getCategoryNumber();
		}
	};

}
