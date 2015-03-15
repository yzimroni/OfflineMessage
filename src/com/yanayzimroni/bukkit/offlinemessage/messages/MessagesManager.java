/**
 * @author yzimroni
 */

package com.yanayzimroni.bukkit.offlinemessage.messages;

import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

public interface MessagesManager {
	void sendMessage(UUID uuid, String message);
	
	void sendMessage(OfflinePlayer player, String message);
	
	void sendMessages(UUID uuid, String... messagse);
	
	void sendMessages(OfflinePlayer player, String... messages);
	
	List<String> getMessages(UUID uuid);
	
	List<String> getMessages(OfflinePlayer player);
	
	void resetMessages(UUID uuid);
	
	void resetMessages(OfflinePlayer player);
	
	String getType();
	
	boolean isAsync();
}
