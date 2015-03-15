/**
 * @author yzimroni
 */

package com.yanayzimroni.bukkit.offlinemessage;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.yanayzimroni.bukkit.offlinemessage.messages.ConfigMessagesManager;
import com.yanayzimroni.bukkit.offlinemessage.messages.MessagesManager;
import com.yanayzimroni.bukkit.offlinemessage.messages.MySqlMessagesManager;

public class OfflineMessagePlugin extends JavaPlugin implements Listener {
	private static MessagesManager messagesmanager;
	public static OfflineMessagePlugin plugin;

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		saveConfig();
		System.out.println("OfflineMessage version " + super.getDescription().getVersion() + " enabled!");
		if (messagesmanager == null) {
			switch (getConfig().getString("manager_type")) {
			case "mysql":
				setMessagesManager(new MySqlMessagesManager(getConfig().getString("db_host"), getConfig().getInt("db_port"), getConfig().getString("db_name"), getConfig().getString("db_user"), getConfig().getString("db_pass"), getConfig().getString("db_prefix")));
				break;
			case "config":
			default:
				setMessagesManager(new ConfigMessagesManager(this));
				break;
			}
		}
		plugin = this;
	}

	@Override
	public void onDisable() {
		System.out.println("OfflineMessage version " + super.getDescription().getVersion() + " disabled!");
	}

	/**
	 * @return the messages manager
	 */
	public static MessagesManager getMessagesManager() {
		return messagesmanager;
	}

	/**
	 * @param messagesmanager
	 *            the messages manager to set
	 */
	public static void setMessagesManager(MessagesManager messagesmanager) {
		if (messagesmanager == null) {
			throw new NullPointerException("messagesmanager cannot be null!");
		}
		OfflineMessagePlugin.messagesmanager = messagesmanager;
		System.out.println("Message Manager set to " + messagesmanager.getType());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent e) {
		if (!e.getPlayer().isOnline()) return;
		Runnable message_task = new Runnable() {
			@Override
			public void run() {
				List<String> messages = getMessagesManager().getMessages(e.getPlayer());
				if (messages != null && !messages.isEmpty()) {
					for (String message : messages) {
						String formattedmessage = message;
						formattedmessage = ChatColor.translateAlternateColorCodes('&', formattedmessage);
						formattedmessage = formattedmessage.replaceAll("%player%", e.getPlayer().getName());
						e.getPlayer().sendMessage(formattedmessage);
					}
					getMessagesManager().resetMessages(e.getPlayer());
				}
			}
		};
		if (getMessagesManager().isAsync()) {
			Bukkit.getScheduler().runTaskAsynchronously(this, message_task);
		} else {
			Bukkit.getScheduler().runTask(this, message_task);
		}
	}
	
}
