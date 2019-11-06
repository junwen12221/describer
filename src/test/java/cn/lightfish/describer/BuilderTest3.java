package cn.lightfish.describer;

import cn.lightfish.rsqlBuilder.Db1;
import cn.lightfish.rsqlBuilder.DotCallResolver;
import cn.lightfish.wu.AstTest;
import cn.lightfish.wu.ast.Schema;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.tools.RelRunner;
import org.apache.commons.io.FileUtils;
import org.codehaus.janino.JavaSourceClassLoader;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class BuilderTest3 {
    static final String complierDir = "codeSrc";
    static long id = 0;
    static String packageName = "Lj";
    static String importClass = " static cn.lightfish.wu.Ast.*";
    private static Path src;
    private final FrameworkConfig config;

    public BuilderTest3() {
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        rootSchema = rootSchema.add("db1", new ReflectiveSchema(new Db1()));
        this.config = Frameworks.newConfigBuilder()
                .defaultSchema(rootSchema).build();
        try {
            src = Paths.get(complierDir);
            FileUtils.forceDelete(src.toFile());
            Files.deleteIfExists(src);
            Files.createDirectories(src);
            src = Files.createDirectories(src.resolve(packageName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String nextName() {
        return "Lj" + id++;
    }

    private static Supplier complie(NodeVisitorImpl nodeVisitor, String className) throws Exception {
        List<String> importList = Arrays.asList(importClass, "java.util.function.Supplier");
        String returnText = nodeVisitor.getSb();
        String sb = MessageFormat.format("package {0};\n", packageName) +
                importList.stream().collect(Collectors.joining(";import ", "import ", ";\n")) +
                MessageFormat.format("public class {0} implements {1}", className, "    Supplier") +
                "{ \npublic Object get(){" + "return \n" + returnText + ";" + "\n}\n}";
        Files.write(src.resolve(className + ".java"), sb.getBytes());
        ClassLoader cl = new JavaSourceClassLoader(
                cn.lightfish.wu.Ast.class.getClassLoader(),  // parentClassLoader
                new File[]{Paths.get(complierDir).toFile()}, // optionalSourcePath
                null);
        Class aClass = cl.loadClass(packageName + "." + className);
        return (Supplier) aClass.newInstance();
    }

    @Test
    public void test1() throws Exception {
        String text = "(from(db1,travelrecord) as t)" +
                ".filter (t.id = 1 or a.id = 2)\n" +
                ".select(t.id)";
        Describer describer = new Describer(text);
        Node primary1 = describer.expression();
        Node primary = processDotCall(primary1);
        NodeVisitorImpl nodeVisitor = new NodeVisitorImpl();
        primary.accept(nodeVisitor);
        String className = nextName();
        Supplier o = complie(nodeVisitor, className);
        cn.lightfish.wu.ast.Schema schema = (Schema) o.get();
        AstTest astTest = new AstTest(RelBuilder.create(config));
        RelNode rexNode = astTest.complie(schema);
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:")) {
            final RelRunner runner = connection.unwrap(RelRunner.class);
            PreparedStatement prepare = runner.prepare(rexNode);
            ResultSet resultSet = prepare.executeQuery();
            System.out.println(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static Node processDotCall(Node primary) {
        DotCallResolver callResolver = new DotCallResolver();
        primary.accept(callResolver);
        primary = callResolver.getStack();
        return primary;
    }
}