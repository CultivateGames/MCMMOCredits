//
// MIT License
//
// Copyright (c) 2023 Cultivate Games
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
package games.cultivate.mcmmocredits.placeholders;

import games.cultivate.mcmmocredits.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

//TODO: test ofRedemption method.
class ResolverTest {
    private static final MiniMessage PARSER = MiniMessage.miniMessage();
    private final UUID senderUUID = new UUID(1, 1);
    private final String senderUsername = "Sender";
    private final int senderCredits = 100;
    private final int senderRedeemed = 50;
    private final UUID targetUUID = new UUID(2, 2);
    private final String targetUsername = "Target";
    private final int targetCredits = 200;
    private final int targetRedeemed = 75;
    private User sender;
    private User target;

    private static String convert(final String input, final TagResolver resolver) {
        Component comp = PARSER.deserialize(input, resolver);
        return PlainTextComponentSerializer.plainText().serialize(comp);
    }

    @BeforeEach
    void setUp() {
        this.sender = new User(this.senderUUID, this.senderUsername, this.senderCredits, this.senderRedeemed);
        this.target = new User(this.targetUUID, this.targetUsername, this.targetCredits, this.targetRedeemed);
    }

    @Test
    void ofUsers_SenderAndTarget_ContainsSenderAndTargetPlaceholders() {
        //Act
        Resolver resolver = Resolver.ofUsers(this.sender, this.target);
        TagResolver tagResolver = resolver.toTagResolver();

        //Assert
        assertEquals(this.senderUsername, convert("<sender>", tagResolver));
        assertEquals(this.senderUUID.toString(), convert("<sender_uuid>", tagResolver));
        assertEquals(String.valueOf(this.senderCredits), convert("<sender_credits>", tagResolver));
        assertEquals(String.valueOf(this.senderRedeemed), convert("<sender_redeemed>", tagResolver));
        assertEquals(this.targetUsername, convert("<target>", tagResolver));
        assertEquals(this.targetUUID.toString(), convert("<target_uuid>", tagResolver));
        assertEquals(String.valueOf(this.targetCredits), convert("<target_credits>", tagResolver));
        assertEquals(String.valueOf(this.targetRedeemed), convert("<target_redeemed>", tagResolver));
    }

    @Test
    void ofUser_Sender_ContainsSenderPlaceholders() {
        //Act
        Resolver resolver = Resolver.ofUser(this.sender);
        TagResolver tagResolver = resolver.toTagResolver();

        //Assert
        assertEquals(this.senderUsername, convert("<sender>", tagResolver));
        assertEquals(this.senderUUID.toString(), convert("<sender_uuid>", tagResolver));
        assertEquals(String.valueOf(this.senderCredits), convert("<sender_credits>", tagResolver));
        assertEquals(String.valueOf(this.senderRedeemed), convert("<sender_redeemed>", tagResolver));
    }

    @Test
    void ofTransaction_SenderAndTargetAndAmount_ContainsSenderAndTargetPlaceholdersWithAmount() {
        //Arrange
        int amount = 25;

        //Act
        Resolver resolver = Resolver.ofTransaction(this.sender, this.target, amount);
        TagResolver tagResolver = resolver.toTagResolver();

        //Assert
        assertEquals(this.senderUsername, convert("<sender>", tagResolver));
        assertEquals(this.senderUUID.toString(), convert("<sender_uuid>", tagResolver));
        assertEquals(String.valueOf(this.senderCredits), convert("<sender_credits>", tagResolver));
        assertEquals(String.valueOf(this.senderRedeemed), convert("<sender_redeemed>", tagResolver));
        assertEquals(String.valueOf(amount), convert("<amount>", tagResolver));
        assertEquals(this.targetUsername, convert("<target>", tagResolver));
        assertEquals(this.targetUUID.toString(), convert("<target_uuid>", tagResolver));
        assertEquals(String.valueOf(this.targetCredits), convert("<target_credits>", tagResolver));
        assertEquals(String.valueOf(this.targetRedeemed), convert("<target_redeemed>", tagResolver));
    }
}
