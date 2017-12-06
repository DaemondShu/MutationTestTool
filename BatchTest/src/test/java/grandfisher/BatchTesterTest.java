package grandfisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.io.File;


public class BatchTesterTest
{

    @Test
    public void runTest() throws Exception
    {
        JsonNode objectNode=new ObjectMapper().createObjectNode();
        // 在这里测试你的模块

        String  mutationPath=".."+ File.separator+ "resources";
        String  originPath=".."+ File.separator+ "resources"+File.separator+"hw1_unittest_source";
        BatchTester batchTester= new BatchTester(mutationPath,originPath);
        System.out.println("----------------------------------------------------------------------");
        System.out.println(
                new ObjectMapper().writerWithDefaultPrettyPrinter()
                        .writeValueAsString(batchTester.runTest()));

    }

}