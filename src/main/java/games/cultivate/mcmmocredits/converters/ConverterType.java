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
package games.cultivate.mcmmocredits.converters;

import java.nio.file.Path;

/**
 * Namespace for Data Converters.
 * CSV: Data is sourced from a file named database.csv in the plugin's directory.
 * GUI_REDEEM_MCMMO: Data is sourced from GuiRedeemMCMMO user data.
 * INTERNAL: Data is sourced from another plugin database type (ex. SQLITE to H2)
 */
public enum ConverterType {
    CSV(Path.of("database.csv")),
    GUI_REDEEM_MCMMO(Path.of("GuiRedeemMCMMO", "playerdata")),
    INTERNAL(null),
    MORPH_REDEEM(Path.of("MorphRedeem", "PlayerData"));

    private final Path path;

    ConverterType(final Path path) {
        this.path = path;
    }

    /**
     * Returns the path.
     *
     * @return The path.
     */
    public Path path() {
        return this.path;
    }
}
