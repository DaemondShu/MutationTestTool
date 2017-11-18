package grandfisher;

import com.fasterxml.jackson.databind.JsonNode;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.*;


public class BatchTester
{
    private static Map<Integer,Map<Integer,Map<String,String>>> map=new HashMap<>();
    private static int count=0;
    private static Map<String,String> funLunar=new HashMap<>();
    private static Map<String,String> funNext=new HashMap<>();
    /**
     * @param mutationPath  变异体输出的位置
     */
    public BatchTester(String mutationPath,String normalPath) throws Exception
    {
        File file=new File(mutationPath);
        File fnormal=new File(normalPath);
        map.put(count,loadXmls(fnormal));
        count++;
        traverseFile(file);
    }

    private int traverseFile(File file) throws Exception
    {
        Process process;
        String cmd;
        File flist[] = file.listFiles();
        if (flist == null || flist.length == 0) {
            System.out.println("no files!");
            return -1;
        }
        for (File f : flist) {
            cmd="cd"+ file.getName()+"|mvn test";
            process=Runtime.getRuntime().exec(cmd);
            map.put(count,loadXmls(f));
            count++;
        }
        return 0;
    }

    private Map<Integer,Map<String,String>> loadXmls(File file)
    {
        Map<Integer,Map<String,String>> tmap=new HashMap<>();

        Map<String,Map<String,String>> temp=new HashMap<>();
        File fDir=new File(file.separator);
        String strFile1= "target"+File.separator+"surefire-reports"+File.separator
                +"TEST-third_party.LunarUtilTest.xml";
        String strFile2= "target"+File.separator+"surefire-reports"+File.separator
                +"TEST-third_party.NextDateTest.xml";

        File f1=new File(fDir,strFile1);
        File f2=new File(fDir,strFile2);

        temp=DOM4JTest.analysis(f1);

        tmap.put(1,DOM4JTest.analysis(f1).get("rMap"));
        tmap.put(2,DOM4JTest.analysis(f2).get("rMap"));
        return tmap;
    }



    private void saveToJson()
    {

    }
    /**
     * 在mutationPath生成变异体
     * @return 以json格式返回每个变异的具体测试情况
     */
    public JsonNode runTest()
    {
        return null;
    }
}


class DOM4JTest {
    private static Map<String,String> rMap= new HashMap<>();
    private static Map<String,String> funmsg=new HashMap<>();
    /**
     * @param file
     */
    public static Map<String,Map<String,String>> analysis(File file) {
        String name="";
        String message="";

        SAXReader reader = new SAXReader();
        try {
            // 通过reader对象的read方法加载xml文件,获取docuemnt对象。
            Document document = reader.read(file);
            // 通过document对象获取根节点testsuite
            Element testsuite = document.getRootElement();
            List<Attribute> rootAttrs=testsuite.attributes();
            for (Attribute attr : rootAttrs){
                if (attr.getName()=="tests"){
                    funmsg.put(attr.getName(),attr.getValue());
                }
                if (attr.getName()=="failures"){
                    funmsg.put(attr.getName(),attr.getValue());
                }
                if (attr.getName()=="name"){
                    funmsg.put(attr.getName(),attr.getValue());
                }
            }
            // 通过element对象的elementIterator方法获取迭代器
            Iterator it = testsuite.elementIterator();
            // 遍历迭代器，获取根节点中的信息
            while (it.hasNext()) {
                System.out.println("=====开始遍历=====");

                Element testcase = (Element) it.next();
                List<Attribute> testcaseAttrs = testcase.attributes();
                for (Attribute attr : testcaseAttrs) {
                    if(attr.getName()=="name"){
                        name=attr.getValue();
                    }

                }
                Iterator itt = testcase.elementIterator();
                while (itt.hasNext()) {

                    Element testcaseEnd = (Element) itt.next();
                    List<Attribute> testEndAttrs = testcaseEnd.attributes();
                    for (Attribute attr : testEndAttrs) {
                        if(attr.getName()=="message"){
                            message=attr.getValue();
                        }
                    }
                }

                rMap.put(name, message);
                System.out.println("=====结束遍历=====");
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Map<String,Map<String,String>> map =new HashMap<>();
        map.put("funmsg",funmsg);
        map.put("rMap",rMap);
        return map;
    }
}