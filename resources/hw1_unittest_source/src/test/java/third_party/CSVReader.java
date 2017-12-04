package third_party;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyn on 17-12-4.
 */
public class CSVReader {
    String testcaseFilePath="../testcase/";
    String funcName="";
    public CSVReader(String name)
    {
        funcName=name;
    }
    public List<JsonNode> read(int ParamNum,int returnNum)
    {
        List<JsonNode> testcaseList=new ArrayList<>();
        File file=new File(testcaseFilePath+funcName+".csv");
        BufferedReader br=null;
        String line="";
        try {
            br = new BufferedReader(new FileReader(file));
            line=br.readLine();
            while ((line = br.readLine()) != null)
            {
                System.out.println(line);
                //解析每个变量
                String[] params=line.split(",");
                //input参数
                ArrayNode in=new ObjectMapper().createArrayNode();
                for(int i=1;i<=ParamNum;i++)
                {
                    in.add(params[i]);
                }
                //期望的return结果
                ArrayNode out=new ObjectMapper().createArrayNode();
                for(int i=1;i<=returnNum && !params[i+ParamNum].equals("");i++)
                {
                    out.add(params[i+ParamNum]);
                }
                ObjectNode tmp=new ObjectMapper().createObjectNode();
                tmp.set("in",in);
                tmp.set("out",out);
                testcaseList.add(tmp);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally
        {
            try
            {
                br.close();
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return testcaseList;
    }

}
