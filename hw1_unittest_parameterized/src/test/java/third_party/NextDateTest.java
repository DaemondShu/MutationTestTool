package third_party;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Iterator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NextDateTest
{



//    @Test
//    public void test()
//    {
//        CSVReader reader=new CSVReader("validDate");
//        reader.read();
//    }

    @ParameterizedTest
    @MethodSource("ValidDateTestcasesProvider")
    void VaildDateTest(JsonNode testcase) throws Exception
    {
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
}