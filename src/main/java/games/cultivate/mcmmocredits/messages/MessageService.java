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
import games.cultivate.mcmmocredits.config.Captions;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import games.cultivate.mcmmocredits.user.UserService;
import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.caption.Caption;

public final class MessageService {
    private final Captions captions;
    private final UserService userService;

    @Inject
    public MessageService(final Captions captions, final UserService userService) {
        this.captions = captions;
        this.userService = userService;
    }

    /**
     * Returns the message located at the caption reference.
     *
     * @param caption The caption reference.
     * @return The message.
     */
    public Message get(final Caption caption) {
        return Message.of(this.captions.get(caption));
    }

    public Message forTransaction(final Caption caption, final CommandSourceData sender, final int amount) {
        return this.forTransaction(caption, sender, sender, amount);
    }

    public Message forTransaction(final Caption caption, final CommandSourceData sender, final CommandSourceData target, final int amount) {
        return this.get(caption).addPair(sender, target).addAmount(amount);
    }

    public Message forTransaction(final Caption caption, final CommandSourceData sender, final int amount, final PrimarySkillType skill) {
        return this.forTransaction(caption, sender, sender, amount, skill);
    }

    public Message forTransaction(final Caption caption, final CommandSourceData sender, final CommandSourceData target, final int amount, final PrimarySkillType skill) {
        return this.forTransaction(caption, sender, target, amount).addSkill(skill);
    }

    public void forward(final Message m, final CommandSourceData data) {
        this.userService.asPlayer(data).ifPresent(m::send);
    }

    public void send(final Caption caption, final CommandSender sender) {
        if (sender instanceof Player p) {
            this.userService.getUser(p.getUniqueId())
                    .map(csd -> this.get(caption).addUser(csd))
                    .ifPresent(x -> x.send(p));
            return;
        }
        this.get(caption).send(sender);
    }
}
