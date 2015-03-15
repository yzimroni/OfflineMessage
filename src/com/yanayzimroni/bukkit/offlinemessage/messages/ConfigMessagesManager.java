/**
 * @author yzimroni
 */

package com.yanayzimroni.bukkit.offlinemessage.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;


public class ConfigMessagesManager implements MessagesManager {

	private Plugin plugin;
	
	public ConfigMessagesManager(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void sendMessage(UUID uuid, String message) {
		List<String> messages_list = getMessages(uuid);
		if (messages_list == null) {
			messages_list = new ArrayList<String>();
		}
		messages_list.add(message);
		plugin.getConfig().set(uuid.toString() + "_messages", messages_list);
		plugin.saveConfig();
	}

	@Override
	public void sendMessage(OfflinePlayer player, String message) {
		sendMessage(player.getUniqueId(), message);
	}

	@Override
	public void sendMessages(UUID uuid, String... messages) {
		List<String> messages_list = getMessages(uuid);
		if (messages_list == null) {
			messages_list = new ArrayList<String>();
		}
		for (String message : messages) {
			messages_list.add(message);
		}
		plugin.getConfig().set(uuid.toString() + "_messages", messages_list);
		plugin.saveConfig();
	}

	@Override
	public void sendMessages(OfflinePlayer player, String... message) {
		sendMessages(player.getUniqueId(), message);
	}

	@Override
	public List<String> getMessages(UUID uuid) {
		return plugin.getConfig().getStringList(uuid.toString() + "_messages");
	}

	@Override
	public List<String> getMessages(OfflinePlayer player) {
		return getMessages(player.getUniqueId());
	}

	@Override
	public void resetMessages(UUID uuid) {
		plugin.getConfig().set(uuid.toString() + "_messages", null);
		plugin.saveConfig();
	}

	@Override
	public void resetMessages(OfflinePlayer player) {
		resetMessages(player.getUniqueId());
	}

	@Override
	public String getType() {
		return "Config";
	}

	@Override
	public boolean isAsync() {
		return false;
	}

}
