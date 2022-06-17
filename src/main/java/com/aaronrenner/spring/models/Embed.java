package com.aaronrenner.spring.models;

import static java.util.Objects.nonNull;

import java.time.Instant;
import java.util.List;
import lombok.Data;

@Data
public class Embed {
	
	String      title;
	String      description;
	Instant     timestamp;
	Boolean     currentTimestamp = false;
	Integer     color;
	EmbedFooter footer;
	String      image;
	String      thumbnail;
	EmbedAuthor author;
	List<EmbedField> fields;
	
	public int fieldsSize() {
		int toReturn = 0;
		if(nonNull(this.fields)) toReturn = this.fields.size();
		return toReturn;
	}

}
