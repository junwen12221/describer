package cn.lightfish.describer;

import cn.lightfish.wu.BaseQuery;
import cn.lightfish.wu.ast.base.Schema;
import org.junit.Test;

import java.io.IOException;

public class AstSpec extends BaseQuery {
    @Test
    public void selectWithoutFrom() throws IOException {
        Schema select = select(empty(), literal(1));
        System.out.println(select);
    }



}