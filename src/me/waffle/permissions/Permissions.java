package me.waffle.permissions;

import me.waffle.permissions.commands.AddGroupCommand;
import me.waffle.permissions.commands.AddPermissionCommand;
import me.waffle.permissions.commands.DelGroupCommand;
import me.waffle.permissions.commands.ListGroupsCommand;
import me.waffle.permissions.commands.ListPermissionsCommand;
import me.waffle.permissions.commands.RemovePermissionCommand;
import me.waffle.permissions.commands.SetGroupCommand;
import me.waffle.permissions.commands.SetPrefixCommand;
import me.waffle.permissions.listeners.PlayerListener;
import me.waffle.permissions.utilities.GroupUtilities;
import me.waffle.permissions.utilities.MySQL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Permissions
  extends JavaPlugin
{
  private static Permissions instance;
  
  public static Permissions getInstance()
  {
    return instance;
  }
  
  public void onEnable()
  {
    instance = this;
    getConfig().options().copyDefaults(true);
    saveConfig();
    MySQL.connect();
    try
    {
      Statement statement = MySQL.getConnection().createStatement();
      statement.executeUpdate("CREATE TABLE IF NOT EXISTS `playerdata`(uuid text, lastip text, groupname text, duration long);");
      statement.executeUpdate("CREATE TABLE IF NOT EXISTS `groups`(id text, prefix text, permissions text);");
      statement.executeUpdate("CREATE TABLE IF NOT EXISTS `authuuids`(uuid text);");
      statement.executeUpdate("CREATE TABLE IF NOT EXISTS `authips`(ip text);");
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    loadClasses();
    if (!GroupUtilities.doesGroupExist("default")) {
      GroupUtilities.createGroup("default");
    }
  }
  
  public void onDisable()
  {
    MySQL.disconnect();
    instance = null;
  }
  
  private void loadClasses()
  {
    PluginManager pluginManager = Bukkit.getServer().getPluginManager();
    
    getCommand("addgroup").setExecutor(new AddGroupCommand());
    getCommand("addpermission").setExecutor(new AddPermissionCommand());
    getCommand("delgroup").setExecutor(new DelGroupCommand());
    getCommand("listgroups").setExecutor(new ListGroupsCommand());
    getCommand("rmpermission").setExecutor(new RemovePermissionCommand());
    getCommand("setgroup").setExecutor(new SetGroupCommand());
    getCommand("setprefix").setExecutor(new SetPrefixCommand());
    getCommand("listperms").setExecutor(new ListPermissionsCommand());
    
    pluginManager.registerEvents(new PlayerListener(), instance);
  }
}
