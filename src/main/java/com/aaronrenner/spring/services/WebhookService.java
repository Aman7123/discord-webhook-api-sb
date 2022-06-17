package com.aaronrenner.spring.services;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.javacord.api.entity.message.Message;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.aaronrenner.spring.discord.DiscordBot;
import com.aaronrenner.spring.exceptions.InternalServerError;
import com.aaronrenner.spring.models.DiscordCredentials;
import com.aaronrenner.spring.models.Webhook;

@Service
public class WebhookService {

	private String SERVER_ERROR = "Failed creating response! Exception: %s";
	private String upstreamResponseHeadersPrefix = "Discord-";
	
	public ResponseEntity<String> createWebhook(Webhook webhook, DiscordCredentials credentials) {
		DiscordBot botInstance = new DiscordBot(credentials);
		Message message = botInstance.buildWebhook(webhook);
		Map<String,Object> info = botInstance.getInfo();
		botInstance.disconnect();
		
		ResponseEntity<String> response = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Message-Id", message.getIdAsString());
			for(Entry<String,Object> e : info.entrySet()) {
				String kabobCaseKey = e.getKey().replaceAll("(?!^)(?=[A-Z][a-z])", "-");
				char firstChar = kabobCaseKey.charAt(0);
				String finishFormat = upstreamResponseHeadersPrefix+kabobCaseKey.replaceFirst(Character.toString(firstChar), Character.toString(firstChar).toUpperCase());
				if(!(e.getValue() instanceof LinkedHashMap))
					headers.add(finishFormat, String.valueOf(e.getValue()));
			}
			
			response = ResponseEntity
						.created(message.getLink().toURI())
						.headers(headers)
						.build();
		} catch (Exception e) {
			throw new InternalServerError(String.format(SERVER_ERROR, e.getMessage()));
		}

		return response;
		
	}

}
