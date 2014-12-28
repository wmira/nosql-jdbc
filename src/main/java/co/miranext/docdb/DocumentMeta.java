package co.miranext.docdb;

/**
 * Information about the document
 */
public class DocumentMeta {

    public final static String DEFAULT_COLUMN = "data";
    public final static String DEFAULT_ID = "id";
    public final static String DEFAULT_EXTRAS[] = new String[]{};

    private String tableName;
    private String columnName;
    private String idField;
    private String[] extras;

    /**
     *
     */
    public DocumentMeta(final String tableName,final String columnName,final String idField, final String[] extras) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.idField = idField;
        this.extras = extras;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getIdField() {
        return idField;
    }

    public String[] getExtras() {
        return extras;
    }

    /**
     *
     *
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> DocumentMeta fromAnnotation(Class<T> cls) {

        Document document = cls.getAnnotation(Document.class);

        if ( document == null ) {
            return null;
        }

        return new DocumentMeta(document.table(),document.column(),document.id(),document.extras());

    }


    /**
     *
     * @param tableName
     * @param <T>
     * @return
     */
    public static <T> DocumentMeta fromDefault(final String tableName) {
        return new DocumentMeta(tableName,DEFAULT_COLUMN,DEFAULT_ID,DEFAULT_EXTRAS);

    }

}
