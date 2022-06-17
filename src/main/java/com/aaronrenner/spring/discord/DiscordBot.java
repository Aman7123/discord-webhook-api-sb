package com.aaronrenner.spring.discord;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aaronrenner.spring.exceptions.BadRequestException;
import com.aaronrenner.spring.exceptions.InternalServerError;
import com.aaronrenner.spring.models.DiscordCredentials;
import com.aaronrenner.spring.models.Embed;
import com.aaronrenner.spring.models.EmbedAuthor;
import com.aaronrenner.spring.models.EmbedField;
import com.aaronrenner.spring.models.EmbedFooter;
import com.aaronrenner.spring.models.Webhook;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.Objects.nonNull;
import static java.util.Objects.isNull;
import java.awt.Color;

public class DiscordBot {
	
	// Errors
	private String SERVER_ERROR = "Failed discord starting bot! Exception: %s";
	private String NOT_IN_SERVER = "Your bot is not part of server id %s, you can use this link to invite it %s";
	private String NOT_IN_CHANNEL = "Your bot is not part of channel id %s";
	private String CANNOT_SEE_CHANNEL = "Your bot cannot see the channel id %s";
	private String NOT_TEXT_CHANNEL = "The channel id %s is not a text channel";
	private String FALIED_SENDING_UPSTREAM = "The message failed to send! Exception: %s";
	
	// Final
	private static final Logger LOGGER  = LoggerFactory.getLogger(DiscordBot.class);
	
	/** Required */
	private DiscordApi disc;
	private TextChannel output;
	private Server access;
	
	public DiscordBot(DiscordCredentials credentials) {
		try {
			// Start Discord connection
	        this.disc = new DiscordApiBuilder()
	        					.setToken(credentials.getToken())
	        					.login()
	        					.join();
	        // End
		} catch (Exception e) {
			LOGGER.error(String.format(SERVER_ERROR, e.getMessage()));
        	throw new InternalServerError(String.format(SERVER_ERROR, e.getMessage()));
		}
		//Now that we know the bot is created
        String inviteLink = disc.createBotInvite(Permissions.fromBitmask(0x0000000008));
        
        // Get Server
        Optional<Server> availableServer = this.disc.getServerById(credentials.getServerId());
        if(availableServer.isPresent()) this.access = availableServer.get();
        if(isNull(this.access)) throw new BadRequestException(String.format(NOT_IN_SERVER, credentials.getServerId(), inviteLink));
        
        // Get potential channel
        Optional<Channel> availableChannel = this.disc.getChannelById(credentials.getChannelId());
        if(availableChannel.isEmpty()) throw new BadRequestException(String.format(NOT_IN_CHANNEL, credentials.getChannelId()));
        Channel presentedChannel = availableChannel.get();
        
        /// Ensure channel permissions and proper type
        if(!presentedChannel.canYouSee()) throw new BadRequestException(String.format(CANNOT_SEE_CHANNEL, credentials.getChannelId()));
        if(!presentedChannel.getType().isTextChannelType()) throw new BadRequestException(String.format(NOT_TEXT_CHANNEL, credentials.getChannelId()));
        
        /// Finally, assign output channel and confidence on the optional because previous check!
        this.output = presentedChannel.asTextChannel().get();
	}
	
	public Message buildWebhook(Webhook data) {
		MessageBuilder msg = new MessageBuilder();

		// Build message
		if(data.getReplyTo() > 0) msg.replyTo(data.getReplyTo());
		if(nonNull(data.getContent())) msg.setContent(data.getContent());
		if(data.embedSize() > 0) {
			for(Embed em : data.getEmbeds()) {
				EmbedBuilder emBuilder = new EmbedBuilder();
				if(nonNull(em.getTitle()))       emBuilder.setTitle(em.getTitle());
				if(nonNull(em.getDescription())) emBuilder.setDescription(em.getDescription());
				if(nonNull(em.getTimestamp()))   emBuilder.setTimestamp(em.getTimestamp());
				if(em.getCurrentTimestamp())     emBuilder.setTimestampToNow();
				if(nonNull(em.getColor()))       emBuilder.setColor(new Color(em.getColor()));
				if(nonNull(em.getFooter())) {
					EmbedFooter footer = em.getFooter();
					if(nonNull(footer.getText())) {
						if(nonNull(footer.getIconUrl())) emBuilder.setFooter(footer.getText(), footer.getIconUrl());
						if(isNull(footer.getIconUrl()))  emBuilder.setFooter(footer.getText());
					}
				}
				if(nonNull(em.getImage()))       emBuilder.setImage(em.getImage());
				if(nonNull(em.getThumbnail()))   emBuilder.setThumbnail(em.getThumbnail());
				if(nonNull(em.getAuthor())) {
					EmbedAuthor author = em.getAuthor();
					if(nonNull(author.getName())) emBuilder.setAuthor(author.getName());
					if(nonNull(author.getIconUrl()) || nonNull(author.getUrl())) emBuilder.setAuthor(author.getName(), author.getUrl(), author.getIconUrl());
				}
				if(em.fieldsSize() > 0) {
					for(EmbedField emField : em.getFields()) {
						emBuilder.addField(emField.getName(), emField.getValue(), emField.getInline());
					}
				}
				
				// Add this embed to the message
				msg.addEmbed(emBuilder);
			}
		}
		
		// Sends message
		CompletableFuture<Message> response = msg.send(this.output);
		Message getResponseBody = null;
		
		// Merge message thread with this
		try {
			getResponseBody = response.join();
		} catch (Exception e) {
			throw new InternalServerError(String.format(FALIED_SENDING_UPSTREAM, e.getMessage()));
		}
		if(isNull(getResponseBody)) throw new InternalServerError(String.format(FALIED_SENDING_UPSTREAM, "Message could not be joined and is null"));
		
		// Return the message
		return getResponseBody;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getInfo() {
		return new ObjectMapper().convertValue(this.disc.getApplicationInfo().join(), Map.class);
	}
	
	public void disconnect() {
		this.disc.disconnect();
	}
}