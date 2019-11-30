package org.alicebot.ab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/* Program AB Reference AIML 2.0 implementation
        Copyright (C) 2013 ALICE A.I. Foundation
        Contact: info@alicebot.org
        Contact: info@alicebot.org

        This library is free software; you can redistribute it and/or
        modify it under the terms of the GNU Library General Public
        License as published by the Free Software Foundation; either
        version 2 of the License, or (at your option) any later version.

        This library is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
        Library General Public License for more details.

        You should have received a copy of the GNU Library General Public
        License along with this library; if not, write to the
        Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
        Boston, MA  02110-1301, USA.
*/
import org.alicebot.ab.utils.IOUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Class representing the AIML bot
 */
@Slf4j
public class Bot {

	public final Properties properties = new Properties();
	public final PreProcessor preProcessor;
	public final Graphmaster brain;
	public Graphmaster learnfGraph;
	public Graphmaster learnGraph;

	// public Graphmaster unfinishedGraph;
	// public final ArrayList<Category> categories;

	public String name = MagicStrings.default_bot_name;
	public HashMap<String, AIMLSet> setMap = new HashMap<String, AIMLSet>();
	public HashMap<String, AIMLMap> mapMap = new HashMap<String, AIMLMap>();
	public HashSet<String> pronounSet = new HashSet<String>();
	public String root_path = "c:/ab";
	public String bot_path = root_path + "/bots";
	public String bot_name_path = bot_path + "/super";
	public String aimlif_path = bot_path + "/aimlif";
	public String aiml_path = bot_path + "/aiml";
	public String config_path = bot_path + "/config";
	public String log_path = bot_path + "/log";
	public String sets_path = bot_path + "/sets";
	public String maps_path = bot_path + "/maps";

	/**
	 * Set all directory path variables for this bot
	 *
	 * @param root root directory of Program AB
	 * @param name name of bot
	 */
	public void setAllPaths(String root, String name) {
		bot_path = root + "/bots";
		bot_name_path = bot_path + "/" + name;
		log.trace("Name = " + name + " Path = " + bot_name_path);
		aiml_path = bot_name_path + "/aiml";
		aimlif_path = bot_name_path + "/aimlif";
		config_path = bot_name_path + "/config";
		log_path = bot_name_path + "/logs";
		sets_path = bot_name_path + "/sets";
		maps_path = bot_name_path + "/maps";
		log.trace(root_path);
		log.trace(bot_path);
		log.trace(bot_name_path);
		log.trace(aiml_path);
		log.trace(aimlif_path);
		log.trace(config_path);
		log.trace(log_path);
		log.trace(sets_path);
		log.trace(maps_path);

	}

	/**
	 * Constructor (default action, default path, default bot name)
	 */
	public Bot() {
		this(MagicStrings.default_bot);
	}

	/**
	 * Constructor (default action, default path)
	 * 
	 * @param name
	 */
	public Bot(String name) {
		this(name, MagicStrings.root_path);
	}

	/**
	 * Constructor (default action)
	 *
	 * @param name
	 * @param path
	 */
	public Bot(String name, String path) {

		int cnt = 0;

		this.name = name;
		setAllPaths(path, name);
		this.brain = new Graphmaster(this);

		this.learnfGraph = new Graphmaster(this, "learnf");
		this.learnGraph = new Graphmaster(this, "learn");

		preProcessor = new PreProcessor(this);
		addProperties();
		cnt = addAIMLSets();
		log.trace("Loaded " + cnt + " set elements.");
		cnt = addAIMLMaps();
		log.trace("Loaded " + cnt + " map elements");
		this.pronounSet = getPronouns();
		AIMLSet number = new AIMLSet(MagicStrings.natural_number_set_name, this);
		setMap.put(MagicStrings.natural_number_set_name, number);
		AIMLMap successor = new AIMLMap(MagicStrings.map_successor, this);
		mapMap.put(MagicStrings.map_successor, successor);
		AIMLMap predecessor = new AIMLMap(MagicStrings.map_predecessor, this);
		mapMap.put(MagicStrings.map_predecessor, predecessor);
		AIMLMap singular = new AIMLMap(MagicStrings.map_singular, this);
		mapMap.put(MagicStrings.map_singular, singular);
		AIMLMap plural = new AIMLMap(MagicStrings.map_plural, this);
		mapMap.put(MagicStrings.map_plural, plural);

		Date aimlDate = new Date(new File(aiml_path).lastModified());
		Date aimlIFDate = new Date(new File(aimlif_path).lastModified());
		log.trace("AIML modified " + aimlDate + " AIMLIF modified " + aimlIFDate);

		MagicStrings.pannous_api_key = Utilities.getPannousAPIKey(this);
		MagicStrings.pannous_login = Utilities.getPannousLogin(this);
		if (aimlDate.after(aimlIFDate)) {
			log.trace("AIML modified after AIMLIF");
			cnt = addCategoriesFromAIML();
			writeAIMLIFFiles();
		} else {
			addCategoriesFromAIMLIF();
			if (brain.getCategories().size() == 0) {
				log.info("No AIMLIF Files found.  Looking for AIML");
				cnt = addCategoriesFromAIML();
			}
		}

		Category b = new Category(0, "PROGRAM VERSION", "*", "*", MagicStrings.program_name_version, "update.aiml");
		brain.addCategory(b);
		brain.nodeStats();
		learnfGraph.nodeStats();

	}

	HashSet<String> getPronouns() {
		HashSet<String> pronounSet = new HashSet<String>();
		String pronouns = Utilities.getFile(config_path + "/pronouns.txt");
		String[] splitPronouns = pronouns.split("\n");
		for (int i = 0; i < splitPronouns.length; i++) {
			String p = splitPronouns[i].trim();
			if (p.length() > 0)
				pronounSet.add(p);
		}
		log.trace("Read pronouns: " + pronounSet);
		return pronounSet;
	}

	/**
	 * add an array list of categories with a specific file name
	 *
	 * @param file           name of AIML file
	 * @param moreCategories list of categories
	 */
	void addMoreCategories(String file, ArrayList<Category> moreCategories) {
		if (file.contains(MagicStrings.deleted_aiml_file)) {
			/*
			 * for (Category c : moreCategories) { //log.info("Delete "+c.getPattern());
			 * deletedGraph.addCategory(c); }
			 */

		} else if (file.contains(MagicStrings.learnf_aiml_file)) {
			log.trace("Reading Learnf file");
			for (Category c : moreCategories) {
				brain.addCategory(c);
				learnfGraph.addCategory(c);
			}
		} else {
			for (Category c : moreCategories) {
				brain.addCategory(c);
			}
		}
	}

	/**
	 * Load all brain categories from AIML directory
	 */
	int addCategoriesFromAIML() {
		Timer timer = new Timer();
		timer.start();
		int cnt = 0;
		try {
			// Directory path here
			String file;
			File folder = new File(aiml_path);
			if (folder.exists()) {
				File[] listOfFiles = IOUtils.listFiles(folder);
				log.trace("Loading AIML files from " + aiml_path);
				for (File listOfFile : listOfFiles) {
					if (listOfFile.isFile()) {
						file = listOfFile.getName();
						if (file.endsWith(".aiml") || file.endsWith(".AIML")) {
							log.trace(file);
							try {
								ArrayList<Category> moreCategories = AIMLProcessor.AIMLToCategories(aiml_path, file);
								addMoreCategories(file, moreCategories);
								cnt += moreCategories.size();
							} catch (Exception iex) {
								log.info("Problem loading " + file);
								log.error(iex.getMessage(), iex);
							}
						}
					}
				}
			} else
				log.info("addCategoriesFromAIML: " + aiml_path + " does not exist.");
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		log.trace("Loaded " + cnt + " categories in " + timer.elapsedTimeSecs() + " sec");
		return cnt;
	}

	/**
	 * load all brain categories from AIMLIF directory
	 */
	public int addCategoriesFromAIMLIF() {
		Timer timer = new Timer();
		timer.start();
		int cnt = 0;
		try {
			// Directory path here
			String file;
			File folder = new File(aimlif_path);
			if (folder.exists()) {
				File[] listOfFiles = IOUtils.listFiles(folder);
				log.trace("Loading AIML files from " + aimlif_path);
				for (File listOfFile : listOfFiles) {
					if (listOfFile.isFile()) {
						file = listOfFile.getName();
						if (file.endsWith(MagicStrings.aimlif_file_suffix) || file.endsWith(MagicStrings.aimlif_file_suffix.toUpperCase())) {

							log.trace(file);
							try {
								ArrayList<Category> moreCategories = readIFCategories(aimlif_path + "/" + file);
								cnt += moreCategories.size();
								addMoreCategories(file, moreCategories);
							} catch (Exception iex) {
								log.info("Problem loading " + file);
								log.error(iex.getMessage(), iex);
							}
						}
					}
				}
			} else
				log.info("addCategoriesFromAIMLIF: " + aimlif_path + " does not exist.");
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		log.trace("Loaded " + cnt + " categories in " + timer.elapsedTimeSecs() + " sec");
		return cnt;
	}

	/**
	 * write all AIML and AIMLIF categories
	 */
	public void writeQuit() {
		writeAIMLIFFiles();
		writeAIMLFiles();
	}

	/**
	 * read categories from specified AIMLIF file into specified graph
	 *
	 * @param graph    Graphmaster to store categories
	 * @param fileName file name of AIMLIF file
	 */
	public int readCertainIFCategories(Graphmaster graph, String fileName) {
		int cnt = 0;
		File file = new File(aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix);
		if (file.exists()) {
			try {
				ArrayList<Category> certainCategories = readIFCategories(aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix);
				for (Category d : certainCategories)
					graph.addCategory(d);
				cnt = certainCategories.size();
				log.info("readCertainIFCategories " + cnt + " categories from " + fileName + MagicStrings.aimlif_file_suffix);
			} catch (Exception iex) {
				log.info("Problem loading " + fileName);
				log.error(iex.getMessage(), iex);
			}
		} else
			log.info("No " + aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix + " file found");
		return cnt;
	}

	/**
	 * write certain specified categories as AIMLIF files
	 *
	 * @param graph the Graphmaster containing the categories to write
	 * @param file  the destination AIMLIF file
	 */
	public void writeCertainIFCategories(Graphmaster graph, String file) {

		log.trace("writeCertainIFCaegories " + file + " size= " + graph.getCategories().size());
		writeIFCategories(graph.getCategories(), file + MagicStrings.aimlif_file_suffix);
		File dir = new File(aimlif_path);
		dir.setLastModified(new Date().getTime());
	}

	/**
	 * write deleted categories to AIMLIF file
	 */

	/**
	 * write learned categories to AIMLIF file
	 */
	public void writeLearnfIFCategories() {
		writeCertainIFCategories(learnfGraph, MagicStrings.learnf_aiml_file);
	}

	/**
	 * write unfinished categories to AIMLIF file
	 */
	/*
	 * public void writeUnfinishedIFCategories() {
	 * writeCertainIFCategories(unfinishedGraph, MagicStrings.unfinished_aiml_file);
	 * }
	 */

	/**
	 * write categories to AIMLIF file
	 *
	 * @param cats     array list of categories
	 * @param filename AIMLIF filename
	 */
	public void writeIFCategories(ArrayList<Category> cats, String filename) {
		BufferedWriter bw = null;
		File existsPath = new File(aimlif_path);
		if (existsPath.exists())
			try {
				// Construct the bw object
				bw = new BufferedWriter(new FileWriter(aimlif_path + "/" + filename));
				for (Category category : cats) {
					bw.write(Category.categoryToIF(category));
					bw.newLine();
				}
			} catch (FileNotFoundException ex) {
				log.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				log.error(ex.getMessage(), ex);
			} finally {
				// Close the bw
				try {
					if (bw != null) {
						bw.flush();
						bw.close();
					}
				} catch (IOException ex) {
					log.error(ex.getMessage(), ex);
				}
			}
	}

	/**
	 * Write all AIMLIF files from bot brain
	 */
	public void writeAIMLIFFiles() {
		log.trace("writeAIMLIFFiles");
		HashMap<String, BufferedWriter> fileMap = new HashMap<String, BufferedWriter>();
		Category b = new Category(0, "BRAIN BUILD", "*", "*", new Date().toString(), "update.aiml");
		brain.addCategory(b);
		ArrayList<Category> brainCategories = brain.getCategories();
		Collections.sort(brainCategories, Category.CATEGORY_NUMBER_COMPARATOR);
		File aimlif = new File(aimlif_path);
		if (!aimlif.exists())
			aimlif.mkdir();
		for (Category c : brainCategories) {
			try {
				BufferedWriter bw;
				String fileName = c.getFilename();
				if (fileMap.containsKey(fileName))
					bw = fileMap.get(fileName);
				else {
					bw = new BufferedWriter(new FileWriter(aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix));
					fileMap.put(fileName, bw);
				}
				bw.write(Category.categoryToIF(c));
				bw.newLine();
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
		}
		Set<String> set = fileMap.keySet();
		for (Object aSet : set) {
			BufferedWriter bw = fileMap.get(aSet);
			// Close the bw
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (IOException ex) {
				log.error(ex.getMessage(), ex);
			}
		}
		File dir = new File(aimlif_path);
		dir.setLastModified(new Date().getTime());
	}

	/**
	 * Write all AIML files. Adds categories for BUILD and DEVELOPMENT ENVIRONMENT
	 */
	public void writeAIMLFiles() {
		log.trace("writeAIMLFiles");
		HashMap<String, BufferedWriter> fileMap = new HashMap<String, BufferedWriter>();
		Category b = new Category(0, "BRAIN BUILD", "*", "*", new Date().toString(), "update.aiml");
		brain.addCategory(b);
		ArrayList<Category> brainCategories = brain.getCategories();
		Collections.sort(brainCategories, Category.CATEGORY_NUMBER_COMPARATOR);
		for (Category c : brainCategories) {

			if (!c.getFilename().equals(MagicStrings.null_aiml_file))
				try {
					BufferedWriter bw;
					String fileName = c.getFilename();
					if (fileMap.containsKey(fileName))
						bw = fileMap.get(fileName);
					else {
						String copyright = Utilities.getCopyright(this, fileName);
						bw = new BufferedWriter(new FileWriter(aiml_path + "/" + fileName));
						fileMap.put(fileName, bw);
						bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<aiml>\n");
						bw.write(copyright);
					}
					bw.write(Category.categoryToAIML(c) + "\n");
				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
				}
		}
		Set<String> set = fileMap.keySet();
		for (Object aSet : set) {
			BufferedWriter bw = fileMap.get(aSet);
			// Close the bw
			try {
				if (bw != null) {
					bw.write("</aiml>\n");
					bw.flush();
					bw.close();
				}
			} catch (IOException ex) {
				log.error(ex.getMessage(), ex);

			}

		}
		File dir = new File(aiml_path);
		dir.setLastModified(new Date().getTime());
	}

	/**
	 * load bot properties
	 */
	void addProperties() {
		try {
			properties.getProperties(config_path + "/properties.txt");
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	/**
	 * read AIMLIF categories from a file into bot brain
	 *
	 * @param filename name of AIMLIF file
	 * @return array list of categories read
	 */
	public ArrayList<Category> readIFCategories(String filename) {
		ArrayList<Category> categories = new ArrayList<Category>();
		try {
			// Open the file that is the first command line parameter
			FileInputStream fstream = new FileInputStream(filename);
			// Get the object
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				try {
					Category c = Category.IFToCategory(strLine);
					categories.add(c);
				} catch (Exception ex) {
					log.info("Invalid AIMLIF in " + filename + " line " + strLine);
				}
			}
			// Close the input stream
			br.close();
		} catch (Exception e) {
			// Catch exception if any
			log.error("Error: " + e.getMessage());
		}
		return categories;
	}

	/**
	 * Load all AIML Sets
	 */
	int addAIMLSets() {
		int cnt = 0;
		Timer timer = new Timer();
		timer.start();
		try {
			// Directory path here
			String file;
			File folder = new File(sets_path);
			if (folder.exists()) {
				File[] listOfFiles = IOUtils.listFiles(folder);

				log.trace("Loading AIML Sets files from " + sets_path);
				for (File listOfFile : listOfFiles) {
					if (listOfFile.isFile()) {
						file = listOfFile.getName();
						if (file.endsWith(".txt") || file.endsWith(".TXT")) {

							log.trace(file);
							String setName = file.substring(0, file.length() - ".txt".length());

							log.trace("Read AIML Set " + setName);
							AIMLSet aimlSet = new AIMLSet(setName, this);
							cnt += aimlSet.readAIMLSet(this);
							setMap.put(setName, aimlSet);
						}
					}
				}
			} else
				log.info("addAIMLSets: " + sets_path + " does not exist.");
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return cnt;
	}

	/**
	 * Load all AIML Maps
	 */
	int addAIMLMaps() {
		int cnt = 0;
		Timer timer = new Timer();
		timer.start();
		try {
			// Directory path here
			String file;
			File folder = new File(maps_path);
			if (folder.exists()) {
				File[] listOfFiles = IOUtils.listFiles(folder);

				log.trace("Loading AIML Map files from " + maps_path);
				for (File listOfFile : listOfFiles) {
					if (listOfFile.isFile()) {
						file = listOfFile.getName();
						if (file.endsWith(".txt") || file.endsWith(".TXT")) {

							log.trace(file);
							String mapName = file.substring(0, file.length() - ".txt".length());

							log.trace("Read AIML Map " + mapName);
							AIMLMap aimlMap = new AIMLMap(mapName, this);
							cnt += aimlMap.readAIMLMap(this);
							mapMap.put(mapName, aimlMap);
						}
					}
				}
			} else
				log.info("addAIMLMaps: " + maps_path + " does not exist.");
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return cnt;
	}

	public void deleteLearnfCategories() {
		ArrayList<Category> learnfCategories = learnfGraph.getCategories();
		for (Category c : learnfCategories) {
			Nodemapper n = brain.findNode(c);
			log.info("Found node " + n + " for " + c.inputThatTopic());
			if (n != null)
				n.category = null;
		}
		learnfGraph = new Graphmaster(this);
	}

	public void deleteLearnCategories() {
		ArrayList<Category> learnCategories = learnGraph.getCategories();
		for (Category c : learnCategories) {
			Nodemapper n = brain.findNode(c);
			log.info("Found node " + n + " for " + c.inputThatTopic());
			if (n != null)
				n.category = null;
		}
		learnGraph = new Graphmaster(this);
	}

	/**
	 * check Graphmaster for shadowed categories
	 */
	public void shadowChecker() {
		shadowChecker(brain.root);
	}

	/**
	 * traverse graph and test all categories found in leaf nodes for shadows
	 *
	 * @param node
	 */
	void shadowChecker(Nodemapper node) {
		if (NodemapperOperator.isLeaf(node)) {
			String input = node.category.getPattern();
			input = brain.replaceBotProperties(input);
			input = input.replace("*", "XXX").replace("_", "XXX").replace("^", "").replace("#", "");
			String that = node.category.getThat().replace("*", "XXX").replace("_", "XXX").replace("^", "").replace("#", "");
			String topic = node.category.getTopic().replace("*", "XXX").replace("_", "XXX").replace("^", "").replace("#", "");
			input = instantiateSets(input);
			log.info("shadowChecker: input=" + input);
			Nodemapper match = brain.match(input, that, topic);
			if (match != node) {
				log.info("" + Graphmaster.inputThatTopic(input, that, topic));
				log.info("MATCHED:     " + match.category.inputThatTopic());
				log.info("SHOULD MATCH:" + node.category.inputThatTopic());
			}
		} else {
			for (String key : NodemapperOperator.keySet(node)) {
				shadowChecker(NodemapperOperator.get(node, key));
			}
		}
	}

	public String instantiateSets(String pattern) {
		String[] splitPattern = pattern.split(" ");
		pattern = "";
		for (String x : splitPattern) {
			if (x.startsWith("<SET>")) {
				String setName = AIMLProcessor.trimTag(x, "SET");
				AIMLSet set = setMap.get(setName);
				if (set != null)
					x = "FOUNDITEM";
				else
					x = "NOTFOUND";
			}
			pattern = pattern + " " + x;
		}
		return pattern.trim();
	}

}
