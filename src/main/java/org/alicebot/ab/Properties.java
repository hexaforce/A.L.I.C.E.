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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

/**
 * Bot Properties
 */
@Slf4j
public class Properties extends HashMap<String, String> {
	private static final long serialVersionUID = 1L;

	// General global strings
	public static String program_name_version = "Program AB 0.0.6.26 beta -- AI Foundation Reference AIML 2.1 implementation";
//	private static String comment = "Added repetition detection.";
//	private static String aimlif_split_char = ",";
//	private static String default_bot = "alice2";
////	private static String default_language = "EN";
//	static String aimlif_split_char_name = "\\#Comma";
	static String aimlif_file_suffix = ".csv";
//	private static String ab_sample_file = "sample.txt";
	static String text_comment_mark = ";;";
	// <sraix> defaults
	static String pannous_api_key = "guest";
	static String pannous_login = "test-user";
	static String sraix_failed = "SRAIXFAILED";
	static String repetition_detected = "REPETITIONDETECTED";
	static String sraix_no_hint = "nohint";
	static String sraix_event_hint = "event";
	static String sraix_pic_hint = "pic";
	static String sraix_shopping_hint = "shopping";
	// AIML files
	static String unknown_aiml_file = "unknown_aiml_file.aiml";
	static String deleted_aiml_file = "deleted.aiml";
	static String learnf_aiml_file = "learnf.aiml";
	static String null_aiml_file = "null.aiml";
//	private static String inappropriate_aiml_file = "inappropriate.aiml";
//	private static String profanity_aiml_file = "profanity.aiml";
//	private static String insult_aiml_file = "insults.aiml";
//	private static String reductions_update_aiml_file = "reductions_update.aiml";
//	private static String predicates_aiml_file = "client_profile.aiml";
//	private static String update_aiml_file = "update.aiml";
//	private static String personality_aiml_file = "personality.aiml";
//	private static String sraix_aiml_file = "sraix.aiml";
//	private static String oob_aiml_file = "oob.aiml";
//	private static String unfinished_aiml_file = "unfinished.aiml";
	// filter responses
//	private static String inappropriate_filter = "FILTER INAPPROPRIATE";
//	private static String profanity_filter = "FILTER PROFANITY";
//	private static String insult_filter = "FILTER INSULT";
	// default templates
//	private static String deleted_template = "deleted";
//	private static String unfinished_template = "unfinished";
	// AIML defaults
	static String bad_javascript = "JSFAILED";
	static String js_enabled = "true";
	static String unknown_history_item = "unknown";
	static String default_bot_response = "会話を理解・認識できません。";
	static String error_bot_response = "私の脳に何か問題があります。";
	static String schedule_error = "I'm unable to schedule that event.";
	static String system_failed = "Failed to execute system command.";
	static String default_get = "unknown";
	static String default_property = "unknown";
	static String default_map = "unknown";
	static String default_Customer_id = "unknown";
//	private static String default_bot_name = "unknown";
	static String default_that = "unknown";
	static String default_topic = "unknown";
	static String default_list_item = "NIL";
	static String undefined_triple = "NIL";
	static String unbound_variable = "unknown";
	public static String template_failed = "Template failed.";
	static String too_much_recursion = "Too much recursion in AIML";
	static String too_much_looping = "Too much looping in AIML";
//	private static String blank_template = "blank template";
	static String null_input = "NORESP";
	static String null_star = "nullstar";
	// sets and maps
	static String set_member_string = "ISA";
	static String remote_map_key = "external";
//	private static String remote_set_key = "external";
	static String natural_number_set_name = "number";
//	private static String map_successor = "successor";
//	private static String map_predecessor = "predecessor";
//	private static String map_singular = "singular";
//	private static String map_plural = "plural";

	//
//	private static int node_activation_cnt = 4; // minimum number of activations to suggest atomic pattern
//	private static int node_size = 4; // minimum number of branches to suggest wildcard pattern
//	private static int displayed_input_sample_size = 6;
	static int max_history = 32;
	static int repetition_count = 2;
//	private static int max_stars = 1000;
	static int max_graph_height = 100000;
//	private static int max_substitutions = 10000;
	static int max_recursion_depth = 765; // assuming java -Xmx512M
	static int max_recursion_count = 2048;
//	private static int max_trace_length = 2048;
	static int max_loops = 10000;
//	private static int estimated_brain_size = 5000;
//	private static int max_natural_number_digits = 10000;
//	private static int brain_print_size = 100; // largest size of brain to print to System.out

	//
//	private static boolean trace_mode = true;
	static boolean enable_external_sets = true;
//	private static boolean enable_external_maps = true;
//	private static boolean jp_tokenize = false;
	static boolean fix_excel_csv = true;
	static boolean enable_network_connection = true;
	static boolean cache_sraix = false;
	static boolean qa_test_mode = false;
//	private static boolean make_verbs_sets_maps = false;

	/**
	 * get the value of a bot property.
	 *
	 * @param key property name
	 * @return property value or a string indicating the property is undefined
	 */
	String get(String key) {
		if (containsKey(key))
			return super.get(key);
		return Properties.default_property;
	}

	/**
	 * Read bot properties from a file.
	 *
	 * @param filename file containing bot properties
	 * @return count
	 */
	int getProperties(String filename) {
		int cnt = 0;
		File file = new File(filename);
		if (file.exists()) {
			log.debug("Get Properties: " + filename);
			try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				String strLine;
				while ((strLine = reader.readLine()) != null) {
					if (strLine.contains(":")) {
						final String property = strLine.substring(0, strLine.indexOf(":"));
						final String value = strLine.substring(strLine.indexOf(":") + 1);
						put(property, value);
						log.debug("load Properties key:{} value:{}", property, value);
						cnt++;
					}
				}
			} catch (final Exception e) {
				log.error("Error: " + e.getMessage());
			}
		}
		return cnt;
	}
}
