package edu.vt.alic.shadowban.listeners;

import edu.vt.alic.shadowban.ShadowBan;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

/**
 * Created by Ali-PC on 7/9/2017.
 */
public class PlayerChatListener implements Listener {

    public PlayerChatListener() {
        ShadowBan.getInstance().getServer().getPluginManager().registerEvents(this, ShadowBan.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (ShadowBan.getInstance().getBannedConfig().getConfigurationSection("Shadow Banned").getKeys(false).contains(p.getUniqueId().toString())) {
            e.setCancelled(true);
            p.sendMessage(String.format(e.getFormat(), p.getDisplayName(), e.getMessage()));
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "*ShadowBanned*"
                    + ChatColor.RESET + String.format(e.getFormat(), p.getDisplayName(), e.getMessage()));

            for (Player pp : Bukkit.getOnlinePlayers()) {
                List<String> spies = ShadowBan.getInstance().getBannedConfig().getStringList("Shadow Banned." + p.getUniqueId().toString() + ".Spied By");

                if (pp.hasPermission("shadowban.spy") && spies.contains(pp.getName())) {
                    pp.sendMessage(ChatColor.BLUE + "*ShadowBanned*"
                            + ChatColor.RESET + String.format(e.getFormat(), p.getDisplayName(), e.getMessage()));
                }
            }
        }
    }
}
