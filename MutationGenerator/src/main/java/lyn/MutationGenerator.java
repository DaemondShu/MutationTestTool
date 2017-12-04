package lyn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MutationGenerator
{
    //源文件路径
    private String SourcePath;
    //变异体生成路径
    private String MutationPath;
    //样本量，即累计几个样本后抽取一个进行变异
    private int MutationSize;
    //对于某一个符号的最大变异种数
    private int MutationMethodNum;
    private static Map<String,ArrayList<String>> MutationMethod=new HashMap<>();
    private static ObjectNode mutationInfo=new ObjectMapper().createObjectNode();
    private static String testCodePath="/src/main/java/third_party/";
    //输出json文件的路径
    private static String jsonFileName="../resources/mutationInfo.json";
    //测试的类集合
    private static String[] testFileList={"LunarUtil.java","NextDate.java"};
    //变异的符号集
    private static String[] operList={"<<",">>","<=",">=","==","!=","||","&&","<",">","++","--","+","-"};

    /**
     * 变异生成器启动时初始化变异的方式
     */
    private void InitMutationMethod()
    {
        MutationMethod.put("<<", new ArrayList<String>(){{add(">>");}});
        MutationMethod.put(">>", new ArrayList<String>(){{add("<<");}});
        MutationMethod.put("<",new ArrayList<String>(){{add("<=");add(">");add(">=");add("==");add("!=");}});
        MutationMethod.put(">",new ArrayList<String>(){{add(">=");add("<");add("<=");add("==");add("!=");}});
        MutationMethod.put("<=",new ArrayList<String>(){{add("<");add(">=");add(">");add("==");add("!=");}});
        MutationMethod.put(">=",new ArrayList<String>(){{add("<=");add(">");add(">");add("==");add("!=");}});
        MutationMethod.put("==",new ArrayList<String>(){{add("<");add(">");add("!=");}});
        MutationMethod.put("!=",new ArrayList<String>(){{add("<=");add(">=");add("==");}});
        MutationMethod.put("+",new ArrayList<String>(){{add("-");}});
        MutationMethod.put("-",new ArrayList<String>(){{add("+");}});
        MutationMethod.put("++",new ArrayList<String>(){{add("--");}});
        MutationMethod.put("--",new ArrayList<String>(){{add("++");}});
        MutationMethod.put("||",new ArrayList<String>(){{add("&&");}});
        MutationMethod.put("&&",new ArrayList<String>(){{add("||");}});
    }
    /**
     *
     * @param sourcePath    原始项目位置
     * @param mutationPath  变异体输出的位置
     * @param config    变异配置
     */
    public MutationGenerator(String sourcePath, String mutationPath, JsonNode config)
    {
        SourcePath=sourcePath;
        MutationPath=mutationPath;
        MutationSize=config.get("MutationSize").asInt();
        MutationMethodNum=config.get("MutationMethodNum").asInt();
        InitMutationMethod();
    }

    /**
     * 在mutationPath生成变异体
     * @return 以json格式返回每次变异的具体信息
     */
    public JsonNode runMutation()
    {


        int testNumber=0;
        //对每个待测源代码文件进行扫描
        //exeCommand("ls ../resources/hw1_unittest_mutation*");
        exeCommand("rm -r "+ MutationPath + "*");
        for(String fileName:testFileList)
        {
            //当前文件名
            String file=SourcePath+testCodePath+fileName;
            //当前正在变异的文件
            File currentFile=new File(file);
            //删除源文件的注释
            clearComment(currentFile,"UTF-8");



            try
            {
                LineNumberReader reader=new LineNumberReader(new FileReader(currentFile));
                String line=null;
                int lineNumber=0;
                int posLineNumber=0;
                int foundCount=0;
                //读取每行
                while((line=reader.readLine())!=null)
                {

                    //记录代码行数
                    lineNumber++;
                    //pos记录可变异符号出现的位置

                    int position=0;
                    //标识是否有匹配的可变异符号
                    boolean found=true;

                    while(found)
                    {
                        int pos=0;
                        //index=pos;
                        found=false;
                        String operation="";

                        //对于每个可变异符号进行匹配，依次获取（符号：位置）的对应信息
                        for(String oper:operList)
                        {
                            int tmp=line.indexOf(oper,position);
                            //又匹配到符号同时比较符号的优先性
                            if(tmp>=0)
                            {
                                if(pos==0 || tmp<pos)
                                {
                                    //更新pos信息以及operation
                                    pos = tmp;
                                    operation = oper;
                                    posLineNumber = lineNumber;
                                    found = true;
                                }
                            }

                        }
                        if(found)
                        {


                            //记录当前可变异数
                            foundCount++;
                            position=pos;
                            //每隔MutationSize个生成变异
                            if(foundCount==MutationSize)
                            {
                                ArrayList<String> method=MutationMethod.get(operation);
                                for(int i=0;i<MutationMethodNum && i<method.size();i++)
                                {
                                    ++testNumber;
                                    //复制源文件
                                    exeCommand("cp -r "+SourcePath+" "+MutationPath+testNumber);
                                    //修改
                                    LineNumberReader antherReader=new LineNumberReader(new FileReader(new File(file)));
                                    String lineStr=null;
                                    BufferedWriter writer=new BufferedWriter(new FileWriter
                                        (new File(MutationPath+testNumber+testCodePath+fileName)));
                                    while((lineStr=antherReader.readLine())!=null)
                                    {
                                        if(antherReader.getLineNumber()!=posLineNumber)
                                            writer.write(lineStr+"\r\n");
                                        else
                                        {

                                            String head=lineStr.substring(0,pos);
                                            String tail=lineStr.substring(pos+operation.length(),lineStr.length());
                                            String newLine=head+method.get(i)+tail+"\r\n";
                                            ObjectNode object=new ObjectMapper().createObjectNode();
                                            object.put("className",fileName).put("lineNumber",posLineNumber)
                                                    .put("origin",lineStr.trim()).put("mutation",newLine.trim())
                                                    .put("ori_oper", operation).put("mu_oper", method.get(i))
                                            ;
                                            mutationInfo.set("mutation"+testNumber,object);
                                            writer.write(newLine);
                                        }
                                    }
                                    writer.flush();
                                    writer.close();
                                    antherReader.close();
                                    foundCount=0;

                                }
                            }
                            position+=operation.length();
                        }

                    }

                }

                reader.close();
               // writeJson(jsonFileName,mutationInfo);

            }catch (Exception e)
            {
                e.printStackTrace();
            }

            testNumber++;
        }

        return mutationInfo;
    }

    /**
     * 执行shell命令
     * @param commandstr
     */
    public void exeCommand(String commandstr)
    {
        BufferedReader reader=null;
        try
        {
            String[] command = new String[] {"/bin/sh","-c",commandstr};

            Process p= Runtime.getRuntime().exec(command);
            p.waitFor();
            reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line=null;
            StringBuilder strBuilder=new StringBuilder();
            while((line=reader.readLine())!=null)       //bufferreader 会block线程直到p结束, 所以可以不用p.waitfor();
            {
                strBuilder.append(line+"\n");

            }
            System.out.println(strBuilder.toString());
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(reader!=null)
            {
                try {
                    reader.close();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 删除所有注释
     * @param file
     * @param charset
     */
    private static void clearComment(File file, String charset) {
        try {
            //递归处理文件夹
            if (!file.exists()) {
                return;
            }

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    clearComment(f, charset); //递归调用
                }
                return;
            } else if (!file.getName().endsWith(".java")) {
                //非java文件直接返回
                return;
            }

            //根据对应的编码格式读取
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
            StringBuffer content = new StringBuffer();
            String tmp = null;
            while ((tmp = reader.readLine()) != null) {
                content.append(tmp);
                content.append("\n");
            }
            String target = content.toString();
            //String s = target.replaceAll("\\/\\/[^\\n]*|\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*\\/", ""); //本段正则摘自网上，有一种情况无法满足（/* ...**/），略作修改
            String s = target.replaceAll("\\/\\/[^\\n]*|\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*+\\/", "");
            //System.out.println(s);
            //使用对应的编码格式输出
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
            out.write(s);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写json文件
     * @param fileName
     * @param json
     */
    private static void writeJson(String fileName,JsonNode json)
    {
        PrintWriter printWriter=null;
        try
        {
            printWriter=new PrintWriter(new FileWriter(fileName));
            printWriter.write(json.toString());
            printWriter.flush();

        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            printWriter.close();
        }


    }
}
