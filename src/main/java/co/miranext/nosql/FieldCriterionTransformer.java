package co.miranext.nosql;

/**
 *
 */
public interface FieldCriterionTransformer {
    public FieldCriterion transform(final DocumentMeta meta,final FieldCriterion criterion);
    public FieldCriterion idFieldCriterion(final DocumentMeta meta,String value);
}