package co.miranext.nosql;

import com.google.common.base.CaseFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Information about the document
 */
public class DocumentMeta {

    public final static String DEFAULT_COLUMN = "data";
    public final static String DEFAULT_ID = "id";
    public final static String DEFAULT_EXTRAS[] = new String[]{};

    private final String tableName;
    private final String columnName;
    private final String idField;
    private final String[] extras;

    private final ColumnExtra[] columnExtras;
    private final ColumnExtra[] nonAutoColumnExtras;

    /**
     *
     */
    public DocumentMeta(final String tableName,final String columnName,final String idField, final String[] extras) {


        this.tableName = tableName;
        this.columnName = columnName;
        this.idField = idField;

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
            throw new RuntimeException("Class: " + cls.getName() + " has no @Document annotation.");
        }
        String table = document.table();
        if ( table == null || "".equals(table.trim()) ) {
            String clsName = cls.getSimpleName();
            table = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,clsName);
        }

        return new DocumentMeta(table,document.column(),document.id(),document.extras());

    }

    /**
     *
     * @param tableName
     * @return
     */
    public static DocumentMeta fromDefault(final String tableName) {
        return new DocumentMeta(tableName,DEFAULT_COLUMN,DEFAULT_ID,DEFAULT_EXTRAS);

    }


    @Override
    public String toString() {
        return "DocumentMeta{" +
                "tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", idField='" + idField + '\'' +
                ", extras=" + Arrays.toString(extras) +
                '}';
    }
}
