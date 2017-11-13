package lyn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;

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
    private static ObjectNode MutationMethod=newJsonObject();


    private static ObjectNode newJsonObject()
    {
        return new ObjectMapper().createObjectNode();
    }
    private static ArrayNode newArrayNode()
    {
        return new ObjectMapper().createArrayNode();
    }

    /**
     * 变异生成器启动时初始化变异的方式
     */
    private void InitMutationMethod()
    {
        MutationMethod.set("<",newArrayNode().add("<=").add(">=").add(">").add("==").add("!="));
        MutationMethod.set(">",newArrayNode().add("<=").add(">=").add("<").add("==").add("!="));
        MutationMethod.set("<=",newArrayNode().add("<").add(">").add(">=").add("==").add("!="));
        MutationMethod.set(">=",newArrayNode().add("<").add(">").add("<=").add("==").add("!="));
        MutationMethod.set("==",newArrayNode().add("<").add(">").add("<=").add(">=").add("!="));
        MutationMethod.set("!=",newArrayNode().add("<").add(">").add("<=").add(">=").add("=="));
        MutationMethod.set("+",newArrayNode().add("-"));
        MutationMethod.set("-",newArrayNode().add("+"));
        //MutationMethod.set("*",newArrayNode().add("/"));
        //MutationMethod.set("/",newArrayNode().add("*"));
        MutationMethod.set("|",newArrayNode().add("&"));
        MutationMethod.set("&",newArrayNode().add("|"));
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
        InitMutationMethod();
    }

    /**
     * 在mutationPath生成变异体
     * @return 以json格式返回每次变异的具体信息
     */
    public JsonNode runMutation()
    {

        int testNumber=1;
        copyFolder(SourcePath,MutationPath+testNumber);
        return null;
    }
    /**
            * 复制单个文件
    * @param oldPath String 原文件路径 如：c:/fqf.txt
    * @param newPath String 复制后路径 如：f:/fqf.txt
    * @return boolean
    */
//    public void copyFile(String oldPath, String newPath) {
//        try {
//            int bytesum = 0;
//            int byteread = 0;
//            File oldfile = new File(oldPath);
//            if (oldfile.exists()) { //文件存在时
//                InputStream inStream = new FileInputStream(oldPath); //读入原文件
//                FileOutputStream fs = new FileOutputStream(newPath);
//                byte[] buffer = new byte[1444];
//                int length;
//                while ( (byteread = inStream.read(buffer)) != -1) {
//                    bytesum += byteread; //字节数 文件大小
//                    System.out.println(bytesum);
//                    fs.write(buffer, 0, byteread);
//                }
//                inStream.close();
//            }
//        }
//        catch (Exception e) {
//            System.out.println("复制单个文件操作出错");
//            e.printStackTrace();
//
//        }
//
//    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile()){

                        BufferedReader reader=new BufferedReader(new FileReader(temp));
                        BufferedWriter writer=new BufferedWriter(new FileWriter(newPath + "/" +
                                (temp.getName()).toString()));

                        int line=0;
                        String readStr=null;
                        while((readStr=reader.readLine())!=null)
                        {
                            line++;
                            //if(temp.getName()==ClassName+".java" && line==LineNumber)

                            writer.write(readStr+"\r\n");

                        }
                        writer.flush();
                        writer.close();
                        reader.close();


                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }
}
