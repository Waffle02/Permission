package me.waffle.permissions.Commands;

import me.waffle.permissions.Utilities.AuthUtilities;
import ca.ikari.ikaripermissions.Utilities.ConfigUtil;
import ca.ikari.ikaripermissions.Utilities.GroupUtilities;
import java.net.InetSocketAddress;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class RemovePermissionCommand
  implements CommandExecutor
{
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    boolean canExecute = false;
    if ((sender instanceof Player))
    {
      Player player = (Player)sender;
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
    if (args.length < 2)
    {
      sender.sendMessage(ChatColor.RED + "Usage: /rmpermission <Group> <Permission>");
      return false;
    }
    if (!GroupUtilities.doesGroupExist(args[0]))
    {
      sender.sendMessage(ChatColor.RED + "Error. Specified group doesn't exist.");
      return false;
    }
    GroupUtilities.removePermissionForGroup(args[0], args[1]);
    
    String message = ChatColor.DARK_PURPLE + sender.getName() + ChatColor.LIGHT_PURPLE + " removed permission '" + args[1] + "' from group " + args[0].toLowerCase() + ".";
    
    AuthUtilities.messageAuthenticatedPlayers(message);
    
    return false;
  }
}

