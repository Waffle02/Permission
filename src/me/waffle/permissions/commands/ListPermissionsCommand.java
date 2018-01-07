package me.waffle.permissions.commands;

import java.util.Set;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class ListPermissionsCommand
  implements CommandExecutor
{
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    Player player = (Player)sender;
    for (PermissionAttachmentInfo permissionAttachmentInfo : player.getEffectivePermissions())
    {
      String permission = permissionAttachmentInfo.getPermission();
      player.sendMessage(permission);
      player.getEffectivePermissions().clear();
    }
    return false;
  }
}

