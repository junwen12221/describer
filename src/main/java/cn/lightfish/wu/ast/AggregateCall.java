package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import cn.lightfish.wu.ast.base.Node;
import cn.lightfish.wu.ast.base.NodeVisitor;
import cn.lightfish.wu.ast.base.OrderItem;
import lombok.Getter;

import java.util.List;


@Getter
public class AggregateCall extends Node {
    private final String function;
    private final Boolean distinct;
    private final Boolean approximate;
    private final Boolean ignoreNulls;
    private final Node filter; // may be null
    private final String alias; // may be null
    private final List<Node> operands; // may be empty, never null
    private final List<OrderItem> orderKeys; // may be empty, never null

    public AggregateCall(String function, String alias, List<Node> operands, Boolean distinct, Boolean approximate, Boolean ignoreNulls, Node filter, List<OrderItem> orderKeys) {
        super(Op.AggregateCall);
        this.function = function;
        this.distinct = distinct;
        this.approximate = approximate;
        this.ignoreNulls = ignoreNulls;
        this.filter = filter;
        this.alias = alias;
        this.operands = operands;
        this.orderKeys = orderKeys;
    }
    //
//    AggregateCall filter(Node condition) {
//        return new AggregateCall(distinct, approximate, ignoreNulls, condition, alias, operands, orderKeys);
//    }
//
//    /**
//     * Returns a copy of this AggCall that sorts its input values by
//     * {@code orderKeys} before aggregating, as in SQL's {@code WITHIN GROUP}
//     * clause.
//     */
//    AggregateCall sort(List<OrderItem> orderKeys) {
//        return new AggregateCall(distinct, approximate, ignoreNulls, filter, alias, operands, orderKeys);
//    }
//
//    /**
//     * Returns a copy of this AggCall that may return approximate results
//     * if {@code approximate} is true.
//     */
//    AggregateCall approximate(boolean approximate) {
//        return new AggregateCall(distinct, approximate, ignoreNulls, filter, alias, operands, orderKeys);
//    }
//
//    /**
//     * Returns a copy of this AggCall that ignores nulls.
//     */
//    AggregateCall ignoreNulls(boolean ignoreNulls) {
//        return new AggregateCall(distinct, approximate, ignoreNulls, filter, alias, operands, orderKeys);
//    }
//
//    /**
//     * Returns a copy of this AggCall with a given alias.
//     */
//    AggregateCall as(String alias) {
//        return new AggregateCall(distinct, approximate, ignoreNulls, filter, alias, operands, orderKeys);
//    }
//
//    /**
//     * Returns a copy of this AggCall that is optionally distinct.
//     */
//    AggregateCall distinct(boolean distinct) {
//        return new AggregateCall(distinct, approximate, ignoreNulls, filter, alias, operands, orderKeys);
//    }
//
//    /**
//     * Returns a copy of this AggCall that is distinct.
//     */
//    AggregateCall distinct() {
//        return new AggregateCall(true, approximate, ignoreNulls, filter, alias, operands, orderKeys);
//    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AggregateCall(");
        if (function != null) {
            stringBuilder.append("function='").append(function).append('\'');
        }
        if (distinct != null) {
            stringBuilder.append(", ").append("distinct=").append(distinct);
        }
        if (approximate != null) {
            stringBuilder.append(", ").append("approximate=").append(approximate).append(", ");
        }
        if (ignoreNulls != null) {
            stringBuilder.append(", ").append("ignoreNulls=").append(ignoreNulls);
        }
        if (filter != null) {
            stringBuilder.append(", ").append("filter=").append(filter);
        }
        if (alias != null) {
            stringBuilder.append(", ").append("alias='").append(alias).append('\'');
        }
        if (operands != null) {
            stringBuilder.append(", ").append("operands=").append(operands);
        }
        if (orderKeys != null) {
            stringBuilder.append(", ").append("orderKeys=").append(orderKeys).append(')');
        }
        return stringBuilder.toString();
    }
}