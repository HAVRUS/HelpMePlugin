package com.example.helpme;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Reads messages out of config.yml under the "messages.<language>.*" tree
 * and picks the right language for a given command sender.
 */
public class MessageManager {

    private final HelpMePlugin plugin;
    private String defaultLanguage;

    public MessageManager(HelpMePlugin plugin) {
        this.plugin = plugin;
        load();
    }

    /** Re-reads config.yml from disk and refreshes cached settings. */
    public void reload() {
        plugin.reloadConfig();
        load();
    }

    private void load() {
        defaultLanguage = plugin.getConfig().getString("default-language", "en");
    }

    /**
     * A multi-line message, stored in config.yml as a YAML list, e.g.:
     *   messages:
     *     en:
     *       helpme:
     *         - "Line 1 Text"
     *         - "Line 2 Text"
     * Falls back to the default language, then to a hardcoded placeholder,
     * if nothing is configured.
     */
    public List<String> getLines(CommandSender sender, String key) {
        String language = resolveLanguage(sender);
        List<String> lines = plugin.getConfig().getStringList("messages." + language + "." + key);

        if (lines.isEmpty() && !language.equals(defaultLanguage)) {
            lines = plugin.getConfig().getStringList("messages." + defaultLanguage + "." + key);
        }
        if (lines.isEmpty()) {
            lines = List.of("&cNo message has been configured yet.");
        }
        return lines;
    }

    /**
     * A single-line message, e.g. messages.<language>.reload.
     * Falls back to the default language, then to the given fallback text.
     */
    public String getText(CommandSender sender, String key, String fallback) {
        String language = resolveLanguage(sender);
        String text = plugin.getConfig().getString("messages." + language + "." + key);

        if (text == null && !language.equals(defaultLanguage)) {
            text = plugin.getConfig().getString("messages." + defaultLanguage + "." + key);
        }
        return text != null ? text : fallback;
    }

    /**
     * Picks which language to use: a player's own client language if we have
     * a "messages" section for it, otherwise the configured default-language.
     * Non-player senders (console, command blocks) always get the default.
     */
    private String resolveLanguage(CommandSender sender) {
        if (sender instanceof Player player) {
            String language = player.locale().getLanguage(); // e.g. "en", "ru", "de"
            if (plugin.getConfig().isConfigurationSection("messages." + language)) {
                return language;
            }
        }
        return defaultLanguage;
    }
}
