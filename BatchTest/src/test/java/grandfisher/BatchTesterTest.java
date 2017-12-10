package grandfisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.io.File;

import static grandfisher.CsvWriter.dealString;


public class BatchTesterTest
{
    public static JsonNode resultNode=new ObjectMapper().createObjectNode();

    @Test
    public void runTest() throws Exception
    {
        JsonNode objectNode=new ObjectMapper().createObjectNode();
        // 在这里测试你的模块

        String mutationPath=".."+ File.separator+ "resources";
        String originPath=".."+ File.separator+ "resources"+File.separator+"hw1_unittest_source";

//        boolean isReTest = true;
//

        BatchTester batchTester= new BatchTester(mutationPath,originPath, false);
        resultNode=batchTester.runTest();

        System.out.println(
                new ObjectMapper().writerWithDefaultPrettyPrinter()
                        .writeValueAsString(resultNode));
        System.out.println("----------------------------------------------------------------------");

    }


    @Test
    public void temp()
    {
        System.out.println(dealString("expected: <[]> but was: <[\\\"己卯年\\\",\\\"正月大\\\",\\\"十四\\\",\\\"兔\\\"]>"));
    }


}