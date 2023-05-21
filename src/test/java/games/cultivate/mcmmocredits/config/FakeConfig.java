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
package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.converters.ConverterType;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import games.cultivate.mcmmocredits.serializers.ItemSerializer;
import games.cultivate.mcmmocredits.serializers.MenuSerializer;
import games.cultivate.mcmmocredits.ui.item.BaseItem;
import games.cultivate.mcmmocredits.ui.item.CommandItem;
import games.cultivate.mcmmocredits.ui.item.Item;
import games.cultivate.mcmmocredits.ui.menu.BaseMenu;
import games.cultivate.mcmmocredits.ui.menu.Menu;
import org.bukkit.Material;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory configuration used for testing.
 */
@SuppressWarnings({"FieldMayBeFinal, unused"})
@ConfigSerializable
final class FakeConfig extends BaseConfig {
    private final transient String configString = """
            """;
    private String prefix = "The message prefix!!";
    private String fakeMessage = "The actual message!";
    private boolean leaderboardEnabled = true;
    private boolean debug = false;
    private int leaderboardPageSize = 10;
    private DatabaseProperties database = DatabaseProperties.defaults();
    private ConverterType converterType = ConverterType.INTERNAL;
    private long retryDelay = 60000L;
    private Menu menu;

    public FakeConfig() {
        super();
        Map<String, Item> map = new HashMap<>();
        map.put("fill", BaseItem.of(Material.BLACK_STAINED_GLASS_PANE));
        map.put("navigation", CommandItem.of("credits menu main", BaseItem.of(Material.COMPASS)));
        this.menu = BaseMenu.of(map, "The menu title!", 54, false, true);
        this.setLoader(YamlConfigurationLoader.builder()
                .headerMode(HeaderMode.PRESET)
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .source(() -> new BufferedReader(new StringReader(this.configString)))
                .sink(() -> new BufferedWriter(new StringWriter()))
                .defaultOptions(opts -> opts.serializers(build -> build
                        .register(Item.class, ItemSerializer.INSTANCE)
                        .register(Menu.class, MenuSerializer.INSTANCE)))
                .build());
    }
}
