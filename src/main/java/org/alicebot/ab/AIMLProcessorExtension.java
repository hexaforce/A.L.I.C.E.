package org.alicebot.ab;

import java.util.Set;
import org.w3c.dom.Node;

public interface AIMLProcessorExtension {
	Set<String> extensionTagSet();
	String recursEval(Node paramNode, ParseState paramParseState);
}
