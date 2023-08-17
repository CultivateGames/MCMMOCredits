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
package games.cultivate.mcmmocredits.transaction;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionBuilderTest {
    private TransactionBuilder builder;
    private User user;
    private User target;

    @BeforeEach
    void setUp() {
        this.target = new User(UUID.randomUUID(), "target1", 100, 1001);
        this.user = new User(UUID.randomUUID(), "user1", 500, 1000);
        this.builder = new TransactionBuilder(this.user, TransactionType.REDEEM, 100);
    }

    @Test
    void skill_ValidSkill_AddedToBuilder() {
        Transaction transaction = this.builder.skill(PrimarySkillType.HERBALISM).build();
        assertEquals(PrimarySkillType.HERBALISM, transaction instanceof RedeemTransaction rt ? rt.skill() : null);
    }

    @Test
    void targets_ValidTargets_AddedToBuilder() {
        Transaction transaction = this.builder.targets(List.of(this.target)).build();
        assertEquals(this.target, transaction.targets().get(0));
    }

    @Test
    void targets_SingleTarget_AddedToBuilder() {
        Transaction transaction = this.builder.targets(this.target).build();
        assertEquals(this.target, transaction.targets().get(0));
    }
}
