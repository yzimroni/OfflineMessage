/**
 * @author yzimroni
 */

package com.yanayzimroni.bukkit.offlinemessage.messages;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.yanayzimroni.bukkit.offlinemessage.OfflineMessagePlugin;


public class MySqlMessagesManager implements MessagesManager {
	
	private String prefix;
	private Connection conn;
	
	public MySqlMessagesManager(String host, int port, String database, String user, String password, String prefix) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
			setPrefix(prefix);
			conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + getPrefix() + "offlinemessage (ID int(11) NOT NULL AUTO_INCREMENT,Player varchar(36) NOT NULL,Message varchar(1000) NOT NULL,PRIMARY KEY (ID)) DEFAULT CHARSET=utf8;");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void sendMessage(final UUID uuid, final String message) {
		Bukkit.getScheduler().runTaskAsynchronously(OfflineMessagePlugin.plugin, new Runnable() {
			
			@Override
			public void run() {
				try {
					PreparedStatement pre = conn.prepareStatement("INSERT INTO " + getPrefix() + "offlinemessage (Player, Message) VALUES (?, ?);");
					pre.setString(1, uuid.toString());
					pre.setString(2, message);
					pre.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}
		});
	}

	@Override
	public void sendMessage(OfflinePlayer player, String message) {
		sendMessage(player.getUniqueId(), message);
	}

	@Override
	public void sendMessages(final UUID uuid, final String... messages) {
		Bukkit.getScheduler().runTaskAsynchronously(OfflineMessagePlugin.plugin, new Runnable() {
			@Override
			public void run() {
				try {
					String values = "";
					for (int i = 0; i<messages.length; i++) {
						if (!values.isEmpty()) values += ",";
						values += "VALUES (?, ?)";
					}
					PreparedStatement pre = conn.prepareStatement("INSERT INTO " + getPrefix() + "offlinemessage (Player, Message) " + values);
					int index = 1;
					for (String message : messages) {
						pre.setString(index, uuid.toString());
						index++;
						pre.setString(index, message);
					}
					
					pre.executeUpdate();
					
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	public void sendMessages(OfflinePlayer player, String... messages) {
		sendMessages(player.getUniqueId(), messages);
	}

	@Override
	public List<String> getMessages(UUID uuid) {
		try {
			List<String> messages = new ArrayList<String>();
			PreparedStatement pre = conn.prepareStatement("SELECT Message FROM " + getPrefix() + "offlinemessage WHERE Player = ?");
			pre.setString(1, uuid.toString());
			ResultSet rs = pre.executeQuery();
			while (rs.next()) {
				messages.add(rs.getString("Message"));
			}
			return messages;
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
		return null;
	}

	@Override
	public List<String> getMessages(OfflinePlayer player) {
		return getMessages(player.getUniqueId());
	}

	@Override
	public void resetMessages(final UUID uuid) {
		Bukkit.getScheduler().runTaskAsynchronously(OfflineMessagePlugin.plugin, new Runnable() {
			@Override
			public void run() {
				try {
					PreparedStatement pre = conn.prepareStatement("DELETE FROM " + getPrefix() + "offlinemessage WHERE Player = ?");
					pre.setString(1, uuid.toString());
					pre.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void resetMessages(OfflinePlayer player) {
		resetMessages(player.getUniqueId());
	}

	@Override
	public String getType() {
		return "MySQL";
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public boolean isAsync() {
		return true;
	}

}
