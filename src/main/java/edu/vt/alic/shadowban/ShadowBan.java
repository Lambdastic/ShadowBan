package edu.vt.alic.shadowban;

import edu.vt.alic.shadowban.commands.ShadowBanCommand;
import edu.vt.alic.shadowban.listeners.PlayerChatListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ali-PC on 7/9/2017.
 */
public class ShadowBan extends JavaPlugin {

    private static ShadowBan plugin;

    private File bannedFile;
    private FileConfiguration bannedConfig;

    public static ShadowBan getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        createFile();

        new ShadowBanCommand();
        new PlayerChatListener();
    }

    private void createFile() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        bannedFile = new File(getDataFolder(), "banned.yml");

        if (!bannedFile.exists()) {
            try {
                bannedFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bannedConfig = YamlConfiguration.loadConfiguration(bannedFile);

        if (bannedConfig.getConfigurationSection("Shadow Banned") == null) {
            bannedConfig.createSection("Shadow Banned");
        }
    }

    public File getBannedFile() {
        return bannedFile;
    }

    public FileConfiguration getBannedConfig() {
        return bannedConfig;
    }
}
