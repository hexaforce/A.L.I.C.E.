package org.alicebot.ab;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Contact {
	
	public static int contactCount = 0;
	public static HashMap<String, Contact> idContactMap = new HashMap<>();
	public String contactId;
	public String displayName;
	public String birthday;
	public static HashMap<String, String> nameIdMap = new HashMap<>();
	public HashMap<String, String> phones;
	public HashMap<String, String> emails;

	public static String multipleIds(String contactName) {
		String patternString = " (" + contactName.toUpperCase() + ") ";
		for (; patternString.contains(" "); patternString = patternString.replace(" ", "(.*)"))
			;

		Pattern pattern = Pattern.compile(patternString);
		Set<String> keys = nameIdMap.keySet();
		String result = "";
		int idCount = 0;
		for (String key : keys) {
			Matcher m = pattern.matcher(key);
			if (m.find()) {
				result = result + (String) nameIdMap.get(key.toUpperCase()) + " ";
				idCount++;
			}
		}
		if (idCount <= 1)
			result = "false";
		return result.trim();
	}

	public static String contactId(String contactName) {
		String patternString = " " + contactName.toUpperCase() + " ";
		for (; patternString.contains(" "); patternString = patternString.replace(" ", ".*"))
			;

		Pattern pattern = Pattern.compile(patternString);
		Set<String> keys = nameIdMap.keySet();
		String result = "unknown";
		for (String key : keys) {
			Matcher m = pattern.matcher(key);
			if (m.find())
				result = (String) nameIdMap.get(key.toUpperCase()) + " ";
		}
		return result.trim();
	}

	public static String displayName(String id) {
		Contact c = idContactMap.get(id.toUpperCase());
		String result = "unknown";
		if (c != null) {
			result = c.displayName;
		}
		return result;
	}

	public static String dialNumber(String type, String id) {
		String result = "unknown";
		Contact c = idContactMap.get(id.toUpperCase());
		if (c != null) {
			String dialNumber = c.phones.get(type.toUpperCase());
			if (dialNumber != null)
				result = dialNumber;
		}
		return result;
	}

	public static String emailAddress(String type, String id) {
		String result = "unknown";
		Contact c = idContactMap.get(id.toUpperCase());
		if (c != null) {
			String emailAddress = c.emails.get(type.toUpperCase());
			if (emailAddress != null)
				result = emailAddress;
		}
		return result;
	}

	public static String birthday(String id) {
		Contact c = idContactMap.get(id.toUpperCase());
		if (c == null)
			return "unknown";
		return c.birthday;
	}

	public Contact(String displayName, String phoneType, String dialNumber, String emailType, String emailAddress, String birthday) {
		this.contactId = "ID" + contactCount;
		contactCount++;
		this.phones = new HashMap<>();
		this.emails = new HashMap<>();
		idContactMap.put(this.contactId.toUpperCase(), this);
		addPhone(phoneType, dialNumber);
		addEmail(emailType, emailAddress);
		addName(displayName);
		addBirthday(birthday);
	}

	public void addPhone(String type, String dialNumber) {
		this.phones.put(type.toUpperCase(), dialNumber);
	}

	public void addEmail(String type, String emailAddress) {
		this.emails.put(type.toUpperCase(), emailAddress);
	}

	public void addName(String name) {
		this.displayName = name;
		nameIdMap.put(this.displayName.toUpperCase(), this.contactId);
	}

	public void addBirthday(String birthday) {
		this.birthday = birthday;
	}
}
