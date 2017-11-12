package lyn;

import clover.com.google.gson.JsonObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class MutationGeneratorTest
{
    @Test
    public void runMutation() throws Exception
    {
        //在这里测试你的代码
        ObjectNode config=new ObjectMapper().createObjectNode();
        config.put("ClassName","NextDate").put("Line",73).put("Expression","yearNow == yearUpper");
        MutationGenerator generator=new MutationGenerator("../resources/hw1_unittest_source","../resources/hw1_unittest_mutation1",config);
        generator.runMutation();

    }

}