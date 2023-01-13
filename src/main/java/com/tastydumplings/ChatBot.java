package com.tastydumplings;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class ChatBot extends JavaPlugin implements Listener, CommandExecutor {

    // config of the plugin
    FileConfiguration config;
    // cFile used to copy config
    static File cFile;
    // plugin used to copy plugin
    static JavaPlugin plugin;
    // List of categories to check
    static ArrayList<ChatBotListen.ConfigurationLoad> categories = new ArrayList<>();
    // List of category names
    static ArrayList<String> categoryNames = new ArrayList<>();


    @Override
    public void onEnable() {

        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        cFile = new File(getDataFolder(), "config.yml");

        plugin = this;

        ConsoleCommandSender console = getServer().getConsoleSender();
        console.sendMessage("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n[ChatBot] ChatBot Initialized.");
        getServer().getPluginManager().registerEvents(new ChatBotListen(), this);
        this.getCommand("cbr").setExecutor(this);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // if player running command
        if (sender instanceof Player) {

            Player player = ((Player) sender).getPlayer();

            if (player.hasPermission("ChatBot.reload")) {
                config = YamlConfiguration.loadConfiguration(cFile);
                sender.sendMessage(ChatColor.GREEN + "[ChatBot] ChatBot Successfully reloaded. Note: You shouldn't have to reload. Make sure the config is saved in the ChatBot folder within your plugin directory.");
                return true;
            }
            else {
                sender.sendMessage(ChatColor.RED + "[ChatBot] You do not have permission to use this command.");
                return false;
            }

        }
        // if console running command
        else {

            ConsoleCommandSender console = getServer().getConsoleSender();
            reloadConfig();
            console.sendMessage("[ChatBot] ChatBot Successfully reloaded. Note: You shouldn't have to reload. Make sure the config is saved correctly.");
            return true;

        }

    }

    public static YamlConfiguration getMyConfig() {
        return YamlConfiguration.loadConfiguration(cFile);
    }

    public static JavaPlugin getMyPlugin() { return plugin; }

    public static ArrayList<ChatBotListen.ConfigurationLoad> getConfigurationList() {
        return categories;
    }

    public static boolean addCategory(ChatBotListen.ConfigurationLoad category) {
        if (!categories.contains(category)) {
            getConfigurationList().add(category);
            return true;
        }
        return false;
    }

    public static ArrayList<String> getConfigurationNames() {
        return categoryNames;
    }

    public static boolean addConfigurationName(String category) {
        if (!categoryNames.contains(category)) {
            categoryNames.add(category);
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ConsoleCommandSender console = getServer().getConsoleSender();
        console.sendMessage("[ChatBot] Thank you for using ChatBot.");
    }


}
