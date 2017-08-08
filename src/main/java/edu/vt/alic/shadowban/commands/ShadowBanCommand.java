package edu.vt.alic.shadowban.commands;

import edu.vt.alic.shadowban.Messages;
import edu.vt.alic.shadowban.ShadowBan;
import edu.vt.alic.shadowban.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShadowBanCommand implements CommandExecutor {

    private ShadowBan plugin;

    public ShadowBanCommand(ShadowBan plugin) {
        this.plugin = plugin;
        plugin.getCommand("shadowban").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("shadowban")) {
            if (args.length == 0) {
                if (!sender.hasPermission("shadowban.help")) {
                    sender.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }
                displayHelpMenu(sender);
                return true;
            }

            Player offender = null;

            // /shadowban spy {player}
            if (args[0].equalsIgnoreCase("spy") && args.length == 2) {
                if (!sender.hasPermission("shadowban.spy")) {
                    sender.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Messages.PLAYER_ONLY);
                    return true;
                }
                if (Bukkit.getPlayer(args[1]) != null) {
                    offender = Bukkit.getPlayer(args[1]);
                    spyPlayer((Player) sender, offender);
                } else {
                    sender.sendMessage(Messages.NOT_ONLINE);
                }
                return true;
            }

            // /shadowban check {player}
            if (args[0].equalsIgnoreCase("check") && args.length == 2) {
                if (!sender.hasPermission("shadowban.check")) {
                    sender.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }
                if (Bukkit.getPlayer(args[1]) != null) {
                    offender = Bukkit.getPlayer(args[1]);
                    checkPlayerBanStatus(sender, offender);
                } else {
                    sender.sendMessage(Messages.NOT_ONLINE);
                }
                return true;
            }

            // /shadowban {player} [reason]
            if (!sender.hasPermission("shadowban.shadowban")) {
                sender.sendMessage(Messages.NO_PERMISSION);
                return true;
            }
            if (Bukkit.getPlayer(args[0]) != null) {
                offender = Bukkit.getPlayer(args[0]);

                if (args.length == 1) {
                    shadowBan(sender, offender, "N/A");
                    return true;
                }
                StringBuilder reason = new StringBuilder();

                for (int i = 1; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }
                shadowBan(sender, offender, reason.toString().trim());
            } else {
                sender.sendMessage(Messages.NOT_ONLINE);
            }
        }
        return true;
    }

    private void displayHelpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + "ShadowBan Commands\n");

        sender.sendMessage(ChatColor.AQUA + " /shadowban {player}"
                + ChatColor.WHITE + " - Shadow ban or remove ban from player based on current status.");
        sender.sendMessage(ChatColor.AQUA + " /shadowban {player} [reason]"
                + ChatColor.WHITE + " - Shadow ban a player for a specific reason.");
        sender.sendMessage(ChatColor.AQUA + " /shadowban check {player}"
                + ChatColor.WHITE + " - Check whether the player is shadow banned and the reason.");
        sender.sendMessage(ChatColor.AQUA + " /shadowban spy {player}"
                + ChatColor.WHITE + " - Receive messages from a shadow banned player.");
    }

    private void shadowBan(CommandSender sender, Player offender, String reason) {
        if (plugin.getBannedConfig().getConfigurationSection("Shadow Banned").getKeys(false).contains(offender.getUniqueId().toString())) {
            sender.sendMessage(ChatColor.GREEN + "Shadow ban removed from "
                    + ChatColor.BLUE + offender.getName() + ChatColor.GREEN + ".");
            plugin.getBannedConfig().set("Shadow Banned." + offender.getUniqueId().toString(), null);
            try {
                plugin.getBannedConfig().save(plugin.getBannedFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "You have shadow banned "
                + ChatColor.BLUE + offender.getName() + ChatColor.GREEN + " for: " + ChatColor.WHITE + reason);

        plugin.getBannedConfig().set(Util.path("Name", offender.getUniqueId().toString()), offender.getName());
        plugin.getBannedConfig().set(Util.path("Reason", offender.getUniqueId().toString()), reason);
        plugin.getBannedConfig().set(Util.path("Date", offender.getUniqueId().toString()),
                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        plugin.getBannedConfig().set(Util.path("Spied By", offender.getUniqueId().toString()), new ArrayList<String>());

        try {
            plugin.getBannedConfig().save(plugin.getBannedFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void checkPlayerBanStatus(CommandSender sender, Player offender) {
        FileConfiguration config = plugin.getBannedConfig();
        if (config.getConfigurationSection("Shadow Banned").getKeys(false).contains(offender.getUniqueId().toString())) {
            String reason = config.getString(Util.path("Reason", offender.getUniqueId().toString()));
            String date = config.getString(Util.path("Date", offender.getUniqueId().toString()));

            sender.sendMessage(ChatColor.YELLOW + offender.getName() + "\n");

            sender.sendMessage(ChatColor.AQUA + "Shadow Banned: " + ChatColor.DARK_RED + "true");
            sender.sendMessage(ChatColor.AQUA + "Reason: " + ChatColor.WHITE + reason);
            sender.sendMessage(ChatColor.AQUA + "Date: " + ChatColor.WHITE + date);

            StringBuilder spiedBy = new StringBuilder("[");

            for (int i = 0; i < config.getStringList(Util.path("Spied By", offender.getUniqueId().toString())).size(); i++) {
                spiedBy.append(config.getStringList(Util.path("Spied By", offender.getUniqueId().toString())).get(i)).append(" ");
            }
            spiedBy.toString().trim();
            spiedBy.append("]");

            sender.sendMessage(ChatColor.AQUA + "Spied By: " + ChatColor.WHITE + spiedBy);
        } else {
            sender.sendMessage(ChatColor.BLUE + offender.getName() + " is not shadow banned.");
        }
    }

    private void spyPlayer(Player sender, Player offender) {
        FileConfiguration config = plugin.getBannedConfig();
        if (config.getConfigurationSection("Shadow Banned").getKeys(false).contains(offender.getUniqueId().toString())) {
            List<String> spies = config.getStringList(Util.path("Spied By", offender.getUniqueId().toString()));
            if (spies.contains(sender.getName())) {
                spies.remove(sender.getName());
                sender.sendMessage(ChatColor.BLUE + " You have disabled spy on "
                        + ChatColor.GREEN + offender.getName() + ChatColor.BLUE + ".");
            } else {
                spies.add(sender.getName());
                sender.sendMessage(ChatColor.BLUE + " You have enabled spy on "
                        + ChatColor.GREEN + offender.getName() + ChatColor.BLUE + ".");
            }

            plugin.getBannedConfig().set(Util.path("Spied By", offender.getUniqueId().toString()), spies);

            try {
                plugin.getBannedConfig().save(plugin.getBannedFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            sender.sendMessage(ChatColor.BLUE + offender.getName() + " is not shadow banned for you to spy on.");
        }
    }
}
