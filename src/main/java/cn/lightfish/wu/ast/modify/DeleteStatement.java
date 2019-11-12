package cn.lightfish.wu.ast.modify;

import cn.lightfish.describer.Node;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class DeleteStatement {
    List<Node> sources;
}