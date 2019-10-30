//package cn.lightfish.rsqlBuilder;
//
//import cn.lightfish.rsqlBuilder.schema.ColumnObject;
//import org.apache.calcite.adapter.java.ReflectiveSchema;
//import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
//import org.apache.calcite.plan.RelOptCluster;
//import org.apache.calcite.plan.RelOptSchema;
//import org.apache.calcite.plan.RelOptTable;
//import org.apache.calcite.plan.RelOptUtil;
//import org.apache.calcite.rel.RelNode;
//import org.apache.calcite.rel.core.RelFactories;
//import org.apache.calcite.rel.logical.LogicalTableScan;
//import org.apache.calcite.rel.type.RelDataTypeFactory;
//import org.apache.calcite.rel.type.RelDataTypeSystem;
//import org.apache.calcite.rex.RexBuilder;
//import org.apache.calcite.rex.RexNode;
//import org.apache.calcite.schema.SchemaPlus;
//import org.apache.calcite.sql.type.SqlTypeName;
//import org.apache.calcite.tools.FrameworkConfig;
//import org.apache.calcite.tools.Frameworks;
//import org.apache.calcite.tools.Planner;
//import org.apache.calcite.tools.RelBuilder;
//
//import java.util.Arrays;
//
//import static org.apache.calcite.util.Static.RESOURCE;
//
//public class RowExpressionBuilder {
//    private final RelBuilder relBuilder;
//    private final RexBuilder rexBuilder;
//    private final RelDataTypeFactory typeFactory;
//    private final RelOptCluster cluster;
//    private final RelOptSchema relOptSchema;
//    private final RelFactories.TableScanFactory scanFactory;
//
//    public static class FoodmartSchema {
//        public final SalesFact[] sales_fact_1997 = {
//                new SalesFact(100, 10),
//                new SalesFact(150, 20),
//        };
//    }
//    public RowExpressionBuilder() {
//       final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
//        rootSchema.add("foodmart",   new ReflectiveSchema(new FoodmartSchema()));
//        final FrameworkConfig config = Frameworks.newConfigBuilder()
//                .defaultSchema(rootSchema).build();
//        this.relBuilder = RelBuilder.create(config);
//        this.rexBuilder = relBuilder.getRexBuilder();
//        this.typeFactory = relBuilder.getTypeFactory();
//        this.cluster = relBuilder.getCluster();
//        this.relOptSchema = relBuilder.getRelOptSchema();
//        this.scanFactory = relBuilder.getScanFactory();
//        from("foodmart","sales_fact_1997")
//                .filter(equals(ColumnObject,1));
//        RelBuilder.create(config)
//                .push(LogicalTableScan.create())
//                .scan("foodmart","sales_fact_1997")
//                .c
//                .build();
//    }
//
//    private void filter() {
//
//    }
//
//    private RowExpressionBuilder from(String schemaName, String tableName) {
//        RelOptTable table = relOptSchema.getTableForMember(Arrays.asList(schemaName, tableName));
//        final RelNode scan = scanFactory.createScan(cluster, table);
//        return this;
//    }
//
//
//    public static void main(String[] args) {
//
//        RowExpressionBuilder builder = new RowExpressionBuilder();
//
//
//        System.out.println();
//    }
////
////    private RexNode makeLiteral(Object value) {
////        RexNode rexNode;
////        if (value == null) {
////            rexNode = builder.makeLiteral(
////                    null,
////                    typeFactory.createSqlType(SqlTypeName.NULL), true);
////        } else {
////            rexNode = builder.makeLiteral(value, typeFactory.createType(value.getClass()), true);
////        }
////        return rexNode;
////    }
//    public static class SalesFact {
//        public final int cust_id;
//        public final int prod_id;
//
//        public SalesFact(int cust_id, int prod_id) {
//            this.cust_id = cust_id;
//            this.prod_id = prod_id;
//        }
//
//        @Override public boolean equals(Object obj) {
//            return obj == this
//                    || obj instanceof SalesFact
//                    && cust_id == ((SalesFact) obj).cust_id
//                    && prod_id == ((SalesFact) obj).prod_id;
//        }
//    }
//}