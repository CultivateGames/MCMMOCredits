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

import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import games.cultivate.mcmmocredits.transaction.Transaction;
import games.cultivate.mcmmocredits.transaction.TransactionResult;
import games.cultivate.mcmmocredits.transaction.TransactionType;
import games.cultivate.mcmmocredits.user.User;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolverTest {
    private User sender;
    private User target;

    @BeforeEach
    void setUp() {
        this.sender = new User(new UUID(1, 1), "Executor", 1000, 10);
        this.target = new User(new UUID(2, 2), "Target", 500, 50);
    }

    @Test
    void ofUsers_SenderAndTarget_ContainsSenderAndTargetPlaceholders() {
        Resolver resolver = Resolver.ofUsers(this.sender, this.target);
        assertEquals(this.sender.username(), this.convert("<sender>", resolver));
        assertEquals(this.sender.uuid().toString(), this.convert("<sender_uuid>", resolver));
        assertEquals(this.sender.credits(), Integer.parseInt(this.convert("<sender_credits>", resolver)));
        assertEquals(this.sender.redeemed(), Integer.parseInt(this.convert("<sender_redeemed>", resolver)));
        assertEquals(this.target.username(), this.convert("<target>", resolver));
        assertEquals(this.target.uuid().toString(), this.convert("<target_uuid>", resolver));
        assertEquals(this.target.credits(), Integer.parseInt(this.convert("<target_credits>", resolver)));
        assertEquals(this.target.redeemed(), Integer.parseInt(this.convert("<target_redeemed>", resolver)));
    }

    @Test
    void ofUser_Sender_ContainsSenderPlaceholders() {
        Resolver resolver = Resolver.ofUser(this.sender);
        assertEquals(this.sender.username(), this.convert("<sender>", resolver));
        assertEquals(this.sender.uuid().toString(), this.convert("<sender_uuid>", resolver));
        assertEquals(this.sender.credits(), Integer.parseInt(this.convert("<sender_credits>", resolver)));
        assertEquals(this.sender.redeemed(), Integer.parseInt(this.convert("<sender_redeemed>", resolver)));
    }

    @Test
    void addUser_AddsUserToResolver() {
        Resolver resolver = new Resolver().addUser(this.sender, "silly");
        assertEquals(this.sender.username(), this.convert("<silly>", resolver));
        assertEquals(this.sender.uuid().toString(), this.convert("<silly_uuid>", resolver));
        assertEquals(this.sender.credits(), Integer.parseInt(this.convert("<silly_credits>", resolver)));
        assertEquals(this.sender.redeemed(), Integer.parseInt(this.convert("<silly_redeemed>", resolver)));
    }

    @Test
    void addAmount_AddsAmountToResolver() {
        Resolver resolver = new Resolver().addAmount(100);
        assertEquals(100, Integer.parseInt(this.convert("<amount>", resolver)));
    }

    @Test
    void addSkill_AddsSkillToResolver() {
        mcMMO.p = mock(mcMMO.class);
        GeneralConfig config = mock(GeneralConfig.class);
        when(mcMMO.p.getGeneralConfig()).thenReturn(config);
        when(config.getLevelCap(PrimarySkillType.HERBALISM)).thenReturn(1000);
        Resolver resolver = new Resolver().addSkill(PrimarySkillType.HERBALISM);
        assertEquals("Herbalism", this.convert("<skill>", resolver));
        assertEquals("1000", this.convert("<cap>", resolver));
    }

    @Test
    void addTag_AddsStringTagToResolver() {
        Resolver resolver = new Resolver().addTag("helloworld", "Hello world!");
        assertEquals("Hello world!", this.convert("<helloworld>", resolver));
    }

    @Test
    void addTag_AddsIntTagToResolver() {
        Resolver resolver = new Resolver().addTag("number", 10500);
        assertEquals("10500", this.convert("<number>", resolver));
    }

    @Test
    void toTagResolver_ValidTags_BuildsCorrectResolver() {
        String key1 = "hello";
        String key2 = "testkey";
        String val1 = "hello world!";
        String val2 = "123";
        Resolver resolver = new Resolver().addTag(key1, val1).addTag(key2, val2);
        TagResolver tagResolver = TagResolver.builder().tag(key1, Tag.preProcessParsed(val1)).tag(key2, Tag.preProcessParsed(val2)).build();
        assertEquals(tagResolver, resolver.toTagResolver());
    }

    @Test
    void ofTransaction_ValidRedeemTransaction_BuildsCorrectResolver() {
        mcMMO.p = mock(mcMMO.class);
        GeneralConfig config = mock(GeneralConfig.class);
        when(mcMMO.p.getGeneralConfig()).thenReturn(config);
        when(config.getLevelCap(PrimarySkillType.HERBALISM)).thenReturn(1000);
        Transaction transaction = Transaction.builder().users(this.sender, this.target).skill(PrimarySkillType.HERBALISM).amount(25).build();
        Resolver resolver = Resolver.ofTransaction(transaction);
        assertEquals(this.sender.username(), this.convert("<sender>", resolver));
        assertEquals(this.sender.uuid().toString(), this.convert("<sender_uuid>", resolver));
        assertEquals(this.sender.credits(), Integer.parseInt(this.convert("<sender_credits>", resolver)));
        assertEquals(this.sender.redeemed(), Integer.parseInt(this.convert("<sender_redeemed>", resolver)));
        assertEquals(this.target.username(), this.convert("<target>", resolver));
        assertEquals(this.target.uuid().toString(), this.convert("<target_uuid>", resolver));
        assertEquals(this.target.credits(), Integer.parseInt(this.convert("<target_credits>", resolver)));
        assertEquals(this.target.redeemed(), Integer.parseInt(this.convert("<target_redeemed>", resolver)));
        assertEquals(25, Integer.parseInt(this.convert("<amount>", resolver)));
        assertEquals("Herbalism", this.convert("<skill>", resolver));
        assertEquals("1000", this.convert("<cap>", resolver));
    }

    @Test
    void ofTransactionResult_ValidTransactionResult_BuildsCorrectResolver() {
        Transaction transaction = Transaction.builder().users(this.sender, this.target).amount(25).type(TransactionType.SET).build();
        TransactionResult result = TransactionResult.of(transaction, this.sender, this.target);
        Resolver resolver = Resolver.ofTransactionResult(result);
        assertEquals(this.sender.username(), this.convert("<sender>", resolver));
        assertEquals(this.sender.uuid().toString(), this.convert("<sender_uuid>", resolver));
        assertEquals(this.sender.credits(), Integer.parseInt(this.convert("<sender_credits>", resolver)));
        assertEquals(this.sender.redeemed(), Integer.parseInt(this.convert("<sender_redeemed>", resolver)));
        assertEquals(this.target.username(), this.convert("<target>", resolver));
        assertEquals(this.target.uuid().toString(), this.convert("<target_uuid>", resolver));
        assertEquals(this.target.credits(), Integer.parseInt(this.convert("<target_credits>", resolver)));
        assertEquals(this.target.redeemed(), Integer.parseInt(this.convert("<target_redeemed>", resolver)));
        assertEquals(25, Integer.parseInt(this.convert("<amount>", resolver)));
    }

    private String convert(final String input, final Resolver resolver) {
        return PlainTextComponentSerializer.plainText().serialize(MiniMessage.miniMessage().deserialize(input, resolver.toTagResolver()));
    }
}
