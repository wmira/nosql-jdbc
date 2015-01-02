package co.miranext.nosql.query;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SQLAlias {

    public final static char START = (char)97; //a;
    public final static char END = (char)122;

    private char start = START;

    private Map<String,String> alias;

    public SQLAlias() {
        alias = new HashMap<String,String>();
    }

    public String addAliasFor(final String table) {
        alias.put(table,Character.valueOf(start++).toString());
        return alias.get(table);
    }

    public String aliasFor(final String table) {
        return this.alias.get(table);
    }
}
