package co.miranext.nosql.criteria;

/**
 *
 */
public enum CriterionOperator {
    EQ("="),GT(">"),LT("<"),GTE(">="),LTE("<="),LIKE("%"),CONTAINS("%"),ENDSWITH("%"),STARTSWITH("%");

    private final String operator;

    CriterionOperator(final String operator) {
        this.operator = operator;
    }
    public String operator() {
        return this.operator;
    }

}
