package com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.logic;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic.Visitor;

public class Bool extends KeyWord {
    public NodeMap m = new NodeMap();

    public Bool(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
