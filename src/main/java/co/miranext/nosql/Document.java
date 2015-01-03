package co.miranext.nosql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Document annotation
 *
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Document {

    /**
     * The table name where this is stored
     *
     * @return
     */
    String table() default "";

    /**
     * The column to use that is usually of type json
     *
     * @return
     */
    String column() default DocumentMeta.DEFAULT_COLUMN;

    /**
     * The key to use, read only
     *
     *
     * @return
     */
    String id() default DocumentMeta.DEFAULT_ID;

    /**
     * Extras are additional columns that will be set as part of the setting the document
     *
     * @return
     */
    String[] extras() default {};
}
