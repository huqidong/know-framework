package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.root;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ObjectNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.Title;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

/**
 * @author D10865
 * 解析title关键字
 * {"timeFieldName":"logTime","title":"heima_tcp.middleware_carreramessage.heima*","fields":"[]"}
 */
public class TitleParser extends DslParser {

    public TitleParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Title node = new Title(name);
        node.n = new ObjectNode(obj);

        return node;
    }

}
