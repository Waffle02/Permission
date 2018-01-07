package me.waffle.permissions.commands;

import me.waffle.permissions.utilities.AuthUtilities;
import me.waffle.permissions.utilities.ConfigUtil;
import me.waffle.permissions.utilities.GroupUtilities;
import java.net.InetSocketAddress;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ListGroupsCommand
  implements CommandExecutor
{
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    boolean canExecute = false;
    Player player;
    if ((sender instanceof Player))
    {
      player = (Player)sender;
      if (AuthUtilities.isPlayerAuthenticated(player.getAddress().toString(), player.getUniqueId().toString())) {
        canExecute = true;
      }
    }
    else if ((sender instanceof ConsoleCommandSender))
    {
      canExecute = true;
    }
    if (!canExecute)
    {
      sender.sendMessage(ConfigUtil.getMessage("no-permission-message"));
      return false;
    }
    sender.sendMessage(ChatColor.GOLD + "Listing all groups:");
    for (String str : GroupUtilities.getGroupList()) {
      sender.sendMessage("- " + str);
    }
    return false;
  }
}

