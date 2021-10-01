package sh.reece.stafflist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import sh.reece.stafflist.util.ConfigUpdater;
import sh.reece.stafflist.util.Metrics;
import sh.reece.stafflist.util.Util;

public class Main extends JavaPlugin {

	public void onEnable() {
		loadConfig();
		
		Metrics metrics = new Metrics(this, 11513);
		
		if(Util.isPluginInstalledOnServer("LuckPerms", "StaffList")) {
			new StaffList(this);
		}
	}
	
	public void onDisable() {
		this.saveDefaultConfig();	
	}
	
	/// - config stuff
	public void loadConfig() {	
		createConfig("config.yml");		
		getConfig().options().copyDefaults(true);	
		
		List<String> ignoredSections = new ArrayList<String>();
		ignoredSections.add("groups");
		
		try {
			ConfigUpdater.update(this, "config.yml", new File(getDataFolder(), "config.yml"), ignoredSections);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public FileConfiguration getConfigFile(String name) {
		return YamlConfiguration.loadConfiguration(new File(getDataFolder(), name));
	}

	public void createConfig(String name) {
		File file = new File(getDataFolder(), name);

		if (!new File(getDataFolder(), name).exists()) {
			saveResource(name, false);
		}

		@SuppressWarnings("static-access")
		FileConfiguration configuration = new YamlConfiguration().loadConfiguration(file);
		if (!file.exists()) {
			try {
				configuration.save(file);
			}			
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveConfig(FileConfiguration config, String name) {
		try {
			config.save(new File(getDataFolder(), name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
