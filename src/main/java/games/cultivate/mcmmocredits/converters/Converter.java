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

import org.slf4j.Logger;

/**
 * Interface to represent a data converter.
 */
public interface Converter {
    /**
     * Load or obtain all required elements for conversion here.
     *
     * @return True if loading process was successful.
     */
    boolean load();

    /**
     * Convert all data here. This may include action such as writing to another database.
     * Note: Flat file conversions will have data flushed to disk. MySQL will not.
     *
     * @return True if conversion process was successful. Does not guarantee data equality.
     */
    boolean convert();

    /**
     * Verify that the conversion was successful by checking data from previous and current source.
     *
     * @return True if all previous data can be found in the new data source.
     */
    boolean verify();

    /**
     * Shutdown any running processes or schedulers. Indicates completion of the conversion.
     */
    void disable();

    default void run(final Logger logger) {
        logger.warn("Data Converter enabled in configuration! Loading...");
        if (!this.load()) {
            logger.warn("Data Converter failed at the loading stage! Look for possible errors thrown in console!");
            return;
        }
        logger.info("Converter has loaded users from source successfully! Starting conversion, this may take some time.");
        if (!this.convert()) {
            logger.warn("Data Converter failed at the conversion stage! Look for possible errors thrown in console!");
            return;
        }
        logger.info("Users have been written to destination database. Starting to verify results...");
        if (!this.verify()) {
            logger.warn("Data Converter failed at the verification stage! Look for possible errors thrown in console!");
            return;
        }
        logger.info("Conversion has been verified! Disabling conversion...");
        this.disable();
    }
}
