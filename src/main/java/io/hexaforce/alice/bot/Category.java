package io.hexaforce.alice.bot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Category {

	private String aimlFileName;
	private String topic;
	private String pattern;
	private String that;
	private String template;
	private int activationCnt;

}
