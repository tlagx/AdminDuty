# UnsubDuty — Discontinued

**UnsubDuty** is a plugin for Minecraft servers running on Bukkit/Spigot/Paper that provides an admin duty system with LuckPerms support.  

## Key Features  

- **Duty System** — Admins can toggle duty mode on/off  
- **Hide from List** — The `/ahide` command completely hides admins from the `/admins` list  
- **LuckPerms Integration** — Automatic group switching when on duty  
- **Multi-Level Rank System** — From Helper to Server Operator  
- **Localization** — Supports English and Russian languages  
- **Customizable** — Fully configurable messages and formats  

## Commands  

| Command | Description | Permissions |  
|---------|-------------|-------------|  
| `/duty` | Main duty command | `unsubduty.use` |  
| `/ahide` | Hide/show yourself in the admin list | `unsubduty.hide` |  
| `/admins` | Show the admin list | `unsubduty.view` |  
| `/duty set <player> <rank>` | Set a player's rank | `unsubduty.set` |  
| `/duty roles` | Show available ranks | `unsubduty.roles` |  

## Installation  

1. Download the latest version of the plugin  
2. Place the `.jar` file in your server's `plugins` folder  
3. Restart the server  
4. Configure ranks in `config.yml`  
5. Ensure LuckPerms is installed  

## Configuration  

### config.yml  
```yaml  
language: en  # or ru  

duty-levels:  
  moderator:  
    to-group: moderator  # LuckPerms group  
    rank-name: "&aModerator"  
    priority: 2  
    join-message: "&aYou started duty as Moderator"  
    leave-message: "&cYou left Moderator duty" 
  overseer:  
    to-group: OP  # Operator permission (/op)  
    rank-name: "&4OP"  
    ...
```  

### perms.yml  
```yaml  
  Notch: admin  
  Jeb: moderator  
```  

## Permissions  

- `unsubduty.use` — Use the `/duty` command  
- `unsubduty.hide` — Use `/ahide`  
- `unsubduty.view` — View the admin list  
- `unsubduty.set` — Set ranks for other players  
- `unsubduty.admin` — Full access to `/duty`  
- `unsubduty.see-hidden` — See hidden admins (alternative)  

## Requirements  

- Java 8 or higher  
- Bukkit/Spigot/Paper 1.8+  
- LuckPerms  

## Support  

For questions and suggestions, contact the plugin developer.
