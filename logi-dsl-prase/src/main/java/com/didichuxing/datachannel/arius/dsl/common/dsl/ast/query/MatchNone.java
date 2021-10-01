package com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.Node;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic.Visitor;

public class MatchNone extends KeyWord {

    public Node n;

    public MatchNone(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
