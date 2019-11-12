package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import cn.lightfish.wu.ast.base.Identifier;
import cn.lightfish.wu.ast.base.Node;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.apache.calcite.util.Pair;

import java.util.List;


@Getter
public class AggregateCall extends Node {
    private final boolean distinct;
    private final boolean approximate;
    private final boolean ignoreNulls;
    private final Node filter; // may be null
    private final String alias; // may be null
    private final ImmutableList<Node> operands; // may be empty, never null
    private final List<Pair<Identifier, Direction>> orderKeys; // may be empty, never null

    public AggregateCall(Op op, boolean distinct, boolean approximate, boolean ignoreNulls, Node filter, String alias, ImmutableList<Node> operands, List<Pair<Identifier, Direction>> orderKeys) {
        super(op);
        this.distinct = distinct;
        this.approximate = approximate;
        this.ignoreNulls = ignoreNulls;
        this.filter = filter;
        this.alias = alias;
        this.operands = operands;
        this.orderKeys = orderKeys;
    }

    AggregateCall filter(Node condition) {
        return new AggregateCall(op, distinct, approximate, ignoreNulls, condition, alias, operands, orderKeys);
    }

    /**
     * Returns a copy of this AggCall that sorts its input values by
     * {@code orderKeys} before aggregating, as in SQL's {@code WITHIN GROUP}
     * clause.
     */
    AggregateCall sort(List<Pair<Identifier, Direction>> orderKeys) {
        return new AggregateCall(op, distinct, approximate, ignoreNulls, filter, alias, operands, orderKeys);
    }

    /**
     * Returns a copy of this AggCall that may return approximate results
     * if {@code approximate} is true.
     */
    AggregateCall approximate(boolean approximate) {
        return new AggregateCall(op, distinct, approximate, ignoreNulls, filter, alias, operands, orderKeys);
    }

    /**
     * Returns a copy of this AggCall that ignores nulls.
     */
    AggregateCall ignoreNulls(boolean ignoreNulls) {
        return new AggregateCall(op, distinct, approximate, ignoreNulls, filter, alias, operands, orderKeys);
    }

    /**
     * Returns a copy of this AggCall with a given alias.
     */
    AggregateCall as(String alias) {
        return new AggregateCall(op, distinct, approximate, ignoreNulls, filter, alias, operands, orderKeys);
    }

    /**
     * Returns a copy of this AggCall that is optionally distinct.
     */
    AggregateCall distinct(boolean distinct) {
        return new AggregateCall(op, distinct, approximate, ignoreNulls, filter, alias, operands, orderKeys);
    }

    /**
     * Returns a copy of this AggCall that is distinct.
     */
    AggregateCall distinct() {
        return new AggregateCall(op, true, approximate, ignoreNulls, filter, alias, operands, orderKeys);
    }
}