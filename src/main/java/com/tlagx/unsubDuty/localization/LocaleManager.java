package com.tlagx.unsubduty.localization;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LocaleManager {
    private final JavaPlugin plugin;
    private final Map<String, YamlConfiguration> locales = new HashMap<>();
    private String currentLocale = "ru";

    public LocaleManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadLocales();
    }

    public void loadLocales() {
        loadLocale("ru");
        loadLocale("en");
    }

    private void loadLocale(String locale) {
        File localeFile = new File(plugin.getDataFolder(), "locales/" + locale + ".yml");
        if (!localeFile.exists()) {
            localeFile.getParentFile().mkdirs();
            plugin.saveResource("locales/" + locale + ".yml", false);
        }
        locales.put(locale, YamlConfiguration.loadConfiguration(localeFile));
    }

    public void setLocale(String locale) {
        if (locales.containsKey(locale)) {
            this.currentLocale = locale;
        } else {
            // If locale file does not exist, fallback to default 'en'
            this.currentLocale = "en";
        }
    }

    public String get(String key) {
        String value = locales.get(currentLocale).getString(key);
        return value != null ? value : key;
    }

    public String get(String key, String locale) {
        YamlConfiguration config = locales.get(locale);
        if (config == null) return key;
        String value = config.getString(key);
        return value != null ? value : key;
    }

    public String getColor(String key) {
        String value = locales.get(currentLocale).getString(key);
        if (value == null) return key;
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', value);
    }

    public String getColor(String key, String locale) {
        YamlConfiguration config = locales.get(locale);
        if (config == null) return key;
        String value = config.getString(key);
        if (value == null) return key;
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', value);
    }
}
