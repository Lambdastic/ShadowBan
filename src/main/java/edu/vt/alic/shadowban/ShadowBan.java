package edu.vt.alic.shadowban;

import edu.vt.alic.shadowban.commands.ShadowBanCommand;
import edu.vt.alic.shadowban.listeners.PlayerChatListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ShadowBan extends JavaPlugin {

    private File bannedFile;
    private FileConfiguration bannedConfig;

    @Override
    public void onEnable() {
        createFile();

        new ShadowBanCommand(this);
        new PlayerChatListener(this);
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
