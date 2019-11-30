package org.alicebot.ab.utils;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class JapaneseUtils {

	/**
	 * Tokenize a fragment of the input that contains only text
	 *
	 * @param fragment fragment of input containing only text and no XML tags
	 * @return tokenized fragment
	 */
	public static String tokenizeFragment(String fragment) {
		String result = "";
		for (Token e : new Tokenizer().tokenize(fragment)) {
			result += e.getSurface() + " ";
		}
		return result.trim();
	}

	/**
	 * Morphological analysis of an input sentence that contains an AIML pattern.
	 *
	 * @param sentence
	 * @return morphed sentence with one space between words, preserving XML markup
	 *         and AIML $ operation
	 */
	public static String tokenizeSentence(String sentence) {
		if (!MagicBooleans.jp_tokenize)
			return sentence;
		String result = "";
		result = tokenizeXML(sentence);
		while (result.contains("$ "))
			result = result.replace("$ ", "$");
		while (result.contains("  "))
			result = result.replace("  ", " ");
		while (result.contains("anon "))
			result = result.replace("anon ", "anon"); // for Triple Store
		result = result.trim();
		return result;
	}

	public static String tokenizeXML(String xmlExpression) {
		String response = MagicStrings.template_failed;
		try {
			xmlExpression = "<sentence>" + xmlExpression + "</sentence>";
			Node root = DomUtils.parseString(xmlExpression);
			response = recursEval(root);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return AIMLProcessor.trimTag(response, "sentence");
	}

	private static String recursEval(Node node) {
		try {
			String nodeName = node.getNodeName();
			if (nodeName.equals("#text"))
				return tokenizeFragment(node.getNodeValue());
			else if (nodeName.equals("sentence"))
				return evalTagContent(node);
			else
				return (genericXML(node));
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return "JP Morph Error";
	}

	public static String genericXML(Node node) {
		String result = evalTagContent(node);
		return unevaluatedXML(result, node);
	}

	public static String evalTagContent(Node node) {
		String result = "";
		try {
			NodeList childList = node.getChildNodes();
			for (int i = 0; i < childList.getLength(); i++) {
				Node child = childList.item(i);
				result += recursEval(child);
			}
		} catch (Exception ex) {
			log.info("Something went wrong with evalTagContent");
			log.error(ex.getMessage(), ex);
		}
		return result;
	}

	private static String unevaluatedXML(String result, Node node) {
		String nodeName = node.getNodeName();
		String attributes = "";
		if (node.hasAttributes()) {
			NamedNodeMap XMLAttributes = node.getAttributes();
			for (int i = 0; i < XMLAttributes.getLength(); i++){
				attributes += " " + XMLAttributes.item(i).getNodeName() + "=\"" + XMLAttributes.item(i).getNodeValue() + "\"";
			}
		}
		if (result.equals(""))
			return " <" + nodeName + attributes + "/> ";
		else
			return " <" + nodeName + attributes + ">" + result + "</" + nodeName + "> "; // add spaces
	}
}
