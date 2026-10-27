package com.example.helpme;

//import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class HelpMePlugin extends JavaPlugin {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private MessageManager messages;

    @Override
    public void onEnable() {
        // Creates config.yml on first run (copied from src/main/resources/config.yml)
        saveDefaultConfig();
        messages = new MessageManager(this);

        getCommand("helpme").setExecutor(this);
        getCommand("helpme").setTabCompleter(this);

        getLogger().info("HelpMe plugin enabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("helpme")) {
            return false;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            return handleReload(sender);
        }

        return handleHelp(sender);
    }

    private boolean handleHelp(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(LEGACY.deserialize(
                    messages.getText(sender, "player-only", "&cThis command can only be used by a player.")));
            return true;
        }

        // Each entry in the "helpme" list is sent as its own chat line
        List<String> lines = messages.getLines(player, "helpme");
        for (String line : lines) {
            player.sendMessage(LEGACY.deserialize(line));
        }
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("helpme.reload")) {
            sender.sendMessage(LEGACY.deserialize(
                    messages.getText(sender, "no-permission", "&cYou don't have permission to do that.")));
            return true;
        }

        messages.reload();
        sender.sendMessage(LEGACY.deserialize(
                messages.getText(sender, "reload", "&aHelpMe configuration reloaded.")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("helpme.reload") && "reload".startsWith(args[0].toLowerCase())) {
            return List.of("reload");
        }
        return List.of();
    }
}
