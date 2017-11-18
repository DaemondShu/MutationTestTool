package grandfisher;

import org.junit.Test;


public class BatchTesterTest
{

    @Test
    public void runTest() throws Exception
    {
        // 在这里测试你的模块
        String  mutationPath="F:\\高级软件测试\\MutationTestTool\\hw1_unittest_manual";
        String  normalPath="F:\\高级软件测试\\MutationTestTool\\resources\\hw1_unittest_source";
        BatchTester batchTester= new BatchTester(mutationPath,normalPath);
        batchTester.runTest();

    }

}