package com.aaronrenner.spring.models;

import java.util.List;
import static java.util.Objects.nonNull;
import lombok.Data;

@Data
public class Webhook {
	
	private String content;
	private List<Embed> embeds;
	private long replyTo;
	
	public int embedSize() {
		int toReturn = 0;
		if(nonNull(this.embeds)) toReturn = this.embeds.size();
		return toReturn;
	}

}
