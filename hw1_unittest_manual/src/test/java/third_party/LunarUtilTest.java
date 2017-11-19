package third_party;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by SunMeng on 2017/10/15.
 */

public class LunarUtilTest {

//    @Test
//    public void getDayNumTest() throws Exception
//    {
//        LunarUtil lunarUtil=new LunarUtil();
//        Set<Integer> dayList=new HashSet<>();
//        int size=dayList.size();
//        for(int i=LunarUtil.yearBase;i<=LunarUtil.yearUpper;i++)
//        {
//            int res = LunarUtil.getDayNum(LunarUtil.lunarInfo[i-1900]);
//            if(res==382) System.out.print("found");
//            dayList.add(res);
//            if(dayList.size()>size)
//            {
//                System.out.println(res+" : "+i);
//                size=dayList.size();
//
//            }
//        }
//        System.out.println(dayList.size());
//        //for(int i=0;i<dayList.size();i++)
//            System.out.println(dayList);
//
//    }


    private static JsonNode testData_GetDayNumTest;
    private static JsonNode testData_GetLunarDateInfoTest;

    private static JsonNode testData_GetDayNumBoundaryTest;
    private static JsonNode testData_GetLunarDateInfoBoundaryTest;

    @BeforeClass
    public static void InitTestData()
    {
        testData_GetDayNumTest = IniTestData_GetDayNumTest();
        testData_GetLunarDateInfoTest=IniTestData_GetLunarDateInfoTest();

        testData_GetDayNumBoundaryTest=IniTestData_GetDayNumBoundaryTest();
        testData_GetLunarDateInfoBoundaryTest=IniTestData_GetLunarDateInfoBoundaryTest();
    }

    private static ObjectNode newObjectNode()
    {
        return new ObjectMapper().createObjectNode();
    }

    private static ArrayNode newArrayNode()
    {
        return new ObjectMapper().createArrayNode();
    }
    public static JsonNode IniTestData_GetDayNumTest()
    {
        ObjectNode testDataSet = new ObjectMapper().createObjectNode();
        testDataSet.set("001",   //没有闰月，7个29天的月份，5个30天的月份 7*29+5*30=353
                newObjectNode().put("in", LunarUtil.lunarInfo[65]).put("out", 353));
        testDataSet.set("002",   //没有闰月，6个29天的月份，6个30天的月份 6*29+6*30=354
                newObjectNode().put("in", LunarUtil.lunarInfo[1]).put("out", 354));
        testDataSet.set("003",   //没有闰月，5个29天的月份，7个30天的月份 5*29+7*30=355
                newObjectNode().put("in", LunarUtil.lunarInfo[2]).put("out", 355));
        testDataSet.set("004",   //有闰月，7个29天的月份，6个30天的月份 7*29+6*30=383
                newObjectNode().put("in", LunarUtil.lunarInfo[3]).put("out", 383));
        testDataSet.set("005",   //有闰月，6个29天的月份，7个30天的月份 6*29+7*30=384
                newObjectNode().put("in", LunarUtil.lunarInfo[0]).put("out", 384));
        testDataSet.set("006",   //有闰月，5个29天的月份，8个30天的月份 5*29+8*30=385
                newObjectNode().put("in", LunarUtil.lunarInfo[25]).put("out", 385));
        testDataSet.set("007",   //输入非法的16进制信息，期望返回-1
                newObjectNode().put("in", 0x00000).put("out", -1));

        return testDataSet;

    }

    public static JsonNode IniTestData_GetDayNumBoundaryTest(){
        ObjectNode testDataSet = new ObjectMapper().createObjectNode();

        testDataSet.set("001",   //没有闰月的边界值测试
                newObjectNode().put("in", LunarUtil.lunarInfo[134]).put("out", 354));
        testDataSet.set("002",   //闰月是小闰月在十一月的边界值测试
                newObjectNode().put("in", LunarUtil.lunarInfo[84]).put("out", 384));
        testDataSet.set("003",   //闰月在十二月的边界值测试
                newObjectNode().put("in", LunarUtil.lunarInfo[133]).put("out", 384));
        testDataSet.set("004",   //闰月是大闰月的边界值测试
                newObjectNode().put("in", LunarUtil.lunarInfo[136]).put("out", 384));
        testDataSet.set("005",   //闰月的超出边界值测试
                newObjectNode().put("in", 0x04afc).put("out", ""));
        testDataSet.set("006",   //闰月在一月的边界值测试
                newObjectNode().put("in", 0x04ad1).put("out", ""));
        testDataSet.set("007",   //闰月大小之外的边界值测试
                newObjectNode().put("in", 0x2d0b6).put("out", ""));
        testDataSet.set("008",   //数组0的边界值测试
                newObjectNode().put("in", LunarUtil.lunarInfo[0]).put("out", 384));
        testDataSet.set("009",   //数组1的边界值测试
                newObjectNode().put("in", LunarUtil.lunarInfo[1]).put("out", 354));
        testDataSet.set("010",   //数组199的边界值测试
                newObjectNode().put("in", LunarUtil.lunarInfo[199]).put("out", 384));
        testDataSet.set("011",   //数组200的边界值测试
                newObjectNode().put("in", LunarUtil.lunarInfo[200]).put("out", 354));



        return testDataSet;
    }


    private static ObjectNode ConstructTestData(ObjectNode data, String index, ArrayNode out, ArrayNode in)
    {
        ObjectNode temp=newObjectNode();
        temp.set("out",out);
        temp.set("in",in);
        data.set(index,temp);
        return data;
    }
    public static JsonNode IniTestData_GetLunarDateInfoTest()
    {
        ObjectNode testdata=newObjectNode();
        //年份小于应用范围
        testdata=ConstructTestData(testdata,"001",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1800).add(1).add(1));
        //年份大于应用范围
        testdata=ConstructTestData(testdata,"002",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(2200).add(1).add(1));

        //月份不合法，小于1，期望输出["","","",""]，但是输出["庚子年","闰八月小","廿六","鼠"]
        testdata=ConstructTestData(testdata,"003",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1901).add(-2).add(20));

        //月份不合法，大于12，期望输出["","","",""]，但是输出["辛丑年","腊月小","初一","牛"]
        testdata=ConstructTestData(testdata,"004",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1901).add(13).add(10));

        //日期不合法，小月>31，期望输出["","","",""]，但是输出["庚辰年","六月小","廿九","龙"]
        testdata=ConstructTestData(testdata,"005",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(2000).add(6).add(60));

        //日期不合法，日期小于1，期望输出["","","",""]，但是输出["庚辰年","五月大","廿八","龙"]
        testdata=ConstructTestData(testdata,"006",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(2000).add(7).add(-1));

        //日期不合法，闰年2月>29,期望输出["","","",""]，但是输出["庚辰年","正月大","廿六","龙"]

        testdata=ConstructTestData(testdata,"007",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(2000).add(2).add(30));

        //日期合法，闰年2月1-29，期望输出["庚辰年","正月大","十六","龙"]
        testdata=ConstructTestData(testdata,"008",
                newArrayNode().add("庚辰年").add("正月大").add("十六").add("龙"),
                newArrayNode().add(2000).add(2).add(20));

        //日期合法，平年2月1-28，期望输出["戊寅年","正月大","廿五","虎"]
        testdata=ConstructTestData(testdata,"009",
                newArrayNode().add("戊寅年").add("正月大").add("廿五").add("虎"),
                newArrayNode().add(1999).add(2).add(10));

        //日期不合法.平年2月>28，期望输出["","","",""]，但是输出["己卯年","正月大","十四","兔"]
        testdata=ConstructTestData(testdata,"010",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1999).add(2).add(29));

        //日期不合法，大月>31，期望输出["","","",""]，但是输出["乙亥年","八月大","初七","猪"]
        testdata=ConstructTestData(testdata,"011",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1995).add(8).add(32));

        //正常情况:total<30,期望结果：["己亥年","腊月大","十九","猪"]
        testdata=ConstructTestData(testdata,"012",
                newArrayNode().add("己亥年").add("腊月大").add("十九").add("猪"),
                newArrayNode().add(1900).add(1).add(20));

        //正常情况:total>30,total < daysInYear,期望结果：["庚子年","闰八月小","廿六","鼠"]
        testdata=ConstructTestData(testdata,"013",
                newArrayNode().add("庚子年").add("闰八月小").add("廿六").add("鼠"),
                newArrayNode().add(1900).add(10).add(20));

        //正常情况:total>30,total > daysInYear,total> monthDays,quotient=0
        //期望结果：["庚辰年","四月小","初七","龙"]
        testdata=ConstructTestData(testdata,"014",
                newArrayNode().add("庚辰年").add("四月小").add("初七").add("龙"),
                newArrayNode().add(2000).add(5).add(10));

        //正常情况:total>30,total > daysInYear,remainder=9,期望结果：["戊辰年","九月小","二十","龙"]
        testdata=ConstructTestData(testdata,"015",
                newArrayNode().add("戊辰年").add("九月小").add("二十").add("龙"),
                newArrayNode().add(1988).add(10).add(30));

        return testdata;
    }

    public static JsonNode IniTestData_GetLunarDateInfoBoundaryTest(){
        ObjectNode testDataSet=newObjectNode();
        //年份的边界值测试
        testDataSet=ConstructTestData(testDataSet,"001",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1899).add(4).add(14));
        testDataSet=ConstructTestData(testDataSet,"002",
                newArrayNode().add("庚子年").add("三月小").add("十五").add("鼠"),
                newArrayNode().add(1900).add(4).add(14));
        testDataSet=ConstructTestData(testDataSet,"003",
                newArrayNode().add("辛丑年").add("二月大").add("廿六").add("牛"),
                newArrayNode().add(1901).add(4).add(14));
        testDataSet=ConstructTestData(testDataSet,"004",
                newArrayNode().add("己未年").add("闰二月小").add("廿四").add("羊"),
                newArrayNode().add(2099).add(4).add(14));
        testDataSet=ConstructTestData(testDataSet,"005",
                newArrayNode().add("庚申年").add("三月小").add("初五").add("猴"),
                newArrayNode().add(2100).add(4).add(14));
        testDataSet=ConstructTestData(testDataSet,"006",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(2101).add(4).add(14));
        //月份的边界值测试 代码里并没有对月超出范围做判断
        testDataSet=ConstructTestData(testDataSet,"007",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1994).add(0).add(14));
        testDataSet=ConstructTestData(testDataSet,"008",
                newArrayNode().add("癸酉年").add("腊月小").add("初三").add("鸡"),
                newArrayNode().add(1994).add(1).add(14));
        testDataSet=ConstructTestData(testDataSet,"009",
                newArrayNode().add("甲戌年").add("正月大").add("初五").add("狗"),
                newArrayNode().add(1994).add(2).add(14));
        testDataSet=ConstructTestData(testDataSet,"010",
                newArrayNode().add("甲戌年").add("十月大").add("十二").add("狗"),
                newArrayNode().add(1994).add(11).add(14));
        testDataSet=ConstructTestData(testDataSet,"011",
                newArrayNode().add("甲戌年").add("十一月小").add("十二").add("狗"),
                newArrayNode().add(1994).add(12).add(14));
        testDataSet=ConstructTestData(testDataSet,"012",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1994).add(13).add(14));
        //日期的边界值测试  代码里并没有对日超出范围做判断
        testDataSet=ConstructTestData(testDataSet,"013",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1994).add(4).add(0));
        testDataSet=ConstructTestData(testDataSet,"014",
                newArrayNode().add("甲戌年").add("二月大").add("廿一").add("狗"),
                newArrayNode().add(1994).add(4).add(1));
        testDataSet=ConstructTestData(testDataSet,"015",
                newArrayNode().add("甲戌年").add("二月大").add("廿二").add("狗"),
                newArrayNode().add(1994).add(4).add(2));
        testDataSet=ConstructTestData(testDataSet,"016",
                newArrayNode().add("甲戌年").add("三月大").add("十九").add("狗"),
                newArrayNode().add(1994).add(4).add(29));
        testDataSet=ConstructTestData(testDataSet,"017",
                newArrayNode().add("甲戌年").add("三月大").add("二十").add("狗"),
                newArrayNode().add(1994).add(4).add(30));
        testDataSet=ConstructTestData(testDataSet,"018",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1994).add(4).add(31));
        testDataSet=ConstructTestData(testDataSet,"019",
                newArrayNode().add("甲戌年").add("十一月小").add("廿八").add("狗"),
                newArrayNode().add(1994).add(12).add(30));
        testDataSet=ConstructTestData(testDataSet,"020",
                newArrayNode().add("甲戌年").add("十一月小").add("廿九").add("狗"),
                newArrayNode().add(1994).add(12).add(31));
        testDataSet=ConstructTestData(testDataSet,"021",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1994).add(12).add(32));
        testDataSet=ConstructTestData(testDataSet,"022",
                newArrayNode().add("甲戌年").add("正月大").add("十八").add("狗"),
                newArrayNode().add(1994).add(2).add(27));
        testDataSet=ConstructTestData(testDataSet,"023",
                newArrayNode().add("甲戌年").add("正月大").add("十九").add("狗"),
                newArrayNode().add(1994).add(2).add(28));
        testDataSet=ConstructTestData(testDataSet,"024",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1994).add(2).add(29));
        testDataSet=ConstructTestData(testDataSet,"025",
                newArrayNode().add("丙子年").add("正月小").add("初十").add("鼠"),
                newArrayNode().add(1996).add(2).add(28));
        testDataSet=ConstructTestData(testDataSet,"026",
                newArrayNode().add("丙子年").add("正月小").add("十一").add("鼠"),
                newArrayNode().add(1996).add(2).add(29));
        testDataSet=ConstructTestData(testDataSet,"027",
                newArrayNode().add("").add("").add("").add(""),
                newArrayNode().add(1996).add(2).add(30));
        return testDataSet;
    }


    /**
     * common test method for GetDayNum
     */
    private void VerifyGetDayNumTest(String Item) throws Exception
    {
        JsonNode temp = testData_GetDayNumTest.get(Item);
        assertEquals(temp.get("out").asInt(), LunarUtil.getDayNum(temp.get("in").asInt()));
    }
    private void VerifyGetLunarDateInfoTest(String index) throws Exception
    {
        LunarUtil lunarUtil=new LunarUtil();
        JsonNode temp=testData_GetLunarDateInfoTest.get(index);
        assertEquals(new ObjectMapper().readValue(temp.get("out").toString(),new TypeReference<String[]>(){}),
                lunarUtil.getLunarDateInfo(temp.get("in").get(0).asInt(),temp.get("in").get(1).asInt(),temp.get("in").get(2).asInt()));
    }

    private void VerifyGetDayNumBoundaryTest(String Item) throws Exception{
        JsonNode temp=testData_GetDayNumBoundaryTest.get(Item);
        assertEquals(temp.get("out").asInt(),LunarUtil.getDayNum(temp.get("in").asInt()));
    }

    private void VerifyGetLunarDateInfoBoundaryTest(String index) throws Exception{
        LunarUtil lunarUtil=new LunarUtil();
        JsonNode temp=testData_GetLunarDateInfoBoundaryTest.get(index);
        assertEquals(new ObjectMapper().readValue(temp.get("out").toString(), new TypeReference<String[]>(){}),
                lunarUtil.getLunarDateInfo(
                temp.get("in").get(0).asInt(),temp.get("in").get(1).asInt(),temp.get("in").get(2).asInt()));
    }
//    /**
//     * 没有闰月，7个29天的月份，5个30天的月份
//     * 7*29+5*30=353
//     * @throws Exception
//     */
//    @Test
//    public void getDayNumTest001() throws Exception
//    {
//        int daynum = LunarUtil.getDayNum(LunarUtil.lunarInfo[65]);
//        assertEquals(353, daynum);
//    }
//
//
//    /**
//     * 没有闰月，6个29天的月份，6个30天的月份
//     * 6*29+6*30=354
//     * @throws Exception
//     */
//    @Test
//    public void getDayNumTest002() throws Exception
//    {
//        int daynum=LunarUtil.getDayNum(LunarUtil.lunarInfo[1]);
//        assertEquals(354,daynum);
//    }
//
//
//
//    /**
//     * 没有闰月，5个29天的月份，7个30天的月份
//     * 5*29+7*30=355
//     * @throws Exception
//     */
//    @Test
//    public void getDayNumTest003() throws Exception
//    {
//        int daynum=LunarUtil.getDayNum(LunarUtil.lunarInfo[2]);
//        assertEquals(355,daynum);
//    }
//
//    /**
//     * 有闰月，7个29天的月份，6个30天的月份
//     * 7*29+6*30=383
//     * @throws Exception
//     */
//    @Test
//    public void getDayNumTest004() throws Exception
//    {
//        int daynum=LunarUtil.getDayNum(LunarUtil.lunarInfo[3]);
//        assertEquals(383,daynum);
//    }
//
//    /**
//     * 有闰月，6个29天的月份，7个30天的月份
//     * 6*29+7*30=384
//     * @throws Exception
//     */
//    @Test
//    public void getDayNumTest005() throws Exception
//    {
//        int daynum=LunarUtil.getDayNum(LunarUtil.lunarInfo[0]);
//        assertEquals(384,daynum);
//    }
//
//    /**
//     * 有闰月，5个29天的月份，8个30天的月份
//     * 5*29+8*30=385
//     * @throws Exception
//     */
//    @Test
//    public void getDayNumTest006() throws Exception
//    {
//        int daynum=LunarUtil.getDayNum(LunarUtil.lunarInfo[25]);
//        assertEquals(385,daynum);
//    }
//
//    /**
//     * 输入非法的16进制信息，期望返回-1
//     * @throws Exception
//     */
//    @Test
//    public void getDayNumTest007() throws Exception
//    {
//        int daynum=LunarUtil.getDayNum(0x00000);
//        assertEquals(-1,daynum);
//    }

    @Test public void getDayNumTest001() throws Exception { VerifyGetDayNumTest("001"); }

    @Test public void getDayNumTest002() throws Exception { VerifyGetDayNumTest("002"); }

    @Test public void getDayNumTest003() throws Exception { VerifyGetDayNumTest("003"); }

    @Test public void getDayNumTest004() throws Exception { VerifyGetDayNumTest("004"); }

    @Test public void getDayNumTest005() throws Exception { VerifyGetDayNumTest("005"); }

    @Test public void getDayNumTest006() throws Exception { VerifyGetDayNumTest("006"); }

    @Test public void getDayNumTest007() throws Exception { VerifyGetDayNumTest("007"); }



    @Test public void getLunarDateInfoTest001() throws Exception { VerifyGetLunarDateInfoTest("001"); }

    @Test public void getLunarDateInfoTest002() throws Exception { VerifyGetLunarDateInfoTest("002"); }

    @Test public void getLunarDateInfoTest003() throws Exception { VerifyGetLunarDateInfoTest("003"); }

    @Test public void getLunarDateInfoTest004() throws Exception { VerifyGetLunarDateInfoTest("004"); }

    @Test public void getLunarDateInfoTest005() throws Exception { VerifyGetLunarDateInfoTest("005"); }

    @Test public void getLunarDateInfoTest006() throws Exception { VerifyGetLunarDateInfoTest("006"); }

    @Test public void getLunarDateInfoTest007() throws Exception { VerifyGetLunarDateInfoTest("007"); }

    @Test public void getLunarDateInfoTest008() throws Exception { VerifyGetLunarDateInfoTest("008"); }

    @Test public void getLunarDateInfoTest009() throws Exception { VerifyGetLunarDateInfoTest("009"); }

    @Test public void getLunarDateInfoTest010() throws Exception { VerifyGetLunarDateInfoTest("010"); }

    @Test public void getLunarDateInfoTest011() throws Exception { VerifyGetLunarDateInfoTest("011"); }

    @Test public void getLunarDateInfoTest012() throws Exception { VerifyGetLunarDateInfoTest("012"); }

    @Test public void getLunarDateInfoTest013() throws Exception { VerifyGetLunarDateInfoTest("013"); }

    @Test public void getLunarDateInfoTest014() throws Exception { VerifyGetLunarDateInfoTest("014"); }

    @Test public void getLunarDateInfoTest015() throws Exception { VerifyGetLunarDateInfoTest("015"); }


    // ------------------------ 边界类 ------------------------------


    @Test public void getDayNumBoundaryTest001() throws Exception{VerifyGetDayNumBoundaryTest("001");}
    @Test public void getDayNumBoundaryTest002() throws Exception{VerifyGetDayNumBoundaryTest("002");}
    @Test public void getDayNumBoundaryTest003() throws Exception{VerifyGetDayNumBoundaryTest("003");}
    @Test public void getDayNumBoundaryTest004() throws Exception{VerifyGetDayNumBoundaryTest("004");}
    //05 06 07明明是我瞎丢进去的数，就不该出结果的，他居然出了
    @Test public void getDayNumBoundaryTest005() throws Exception{VerifyGetDayNumBoundaryTest("005");}
    @Test public void getDayNumBoundaryTest006() throws Exception{VerifyGetDayNumBoundaryTest("006");}
    @Test public void getDayNumBoundaryTest007() throws Exception{VerifyGetDayNumBoundaryTest("007");}

    @Test public void getDayNumBoundaryTest008() throws Exception{VerifyGetDayNumBoundaryTest("008");}
    @Test public void getDayNumBoundaryTest009() throws Exception{VerifyGetDayNumBoundaryTest("009");}
    @Test public void getDayNumBoundaryTest010() throws Exception{VerifyGetDayNumBoundaryTest("010");}
    @Test public void getDayNumBoundaryTest011() throws Exception{VerifyGetDayNumBoundaryTest("011");}


    @Test public void
    getLunarDateInfoBoundaryTest001() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("001");}
    //万年历查出来是 三月小十五，它算出来是十四
    @Test public void getLunarDateInfoBoundaryTest002() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("002");}
    @Test public void getLunarDateInfoBoundaryTest003() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("003");}
    @Test public void getLunarDateInfoBoundaryTest004() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("004");}
    @Test public void getLunarDateInfoBoundaryTest005() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("005");}
    @Test public void getLunarDateInfoBoundaryTest006() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("006");}
    //1994年0月也能算出来
    @Test public void getLunarDateInfoBoundaryTest007() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("007");}
    //万年历查出来是腊月小，它是正月小
    @Test public void getLunarDateInfoBoundaryTest008() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("008");}
    @Test public void getLunarDateInfoBoundaryTest009() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("009");}
    @Test public void getLunarDateInfoBoundaryTest010() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("010");}

    @Test public void getLunarDateInfoBoundaryTest011() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("011");}
    //1994想13.14 它首先要有个对象
    @Test public void getLunarDateInfoBoundaryTest012() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("012");}
    //0就是空 空就是""""""""
    @Test public void getLunarDateInfoBoundaryTest013() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("013");}
    @Test public void getLunarDateInfoBoundaryTest014() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("014");}
    @Test public void getLunarDateInfoBoundaryTest015() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("015");}
    @Test public void getLunarDateInfoBoundaryTest016() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("016");}
    @Test public void getLunarDateInfoBoundaryTest017() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("017");}
    //4月31 小伙子可以啊
    @Test public void getLunarDateInfoBoundaryTest018() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("018");}
    @Test public void getLunarDateInfoBoundaryTest019() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("019");}
    @Test public void getLunarDateInfoBoundaryTest020() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("020");}

    //你就说那一年有12月32日，我吃俩双飞燕给你看
    @Test public void getLunarDateInfoBoundaryTest021() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("021");}
    @Test public void getLunarDateInfoBoundaryTest022() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("022");}
    @Test public void getLunarDateInfoBoundaryTest023() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("023");}
    //1994年要有2月29日，我把键盘吃了
    @Test public void getLunarDateInfoBoundaryTest024() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("024");}
    @Test public void getLunarDateInfoBoundaryTest025() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("025");}
    @Test public void getLunarDateInfoBoundaryTest026() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("026");}
    //哪个丙子年有2月30日，你找出来我把显示器吃了
    @Test public void getLunarDateInfoBoundaryTest027() throws  Exception{VerifyGetLunarDateInfoBoundaryTest("027");}





}
