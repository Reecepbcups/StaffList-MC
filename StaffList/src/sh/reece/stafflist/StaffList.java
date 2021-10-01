package sh.reece.stafflist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import sh.reece.stafflist.util.Util;

public class StaffList implements CommandExecutor {

	private static Main plugin;
	private FileConfiguration config;
	//private String Section;
	private Set<String> groups;

	// owner: "&8&l<&d&lOWNER&8&l> &f» &d"
	private HashMap<String, String> groupFormating = new HashMap<String, String>();

	public StaffList(Main instance) { 
		plugin = instance;
		reloadConfig();

		plugin.getCommand("stafflist").setExecutor(this);   	
	}

	public void reloadConfig() {
		config = plugin.getConfigFile("config.yml");

		if(config.contains("groups")) {
			groups = config.getConfigurationSection("groups").getKeys(false);
		} else {
			Util.consoleMSG("&c[!] &4NO GROUPS DEFINED AT " + "groups");
			return;
		}    	
		//Util.consoleMSG("Group Keys " + groups);
		
		for(String group : groups) { // group formating cache        		
			groupFormating.put(group, config.getString("groups."+group));          		        		      	
		} 
	}
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {				
		Player p = (Player) sender;

		if(args.length>0) {
			if(args[0].equalsIgnoreCase("reload")) {
				
				if(checkPerm(p, "stafflist.admin")) {
					reloadConfig();
					Util.coloredMessage(p, "&a[!] StaffList Config Reloaded");
				} 
				
				return true;
			}
			if(args[0].equalsIgnoreCase("groups")) {
				
				if(checkPerm(p, "stafflist.admin")) {
					Util.coloredMessage(p, "&a[!] &fLuckPerm groups: " + groups);	
				} 
				return true;
			}
		}
		
		// owner, ['reece', 'phasha']
		HashMap<String, Set<String>> staff = new HashMap<String, Set<String>>();

		for(Player online : Bukkit.getOnlinePlayers()) {
			User u = LuckPermsProvider.get().getUserManager().getUser(online.getUniqueId());
			String mainGroup = u.getPrimaryGroup().toString(); // staff ranks

			//Util.consoleMSG(online.getName() + " main group: " + mainGroup);

			// if their main group is not in the groups list
			// for example, default
			if(!groups.contains(mainGroup)) {
				continue;
			}

			// if they are vanished, don't show in list
			if(isVanished(online) || online.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				continue;
			}

			// if staff does not have that staff key, add to new list and do that
			if(!staff.containsKey(mainGroup)) {
				Set<String> newgroup = new HashSet<String>();
				newgroup.add(online.getName());
				staff.put(mainGroup, newgroup);

				Util.consoleMSG("Added " + mainGroup + " key to staff hash");
			} else { 
				// if staff hash has that key already
				// adds user to hashmap if they have the rank
				staff.get(mainGroup).add(online.getName());
			}
		}


		// Outputting to user		
		Util.coloredMessage(p, " ");		
		// owner: "&8&l<&d&lOWNER&8&l> &f» &d"
		for(String group : groups) {
			String finalOutput = "";

			String title = groupFormating.get(group);

			finalOutput+=title;

			// if no staff, set to N/A
			if(!staff.containsKey(group)) {
				finalOutput+=" &c&oN/A";				
			} else {
				// for StaffPlayer in that group, append to group
				for(String SPlayer : staff.get(group)) {
					finalOutput += SPlayer+" ";
				}
			}			
			Util.coloredMessage(p, finalOutput);
		}		
		Util.coloredMessage(p, " ");

		return true;
	}
	
	
	public Boolean checkPerm(Player p, String perm) {
		if(p.hasPermission(perm)) {
			return true;
		} 		
		Util.coloredMessage(p, "&c[!] You do not have permission '"+perm+"' to use this command!");
		return false;
	}

	private boolean isVanished(Player player) {
		for (MetadataValue meta : player.getMetadata("vanished")) {
			if (meta.asBoolean()) {
				return true;
			}
		}
		return false;
	}


}
