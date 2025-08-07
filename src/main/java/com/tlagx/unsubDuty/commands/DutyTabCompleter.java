package com.tlagx.unsubduty.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.tlagx.unsubduty.UnsubDuty;
import com.tlagx.unsubduty.models.DutyRank;

public class DutyTabCompleter implements TabCompleter {
    private final UnsubDuty plugin;

    public DutyTabCompleter(UnsubDuty plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            // First argument: subcommands
            if (player.hasPermission("unsubduty.admin")) {
                completions.add("set");
                completions.add("roles");
                completions.add("reload");
            }
            return completions.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            // Second argument for "set": player names
            if (player.hasPermission("unsubduty.admin")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            // Third argument for "set": role keys
            if (player.hasPermission("unsubduty.admin")) {
                return plugin.getConfigManager().getAllDutyRanksSorted().stream()
                        .map(DutyRank::getKey)
                        .filter(key -> key.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        return completions;
    }
}
