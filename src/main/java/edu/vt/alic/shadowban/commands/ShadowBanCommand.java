package edu.vt.alic.shadowban.commands;

import edu.vt.alic.shadowban.ShadowBan;
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

/**
 * Created by Ali-PC on 7/9/2017.
 */
public class ShadowBanCommand implements CommandExecutor {

    public ShadowBanCommand() {
        ShadowBan.getInstance().getCommand("shadowban").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("shadowban")) {
            if (args.length == 0) {
                if (!sender.hasPermission("shadowban.help")) {
                    sender.sendMessage(ChatColor.RED + "You do not have the permission to use that command.");
                    return true;
                }
                displayHelpMenu(sender);
            }

            Player offender = null;

            if (args.length == 1) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    offender = Bukkit.getPlayer(args[0]);
                    shadowBan(sender, offender, "N/A");
                } else {
                    if (!sender.hasPermission("shadowban.shadowban")) {
                        sender.sendMessage(ChatColor.RED + "You do not have the permission to use that command.");
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "Either that player is currently not online or does not exist.");
                }
            }
            if (args.length > 1) {
                if (args[0].equalsIgnoreCase("spy")) {
                    if (!sender.hasPermission("shadowban.spy")) {
                        sender.sendMessage(ChatColor.RED + "You do not have the permission to use that command.");
                        return true;
                    }
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "You must be a player to use that command.");
                        return true;
                    }
                    if (Bukkit.getPlayer(args[1]) != null) {
                        offender = Bukkit.getPlayer(args[1]);
                        spyPlayer((Player)sender, offender);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Either that player is currently not online or does not exist.");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("check")) {
                    if (!sender.hasPermission("shadowban.check")) {
                        sender.sendMessage(ChatColor.RED + "You do not have the permission to use that command.");
                        return true;
                    }
                    if (Bukkit.getPlayer(args[1]) != null) {
                        offender = Bukkit.getPlayer(args[1]);
                        checkPlayerBanStatus(sender, offender);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Either that player is currently not online or does not exist.");
                    }
                    return true;
                }
                if (Bukkit.getPlayer(args[0]) != null) {
                    offender = Bukkit.getPlayer(args[0]);
                    String reason = "";


                    for (int i = 1; i < args.length; i++) {
                        reason += args[i] + " ";
                    }
                    shadowBan(sender, offender, reason);
                } else {
                    if (!sender.hasPermission("shadowban.shadowban")) {
                        sender.sendMessage(ChatColor.RED + "You do not have the permission to use that command.");
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "Either that player is currently not online or does not exist.");
                }
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
        if (!sender.hasPermission("shadowban.shadowban")) {
            sender.sendMessage(ChatColor.RED + "You do not have the permission to use that command.");
            return;
        }
        if (ShadowBan.getInstance().getBannedConfig().getConfigurationSection("Shadow Banned").getKeys(false).contains(offender.getUniqueId().toString())) {
            sender.sendMessage(ChatColor.GREEN + "Shadow ban removed from "
                    + ChatColor.BLUE + offender.getName() + ChatColor.GREEN + ".");
            ShadowBan.getInstance().getBannedConfig().set("Shadow Banned." + offender.getUniqueId().toString(), null);
            try {
                ShadowBan.getInstance().getBannedConfig().save(ShadowBan.getInstance().getBannedFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "You have shadow banned "
                + ChatColor.BLUE + offender.getName() + ChatColor.GREEN + " for: " + ChatColor.WHITE + reason);

        ShadowBan.getInstance().getBannedConfig().set("Shadow Banned." + offender.getUniqueId().toString() + ".Name",
                offender.getName());
        ShadowBan.getInstance().getBannedConfig().set("Shadow Banned." + offender.getUniqueId().toString() + ".Reason",
                reason);
        ShadowBan.getInstance().getBannedConfig().set("Shadow Banned." + offender.getUniqueId().toString() + ".Date",
                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        ShadowBan.getInstance().getBannedConfig().set("Shadow Banned." + offender.getUniqueId().toString() + ".Spied By",
                new ArrayList<String>());

        try {
            ShadowBan.getInstance().getBannedConfig().save(ShadowBan.getInstance().getBannedFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void checkPlayerBanStatus(CommandSender sender, Player offender) {
        FileConfiguration config = ShadowBan.getInstance().getBannedConfig();

        if (config.getConfigurationSection("Shadow Banned").getKeys(false).contains(offender.getUniqueId().toString())) {
            String reason = config.getString("Shadow Banned." + offender.getUniqueId().toString() + ".Reason");
            String date = config.getString("Shadow Banned." + offender.getUniqueId().toString() + ".Date");

            sender.sendMessage(ChatColor.YELLOW + offender.getName() + "\n");

            sender.sendMessage(ChatColor.AQUA + "Shadow Banned: " + ChatColor.DARK_RED + "true");
            sender.sendMessage(ChatColor.AQUA + "Reason: " + ChatColor.WHITE + reason);
            sender.sendMessage(ChatColor.AQUA + "Date: " + ChatColor.WHITE + date);

            String spiedBy = "[";

            for (int i = 0; i < config.getStringList("Shadow Banned." + offender.getUniqueId().toString() + ".Spied By").size(); i++) {
                spiedBy += config.getStringList("Shadow Banned." + offender.getUniqueId().toString() + ".Spied By").get(i) + " ";
            }

            spiedBy = spiedBy.trim();
            spiedBy += "]";
            sender.sendMessage(ChatColor.AQUA + "Spied By: " + ChatColor.WHITE + spiedBy);
        } else {
            sender.sendMessage(ChatColor.BLUE + offender.getName() + " is not shadow banned.");
        }
    }

    private void spyPlayer(Player sender, Player offender) {
        FileConfiguration config = ShadowBan.getInstance().getBannedConfig();

        if (config.getConfigurationSection("Shadow Banned").getKeys(false).contains(offender.getUniqueId().toString())) {
            List<String> spies = config.getStringList("Shadow Banned." + offender.getUniqueId().toString() + ".Spied By");

            if (spies.contains(sender.getName())) {
                spies.remove(sender.getName());
                sender.sendMessage(ChatColor.BLUE + " You have disabled spy on "
                        + ChatColor.GREEN + offender.getName() + ChatColor.BLUE + ".");
            } else {
                spies.add(sender.getName());
                sender.sendMessage(ChatColor.BLUE + " You have enabled spy on "
                        + ChatColor.GREEN + offender.getName() + ChatColor.BLUE + ".");
            }

            ShadowBan.getInstance().getBannedConfig().set("Shadow Banned." + offender.getUniqueId().toString() + ".Spied By", spies);

            try {
                ShadowBan.getInstance().getBannedConfig().save(ShadowBan.getInstance().getBannedFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            sender.sendMessage(ChatColor.BLUE + offender.getName() + " is not shadow banned for you to spy on.");
        }
    }
}
