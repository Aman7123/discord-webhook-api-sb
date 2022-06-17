package com.aaronrenner.spring.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiscordCredentials {
	
	private String token;
	private String serverId;
	private String channelId;

}
