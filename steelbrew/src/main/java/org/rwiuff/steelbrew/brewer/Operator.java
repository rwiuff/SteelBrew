package org.rwiuff.steelbrew.brewer;

public enum Operator {
    eq,
    neq,
    gt,
    lt,
    geq,
    leq,
    and,
    or,
    not,
    ;

    @Override
    public String toString() {
        switch (this) {
            case eq:
                return "==";
            case neq:
                return "!=";
            case gt:
                return ">";
            case lt:
                return "<";
            case geq:
                return ">=";
            case leq:
                return "<=";
            case and:
                return "&&";
            case or:
                return "||";
            case not:
                return "!";
        }
        return null;
    }
}
