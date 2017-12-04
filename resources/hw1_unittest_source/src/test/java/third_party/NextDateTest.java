package third_party;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NextDateTest
{


    @ParameterizedTest
    @MethodSource(value = "ValidDateTestcasesProvider")
    void VaildDateTest(JsonNode testcase) throws Exception
    {
        System.out.println(".....VaildDateTest....");
        JsonNode in=testcase.get("in");
        int res=NextDate.validDate(in.get(0).asInt(),in.get(1).asInt(),in.get(2).asInt());
        assertEquals(testcase.get("out").get(0).asInt(),res);
    }


    static Iterator<JsonNode> ValidDateTestcasesProvider()
    {
        CSVReader reader=new CSVReader("validDate");
        List<JsonNode> testcase=reader.read(3,1);
        return testcase.iterator();
    }

    @ParameterizedTest
    @MethodSource(value = "GetNextDateInfoTestcasesProvider")
    void GetNextDateInfoTest(JsonNode testcase) throws Exception
    {
        System.out.println(".....GetNextDateInfoTest....");
        NextDate nextDate=new NextDate();
        //造木桩 模拟对象以及对象返回值
        LunarUtil mockLunarUtil = mock(LunarUtil.class);
        when(mockLunarUtil.getLunarDateInfo(anyInt(),anyInt(),anyInt())).thenReturn(new String[]{"己丑年", "二月小", "初二", "牛"});
        // 打桩
        nextDate.setLunarUtil(mockLunarUtil);
        JsonNode in=testcase.get("in");
        ArrayList<String> res=nextDate.getNextDateInfo(in.get(0).asInt(),in.get(1).asInt(),in.get(2).asInt(),in.get(3).asInt());
        ArrayNode node=new ObjectMapper().createArrayNode();
        for (String r:res)
        {
                node.add(r);
        }
        assertEquals(testcase.get("out"),node);
    }


    static Iterator<JsonNode> GetNextDateInfoTestcasesProvider()
    {
        CSVReader reader=new CSVReader("getNextDateInfo");
        List<JsonNode> testcase=reader.read(4,8);
        return testcase.iterator();
    }

}