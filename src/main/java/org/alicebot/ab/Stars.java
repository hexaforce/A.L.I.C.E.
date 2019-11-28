package org.alicebot.ab;

import java.util.ArrayList;

public class Stars extends ArrayList<String> {
	private static final long serialVersionUID = 1L;

	public String star(int i) {
		if (i < size())
			return get(i);
		return null;
	}

}
