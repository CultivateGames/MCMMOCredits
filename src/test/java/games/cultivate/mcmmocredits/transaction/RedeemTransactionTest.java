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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//TODO: determine if there's a better method to unit testing MCMMO code.
class RedeemTransactionTest {
    private final CommandExecutor executor = Console.INSTANCE;
    private final UUID uuid = new UUID(0, 1);
    private User target;
    private PlayerProfile profile;
    private Player player;
    private MockedStatic<Bukkit> mockBukkit;
    private MockedStatic<UserManager> mockUser;

    @BeforeEach
    void setUp() {
        this.target = new User(this.uuid, "tester1", 1000, 100);
        this.mockUser = mockStatic(UserManager.class);
        this.mockBukkit = mockStatic(Bukkit.class);
        this.player = mock(Player.class);
        McMMOPlayer mcMMOPlayer = mock(McMMOPlayer.class);
        this.profile = mock(PlayerProfile.class);
        this.mockBukkit.when(() -> Bukkit.getPlayer(this.uuid)).thenReturn(this.player);
        this.mockUser.when(() -> UserManager.getPlayer(player)).thenReturn(mcMMOPlayer);
        when(this.player.getUniqueId()).thenReturn(this.uuid);
        when(mcMMOPlayer.getProfile()).thenReturn(this.profile);
    }

    @AfterEach
    void tearDown() {
        this.mockUser.close();
        this.mockBukkit.close();
    }

    @Test
    void execute_ValidUser_TransactionApplied() {
        RedeemTransaction herbalism = RedeemTransaction.of(this.target, PrimarySkillType.HERBALISM, 100);
        TransactionResult result = herbalism.execute();
        assertEquals(900, result.target().credits());
        verify(this.profile).addLevels(PrimarySkillType.HERBALISM, 100);
        verify(this.profile).save(true);
    }

    @Test
    void execute_ValidUsers_TransactionApplied() {
        RedeemTransaction herbalism = RedeemTransaction.of(this.executor, this.target, PrimarySkillType.HERBALISM, 100);
        TransactionResult result = herbalism.execute();
        assertEquals(900, result.target().credits());
        verify(this.profile).addLevels(PrimarySkillType.HERBALISM, 100);
        verify(this.profile).save(true);
    }

    @Test
    void executable_ValidTransaction_ReturnsNoFailure() {
        when(this.profile.isLoaded()).thenReturn(true);
        mcMMO.p = mock(mcMMO.class);
        GeneralConfig config = mock(GeneralConfig.class);
        when(mcMMO.p.getGeneralConfig()).thenReturn(config);
        when(config.getLevelCap(PrimarySkillType.HERBALISM)).thenReturn(10000);
        RedeemTransaction herbalism = RedeemTransaction.of(this.executor, this.target, PrimarySkillType.HERBALISM, 100);
        assertEquals(Optional.empty(), herbalism.executable());
    }

    @Test
    void executable_NotEnoughCredits_ReturnsFailure() {
        RedeemTransaction tooManyCredits = RedeemTransaction.of(this.executor, this.target, PrimarySkillType.HERBALISM, 1100);
        assertEquals(Optional.of(FailureReason.NOT_ENOUGH_CREDITS), tooManyCredits.executable());
    }

    @Test
    void executable_ProfileNotLoaded_ReturnsFailure() {
        RedeemTransaction profileNotLoaded = RedeemTransaction.of(this.executor, this.target, PrimarySkillType.HERBALISM, 100);
        assertEquals(Optional.of(FailureReason.MCMMO_PROFILE_FAIL), profileNotLoaded.executable());
    }

    @Test
    void executable_SkillLevelCap_ReturnsFailure() {
        when(this.profile.isLoaded()).thenReturn(true);
        mcMMO.p = mock(mcMMO.class);
        GeneralConfig config = mock(GeneralConfig.class);
        when(mcMMO.p.getGeneralConfig()).thenReturn(config);
        when(config.getLevelCap(PrimarySkillType.HERBALISM)).thenReturn(99);
        RedeemTransaction profileNotLoaded = RedeemTransaction.of(this.executor, this.target, PrimarySkillType.HERBALISM, 100);
        assertEquals(Optional.of(FailureReason.MCMMO_SKILL_CAP), profileNotLoaded.executable());
    }
}
