package daemond;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import grandfisher.BatchTester;
import lyn.MutationGenerator;

import java.io.*;

public class Commander
{
    @Parameter(names= {"-s", "--MutationSize"}, description = "每s个符号进行一次变异")
    public Integer MutationSize = 6;

    @Parameter(names= {"-n", "--MaxMethodNum"}, description = "单符号最大变异种数")
    public Integer MutationMethodNum = 2;

    @Parameter(names= {"-i", "--inputSourcePath"}, description = "源代码路径，同时包含测试数据")
    public String sourcePath = "resources/hw1_unittest_source";

    @Parameter(names= {"-o", "--outputMutationPath"}, description = "所有变异体输出路径,会调用rm指令清空resource以下含有mutation的目录,慎用")
    public String MutationPath = "resources/";

    @Parameter(names = {"-h", "--help"}, help = true)
    public boolean help;

    @Parameter(names = {"-r", "--role"}, description = "单独执行某一步, 1=变异体生成, 2=变异体测试, 3=报告生成")
    public Integer role = 0;

    static private void MutationGenerate(Commander config) throws Exception
    {
        ObjectNode genConfig=new ObjectMapper().createObjectNode();
        genConfig.put("MutationSize", config.MutationSize);
        genConfig.put("MutationMethodNum", config.MutationMethodNum);
        MutationGenerator generator = new MutationGenerator
                (config.sourcePath, config.MutationPath+"mutation", genConfig);
        String MutationInfo = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(generator.runMutation());

        FileOutputStream mi = new FileOutputStream("MutationInfo.json");
        mi.write(MutationInfo.getBytes());
        mi.close();
    }


    static private void BatchTest(Commander config) throws Exception
    {
        BatchTester batchTester= new BatchTester(config.MutationPath, config.sourcePath);
        String testResult = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(batchTester.runTest());
        FileOutputStream tr = new FileOutputStream("TestResult.json");
        tr.write(testResult.getBytes());
        tr.close();
    }

    public static String readToString(String fileName) {
        String encoding = "ISO-8859-1";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    static private void report(Commander config) throws Exception
    {
        String template = readToString("report_template.html");
        String MutationInfo = readToString("MutationInfo.json");
        String TestResult =  readToString("TestResult.json");
        template = template.replace("mutationInfoReplace", MutationInfo);
        template = template.replace("testResultReplace", TestResult);

        FileOutputStream report = new FileOutputStream("report.html");
        report.write(template.getBytes());
        report.close();

    }


    static public void main(String[] args) throws Exception
    {
        Commander config = new Commander();
        JCommander jCommander = JCommander.newBuilder().addObject(config).build();
        jCommander.parse(args);

        if (config.help)
        {
            jCommander.usage();
            return;
        }


        if (config.role == 0 || config.role == 1)
        {
            MutationGenerate(config);
        }

        if (config.role == 0 || config.role == 2)
        {
            BatchTest(config);
        }

        if (config.role == 0 || config.role == 3)
        {
            report(config);
        }






    }

}
