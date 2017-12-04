package third_party;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class NextDateTest
{
    private static JsonNode testData_validDateTest;
    private static JsonNode testData_getNextDataInfoTest;
    private static String[] lunarInfoMock=new String[]{"己丑年", "二月小", "初二", "牛"};

    private static JsonNode testData_validDateBoundaryTest;
    private static JsonNode testData_getNextDataInfoBoundaryTest;

    @BeforeClass
    public static void InitTestData()
    {
        testData_validDateTest = IniTestData_validDateTest();
        testData_getNextDataInfoTest=IniTestData_getNextDateInfoTest();

        testData_validDateBoundaryTest=IniTestData_validDataBoundaryTest();
        testData_getNextDataInfoBoundaryTest=IniTestData_getNextDateBoundaryInfoTest();
    }

    private static ObjectNode newObjectNode()
    {
        return new ObjectMapper().createObjectNode();
    }
    private static ArrayNode newArrayNode()
    {
        return new ObjectMapper().createArrayNode();
    }

    public static JsonNode IniTestData_validDateTest()
    {

        ObjectNode testDataSet = new ObjectMapper().createObjectNode();

        //年份不符合要求，大于可用范围，期望返回错误码 errDateOutOfRange = 101
        testDataSet.set("001",
                newObjectNode().put("out",101).set("in",newArrayNode().add(2101).add(1).add(1)));

        //年份不符合，小于可用范围， 期望返回错误码 errDateOutOfRange = 101
        testDataSet.set("002",
                newObjectNode().put("out",101).set("in",newArrayNode().add(1800).add(1).add(1)));

        //月份非法，小于1， 期望返回错误码 errNoSuchDate = 102
        testDataSet.set("003",
                newObjectNode().put("out",102).set("in",newArrayNode().add(2001).add(0).add(1)));

        //月份非法，大于13，期望返回错误码 errNoSuchDate = 102
        testDataSet.set("004",
                newObjectNode().put("out",102).set("in",newArrayNode().add(2001).add(13).add(1)));

        //日期非法，小于1， 期望返回错误码 errNoSuchDate = 102
        testDataSet.set("005",
                newObjectNode().put("out",102).set("in",newArrayNode().add(2007).add(4).add(0)));

        //日期非法，大于该月份的最大日期，非闰年的2月29日， 期望返回错误码 errNoSuchDate = 102
        testDataSet.set("006",
                newObjectNode().put("out",102).set("in",newArrayNode().add(1905).add(2).add(29)));

        //日期非法，大于该月份的最大日期，小月的31日，期望返回错误码 errNoSuchDate = 102
        testDataSet.set("007",
                newObjectNode().put("out",102).set("in",newArrayNode().add(2010).add(9).add(31)));

        //日期非法，大于该月份的最大日期，大月的32日，期望返回错误码 errNoSuchDate = 102
        testDataSet.set("008",
                newObjectNode().put("out",102).set("in",newArrayNode().add(2011).add(8).add(32)));

        //日期非法，大于该月份的最大日期，闰年的2月30日，期望返回错误码 errNoSuchDate = 102
        testDataSet.set("009",
                newObjectNode().put("out",102).set("in",newArrayNode().add(1904).add(2).add(30)));

        //日期合法，期望返回正确码 validSuccess = 100
        testDataSet.set("010",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1968).add(8).add(10)));

        return testDataSet;
    }

    public static JsonNode IniTestData_validDataBoundaryTest()
    {

        ObjectNode testDataSet = new ObjectMapper().createObjectNode();

        //时间正确，期望返回validSuccess 100

        //年份非法，期望返回错误码 errDateOutOfRange = 101
        testDataSet.set("001",
                newObjectNode().put("out",101).set("in",newArrayNode().add(1899).add(4).add(14)));
        testDataSet.set("002",
                newObjectNode().put("out",102).set("in",newArrayNode().add(1900).add(4).add(0)));
        testDataSet.set("003",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1901).add(4).add(1)));
        testDataSet.set("004",
                newObjectNode().put("out",100).set("in",newArrayNode().add(2099).add(4).add(30)));
        testDataSet.set("005",
                newObjectNode().put("out",100).set("in",newArrayNode().add(2100).add(4).add(29)));
        testDataSet.set("006",
                newObjectNode().put("out",101).set("in",newArrayNode().add(2101).add(4).add(30)));

        //月份非法， 期望返回错误码 errNoSuchDate = 102
        testDataSet.set("007",
                newObjectNode().put("out",102).set("in",newArrayNode().add(1994).add(0).add(28)));
        testDataSet.set("008",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1994).add(1).add(29)));
        testDataSet.set("009",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1994).add(2).add(1)));
        testDataSet.set("010",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1994).add(11).add(29)));
        testDataSet.set("011",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1994).add(12).add(30)));
        testDataSet.set("012",
                newObjectNode().put("out",102).set("in",newArrayNode().add(1994).add(13).add(31)));

        //日期非法，期望返回错误码 errNoSuchDate = 102
        testDataSet.set("013",
                newObjectNode().put("out",102).set("in",newArrayNode().add(1994).add(4).add(0)));
        testDataSet.set("014",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1994).add(4).add(1)));
        testDataSet.set("015",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1994).add(4).add(2)));


        testDataSet.set("016",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1994).add(4).add(29)));
        testDataSet.set("017",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1994).add(4).add(30)));
        testDataSet.set("018",
                newObjectNode().put("out",102).set("in",newArrayNode().add(1994).add(4).add(31)));


        testDataSet.set("019",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1994).add(12).add(30)));
        testDataSet.set("020",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1994).add(12).add(31)));
        testDataSet.set("021",
                newObjectNode().put("out",102).set("in",newArrayNode().add(1994).add(12).add(32)));

        testDataSet.set("022",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1994).add(2).add(27)));
        testDataSet.set("023",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1994).add(2).add(28)));
        testDataSet.set("024",
                newObjectNode().put("out",102).set("in",newArrayNode().add(1994).add(2).add(29)));
        testDataSet.set("025",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1996).add(2).add(28)));
        testDataSet.set("026",
                newObjectNode().put("out",100).set("in",newArrayNode().add(1996).add(2).add(29)));
        testDataSet.set("027",
                newObjectNode().put("out",102).set("in",newArrayNode().add(1996).add(2).add(30)));


        return testDataSet;
    }


    private void VerifyValidDateTest(String Item) throws Exception
    {
        JsonNode temp = testData_validDateTest.get(Item).get("in");
        int res=NextDate.validDate(temp.get(0).asInt(),temp.get(1).asInt(),temp.get(2).asInt());
        assertEquals(testData_validDateTest.get(Item).get("out").asInt(),res);
    }

    private void VerifyValidDateBoundaryTest(String Item) throws Exception
    {
        JsonNode temp = testData_validDateBoundaryTest.get(Item).get("in");
        int res=NextDate.validDate(temp.get(0).asInt(),temp.get(1).asInt(),temp.get(2).asInt());
        assertEquals(testData_validDateBoundaryTest.get(Item).get("out").asInt(),res);
    }


    @Test public void validDateTest001() throws Exception { VerifyValidDateTest("001"); }

    @Test public void validDateTest002() throws Exception { VerifyValidDateTest("002"); }

    @Test public void validDateTest003() throws Exception { VerifyValidDateTest("003"); }

    @Test public void validDateTest004() throws Exception { VerifyValidDateTest("004"); }

    @Test public void validDateTest005() throws Exception { VerifyValidDateTest("005"); }

    @Test public void validDateTest006() throws Exception { VerifyValidDateTest("006"); }

    @Test public void validDateTest007() throws Exception { VerifyValidDateTest("007"); }

    @Test public void validDateTest008() throws Exception { VerifyValidDateTest("008"); }

    @Test public void validDateTest009() throws Exception { VerifyValidDateTest("009"); }

    @Test public void validDateTest010() throws Exception { VerifyValidDateTest("010"); }


    @Test public void ValidDateBoundaryTest001() throws  Exception{VerifyValidDateBoundaryTest("001");}
    @Test public void ValidDateBoundaryTest002() throws  Exception{VerifyValidDateBoundaryTest("002");}
    @Test public void ValidDateBoundaryTest003() throws  Exception{VerifyValidDateBoundaryTest("003");}
    @Test public void ValidDateBoundaryTest004() throws  Exception{VerifyValidDateBoundaryTest("004");}
    @Test public void ValidDateBoundaryTest005() throws  Exception{VerifyValidDateBoundaryTest("005");}
    @Test public void ValidDateBoundaryTest006() throws  Exception{VerifyValidDateBoundaryTest("006");}
    @Test public void ValidDateBoundaryTest007() throws  Exception{VerifyValidDateBoundaryTest("007");}
    @Test public void ValidDateBoundaryTest008() throws  Exception{VerifyValidDateBoundaryTest("008");}
    @Test public void ValidDateBoundaryTest009() throws  Exception{VerifyValidDateBoundaryTest("009");}
    @Test public void ValidDateBoundaryTest010() throws  Exception{VerifyValidDateBoundaryTest("010");}

    @Test public void ValidDateBoundaryTest011() throws  Exception{VerifyValidDateBoundaryTest("011");}
    @Test public void ValidDateBoundaryTest012() throws  Exception{VerifyValidDateBoundaryTest("012");}
    @Test public void ValidDateBoundaryTest013() throws  Exception{VerifyValidDateBoundaryTest("013");}
    @Test public void ValidDateBoundaryTest014() throws  Exception{VerifyValidDateBoundaryTest("014");}
    @Test public void ValidDateBoundaryTest015() throws  Exception{VerifyValidDateBoundaryTest("015");}
    @Test public void ValidDateBoundaryTest016() throws  Exception{VerifyValidDateBoundaryTest("016");}
    @Test public void ValidDateBoundaryTest017() throws  Exception{VerifyValidDateBoundaryTest("017");}
    @Test public void ValidDateBoundaryTest018() throws  Exception{VerifyValidDateBoundaryTest("018");}
    @Test public void ValidDateBoundaryTest019() throws  Exception{VerifyValidDateBoundaryTest("019");}
    @Test public void ValidDateBoundaryTest020() throws  Exception{VerifyValidDateBoundaryTest("020");}

    @Test public void ValidDateBoundaryTest021() throws  Exception{VerifyValidDateBoundaryTest("021");}
    @Test public void ValidDateBoundaryTest022() throws  Exception{VerifyValidDateBoundaryTest("022");}
    @Test public void ValidDateBoundaryTest023() throws  Exception{VerifyValidDateBoundaryTest("023");}
    @Test public void ValidDateBoundaryTest024() throws  Exception{VerifyValidDateBoundaryTest("024");}
    @Test public void ValidDateBoundaryTest025() throws  Exception{VerifyValidDateBoundaryTest("025");}
    @Test public void ValidDateBoundaryTest026() throws  Exception{VerifyValidDateBoundaryTest("026");}
    @Test public void ValidDateBoundaryTest027() throws  Exception{VerifyValidDateBoundaryTest("027");}


    private static ObjectNode ConstructTestData(ObjectNode data,String index,ArrayNode out,ArrayNode in)
    {
        ObjectNode temp=newObjectNode();
        temp.set("out",out);
        temp.set("in",in);
        data.set(index,temp);
        return data;
    }
    public static JsonNode IniTestData_getNextDateInfoTest()
    {
        ObjectNode testdata=newObjectNode();

        //1800年1月1日，日期超出范围，期望返回fail以及错误信息：输入的日期超出应用可用范围
        testdata=ConstructTestData(testdata,"001",
                newArrayNode().add(NextDate.FAIL).add("输入的日期超出应用可用范围"),
                newArrayNode().add(1800).add(1).add(1).add(1));

        //2200年1月1日，日期超出范围，期望返回fail以及错误信息：输入的日期超出应用可用范围1
        testdata=ConstructTestData(testdata,"002",
                newArrayNode().add(NextDate.FAIL).add("输入的日期超出应用可用范围"),
                newArrayNode().add(2200).add(1).add(1).add(1));

        //月份无效，日期不存在，期望返回fail以及错误信息：输入的日期不存在
        testdata=ConstructTestData(testdata,"003",
                newArrayNode().add(NextDate.FAIL).add("输入的日期不存在"),
                newArrayNode().add(2001).add(0).add(1).add(1));

        //月份无效，日期不存在，期望返回fail以及错误信息：输入的日期不存在
        testdata=ConstructTestData(testdata,"004",
                newArrayNode().add(NextDate.FAIL).add("输入的日期不存在"),
                newArrayNode().add(2001).add(13).add(1).add(1));

        //2007年4月0日，日期不存在，期望返回fail以及错误信息：输入的日期不存在
        testdata=ConstructTestData(testdata,"005",
                newArrayNode().add(NextDate.FAIL).add("输入的日期不存在"),
                newArrayNode().add(2007).add(4).add(0).add(1));

        //1905年2月29日，日期不存在，期望返回fail以及错误信息：输入的日期不存在
        testdata=ConstructTestData(testdata,"006",
                newArrayNode().add(NextDate.FAIL).add("输入的日期不存在"),
                newArrayNode().add(1905).add(2).add(29).add(1));

        //小月>31，日期不存在，期望返回fail以及错误信息：输入的日期不存在
        testdata=ConstructTestData(testdata,"007",
                newArrayNode().add(NextDate.FAIL).add("输入的日期不存在"),
                newArrayNode().add(2010).add(9).add(31).add(1));

        //2011年8月32日，日期不存在，期望返回fail以及错误信息：输入的日期不存在
        testdata=ConstructTestData(testdata,"008",
                newArrayNode().add(NextDate.FAIL).add("输入的日期不存在"),
                newArrayNode().add(2011).add(8).add(32).add(1));

        //闰年2月>29，日期不存在，期望返回fail以及错误信息：输入的日期不存在
        testdata=ConstructTestData(testdata,"009",
                newArrayNode().add(NextDate.FAIL).add("输入的日期不存在"),
                newArrayNode().add(1904).add(2).add(30).add(1));


        //2100年12月31日，nextdate超出范围，返回NO_LUNAR_INFO以及桩公历信息
        testdata=ConstructTestData(testdata,"010",
                newArrayNode().add(NextDate.NO_LUNAR_INFO).add(2101).add(1).add(1)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2100).add(12).add(31).add(1));

        //n<0 计算前几天：2001年1月6日，计算10天前,_getBackNDateInfo中day<0,month=0
        testdata=ConstructTestData(testdata,"011",
                newArrayNode().add(NextDate.SUCCESS).add(2000).add(12).add(27)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2001).add(1).add(6).add(-10));

        //计算前几天：2000年8月6日，计算10天前,_getBackNDateInfo中day<0,month！=0
        testdata=ConstructTestData(testdata,"012",
                newArrayNode().add(NextDate.SUCCESS).add(2000).add(7).add(27)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2000).add(8).add(6).add(-10));

        //计算前几天：2001年1月5日，计算4天前，_getBackNDateInfo中day=0,month=0
        testdata=ConstructTestData(testdata,"013",
                newArrayNode().add(NextDate.SUCCESS).add(2001).add(1).add(1)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2001).add(1).add(5).add(-4));

        //计算前几天：2001年8月5日，计算4天前，_getBackNDateInfo中day=0,month！=0
        testdata=ConstructTestData(testdata,"014",
                newArrayNode().add(NextDate.SUCCESS).add(2001).add(8).add(1)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2001).add(8).add(5).add(-4));

        //n=1 计算后一天：2007年12月31日，n=1，dayNext>,monthNext>
        testdata=ConstructTestData(testdata,"015",
                newArrayNode().add(NextDate.SUCCESS).add(2008).add(1).add(1)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2007).add(12).add(31).add(1));

        //计算后一天：2008年4月5日，n=1，dayNext<,monthNext<
        testdata=ConstructTestData(testdata,"016",
                newArrayNode().add(NextDate.SUCCESS).add(2008).add(4).add(6)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2008).add(4).add(5).add(1));

        //计算后一天：2008年5月31日，n=1，dayNext<,monthNext<
        testdata=ConstructTestData(testdata,"017",
                newArrayNode().add(NextDate.SUCCESS).add(2008).add(6).add(1)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2008).add(5).add(31).add(1));

        //n>1计算后几天：2007年12月1日，n=32，day超过当月
        testdata=ConstructTestData(testdata,"018",
                newArrayNode().add(NextDate.SUCCESS).add(2008).add(1).add(2)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2007).add(12).add(1).add(32));

        //计算后几天：2008年5月1日，n=3,day不超过当月日期
        testdata=ConstructTestData(testdata,"019",
                newArrayNode().add(NextDate.SUCCESS).add(2008).add(5).add(4)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2008).add(5).add(1).add(3));

        //计算后几天：2008年3月10日，n=30,day超过当月日期但month不等于13
        testdata=ConstructTestData(testdata,"020",
                newArrayNode().add(NextDate.SUCCESS).add(2008).add(4).add(9)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2008).add(3).add(10).add(30));

        //计算后几天：2008年3月10日，n=30,day超过当月日期但month不等于13
        testdata=ConstructTestData(testdata,"021",
                newArrayNode().add(NextDate.SUCCESS).add(2008).add(12).add(1)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2008).add(11).add(10).add(21));

        return testdata;
    }


    public static JsonNode IniTestData_getNextDateBoundaryInfoTest()
    {
        ObjectNode testDataSet=newObjectNode();

        //1800年1月1日，日期超出范围，期望返回fail以及错误信息：输入的日期超出应用可用范围
        testDataSet=ConstructTestData(testDataSet,"001",
                newArrayNode().add(NextDate.FAIL).add("nextdate的日期超出范围"),
                newArrayNode().add(1900).add(1).add(1).add(Integer.MIN_VALUE));
        testDataSet=ConstructTestData(testDataSet,"002",
                newArrayNode().add(NextDate.FAIL).add("nextdate的日期超出范围"),
                newArrayNode().add(1900).add(1).add(1).add(-1));
        testDataSet=ConstructTestData(testDataSet,"003",
                newArrayNode().add(NextDate.SUCCESS).add(1900).add(1).add(1)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(1900).add(1).add(1).add(0));
        testDataSet=ConstructTestData(testDataSet,"004",
                newArrayNode().add(NextDate.SUCCESS).add(1900).add(1).add(2)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(1900).add(1).add(1).add(1));
        testDataSet=ConstructTestData(testDataSet,"005",
                newArrayNode().add(NextDate.FAIL).add("nextdate的日期超出范围"),
                newArrayNode().add(1900).add(1).add(1).add(Integer.MAX_VALUE));
        testDataSet=ConstructTestData(testDataSet,"006",
                newArrayNode().add(NextDate.FAIL).add("nextdate的日期超出范围"),
                newArrayNode().add(2100).add(12).add(31).add(Integer.MIN_VALUE));
        testDataSet=ConstructTestData(testDataSet,"007",
                newArrayNode().add(NextDate.NO_LUNAR_INFO).add(2100).add(12).add(30)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2100).add(12).add(31).add(-1));
        testDataSet=ConstructTestData(testDataSet,"008",
                newArrayNode().add(NextDate.NO_LUNAR_INFO).add(2100).add(12).add(31)
                        .add(lunarInfoMock[0]).add(lunarInfoMock[1]).add(lunarInfoMock[2]).add(lunarInfoMock[3]),
                newArrayNode().add(2100).add(12).add(31).add(0));
        testDataSet=ConstructTestData(testDataSet,"009",
                newArrayNode().add(NextDate.FAIL).add("nextdate的日期超出范围"),
                newArrayNode().add(2100).add(12).add(31).add(1));
        testDataSet=ConstructTestData(testDataSet,"010",
                newArrayNode().add(NextDate.FAIL).add("nextdate的日期超出范围"),
                newArrayNode().add(2100).add(12).add(31).add(Integer.MAX_VALUE));

        return testDataSet;
    }



    private void VerifyGetNextDateInfo(String index) throws Exception
    {
        NextDate nextDate=new NextDate();
        //造木桩 模拟对象以及对象返回值
        LunarUtil mockLunarUtil = mock(LunarUtil.class);
        when(mockLunarUtil.getLunarDateInfo(anyInt(),anyInt(),anyInt())).thenReturn(lunarInfoMock);
        // 打桩
        nextDate.setLunarUtil(mockLunarUtil);
        JsonNode temp=testData_getNextDataInfoTest.get(index);
        //ArrayList<String> list=new ObjectMapper().readValue(temp.get("out").toString(),new TypeReference<ArrayList<String>>(){});
        assertEquals(new ObjectMapper().readValue(temp.get("out").toString(),new TypeReference<List<String>>(){}),
                nextDate.getNextDateInfo(temp.get("in").get(0).asInt(),temp.get("in").get(1).asInt(),temp.get("in").get(2).asInt(),temp.get("in").get(3).asInt()));

    }


    private void VerifyGetNextDateInfoBoundaryTest(String index) throws Exception
    {
        NextDate nextDate=new NextDate();
        //造木桩 模拟对象以及对象返回值
        LunarUtil mockLunarUtil = mock(LunarUtil.class);
        when(mockLunarUtil.getLunarDateInfo(anyInt(),anyInt(),anyInt())).thenReturn(lunarInfoMock);
        // 打桩
        nextDate.setLunarUtil(mockLunarUtil);
        JsonNode temp=testData_getNextDataInfoBoundaryTest.get(index);
        //ArrayList<String> list=new ObjectMapper().readValue(temp.get("out").toString(),new TypeReference<ArrayList<String>>(){});
        assertEquals(new ObjectMapper().readValue(temp.get("out").toString(),new TypeReference<List<String>>(){}),
                nextDate.getNextDateInfo(temp.get("in").get(0).asInt(),temp.get("in").get(1).asInt(),temp.get("in").get(2).asInt(),temp.get("in").get(3).asInt()));
    }



    @Test public void GetNextDateInfoTest001() throws Exception { VerifyGetNextDateInfo("001"); }

    @Test public void GetNextDateInfoTest002() throws Exception { VerifyGetNextDateInfo("002"); }

    @Test public void GetNextDateInfoTest003() throws Exception { VerifyGetNextDateInfo("003"); }

    @Test public void GetNextDateInfoTest004() throws Exception { VerifyGetNextDateInfo("004"); }

    @Test public void GetNextDateInfoTest005() throws Exception { VerifyGetNextDateInfo("005"); }

    @Test public void GetNextDateInfoTest006() throws Exception { VerifyGetNextDateInfo("006"); }

    @Test public void GetNextDateInfoTest007() throws Exception { VerifyGetNextDateInfo("007"); }

    @Test public void GetNextDateInfoTest008() throws Exception { VerifyGetNextDateInfo("008"); }

    @Test public void GetNextDateInfoTest009() throws Exception { VerifyGetNextDateInfo("009"); }

    @Test public void GetNextDateInfoTest010() throws Exception { VerifyGetNextDateInfo("010"); }

    @Test public void GetNextDateInfoTest011() throws Exception { VerifyGetNextDateInfo("011"); }

    @Test public void GetNextDateInfoTest012() throws Exception { VerifyGetNextDateInfo("012"); }

    @Test public void GetNextDateInfoTest013() throws Exception { VerifyGetNextDateInfo("013"); }

    @Test public void GetNextDateInfoTest014() throws Exception { VerifyGetNextDateInfo("014"); }

    @Test public void GetNextDateInfoTest015() throws Exception { VerifyGetNextDateInfo("015"); }

    @Test public void GetNextDateInfoTest016() throws Exception { VerifyGetNextDateInfo("016"); }

    @Test public void GetNextDateInfoTest017() throws Exception { VerifyGetNextDateInfo("017"); }

    @Test public void GetNextDateInfoTest018() throws Exception { VerifyGetNextDateInfo("018"); }

    @Test public void GetNextDateInfoTest019() throws Exception { VerifyGetNextDateInfo("019"); }

    @Test public void GetNextDateInfoTest020() throws Exception { VerifyGetNextDateInfo("020"); }
    @Test public void GetNextDateInfoTest021() throws Exception { VerifyGetNextDateInfo("021"); }


    @Test public void GetNextDateInfoBoundaryTest001() throws  Exception{VerifyGetNextDateInfoBoundaryTest("001");}
    @Test public void GetNextDateInfoBoundaryTest002() throws  Exception{VerifyGetNextDateInfoBoundaryTest("002");}
    @Test public void GetNextDateInfoBoundaryTest003() throws  Exception{VerifyGetNextDateInfoBoundaryTest("003");}
    @Test public void GetNextDateInfoBoundaryTest004() throws  Exception{VerifyGetNextDateInfoBoundaryTest("004");}
    @Test public void GetNextDateInfoBoundaryTest005() throws  Exception{VerifyGetNextDateInfoBoundaryTest("005");}
    @Test public void GetNextDateInfoBoundaryTest006() throws  Exception{VerifyGetNextDateInfoBoundaryTest("006");}
    @Test public void GetNextDateInfoBoundaryTest007() throws  Exception{VerifyGetNextDateInfoBoundaryTest("007");}

    @Test public void GetNextDateInfoBoundaryTest008() throws  Exception{VerifyGetNextDateInfoBoundaryTest("008");}
    @Test public void GetNextDateInfoBoundaryTest009() throws  Exception{VerifyGetNextDateInfoBoundaryTest("009");}
    @Test public void GetNextDateInfoBoundaryTest010() throws  Exception{VerifyGetNextDateInfoBoundaryTest("010");}




}