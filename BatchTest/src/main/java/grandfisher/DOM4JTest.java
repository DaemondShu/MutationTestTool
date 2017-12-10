package grandfisher;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

public class DOM4JTest {

    private static Map<String,List<String>> mapCmp=new TreeMap<>();
    //private static Map<String,List<String>> maptem=new TreeMap<>();

    private static int flag=1;
    private static int cnt=0;

    public static Map<String,Map<String,String>> analysisNormal(File file,int count) {
        Map<String, Map<String, String>> map;
        flag=count;
        map=analysis(file);
        flag=1;
        return map;
    }

    public static Map<String,Map<String,String>> analysis(File file) {

        Map<String, List<String>> mMap=new TreeMap<>();

        Map<String, Map<String, String>> map = new TreeMap<>();
        Map<String,String> rMap= new TreeMap<>();
        Map<String,String> funmsg=new TreeMap<>();

        SAXReader reader = new SAXReader();
        try {
            // 通过reader对象的read方法加载xml文件,获取docuemnt对象。
            Document document = reader.read(file);
            // 通过document对象获取根节点testsuite
            Element testsuite = document.getRootElement();
            List<Attribute> rootAttrs = testsuite.attributes();
            for (Attribute attr : rootAttrs) {
                if (attr.getName() == "tests") {
                    funmsg.put(attr.getName(), attr.getValue());
                }
                if (attr.getName() == "failures") {
                    funmsg.put(attr.getName(), attr.getValue());
                }
                if (attr.getName() == "name") {
                    funmsg.put(attr.getName(), attr.getValue());
                }
            }
            // 通过element对象的elementIterator方法获取迭代器
            Iterator it = testsuite.elementIterator();
            // 遍历迭代器，获取根节点中的信息
            int count=0;
            String prename="";
            while (it.hasNext()) {
                String end="success";
                String name = "";
                String message = "";


                List<String> list= new ArrayList<>();

                Element testcase = (Element) it.next();
                List<Attribute> testcaseAttrs = testcase.attributes();
                for (Attribute attr : testcaseAttrs) {
                    if (attr.getName() == "name") {
                        name = attr.getValue();
                        if (prename.equals("")){
                            prename=name;
                        }

                        if (!name.equals(prename)){
                            count=0;
                            prename=name;
                        }
                        count++;
                    }
                }
                Iterator itt = testcase.elementIterator();
                while (itt.hasNext()) {
                    end="failure";
                    Element testcaseEnd = (Element) itt.next();
                    List<Attribute> testEndAttrs = testcaseEnd.attributes();
                    for (Attribute attr : testEndAttrs) {
                        if (attr.getName() == "message") {
                            message = attr.getValue();
                        }
                    }
                }

                if (flag==0){
                    list.add(end);
                    list.add(message);
                    mMap.put(name+count,list);
                    rMap.put(name+count,message);
                }
                if (flag!=0) {

                    if (!end.equals(mapCmp.get(name+count).get(0))) {
                        rMap.put(name+count, message);
                    }

                }
            }
        } catch (DocumentException e) {
            map.put("compile error",funmsg);
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
        if (flag==0){
            mapCmp.putAll(mMap);
        }
//        else {
//            maptem.putAll(mMap);
//            cnt++;
//            if (cnt>=2){
//                cnt=0;
//                maptem.clear();
//            }
//        }
        map.put("funmsg", funmsg);
        map.put("rMap", rMap);
        return map;
    }
}
