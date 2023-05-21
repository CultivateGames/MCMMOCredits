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
 * Represents a Data Converter.
 */
public interface Converter {
    /**
     * Loads all required user data for conversion.
     *
     * @return Returns true if successful, false otherwise.
     */
    boolean load();

    /**
     * Converts user data between sources. May include writing to another database.
     * Flat file conversions will have data flushed to disk. MySQL will use default behavior.
     *
     * @return Returns true if successful, false otherwise. Does not guarantee data equality.
     */
    boolean convert();

    /**
     * Verifies that user data conversion was successful by checking if data is present in destination database.
     *
     * @return Returns true if all previous data can be found in the new data source, false otherwise.
     */
    boolean verify();

    /**
     * Runs the data conversion process.
     *
     * @param logger The logger used to log current status of the converter.
     */
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
    }
}
