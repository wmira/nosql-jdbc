package co.miranext.nosql.criteria;

import co.miranext.nosql.DocumentMeta;

/**
 *
 */
public interface FieldCriterionTransformer {
    public FieldCriterion transform(final DocumentMeta meta,final FieldCriterion criterion);
    public FieldCriterion idFieldCriterion(final DocumentMeta meta,String value);
}