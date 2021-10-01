package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.logic;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.logic.And;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.logic.Or;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeList;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

public class OrParser extends DslParser {

    public OrParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Or node = new Or(name);
        node.n = NodeList.toNodeList(parserType, (JSON) obj, false);
        return node;
    }
}
