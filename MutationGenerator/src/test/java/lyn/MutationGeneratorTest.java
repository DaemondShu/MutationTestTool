package lyn;

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
        config.put("MutationSize",5);
        config.put("MutationMethodNum",2);
        MutationGenerator generator=new MutationGenerator("../resources/hw1_unittest_source","../resources/hw1_unittest_mutation",config);
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(generator.runMutation()));

    }


    @Test
    public void testExecCommandTime()
    {
        ObjectNode config=new ObjectMapper().createObjectNode();
        config.put("MutationSize",5);
        config.put("MutationMethodNum",2);
        MutationGenerator generator=new MutationGenerator("../resources/hw1_unittest_source","../resources/hw1_unittest_mutation",config);

        generator.exeCommand("sleep 10");

    }

}