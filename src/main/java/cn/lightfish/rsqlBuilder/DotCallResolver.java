package cn.lightfish.rsqlBuilder;

import cn.lightfish.describer.CallExpr;
import cn.lightfish.describer.Node;
import cn.lightfish.describer.ParenthesesExpr;

import java.util.ArrayList;
import java.util.List;

public class DotCallResolver extends CopyNodeVisitor {

    public DotCallResolver() {
    }

    @Override
    public void visit(CallExpr call) {
        String name = call.getName();
        List<Node> args = call.getArgs().getExprs();
        if ("DOT".equalsIgnoreCase(name) && args.size() == 2) {
            Node m = args.get(1);
            if (m instanceof CallExpr) {
                List<Node> exprs1 = ((CallExpr) m).getArgs().getExprs();
                ArrayList<Node> objects = new ArrayList<>();
                objects.add(0, args.get(0));
                objects.addAll(exprs1);
                CallExpr callExpr = new CallExpr(((CallExpr) m).getName(), new ParenthesesExpr(objects));
                callExpr.accept(this);
                return;
            }
        }
        super.visit(call);
    }

    @Override
    public void endVisit(CallExpr call) {
        List<Node> exprs = call.getArgs().getExprs();
        if ("DOT".equals(call.getName().toUpperCase())) {
            if (exprs.size() == 2 && (exprs.get(1) instanceof CallExpr)) {
                return;
            }
        }
        super.endVisit(call);
    }

}