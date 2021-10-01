package com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.logic;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.Node;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeList;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic.Visitor;

public class MustNot extends KeyWord {

    public Node n;

    public MustNot(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
