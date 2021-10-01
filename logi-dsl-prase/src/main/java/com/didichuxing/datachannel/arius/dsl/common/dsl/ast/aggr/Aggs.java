package com.didichuxing.datachannel.arius.dsl.common.dsl.ast.aggr;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic.Visitor;

public class Aggs extends KeyWord {

    public NodeMap m = new NodeMap();

    public Aggs(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }


}
