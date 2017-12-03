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
    private static ObjectNode resultNode = new ObjectMapper().createObjectNode();
    private static Map<Integer,Map<Integer,Map<String,String>>> map=new TreeMap<>();
    private static int count=0;
    private static Map<String,String> funLunar=new TreeMap<>();
    private static Map<String,String> funNext=new TreeMap<>();
    private static String mutationPath;
    private static String originPath;
    /**
     * @param mutationPath  变异体输出的位置
     */
    public BatchTester(String mutationPath,String originPath) throws Exception
    {
        this.mutationPath=mutationPath;
        this.originPath=originPath;
    }


    public void exeCommand(String commandstr)
    {
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

            try
            {
                if (f.getPath().equals(originPath)){
                    continue;
                }
                exeCommand("(cd " + f.getPath()+";mvn clean;mvn test)");
                System.out.println(f.getPath());
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

        Map<String,Map<String,String>> temp;
        //File fDir=new File(file.separator);
        String strFile1= file.getPath()+File.separator+"target"+File.separator+"surefire-reports"+File.separator
                +"TEST-third_party.LunarUtilTest.xml";
        String strFile2= file.getPath()+File.separator+"target"+File.separator+"surefire-reports"+File.separator
                +"TEST-third_party.NextDateTest.xml";

        File f1=new File(strFile1);
        File f2=new File(strFile2);


        if (count==0){
            temp=DOM4JTest.analysisNormal(f1,0);
        }else {
            temp=DOM4JTest.analysis(f1);
        }
        funLunar=temp.get("funmsg");
        tmap.put(1,temp.get("rMap"));

        if (count==0){
            temp=DOM4JTest.analysisNormal(f2,0);
        }else {
            temp=DOM4JTest.analysis(f2);
        }
        funNext=temp.get("funmsg");
        tmap.put(2,temp.get("rMap"));
        return tmap;
    }


    private ObjectNode toJson(int count,File f,int num){
        Map<String,String> fun=new TreeMap<>();
        if (num==1){
            fun=funLunar;
        }else if (num==2){
            fun=funNext;
        }else {
            return null;
        }

        ObjectNode funcNode = new ObjectMapper().createObjectNode();
        Map<String,String> normalm=map.get(0).get(num);
        Map<String,String> m=map.get(count).get(num);

        ArrayNode jsonArray=new ObjectMapper().createArrayNode();
        for (String name: m.keySet()){
            jsonArray.add(new ObjectMapper().createObjectNode().put("name",name).put("origin",normalm.get(name)).put("mutation",m.get(name)));
        }

        funcNode.put("testNum", Integer.parseInt(fun.get("tests")));
        funcNode.put("OK",Integer.parseInt(fun.get("tests"))-Integer.parseInt(fun.get("failures")));
        funcNode.put("failures",Integer.parseInt(fun.get("failures")));
        funcNode.set("difference",jsonArray);
        return funcNode;

    }


    private JsonNode saveToJson(int count,File f)
    {
        String fileName=f.getName();
//        ArrayNode jsonArray=new ObjectMapper().createArrayNode();
        ObjectNode mutaNode = new ObjectMapper().createObjectNode();
//        ObjectNode funcNode = new ObjectMapper().createObjectNode();
//        ObjectNode LeafNode = new ObjectMapper().createObjectNode();

//        Map<String,String> normalm=map.get(count).get(1);
//        Map<String,String> m=map.get(count).get(1);
//        for (String name: m.keySet()){
//            new ObjectMapper().createObjectNode().put("name",name).put("origin",normalm.get(name)).put("mutation",m.get(name));
//            //LeafNode.put("origin",normalm.get(name));
//            //LeafNode.put("mutation",m.get(name));
//            jsonArray.add(LeafNode);
//        }
//        funcNode.put("testNum:", Integer.parseInt(funLunar.get("tests")));
//        funcNode.put("OK:",Integer.parseInt(funLunar.get("tests"))-Integer.parseInt(funLunar.get("failures")));
//        funcNode.put("failures:",Integer.parseInt(funLunar.get("failures")));
//        funcNode.set("difference",jsonArray);
        mutaNode.set("third_party.lunarUtilTest",toJson(count,f,1));

//        normalm=map.get(count).get(2);
//        m=map.get(count).get(2);
//        for (String name: m.keySet()){
//            LeafNode.put("name",name);
//            LeafNode.put("origin",normalm.get(name));
//            LeafNode.put("mutation",m.get(name));
//            jsonArray.add(LeafNode);
//        }
//        funcNode.put("testNum:", Integer.parseInt(funNext.get("tests")));
//        funcNode.put("OK:",Integer.parseInt(funNext.get("tests"))-Integer.parseInt(funLunar.get("failures")));
//        funcNode.put("failures:",Integer.parseInt(funNext.get("failures")));
//        funcNode.set("difference",jsonArray);
        mutaNode.set("third_party.NextDateTest",toJson(count,f,2));
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

        System.out.println("ok");
        map.put(count,loadXmls(forgin));
//        saveToJson(count, forgin);
        count++;
        traverseFile(file);
        return resultNode;
    }
}



