package lyn;

import com.fasterxml.jackson.databind.JsonNode;
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
    //变异生成器参数的配置
    private JsonNode Config;
    //样本量，即累计几个样本后抽取一个进行变异
    private int MutationSize;
    //变异方式的种数
    private int MutationMethodNum;
    private static Map<String,ArrayList<String>> MutationMethod=new HashMap<>();

    private static String testCodePath="/src/main/java/third_party/";
    private static String[] testFileList={"LunarUtil.java","NextDate.java"};
    private static String[] operList={"<=",">=","==","!=","||","&&","<",">","++","--","+","-"};

    /**
     * 变异生成器启动时初始化变异的方式
     */
    private void InitMutationMethod()
    {
        MutationMethod.put("<",new ArrayList<String>(){{add("<=");add(">");add(">=");add("==");add("!=");}});
        MutationMethod.put(">",new ArrayList<String>(){{add("<");add(">=");add("<=");add("==");add("!=");}});
        MutationMethod.put("<=",new ArrayList<String>(){{add("<");add(">=");add(">");add("==");add("!=");}});
        MutationMethod.put(">=",new ArrayList<String>(){{add("<=");add(">");add(">");add("==");add("!=");}});
        MutationMethod.put("==",new ArrayList<String>(){{add("<=");add(">=");add("!=");}});
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
        Config=config;
        MutationSize=Config.get("MutationSize").asInt();
        MutationMethodNum=Config.get("MutationMethodNum").asInt();
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
        for(String fileName:testFileList)
        {
            //当前文件名
            String file=SourcePath+testCodePath+fileName;
            File currentFile=new File(file);
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
                    //line="        if (year > yearUpper || year < yearBase)";
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

                            foundCount++;
                            position=pos;
                            if(foundCount==MutationSize)
                            {
                                ArrayList<String> method=MutationMethod.get(operation);
                                for(int i=0;i<MutationMethodNum && i<method.size();i++)
                                {
                                    ++testNumber;

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
                                            writer.write(head+method.get(i)+tail+"\r\n");
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

            }catch (Exception e)
            {
                e.printStackTrace();
            }

            testNumber++;
        }

        return null;
    }

    /**
     * 执行shell命令
     * @param command
     */
    private void exeCommand(String command)
    {
        BufferedReader reader=null;
        try
        {
            Process p=Runtime.getRuntime().exec(command);
            reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line=null;
            StringBuilder strBuilder=new StringBuilder();
            while((line=reader.readLine())!=null)
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

    public static void clearComment(File file, String charset) {
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


}
