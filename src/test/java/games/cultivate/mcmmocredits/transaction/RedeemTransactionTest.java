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

import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.Console;
import games.cultivate.mcmmocredits.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedeemTransactionTest {
    private final CommandExecutor executor = Console.INSTANCE;
    private final UUID uuid = new UUID(0, 1);
    private User target;
    @Mock
    private PlayerProfile mockProfile;
    @Mock
    private Player mockPlayer;
    @Mock
    private MockedStatic<Bukkit> mockBukkit;
    @Mock
    private MockedStatic<UserManager> mockUser;
    @Mock
    private McMMOPlayer mockMCMMOPlayer;
    @Mock
    private mcMMO mockMCMMO;
    @Mock
    private GeneralConfig mockConfig;

    @BeforeEach
    void setUp() {
        this.target = new User(this.uuid, "tester1", 1000, 100);
        this.mockBukkit.when(() -> Bukkit.getPlayer(this.uuid)).thenReturn(this.mockPlayer);
        this.mockUser.when(() -> UserManager.getPlayer(this.mockPlayer)).thenReturn(this.mockMCMMOPlayer);
        mcMMO.p = this.mockMCMMO;
    }

    @Test
    void execute_ValidUser_TransactionApplied() {
        when(this.mockMCMMOPlayer.getProfile()).thenReturn(this.mockProfile);
        Transaction herbalism = Transaction.builder().self(this.target).skill(PrimarySkillType.HERBALISM).amount(100);
        TransactionResult result = herbalism.execute();
        assertEquals(900, result.target().credits());
        verify(this.mockProfile).addLevels(PrimarySkillType.HERBALISM, 100);
        verify(this.mockProfile).save(true);
    }

    @Test
    void execute_ValidUsers_TransactionApplied() {
        when(this.mockMCMMOPlayer.getProfile()).thenReturn(this.mockProfile);
        Transaction herbalism = Transaction.builder().users(this.executor, this.target).skill(PrimarySkillType.HERBALISM).amount(100);
        TransactionResult result = herbalism.execute();
        assertEquals(900, result.target().credits());
        verify(this.mockProfile).addLevels(PrimarySkillType.HERBALISM, 100);
        verify(this.mockProfile).save(true);
    }

    @Test
    void executable_ValidTransaction_ReturnsNoFailure() {
        when(this.mockMCMMOPlayer.getProfile()).thenReturn(this.mockProfile);
        when(this.mockProfile.isLoaded()).thenReturn(true);
        when(mcMMO.p.getGeneralConfig()).thenReturn(this.mockConfig);
        when(this.mockConfig.getLevelCap(PrimarySkillType.HERBALISM)).thenReturn(10000);
        Transaction herbalism = Transaction.builder().users(this.executor, this.target).skill(PrimarySkillType.HERBALISM).amount(100);
        assertEquals(Optional.empty(), herbalism.isExecutable());
    }

    @Test
    void executable_NotEnoughCredits_ReturnsFailure() {
        Transaction tooManyCredits = Transaction.builder().users(this.executor, this.target).skill(PrimarySkillType.HERBALISM).amount(1100);
        assertEquals(Optional.of("not-enough-credits"), tooManyCredits.isExecutable());
    }

    @Test
    void executable_ProfileNotLoaded_ReturnsFailure() {
        when(this.mockMCMMOPlayer.getProfile()).thenReturn(this.mockProfile);
        Transaction profileNotLoaded = Transaction.builder().users(this.executor, this.target).skill(PrimarySkillType.HERBALISM).amount(100);
        assertEquals(Optional.of("mcmmo-profile-fail"), profileNotLoaded.isExecutable());
    }

    @Test
    void executable_SkillLevelCap_ReturnsFailure() {
        when(this.mockMCMMOPlayer.getProfile()).thenReturn(this.mockProfile);
        when(this.mockProfile.isLoaded()).thenReturn(true);
        when(mcMMO.p.getGeneralConfig()).thenReturn(this.mockConfig);
        when(this.mockConfig.getLevelCap(PrimarySkillType.HERBALISM)).thenReturn(99);
        Transaction profileNotLoaded = Transaction.builder().users(this.executor, this.target).skill(PrimarySkillType.HERBALISM).amount(100);
        assertEquals(Optional.of("mcmmo-skill-cap"), profileNotLoaded.isExecutable());
    }
}
