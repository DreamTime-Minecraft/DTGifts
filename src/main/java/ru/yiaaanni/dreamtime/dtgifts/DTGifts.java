package ru.yiaaanni.dreamtime.dtgifts;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public final class DTGifts extends JavaPlugin implements CommandExecutor {

    private HashMap<String, String> gifts = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCodes();

        Bukkit.getPluginCommand("gift").setExecutor(this);
    }

    private synchronized void getCodes() {
        Set<String> codes = getConfig().getConfigurationSection("codes").getKeys(false);
        for(String str : codes) {
            gifts.put(str, getConfig().getString("codes."+str));
        }
    }

    private synchronized boolean savePlayerToUsedCode(String name, String code) {
        List<String> alrUsed = getConfig().getStringList("used."+code);
        if(alrUsed.contains(name)) {
            return false;
        }

        alrUsed.add(name);
        getConfig().set("used."+code, alrUsed);
        return true;
    }

    private synchronized boolean hasSlots(Player p) {
        Inventory inv = p.getInventory();
        boolean check = false;
        for(ItemStack item : inv.getContents()) {
            if(item == null) {
                check = true;
                break;
            }
        }
        return check;
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("§cПожалуйста, введите команду вместе с правильным кодом!");
            return false;
        }

        String code = args[0];

        if(!gifts.containsKey(code)) {
            sender.sendMessage("§cКод не обнаружен! Проверьте правильность ввода!");
            return false;
        }

        if(!hasSlots((Player)sender)) {
            sender.sendMessage("§cУ Вас нет свободного места в инвентаре. Как Вы получите награду? =( \n§8(Освободите место в инвентаре)");
            return false;
        }

        if(savePlayerToUsedCode(sender.getName(), code)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), gifts.get(code).replace("%player%", sender.getName()));
            sender.sendMessage("§aПоздравляем с призом!");
        }

        return true;
    }

}
