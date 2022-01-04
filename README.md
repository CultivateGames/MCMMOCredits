# MCMMOCredits
The most essential and basic MCMMO Credits plugin.

## Requirements
This plugin currently requires a Minecraft server with the following installed:
1. Java 17
2. 1.18.1 Paper or any of it's forks (Pufferfish).
3. MCMMO

#### Why is there no Spigot support or x.xx version support?
This plugin is developed for (mostly) personal use. I do not have the bandwidth to support Spigot and a plethora of versions. It also has a small bit of API usage that is Paper exclusive.

## Command Table
```<> = required argument, [] = optional argument```
| Command | Permission | Aliases | Description |
| --- | --- | --- | --- |
| ```/credits``` | mcmmocredits.check.self | N/A | Check your own MCMMO Credit balance. | 
| ```/credits [player]``` | mcmmocredits.check.other | N/A | Check MCMMO Credit balance of another user. |
| ```/modifycredits add <amount> <player>``` | mcmmocredits.modify.add | N/A | Add to a user's MCMMO Credit balance. |
| ```/modifycredits set <amount> <player>``` | mcmmocredits.modify.set | N/A | Set a user's MCMMO Credit balance to a new amount. |
| ```/modifycredits take <amount> <player>``` | mcmmocredits.modify.take | N/A | Take the specified amount from a user's MCMMO Credit balance. |
| ```/redeem <skill> <amount>``` | mcmmocredits.redeem.self | /redeemcredits, /rmc | Redeem MCMMO Credits into a specific MCMMO Skill. |
| ```/redeem <skill> <amount> <player>``` | mcmmocredits.redeem.other | /redeemcredits, /rmc | Redeem MCMMO Credits into a specific MCMMO Skill for another user (works like a sudo) |
| ```/creditsreload settings``` | mcmmocredits.admin.reload.settings | /creload | Reloads the settings.conf file. |
| ```/creditsreload messages``` | mcmmocredits.admin.reload.messages | /creload | Reloads the messages.conf file. |
| ```/creditsreload all``` | mcmmocredits.admin.reload.all | /creload | Reloads all configuration files. |

## Quick Setup
If you want to quickly setup this plugin, go through the following steps (assumes you know how plugins work, and that you are using LuckPerms + LP groups):
1. Install MCMMO. The plugin will not enable if MCMMO is not detected.
2. Install this plugin. Start the server once and let the configuration files generate. Stop the server.
3. Customize the configuration files. There are a few settings, but it is mostly messages that you should customize to align with your server's style. There is a section below about how to customize all settings/messages.
4. Start up the server again. After this, please setup some basic permissions. No permissions are given by default for this plugin.
```
(With wildcards disabled, assumes inheritance)
/lp group <defaultGroup> permission set mcmmocredits.check.self true
/lp group <defaultGroup> permission set mcmmocredits.redeem.self true
/lp group <modGroup> permission set mcmmocredits.check.other true
/lp group <modGroup> permission set mcmmocredits.redeem.other true
/lp group <adminGroup> permission set mcmmocredits.modify.add true
/lp group <adminGroup> permission set mcmmocredits.modify.take true
/lp group <adminGroup> permission set mcmmocredits.modify.set true
/lp group <adminGroup> permission set mcmmocredits.admin.reload.messages true
/lp group <adminGroup> permission set mcmmocredits.admin.reload.settings true
/lp group <adminGroup> permission set mcmmocredits.admin.reload.all true

With wildcards ENABLED, assign these permissions instead:
Mod: mcmmocredits.check.*, mcmmocredits.redeem.*
Admin: mcmmocredits.modify.*, mcmmocredits.admin.reload.*
```
At this point, setup is complete. Users are able to check their own balance and redeem their credits, mods are able to check other people's credit balance and help them redeem credits, and admins are able to manage the plugin with ease.

## Hocon Configuration
This plugin uses Hocon for configuration, because of the ability to include comments more seamlessly within the configuration.
Some basics about configuring within Hocon can be found here: https://docs.spongepowered.org/stable/en/server/getting-started/configuration/hocon.html

For plugin specific information, default configurations can be found at the following locations:

Messages: https://github.com/CultivateGames/MCMMOCredits/blob/master/src/main/resources/messages.conf

Settings: https://github.com/CultivateGames/MCMMOCredits/blob/master/src/main/resources/settings.conf

#### Message Customization
This plugin uses MiniMessage to parse messages. This means all messages within the plugin can support some enhanced functionalities, which can be found here: https://docs.adventure.kyori.net/minimessage#the-components

You can also preview any Mini Message configuration by going here: https://webui.adventure.kyori.net/
