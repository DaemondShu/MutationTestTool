package grandfisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;


import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;




public class CsvWriter {

    final static String comma=",";
    final static String BR="\r\n";

    final static int input=1;
    final static int output=2;

    private static Map<String,String[]> funInput= new TreeMap<>();
    private static Map<String,String[]> funOutput= new TreeMap<>();

    //入参出参表头
    private static String inputHead="input parameter name";
    private static String outputHead="output parameter name";

    private static Iterator<JsonNode> itj;
    private static int count;//case num


    public CsvWriter()
    {

        funInput.put("getLunarDateInfo",new String[]{"Year","Month","Day"});
        funInput.put("getDayNum",new String[]{"DayInfo"});
        funInput.put("getNextDateInfo",new String[]{"Year","Month","Day","N"});
        funInput.put("vaildDate",new String[]{"Year","Month","Day"});

        funOutput.put("getLunarDateInfo",new String[]{"LunarYear","LunarMonth","LunarDay","LunarZodiac"});
        funOutput.put("getDayNum",new String[]{"DayNum"});
        funOutput.put("getNextDateInfo",new String[]{"ErrorCode","ErrorMsg"});
        funOutput.put("vaildDate",new String[]{"ErrorCode"});
    }


    public static void csvCreate(String path,String funName)
    {
        count=0;
        switch (funName){
            case "getLunarDateInfo":itj=CSVReader.GetLunarDateInfoTestcasesProvider(); break;
            case "getDayNum":itj=CSVReader.GetDayNumTestcasesProvider();break;
            case "getNextDateInfo":itj=CSVReader.GetNextDateInfoTestcasesProvider();break;
            case "vaildDate":itj=CSVReader.ValidDateTestcasesProvider();break;
        }

        File file = new File(path);
        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out, "UTF8");
            bw = new BufferedWriter(osw);

            //函数头
            writeLine(bw,funName);
            while (itj.hasNext()) {
                writeParaHead(bw, funName, input);
                writeParaValue(bw);
                writeParaHead(bw, funName, output);
                writeResValue(bw,funName);
//                System.out.println("----------------------------------------------------------------------");
//                System.out.println(count);
//                System.out.println("----------------------------------------------------------------------");
            }


        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                osw.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private static void writeLine(BufferedWriter bw ,String line)
    {
        try {
            bw.write(line+BR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //num 来决定是input还是output
    private static void writeParaHead(BufferedWriter bw,String funName,int num)
    {
        String Headline=null;
        Map<String,String[]> fun= new TreeMap<>();
        switch (num){
            case 1:
                fun=funInput;
                Headline=inputHead;
                break ;
            case 2:
                fun=funOutput;
                Headline=outputHead;
                break;
        }
        String[] sArray=fun.get(funName);

        for(String s:sArray){
            Headline=Headline+comma+s;
        }
        if (num==1) {
            Headline = Headline + comma + "Test case No.";
        }
        writeLine(bw,Headline);

    }


    private static void writeParaValue(BufferedWriter bw)
    {
        count++;
        String line = "Input Value";
        Iterator iterator = itj.next().get("in").iterator();
        while (iterator.hasNext()) {
            line=line+comma+iterator.next();
        }
        line=line+comma+String.valueOf(count);
        writeLine(bw,line);
    }



    private static void writeResValue(BufferedWriter bw,String funName) {
        //解析json，需要count
        String caseName = getCaseName(funName);

        switch (funName) {
            case "getLunarDateInfo":
                funName = "third_party.lunarUtilTest";
                break;
            case "getDayNum":
                funName = "third_party.lunarUtilTest";
                break;
            case "getNextDateInfo":
                funName = "third_party.NextDateTest";
                break;
            case "vaildDate":
                funName = "third_party.NextDateTest";
                break;
        }

        JsonNode resultNode = BatchTester.resultNode;
        JsonNode mutaNode;
        JsonNode funNode;
        ArrayNode diffNode;
        JsonNode tempNode;
        //JsonNode caseNode;
        String line = "";


        mutaNode = resultNode.get("hw1_unittest_source");
        funNode = mutaNode.get(funName);
        diffNode = (ArrayNode) (funNode.get("difference"));
        Iterator<JsonNode> iterator = diffNode.iterator();
        while (iterator.hasNext())
        {
            tempNode = iterator.next();
            String tempName="\""+caseName+"Test"+count+"\"";
            String NodeName=tempNode.get("name").toString();

//            System.out.println(tempName);
//            System.out.println(NodeName);
//            System.out.println(NodeName.equals(tempName));

            if (tempNode.get("name").toString().equals("\""+caseName+"Test"+count+"\"")) {
                line = tempNode.get("mutation").toString();
                if(!line.equals("")){
                    line = dealString(line);
                } else {
                    line=" ";
                }
                line = "hw1_unittest_source" + " result" + comma + line;
                writeLine(bw, line);
                break;
            }
        }



        for (Iterator<String> it = resultNode.fieldNames(); it.hasNext(); )
        {

            String key = it.next();
            mutaNode=resultNode.get(key);
            if (key.equals("hw1_unittest_source")||key.equals("testcase")){
                continue;
            }
            if (mutaNode.has("error"))
            {
                continue;
//                line = key + " result" + comma + "compile error";
//                writeLine(bw, line);
            } else {
                funNode = mutaNode.get(funName);
                diffNode = (ArrayNode) (funNode.get("difference"));
                iterator = diffNode.iterator();
                while (iterator.hasNext())
                {
                    tempNode = iterator.next();

                    if (tempNode.get("name").toString().equals("\""+caseName+"Test"+count+"\""))
                    {
                        line = tempNode.get("mutation").toString();
                        line = dealString(line);
                        line = key+ " result" + comma + line;
                        writeLine(bw, line);

                    }
                }
            }

        }

    }

    private static String getCaseName(String caseName)
    {
        char[] cs=caseName.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);
    }



    public  static String dealString(String line)
    {
        String[] s = line.split("but was:");
//        if (s.length == 2)
//        {
//            s[1].replace("<", "");
//            s[1].replace(">", "");
//            s[1].replace("\\", "");
//            s[1].replace("[", "");
//            s[1].replace("]", "");
//            s[1].replace("\"", "");
//            line = s[1];
//        } else
//        {
//            s[0].replace("<", "");
//            s[0].replace(">", "");
//            s[0].replace("\\", "");
//            s[0].replace("[", "");
//            s[0].replace("]", "");
//            s[0].replace("\"", "");
//            line = s[0];
//        }

        line = (s.length == 2) ? s[1] : s[0];
        return line.replaceAll("[<>\\]\\[\\\"\\\\]","");
    }



}
