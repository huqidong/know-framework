package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.FieldNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.StringNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ValueNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.GeoBbox;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

import java.util.HashSet;
import java.util.Set;

public class GeoBboxParser extends DslParser {
    private static final Set<String> KeyWords = new HashSet<>();

    static {
        KeyWords.add("type");
    }

    public GeoBboxParser(ParserType type) {
        super(type);
    }


    @Override
    public KeyWord parse(String name, Object root) throws Exception {
        GeoBbox node = new GeoBbox(name);
        NodeMap nm = new NodeMap();

        JSONObject obj = (JSONObject) root;

        boolean haveField = false;
        for (String key : obj.keySet()) {
            if (KeyWords.contains(key.toLowerCase())) {
                nm.m.put(new StringNode(key), ValueNode.getValueNode(obj.get(key)));
            } else {
                if (haveField) {
                    throw new Exception("wrong geo, json:" + root);
                }
                haveField = true;

                nm.m.put(new FieldNode(key), ValueNode.getValueNode(obj.get(key)));
            }
        }

        node.n = nm;
        return node;
    }
}
