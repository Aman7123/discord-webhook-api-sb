package com.aaronrenner.spring.models;

import lombok.Data;

@Data
public class EmbedField {
	
	String name;
	String value;
	Boolean inline = false;

}
