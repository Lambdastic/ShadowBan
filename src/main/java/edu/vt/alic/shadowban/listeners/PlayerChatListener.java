package edu.vt.alic.shadowban.listeners;

import edu.vt.alic.shadowban.ShadowBan;
import edu.vt.alic.shadowban.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class PlayerChatListener implements Listener {

    private ShadowBan plugin;

    public PlayerChatListener(ShadowBan plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player offender = e.getPlayer();

        if (plugin.getBannedConfig().getConfigurationSection("Shadow Banned").getKeys(false).contains(offender.getUniqueId().toString())) {
            e.setCancelled(true);
            offender.sendMessage(String.format(e.getFormat(), offender.getDisplayName(), e.getMessage()));
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "*ShadowBanned*"
                    + ChatColor.RESET + String.format(e.getFormat(), offender.getDisplayName(), e.getMessage()));

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                List<String> spies = plugin.getBannedConfig().getStringList(Util.path("Spied By", offender.getUniqueId().toString()));

                if (onlinePlayer.hasPermission("shadowban.spy") && spies.contains(onlinePlayer.getName())) {
                    onlinePlayer.sendMessage(ChatColor.BLUE + "*ShadowBanned*"
                            + ChatColor.RESET + String.format(e.getFormat(), offender.getDisplayName(), e.getMessage()));
                }
            }
        }
    }
}
