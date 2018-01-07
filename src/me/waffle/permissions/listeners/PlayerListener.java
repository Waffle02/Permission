package me.waffle.permissions.listeners;

import me.waffle.permissions.utilities.ConfigUtil;
import me.waffle.permissions.utilities.GroupUtilities;
import me.waffle.permissions.utilities.PlayerUtilities;
import java.net.InetAddress;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener
  implements Listener
{
  @EventHandler
  public void onPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event)
  {
    if (!PlayerUtilities.doesHavePlayerObject(event.getUniqueId())) {
      PlayerUtilities.setupPlayerObject(event.getUniqueId(), event.getAddress().toString());
    }
    PlayerUtilities.updateLastIP(event.getUniqueId(), event.getAddress().toString());
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    PlayerUtilities.updatePermissions(event.getPlayer());
  }
  
  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent event)
  {
    String format = ConfigUtil.getMessage("chat-format");
    
    String playerGroup = PlayerUtilities.getPlayerGroup(event.getPlayer().getUniqueId());
    if (GroupUtilities.doesGroupHavePrefix(playerGroup)) {
      format = format.replaceAll("_prefix_", GroupUtilities.getPrefix(playerGroup) + " ");
    } else {
      format = format.replaceAll("_prefix_", "");
    }
    format = format.replaceAll("_player-name_", event.getPlayer().getName());
    format = format.replaceAll("_message_", event.getMessage());
    
    event.setFormat(format);
  }
}
