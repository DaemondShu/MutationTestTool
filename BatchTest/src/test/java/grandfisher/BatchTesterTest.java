package grandfisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.io.File;


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

        BatchTester batchTester= new BatchTester(mutationPath,originPath);
        resultNode=batchTester.runTest();


        System.out.println(
                new ObjectMapper().writerWithDefaultPrettyPrinter()
                        .writeValueAsString(resultNode));
        System.out.println("----------------------------------------------------------------------");

        String[][] csvMsg={{".."+ File.separator+ "BatchTest"+File.separator+"getLunarDateInfo.csv","getLunarDateInfo"},
                {".."+ File.separator+ "BatchTest"+File.separator+"getDayNum.csv","getDayNum"},
                {".."+ File.separator+ "BatchTest"+File.separator+"getNextDateInfo.csv","getNextDateInfo"},
                {".."+ File.separator+ "BatchTest"+File.separator+"vaildDate","vaildDate"}};
        CsvWriter csvWriter=new CsvWriter();
        for (int i=0;i<4;i++) {
            csvWriter.csvCreate(csvMsg[i][0],csvMsg[i][1]);
        }
        System.out.println("----------------------------------------------------------------------");

    }


}