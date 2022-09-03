# MCMMOCredits
A modern mcMMO Credits plugin.

## Requirements
This plugin currently requires a Minecraft server with the following installed: Java 17, 1.19.2 Paper (and forks), mcMMO.

## Commands
```<> = required argument, [] = optional argument, Command Alias: /mcmmocredits```
| Command | Permission | Description |
| --- | --- | --- |
| ```/credits balance``` | mcmmocredits.balance.self | Check your own mcMMO Credit balance. | 
| ```/credits balance [player]``` | mcmmocredits.balance.other | Check mcMMO Credit balance of another user. |
| ```/credits add/set/take <amount>``` | mcmmocredits.modify.self | Modify your own mcMMO Credit balance. |
| ```/credits add/set/take <amount> <player> [--s]``` | mcmmocredits.modify.other | Modify someone else's mcMMO Credit balance. |
| ```/credits redeem <skill> <amount>``` | mcmmocredits.redeem.self | Redeem mcMMO Credits into a specific mcMMO Skill. |
| ```/credits redeem <skill> <amount> <player> [--s]``` | mcmmocredits.redeem.other | Redeem mcMMO Credits into a specific mcMMO Skill for another user (works like a sudo) |
| ```/credits menu main``` | mcmmocredits.menu.main | Opens the main menu. |
| ```/credits menu config``` | mcmmocredits.menu.config | Opens the main menu. |
| ```/credits menu redeem``` | mcmmocredits.menu.redeem | Opens a menu where players are able to exchange credits for levels in mcMMO skills. |
| ```/credits reload``` | mcmmocredits.admin.reload | Reloads all configuration files. |

Adding ```--s``` to the end of a command will make it silent where eligible, meaning only the sender of the command will receive feedback from execution. The recipient/target of the command will not see any messaging when this flag is used. It requires no additional permission.

## Quick Setup
1. Install mcMMO. The plugin will not enable if mcMMO is not detected. It has no tangible use without mcMMO.
2. Install this plugin. Start the server once and let the configuration files generate. Configuration files and the database will now generate.
3. Customize the configuration files. The plugin is usable without doing so, but there are vast configuration options for menus and messages. There are a few QOL settings as well, and multiple database types to choose from!
4. Assign basic permissions so users can interact with the plugin. The following would suffice:
```
All users: mcmmocredits.balance.self, mcmmocredits.balance.other, mcmmocredits.redeem.self
Admin: mcmmocredits.modify.self, mcmmocredits.modify.other, mcmmocredits.redeem.other, mcmmocredits.admin.reload 

If you have permission wildcards enabled, this setup would work:
All users: mcmmocredits.balance.*, mcmmocredits.redeem.self
Admin: mcmmocredits.*
```
At this point, setup is complete. Users are able to check their own balance and redeem their credits, mods are able to check other people's credit balance and help them redeem credits, and admins are able to manage the plugin with ease.

## Storage
The plugin can currently store player data using 3 database types: SQLite, MySQL, and JSON.

**JSON is currently not recommended. The implementation has performance slowdowns with large datasets.**

## Placeholders

### PlaceholderAPI
All messages are able to parse external PlaceholderAPI placeholders. They will be parsed for the recipient of the message.
%mcmmocredits_credits%: Returns the amount of mcMMO Credits a user currently has.

### Local Placeholders
If the information is available, then messages can be parsed for the following information:
<sender>: Name of the command sender/executor.
<target>: Name of the command target. If sender and target are the same, then either set of tags will work.
<sender_credits>: Amount of credits the command sender/executor currently has.
<target_credits>: Amount of credits the command recipient/target currently has.
<skill>: Name of affected mcMMO skill, formatted. Ex. Acrobatics
<cap>: Level cap of affected mcMMO skill
<amount>: Amount of credits used within a transaction. This may be the amount of credits added to a balance, or the amount of credits redeemed into a mcMMO skill.

## Hocon Configuration
Basics about configuring within Hocon can be found here: https://docs.spongepowered.org/stable/en/server/getting-started/configuration/hocon.html

Configuration files can be found here: https://github.com/CultivateGames/MCMMOCredits/tree/master/src/main/resources. They are not used to generate the config currently.

#### Message Customization
This plugin uses MiniMessage to parse messages. This means all messages within the plugin can support some enhanced functionalities, which can be found here: https://docs.adventure.kyori.net/minimessage#the-components

You can also preview any Mini Message configuration by going here: https://webui.adventure.kyori.net/

## Why is there no Spigot support or x.xx version support?
A majority of Minecraft servers use Paper. One external library requires Paper, and some methods use Paper-exclusive API. This does not warrant a multi-platform release or special adjustment at this time.
