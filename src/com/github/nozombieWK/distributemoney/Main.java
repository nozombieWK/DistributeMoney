package com.github.nozombieWK.distributemoney;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Main extends JavaPlugin implements Listener {
    public static Economy econ = null;
	public void onEnable(){
		if(setupEconomy()){
			getLogger().info("Plugin is loaded！");
		}else{
			getLogger().info("No Vault found, disabling...");
			Bukkit.getPluginManager().disablePlugin(this);;
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(label.equalsIgnoreCase("dismoney") && args.length == 1){
			if(sender.hasPermission("dismoney.use")){
                new BukkitRunnable(){
                	public void run(){
                		Integer tmp1 = 0;
                      	for (Player wj : getServer().getOnlinePlayers()){
                      		tmp1++;
                      	}
                      	if(Bukkit.getPlayer(args[0]) != null){
                      		tmp1--;
                      	}else{
                      		sender.sendMessage("§c[Warning] §eThe target player isn't now online!");
                      	}
        				if(tmp1 <= 0){
        					sender.sendMessage("You are the only player in the server...");
        					return;
        				}
        				Integer tmp = (int)econ.getBalance(args[0]);
        	            EconomyResponse r1 = econ.deleteBank(args[0]);
        	            if(r1.transactionSuccess() && econ.createPlayerAccount(args[0])) {
            	            tmp = tmp / tmp1;
        	                sender.sendMessage("Successfully distribute！");
        	            } else {
        	                sender.sendMessage(String.format("§cError: %s", r1.errorMessage));
        	                return;
        	            }
        				Bukkit.getServer().broadcastMessage("§e[§cAlert§e]The money of player "+args[0]+" will be distributed to every online players!");
        				

                      	for (Player wj : getServer().getOnlinePlayers()){
                      		if (wj == Bukkit.getPlayer(args[0])){
                      			wj.sendMessage("§cYour bank account has been reset!");
                      			wj.sendMessage("§cNow you have:" + econ.getBalance(wj));
                      			continue;
                      		}
        		            EconomyResponse r = econ.depositPlayer(wj, tmp);
        		            if(r.transactionSuccess()) {
        		                wj.sendMessage(String.format("§eYou get  %s , now you have%s", econ.format(r.amount), econ.format(r.balance)));
        		            } else {
        		            	sender.sendMessage(wj.getName() + ":");
        		                sender.sendMessage(String.format("§cError: %s", r.errorMessage));
        		            }
        		            tmp1 = 0;
                      	}
                	}
                }.runTaskAsynchronously(this);
		}else{
			sender.sendMessage("§cYou don't have permission to do that!");
		}
		}else{
			sender.sendMessage("§eUsage:");
			sender.sendMessage("§b§n/dismoney player");
		}
		return true;
	}
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
