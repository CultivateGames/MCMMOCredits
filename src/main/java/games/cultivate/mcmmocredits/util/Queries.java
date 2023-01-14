package games.cultivate.mcmmocredits.util;

import org.jdbi.v3.core.locator.ClasspathSqlLocator;

/**
 * Utility class to load locally saved SQL queries.
 */
public class Queries {
    //https://github.com/broccolai/tickets/blob/rewrite/everything/core/src/main/java/love/broccolai/tickets/core/utilities/QueriesLocator.java
    private final ClasspathSqlLocator locator = ClasspathSqlLocator.create();

    public String query(final String name) {
        return this.locator.locate("queries/" + name);
    }
}
