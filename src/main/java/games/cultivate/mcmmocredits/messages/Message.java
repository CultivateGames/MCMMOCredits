//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package games.cultivate.mcmmocredits.messages;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import games.cultivate.mcmmocredits.user.CommandSource;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Message {
    private static final Component NO_ITALICS = Component.empty().decoration(TextDecoration.ITALIC, false);
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final String SENDER_PREFIX = "sender";
    private static final String TARGET_PREFIX = "target";
    private final Map<String, TagResolver.Single> placeholders;
    private String content;

    private Message(final String content) {
        this.content = content;
        this.placeholders = new HashMap<>();
    }

    public static Message of(final String content) {
        return new Message(content);
    }

    public void send(final CommandSource sender) {
        this.send(sender.stack().getSender());
    }

    public void send(final CommandSender sender) {
        sender.sendMessage(this.parseFor(sender));
    }

    public Message addUser(final CommandSource sender) {
        return this.addPair(sender.data(), sender.data());
    }

    public Message addUser(final CommandSourceData sender) {
        return this.addPair(sender, sender);
    }

    public Message addPair(final CommandSourceData sender, final CommandSourceData target) {
        return this.addSender(sender).addTarget(target);
    }

    public Message addTarget(final CommandSourceData data) {
        return this.addUserData(data, TARGET_PREFIX);
    }

    public Message addSender(final CommandSourceData sender) {
        return this.addUserData(sender, SENDER_PREFIX);
    }

    public Message addAmount(final int amount) {
        return this.addTag("amount", amount);
    }

    public Message addSkill(final PrimarySkillType skill) {
        var name = this.toTag("skill", skill.name().charAt(0) + skill.name().substring(1).toLowerCase(Locale.ROOT));
        var cap = this.toTag("cap", mcMMO.p.getGeneralConfig().getLevelCap(skill));
        this.placeholders.putAll(Map.of(name.key(), name, cap.key(), cap));
        return this;
    }

    public Message addPage(final Integer page) {
        return this.addTag("page", page == null ? 0 : page);
    }

    public Message addRank(final int rank) {
        return this.addTag("rank", rank);
    }

    public Component parseFor(final CommandSender sender) {
        if (sender instanceof Player p && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.content = PlaceholderAPI.setPlaceholders(p, this.content);
        }
        TagResolver resolver = TagResolver.resolver(this.placeholders.values());
        return NO_ITALICS.append(MINI_MESSAGE.deserialize(this.content, resolver));
    }

    private <T> Message addTag(final String key, final T value) {
        this.placeholders.put(key, this.toTag(key, value));
        return this;
    }

    private Message addUserData(final CommandSourceData data, final String prefix) {
        this.placeholders.putAll(data.resolvers(prefix));
        return this;
    }

    private <T> TagResolver.Single toTag(final String key, final T value) {
        return Placeholder.parsed(key.toLowerCase(Locale.ROOT), value.toString());
    }
}
