package org.alicebot.ab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Properties extends HashMap<String, String> {
	private static final long serialVersionUID = 1L;

	public String get(String key) {
		String result = (String) get(key);
		if (result == null)
			return MagicStrings.unknown_property_value;
		return result;
	}

	public void getPropertiesFromInputStream(InputStream in) {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		try {
			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (strLine.contains(":")) {
					String property = strLine.substring(0, strLine.indexOf(":"));
					String value = strLine.substring(strLine.indexOf(":") + 1);
					put(property, value);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void getProperties(String filename) {
		System.out.println("Get Properties: " + filename);

		try {
			File file = new File(filename);
			if (file.exists()) {
				System.out.println("Exists: " + filename);
				FileInputStream fstream = new FileInputStream(filename);

				getPropertiesFromInputStream(fstream);

				fstream.close();
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
