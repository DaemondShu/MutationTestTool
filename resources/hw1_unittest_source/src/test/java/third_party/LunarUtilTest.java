package third_party;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by SunMeng on 2017/10/15.
 */

public class LunarUtilTest {

    @ParameterizedTest
    @MethodSource(value = "GetDayNumTestcasesProvider")
    void GetDayNumTest(JsonNode testcase) throws Exception
    {
        System.out.println(".....GetDayNumTest....");
        JsonNode in=testcase.get("in");
        int res=LunarUtil.getDayNum(in.get(0).asInt());
        assertEquals(testcase.get("out").get(0).asInt(),res);
    }


    static Iterator<JsonNode> GetDayNumTestcasesProvider()
    {
        CSVReader reader=new CSVReader("getDayNum");
        List<JsonNode> testcase=reader.read(1,1);
        return testcase.iterator();
    }

    @ParameterizedTest
    @MethodSource(value = "GetLunarDateInfoTestcasesProvider")
    void GetLunarDateInfoTest(JsonNode testcase) throws Exception
    {
        System.out.println(".....GetLunarDateInfoTest....");
        LunarUtil lunarUtil=new LunarUtil();
        JsonNode in=testcase.get("in");
        String[] res=lunarUtil.getLunarDateInfo(in.get(0).asInt(),in.get(1).asInt(),in.get(2).asInt());
        ArrayNode node=new ObjectMapper().createArrayNode();
        for (String r:res)
        {
            node.add(r);
        }
        assertEquals(testcase.get("out"),node);
    }


    static Iterator<JsonNode> GetLunarDateInfoTestcasesProvider()
    {
        CSVReader reader=new CSVReader("getLunarDateInfo");
        List<JsonNode> testcase=reader.read(3,4);
        return testcase.iterator();
    }


}
