package grandfisher;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;


public class BatchTester
{
    public static ObjectNode resultNode = new ObjectMapper().createObjectNode();
    private static Map<Integer,Map<Integer,Map<String,String>>> map=new TreeMap<>();
    private static int count=0;
    //
    private static Map<String,String> funLunar=new TreeMap<>();
    private static Map<String,String> funNext=new TreeMap<>();
    private static String mutationPath;
    private static String originPath;
    private static String errmsg="compile error";

    private boolean isReTest;

    /**
     * @param mutationPath  变异体输出的位置
     */
    public BatchTester(String mutationPath,String originPath, boolean isReTest) throws Exception
    {
        this.mutationPath=mutationPath;
        this.originPath=originPath;
        this.isReTest = isReTest;
    }


    public void exeCommand(String commandstr)
    {
        if (!isReTest)
        {
            return;
        }
        BufferedReader reader=null;
        try
        {
            System.out.println("start" + commandstr);
            String[] command = new String[] {"/bin/sh","-c",commandstr};

            Process p= Runtime.getRuntime().exec(command);
//            p.waitFor();
            reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line=null;
            StringBuilder strBuilder=new StringBuilder();
            while((line=reader.readLine())!=null)       //bufferreader 会block线程直到p结束, 所以可以不用p.waitfor();
            {
                strBuilder.append(line+"\n");
            }
            p.waitFor();
            System.out.println("done" + commandstr + " " + strBuilder.toString().length());
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

    private int traverseFile(File file) throws Exception
    {
        System.out.println("traverse");
        Process process;
        String cmd;
        File flist[] = file.listFiles();
        if (flist == null || flist.length == 0) {
            System.out.println("no files!");
            return -1;
        }
        System.out.println("ready");
        for (File f : flist) {
            if (f.getName().contains("testcase")){
                continue;
            }
            try
            {
                if (f.getPath().equals(originPath)){
                    continue;
                }
                exeCommand("(cd " + f.getPath()+";mvn clean;mvn test)");
                //System.out.println(f.getPath());
                map.put(count,loadXmls(f));

                if (count!=0) {
                    saveToJson(count, f);
                }
                //saveToJson(count, f);
                count++;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private Map<Integer,Map<String,String>> loadXmls(File file)
    {
        Map<Integer,Map<String,String>> tmap=new TreeMap<>();

        //File fDir=new File(file.separator);
        String strFile1= file.getPath()+File.separator+"target"+File.separator+"surefire-reports"+File.separator
                +"TEST-third_party.LunarUtilTest.xml";
        String strFile2= file.getPath()+File.separator+"target"+File.separator+"surefire-reports"+File.separator
                +"TEST-third_party.NextDateTest.xml";
        File f1=new File(strFile1);
        File f2=new File(strFile2);

        tmap.putAll(load(f1,tmap,1));
        tmap.putAll(load(f2,tmap,2));

        return tmap;
    }

    private Map<Integer,Map<String,String>> load(File file,Map<Integer,Map<String,String>> tmap,int num){
        Map<String,Map<String,String>> temp;

        if (count==0){
            temp=DOM4JTest.analysisNormal(file,0);
        }else {
            temp=DOM4JTest.analysis(file);
        }
        if (temp.containsKey("compile error")){
            tmap.put(0, (Map<String, String>) new TreeMap<>().put("error","compile error"));
        }else {
            if (num==1){
                funLunar = temp.get("funmsg");
            }else {
                funNext = temp.get("funmsg");
            }

            tmap.put(num, temp.get("rMap"));
        }
        return tmap;
    }


    private ObjectNode toJson(int count,File f,int num){
        ObjectNode funcNode = new ObjectMapper().createObjectNode();
        Map<String,String> fun=new TreeMap<>();
        if (num==1){
            fun=funLunar;
        }else if (num==2){
            fun=funNext;
        }else {
            return null;
        }


        //source
        Map<String,String> normalm=map.get(0).get(num);
        //mutation
        Map<String,String> m=map.get(count).get(num);

        ArrayNode jsonArray=new ObjectMapper().createArrayNode();

     // {
        for (String name: m.keySet()){
            if ((normalm.get(name)!=null && !normalm.get(name).isEmpty()) || (m.get(name)!=null && !m.get(name).isEmpty()))
            //if (normalm.get(name)!=null && m.get(name)!=null && !normalm.get(name).isEmpty() && !m.get(name).isEmpty())
                jsonArray.add(new ObjectMapper().createObjectNode().put("name",name).put("origin",normalm.get(name)).put("mutation",m.get(name)));
        }
        //}



        funcNode.put("testNum", Integer.parseInt(fun.get("tests")));
        funcNode.put("OK",Integer.parseInt(fun.get("tests"))-Integer.parseInt(fun.get("failures")));
        funcNode.put("failures",Integer.parseInt(fun.get("failures")));
        funcNode.set("difference",jsonArray);
        return funcNode;

    }


    private JsonNode saveToJson(int count,File f)
    {
        String fileName=f.getName();

        ObjectNode mutaNode = new ObjectMapper().createObjectNode();

        if (!map.get(count).containsKey(0)){
            mutaNode.set("third_party.lunarUtilTest",toJson(count,f,1));
            mutaNode.set("third_party.NextDateTest",toJson(count,f,2));
        }else {
            mutaNode.put("error",errmsg);
        }
        resultNode.set(fileName,mutaNode);

        return resultNode;
    }
    /**
     * 在mutationPath生成变异体
     * @return 以json格式返回每个变异的具体测试情况
     */
    public JsonNode runTest() throws Exception
    {

        File file=new File(mutationPath);
        File forgin=new File(originPath);

        exeCommand("(cd " + forgin.getPath()+";mvn test)");
//        exeCommand("(cd ../resources/hw1_unittest_source;mvn test)");

        System.out.println("source ok");
        map.put(count,loadXmls(forgin));
        saveToJson(count,forgin);
        count++;
        traverseFile(file);
        System.out.println("traverse ok");

//        resultNode.remove("testcase");
//      输出CSV

        String[][] csvMsg={{"getLunarDateInfo.csv","getLunarDateInfo"},
                {"getDayNum.csv","getDayNum"},
                {"getNextDateInfo.csv","getNextDateInfo"},
                {"vaildDate.csv","vaildDate"}};
        CsvWriter csvWriter=new CsvWriter();
        for (int i=0;i<4;i++) {
            csvWriter.csvCreate(csvMsg[i][0],csvMsg[i][1]);
        }
        System.out.println("----------------------------------------------------------------------");



        return resultNode;
    }
}



