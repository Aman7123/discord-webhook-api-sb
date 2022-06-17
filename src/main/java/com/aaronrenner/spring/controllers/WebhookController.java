package com.aaronrenner.spring.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import static java.util.Objects.isNull;
import com.aaronrenner.spring.exceptions.BadRequestException;
import com.aaronrenner.spring.exceptions.UnauthorizedException;
import com.aaronrenner.spring.models.DiscordCredentials;
import com.aaronrenner.spring.models.Webhook;
import com.aaronrenner.spring.services.WebhookService;

@RestController
public class WebhookController {
	
	// Errors
	private String UNAUTHORIZED = "The 'Authorization', 'Server-Id', and 'Channel-Id' headers must be present";
	private String UNAUTHORIZED_FORMAT = "The 'Authorization' header follows the Bot Token like "
			+ "https://discord.com/developers/docs/reference#authentication";
	private String BAD_REQUEST  = "The body must contain 'content' or 'embeds'";
	
	@Autowired
	WebhookService webhookService;

	// Endpoints
	private final String CONTENT_TYPE = "application/json";
	private final String BASE_PATH    = "/webhooks";

	@ResponseStatus(value = HttpStatus.CREATED)
	@RequestMapping(
			method   = RequestMethod.POST,
			value    = BASE_PATH,
			consumes = CONTENT_TYPE,
			produces = CONTENT_TYPE)
	public ResponseEntity<String> createWebhook(@RequestHeader("Authorization") Optional<String> authToken, 
										@RequestHeader("Server-Id") Optional<String> serverId, 
										@RequestHeader("Channel-Id") Optional<String> channelId, 
										@RequestBody Webhook webhook) throws Exception {
		
		if(authToken.isPresent() && serverId.isPresent() && channelId.isPresent()) {
			// Parse out Discord token
			String discToken = getTokenFromAuth(authToken.get());
			// Create internal object for passing credentials
			DiscordCredentials creds = new DiscordCredentials(discToken, serverId.get(), channelId.get());
			if(isNull(webhook.getContent()) && webhook.embedSize() == 0) throw new BadRequestException(BAD_REQUEST);
			// Make things happen
			return webhookService.createWebhook(webhook, creds);
		}
		throw new UnauthorizedException(UNAUTHORIZED);
	}
	
	private String getTokenFromAuth(String token) {
		String[] parseToken = token.split(" "); // split [0]="token", [1]="xxx"
		if(parseToken[0].equalsIgnoreCase("bot")) { // if [0] is actually token
			return parseToken[1];
		}
		// Catch wrong auth format
		throw new UnauthorizedException(UNAUTHORIZED_FORMAT);

	}
}
