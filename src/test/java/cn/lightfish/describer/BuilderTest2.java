package cn.lightfish.describer;

import cn.lightfish.describer.literal.IdLiteral;
import cn.lightfish.rsqlBuilder.RexBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class BuilderTest2 {

    @Test
    public void id() throws IOException {
        Describer describer = new Describer("treavelrecord");
        Node primary = describer.primary();
        RexBuilder rexBuilder = new RexBuilder();
        primary.accept(rexBuilder);
        Assert.assertEquals(new IdLiteral("treavelrecord"), rexBuilder.getStack());
    }
}