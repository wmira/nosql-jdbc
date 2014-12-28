package co.miranext.docdb;

import java.util.ArrayList;
import java.util.List;

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

    private ColumnExtra[] columnExtras;
    private ColumnExtra[] nonAutoColumnExtras;

    /**
     *
     */
    public DocumentMeta(final String tableName,final String columnName,final String idField, final String[] extras) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.idField = idField;
        this.extras = extras;

        List<ColumnExtra> columnExtras = new ArrayList<>();
        List<ColumnExtra> nonAutoColumnExtras = new ArrayList<>();
        List<String> extrasStr = new ArrayList<>();
        for ( String ext : extras ) {
            ColumnExtra extra = new ColumnExtra(ext);
            columnExtras.add(new ColumnExtra(ext));
            if ( !extra.isAuto() ) {
                nonAutoColumnExtras.add(extra);
            }
            extrasStr.add(extra.getColumn());
        }
        this.extras = extrasStr.toArray(new String[columnExtras.size()]);
        this.columnExtras = columnExtras.toArray(new ColumnExtra[columnExtras.size()]);
        this.nonAutoColumnExtras = columnExtras.toArray(new ColumnExtra[nonAutoColumnExtras.size()]);
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

    public ColumnExtra[] getColumnExtras() {
        return columnExtras;
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
