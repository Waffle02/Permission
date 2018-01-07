package me.waffle.permissions.utilities;

import me.waffle.permissions.Permissions;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PlayerUtilities
{
  public static ResultSet getDataForPlayer(UUID uuid)
  {
    try
    {
      Statement statement = MySQL.getConnection().createStatement();
      return statement.executeQuery("SELECT * FROM playerdata WHERE uuid='" + uuid.toString() + "';");
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static boolean doesHavePlayerObject(UUID uuid)
  {
    try
    {
      ResultSet resultSet = getDataForPlayer(uuid);
      if (resultSet.next())
      {
        if (resultSet.wasNull()) {
          return false;
        }
        return true;
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return false;
  }
  
  public static void setupPlayerObject(UUID uuid, String ip)
  {
    try
    {
      PreparedStatement preparedStatement = MySQL.getConnection().prepareStatement("INSERT INTO `playerdata`(uuid, lastip, groupname, duration) VALUES (?, ?, ?, ?)");
      preparedStatement.setString(1, uuid.toString());
      preparedStatement.setString(2, ip);
      preparedStatement.setString(3, "default");
      preparedStatement.setLong(4, 0L);
      preparedStatement.executeUpdate();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void updateLastIP(UUID uuid, String ip)
  {
    try
    {
      PreparedStatement statement = MySQL.getConnection().prepareStatement("UPDATE playerdata SET lastip='" + ip + "' WHERE uuid='" + uuid.toString() + "';");
      statement.executeUpdate();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  public static String getPlayerGroup(UUID uuid)
  {
    try
    {
      ResultSet resultSet = getDataForPlayer(uuid);
      if (resultSet.next())
      {
        String result = resultSet.getString("groupname");
        if (hasDurationExpiredForPlayer(uuid))
        {
          updatePlayerGroup(uuid, "default", Long.valueOf(0L));
          OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
          if (player.isOnline()) {
            player.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Your temporary " + result + " rank has expired.");
          }
          return "default";
        }
        return result;
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static boolean hasDurationExpiredForPlayer(UUID uuid)
  {
    try
    {
      ResultSet resultSet = getDataForPlayer(uuid);
      if (resultSet.next())
      {
        Long duration = Long.valueOf(resultSet.getLong("duration"));
        if (duration.longValue() == 0L) {
          return false;
        }
        Long currentTime = Long.valueOf(System.currentTimeMillis() / 1000L);
        if (currentTime.longValue() > duration.longValue()) {
          return true;
        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return false;
  }
  
  public static void removeAllPlayersFromGroup(String id)
  {
    try
    {
      PreparedStatement statement = MySQL.getConnection().prepareStatement("UPDATE playerdata SET groupname='default', duration='0' WHERE groupname='" + id.toLowerCase() + "';");
      statement.executeUpdate();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void updatePlayerGroup(UUID uuid, String newGroup, Long duration)
  {
    Long dura = Long.valueOf(0L);
    if (duration.longValue() != 0L) {
      dura = Long.valueOf(duration.longValue() * 86400L + System.currentTimeMillis() / 1000L);
    }
    try
    {
      PreparedStatement statement = MySQL.getConnection().prepareStatement("UPDATE playerdata SET groupname='" + newGroup + "', duration='" + dura + "' WHERE uuid='" + uuid.toString() + "';");
      statement.executeUpdate();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
    if (player.isOnline()) {
      updatePermissions(player.getPlayer());
    }
  }
  
  public static void updatePermissions(Player player)
  {
    removeAllPermissions(player);
    
    String playerGroup = getPlayerGroup(player.getUniqueId());
    List<String> playerPermissions = GroupUtilities.getPermissionsForGroup(playerGroup);
    PermissionAttachment attachment = player.addAttachment(Permissions.getInstance());
    for (String str : playerPermissions) {
      attachment.setPermission(str, true);
    }
  }
  
  public static void removeAllPermissions(Player player)
  {
    for (PermissionAttachmentInfo permissionAttachmentInfo : player.getEffectivePermissions())
    {
      Bukkit.broadcastMessage(permissionAttachmentInfo.getPermission());
      permissionAttachmentInfo.getAttachment().unsetPermission(permissionAttachmentInfo.getPermission());
    }
  }
}

