package cn.lightfish.wu.ast.modify;

import cn.lightfish.wu.ast.base.Schema;

import java.util.List;

public class ModifyStatement {

    private final Schema source;
    private final List<RowModifer> tables;

    public ModifyStatement(Schema source, List<RowModifer> tables) {

        this.source = source;
        this.tables = tables;
    }
}