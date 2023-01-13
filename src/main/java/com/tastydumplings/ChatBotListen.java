package com.tastydumplings;

import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

import static com.tastydumplings.ChatBot.*;

public class ChatBotListen implements Listener {

    public static class ConfigurationLoad {

        String name;
        int delay_after;
        int delay_between;
        String response;
        List<String> keys;
        List<String> with_keys;
        List<String> without_keys;
        ArrayList<String> remindList = new ArrayList<>();

        public ConfigurationLoad(String name, int delay_after, int delay_between, String response, List<String> keys, List<String> with_keys, List<String> without_keys) {
            this.name = name;
            this.delay_after = delay_after;
            this.delay_between = delay_between;
            this.response = response;
            this.keys = keys;
            this.with_keys = with_keys;
            this.without_keys = without_keys;
        }


    }

    // Check message
    @EventHandler
    public boolean onChat(PlayerChatEvent e) {

        // List of categories to check
        ArrayList<ChatBotListen.ConfigurationLoad> categories = ChatBot.getConfigurationList();
        ArrayList<String> configurationNames = ChatBot.getConfigurationNames();

        e.getPlayer().sendMessage("Configuration Names: " + configurationNames + " and list of types: " + categories);

        for (String configuration : getMyConfig().getConfigurationSection("types").getKeys(false)) {
            e.getPlayer().sendMessage("Configuration: " + configuration + " and list of types: " + categories);
            if (!configurationNames.contains(configuration)) {
                // Load texture pack config
                addConfigurationName(configuration);
                addCategory(new ConfigurationLoad(getStringD("types."+configuration+".name"), getIntD("types."+configuration+".delay_after"), getIntD("types."+configuration+".delay_between"), getStringD("types."+configuration+".response"), getStringListD("types."+configuration+".keys"), getStringListD("types."+configuration+".with_keys"), getStringListD("types."+configuration+".without_keys")));
                e.getPlayer().sendMessage("Configuration: " + configuration + " and list of types: " + categories);
            }
        }

        // player object used in event
        Player player = e.getPlayer();
        // message player has sent
        String message = e.getMessage().toLowerCase();

        // check if message contains any conditions
        for (ConfigurationLoad category : categories) {
            if (checkConditions(categories, category, player, message)) {
                // if there is a condition - return true and end loop
                // means a reminder is sent
                return true;
            }
        }
        // message is cleared :( nothing happens - return false
        // no reminder is sent
        return false;

    }

    // check if player's message contains any of the conditions for a reminder within the category
    public boolean checkConditions(List<ConfigurationLoad> categories, ConfigurationLoad category, Player player, String message) {
        for (String condition : category.with_keys) {
            for (String key : category.keys) {
                if (message.contains(condition) && message.contains(key)) {
                    // if sequence reminders are on
                    if (getRemindSequenceValue()) {
                        // send message to player
                        remindPlayer(player, category);
                        // end loop
                        return true;
                    }
                    // check if they were reminded by any other reminder in the past Delay_Between time
                    else {
                        if (!wasReminded(categories, player)) {
                            // send message to player
                            remindPlayer(player, category);
                            // end loop
                            return true;
                        }
                    }
                }
            }
        }
        for (String condition : category.without_keys) {
            if (message.contains(condition)) {
                // if sequence reminders are on
                if (getRemindSequenceValue()) {
                    // send message to player
                    remindPlayer(player, category);
                    // end loop
                    return true;
                }
                else {
                    // check if they were reminded by any other reminder in the past Delay_Between time
                    if (!wasReminded(categories, player)) {
                        // send message to player
                        remindPlayer(player, category);
                        // end loop
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // code to remind player after 3 seconds for the Resource Pack
    public static void remindPlayer(Player player, ConfigurationLoad category) {

        player.getServer().getScheduler().scheduleSyncDelayedTask(ChatBot.getMyPlugin(), () -> {
            if (!(category.remindList.contains(player.getName()))) {
                player.sendMessage(ChatColor.GRAY.toString() + "TIP: " + ChatColor.ITALIC.toString() + category.response);
                player.playNote(player.getLocation(), Instrument.CHIME, new Note(10));
                // adds player name to remind list
                category.remindList.add(player.getName());
                // calls scheduled remove name method
                removeNameRPList(player, category);
            }
        }, category.delay_after);

    }

    // code to remove player name from the given reminder list after 5 minutes
    public static void removeNameRPList(Player player, ConfigurationLoad category) {

        player.getServer().getScheduler().scheduleSyncDelayedTask(ChatBot.getMyPlugin(), () -> {
            // remove name from list
            category.remindList.remove(player.getName());
        }, category.delay_between);

    }

    // code to quickly get the config info with directory - STRING LIST
    public List<String> getStringListD(String directory) {
        return ChatBot.getMyConfig().getStringList(directory);
    }
    // code to quickly get the config info with directory - STRING
    public String getStringD(String directory) {
        return ChatBot.getMyConfig().getString(directory);
    }
    // code to quickly get config info with directory - INTEGER
    public int getIntD(String directory) {
        return ChatBot.getMyConfig().getInt(directory);
    }
    public static boolean getRemindSequenceValue() {
        return ChatBot.getMyConfig().getBoolean("ChatBot_Config.can_remind_in_sequence");
    }
    // checks if player was reminded in the past delay_between time
    public static boolean wasReminded(List<ConfigurationLoad> categories, Player player) {
        for (ConfigurationLoad category : categories) {
            if (category.remindList.contains(player.getName())) {
                // name in any category remind list
                return true;
            }
        }
        // name not in any other category remind list
        return false;
    }

}
