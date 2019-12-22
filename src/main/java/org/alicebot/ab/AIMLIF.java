package org.alicebot.ab;

public class AIMLIF {
	/**
	 * convert a template to a single-line representation by replacing "," with
	 * #Comma and newline with #Newline
	 * 
	 * @param template original template
	 * @return template on a single line of text
	 */
	static String templateToLine(String template) {
		String result = template;
		result = result.replaceAll("(\r\n|\n\r|\r|\n)", Category.Newline);
		result = result.replaceAll(",", Category.Comma);
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
		String result = line.replaceAll(Category.Newline, System.getProperty("line.separator"));
		result = result.replaceAll(Category.Comma, ",");
		return result;
	}

	/**
	 * convert a category from AIMLIF format to a Category object
	 *
	 * @param IF Category in AIMLIF format
	 * @return Category object
	 */
	static Category toCategory(String IF) {
		final String[] split = IF.split(",");
		return new Category(Integer.parseInt(split[0]), split[1], split[2], split[3], lineToTemplate(split[4]), split[5]);
	}

	/**
	 * convert a Category object to AIMLIF format
	 * 
	 * @param category Category object
	 * @return category in AIML format
	 */
	static String toIF(Category category) {
		final String c = ",";
		return category.getActivationCnt() + c + category.getPattern() + c + category.getThat() + c + category.getTopic() + c + templateToLine(category.getTemplate()) + c + category.getFilename();
	}

}
