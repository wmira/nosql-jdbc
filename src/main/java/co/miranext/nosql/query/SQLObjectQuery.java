package co.miranext.nosql.query;

import co.miranext.nosql.*;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.fields.FieldAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 *
 */
public class SQLObjectQuery<T> {
    public static String SQL_STMT_DELIM = " , ";
    public static String SQL_AND_DELIMITER = " AND ";

    private final Class<T> documentClass;
    private final DocumentMeta meta;

    //create alias helper
    private final SQLAlias alias;

    private final Map<String, FieldAccess> fields;
    private final Map<String,DocumentRefMeta> fieldRefs;

    private final Map<String,SQLObjectQuery> referenceObjectQuery = new HashMap<>();
    private final SQLColumnQuery columnQuery;

    private Map<String,SQLColumnQuery> refsColumnQuery = new HashMap<>();

    public SQLObjectQuery(final Class<T> documentClass) {
        this(documentClass,null,true);
    }
    /**
     *
     *
     * @param documentClass
     */
    public SQLObjectQuery(final Class<T> documentClass,final SQLAlias thealias,boolean traverse) {

        this.documentClass = documentClass;
        this.meta = DocumentMeta.fromAnnotation(this.documentClass);
        this.alias = thealias == null ? new SQLAlias() : thealias;


        //create some needed stuf
        //TODO: opportunity for caching here
        this.fields = BeanUtils.getFieldsFromObject(this.documentClass);
        this.fieldRefs = DocumentRefMeta.lookupDocumentMetaRefs(fields);


        this.alias.addAliasFor(this.meta.getTableName());
        this.columnQuery = new SQLColumnQuery(this,null,null);

        if ( traverse && this.fieldRefs != null && this.fieldRefs.size() > 0 ) {

            for ( String documentRefKey : this.fieldRefs.keySet() ) {
                DocumentRefMeta refMeta = this.fieldRefs.get(documentRefKey);
                //DocumentRef ref = refMeta.getDocumentRef();
                Class<T> cls = refMeta.document();
                SQLObjectQuery cobjectQuery = new SQLObjectQuery(cls,alias,false);
                referenceObjectQuery.put(documentRefKey,cobjectQuery);
                refsColumnQuery.put(documentRefKey,new SQLColumnQuery(cobjectQuery,refMeta,this));
            }
        }
    }

    public Map<String,DocumentRefMeta> getFieldRefs() {
        return this.fieldRefs;
    }

    public Map<String,SQLObjectQuery> getRefSQLObjectQuery() {
        return this.referenceObjectQuery;
    }

    public Map<String,FieldAccess> getObjectFields() {
        return this.fields;
    }
    /**
     *
     * @return
     */
    public SQLColumnQuery getColumnQuery() {
        return this.columnQuery;
    }
    public String toSQLSelectQuery() {
        return toSQLSelectQuery(null,null);
    }

    /**
     *
     *
     * @param criteria
     * @param transformer
     * @return
     */
    public String toSQLSelectQuery(final Criteria criteria,final FieldCriterionTransformer transformer) {

        boolean hasRefs = this.refsColumnQuery != null && this.refsColumnQuery.size() > 0;
        StringBuilder sb = new StringBuilder();

        List<String> fieldSelects = new ArrayList<>();
        fieldSelects.add(this.columnQuery.getFieldsQuery());

        if ( hasRefs ) {
            for ( String key : this.refsColumnQuery.keySet() ) {
                fieldSelects.add(this.refsColumnQuery.get(key).getFieldsQuery());
            }
        }

        sb.append("SELECT ");
        sb.append(SQLObjectQuery.join(fieldSelects.toArray(new String[fieldSelects.size()]), SQL_STMT_DELIM));

        sb.append(" FROM ");
        sb.append(columnQuery.toFromQuery());

        if ( hasRefs ) {
            for ( String key : this.refsColumnQuery.keySet() ) {
                sb.append(this.refsColumnQuery.get(key).getJoinQuery());
            }
        }

        if ( criteria != null ) {
            sb.append(" WHERE ");
            sb.append(toSQLCriteriaFilter(this.getDocumentAlias(),this.meta,criteria,transformer));
        }
        return sb.toString();
    }


    public DocumentMeta getMeta() {
        return meta;
    }

    public String getDocumentAlias() {
        return this.alias.aliasFor(meta.getTableName());
    }

    /**
     * Helper to create criteria filter
     *
     * @param alias
     * @param meta
     * @param criteria
     * @param transformer
     * @return
     */
    public final static String toSQLCriteriaFilter(final String alias,final DocumentMeta meta,final Criteria criteria,final FieldCriterionTransformer transformer) {
        List<String> strings = new ArrayList<String>();

        for ( Criterion criterion :criteria.getCriteria()  ) {
            Criterion toprocess = criterion;
            if ( transformer != null && toprocess instanceof FieldCriterion) {
                toprocess = transformer.transform(meta,(FieldCriterion)toprocess);
            }
            strings.add(toprocess.toSQLString(alias));
        }

        return SQLObjectQuery.join(strings.toArray(new String[strings.size()]),SQL_AND_DELIMITER);
    }

    public static String join(final String[] strings,String joinString) {

        StringBuilder sb = new StringBuilder();
        for ( int i=0; i < strings.length; i++ ) {
            sb.append(strings[i]);
            if ( i != strings.length -1 ) {
                sb.append(joinString);
            }

        }
        return sb.toString();

    }


}
