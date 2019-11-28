package org.alicebot.ab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIMLSet extends HashSet<String> {
	private static final long serialVersionUID = 1L;
	
	public String setName;
	int maxLength = 1;
	String host;
	String botid;
	boolean isExternal = false;

	public AIMLSet(String name) {
		this.setName = name.toLowerCase();
		if (this.setName.equals(MagicStrings.natural_number_set_name))
			this.maxLength = 1;

	}

	public boolean contains(String s) {
		if (this.isExternal && MagicBooleans.enable_external_sets) {
			String[] split = s.split(" ");
			if (split.length > this.maxLength)
				return false;
			String query = MagicStrings.set_member_string + this.setName.toUpperCase() + " " + s;
			String response = Sraix.sraix(null, query, "false", null, this.host, this.botid, null, "0");
			System.out.println("External " + this.setName + " contains " + s + "? " + response);
			if (response.equals("true"))
				return true;
			return false;
		}
		if (this.setName.equals(MagicStrings.natural_number_set_name)) {
			Pattern numberPattern = Pattern.compile("[0-9]+");
			Matcher numberMatcher = numberPattern.matcher(s);
			Boolean isanumber = Boolean.valueOf(numberMatcher.matches());

			return isanumber.booleanValue();
		}
		return contains(s);
	}

	public void writeAIMLSet() {
		System.out.println("Writing AIML Set " + this.setName);

		try {
			FileWriter fstream = new FileWriter(MagicStrings.sets_path + "/" + this.setName + ".txt");
			BufferedWriter out = new BufferedWriter(fstream);
			for (String p : this) {

				out.write(p.trim());
				out.newLine();
			}

			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public int readAIMLSetFromInputStream(InputStream in, Bot bot) {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		int cnt = 0;
		try {
			String strLine;
			while ((strLine = br.readLine()) != null && strLine.length() > 0) {
				cnt++;

				if (strLine.startsWith("external")) {
					String[] splitLine = strLine.split(":");
					if (splitLine.length >= 4) {
						this.host = splitLine[1];
						this.botid = splitLine[2];
						this.maxLength = Integer.parseInt(splitLine[3]);
						this.isExternal = true;
						System.out.println("Created external set at " + this.host + " " + this.botid);
					}
					continue;
				}
				strLine = strLine.toUpperCase().trim();
				String[] splitLine = strLine.split(" ");
				int length = splitLine.length;
				if (length > this.maxLength)
					this.maxLength = length;

				add(strLine.trim());

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return cnt;
	}

	public void readAIMLSet(Bot bot) {
		System.out.println("Reading AIML Set " + MagicStrings.sets_path + "/" + this.setName + ".txt");

		try {
			File file = new File(MagicStrings.sets_path + "/" + this.setName + ".txt");
			if (file.exists()) {
				FileInputStream fstream = new FileInputStream(MagicStrings.sets_path + "/" + this.setName + ".txt");

				readAIMLSetFromInputStream(fstream, bot);
				fstream.close();
			} else {
				System.out.println(MagicStrings.sets_path + "/" + this.setName + ".txt not found");
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
