/**
 * 被测源代码来自﻿https://github.com/git-new/next_date.git 然后做了少改动。
 */
package third_party;

import java.util.Date;

/**
 * create by __yore
 * 公历转农历工具类(1900~2100)
 */

public class LunarUtil
{
    // 农历信息表上下限
    public static final int yearBase = 1900;
    public static final int yearUpper = 2100;
    /**
     * 农历信息表 由5位十六进制数组成
     * 组成示例
     * 20<====== 数位 ========>1
     * 0000 0100 1011 1101 1000     // 1900年
     * 1~4位     表示当年是否有闰年，有则为闰年的月份，没有则为0000
     * 5~16位    表示当年各月是大月还是小月，大月为1，小月为0
     * 17~20位   表示今年的闰月是润大月还是润小月
     * 以1900年为例，1~4位为8，即润8月
     * 5~16位说明这年的月份为：小大小小 大小大大 大大小大
     * 17~20位为0说明闰月8月为润小月，则这年的月份变为：小大小小 大小大大+小 大大小大
     * 其中大月30天，小月29天
     */
    public static long[] lunarInfo = {
            0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,//1900-1909
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,//1910-1919
            0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,//1920-1929
            0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,//1930-1939
            0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,//1940-1949
            0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5b0, 0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0,//1950-1959
            0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,//1960-1969
            0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6,//1970-1979
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,//1980-1989
            0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,//1990-1999
            0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,//2000-2009
            0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,//2010-2019
            0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,//2020-2029
            0x05aa0, 0x076a3, 0x096d0, 0x04afb, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,//2030-2039
            0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0,//2040-2049
            0x14b63, 0x09370, 0x049f8, 0x04970, 0x064b0, 0x168a6, 0x0ea50, 0x06b20, 0x1a6c4, 0x0aae0,//2050-2059
            0x0a2e0, 0x0d2e3, 0x0c960, 0x0d557, 0x0d4a0, 0x0da50, 0x05d55, 0x056a0, 0x0a6d0, 0x055d4,//2060-2069
            0x052d0, 0x0a9b8, 0x0a950, 0x0b4a0, 0x0b6a6, 0x0ad50, 0x055a0, 0x0aba4, 0x0a5b0, 0x052b0,//2070-2079
            0x0b273, 0x06930, 0x07337, 0x06aa0, 0x0ad50, 0x14b55, 0x04b60, 0x0a570, 0x054e4, 0x0d160,//2080-2089
            0x0e968, 0x0d520, 0x0daa0, 0x16aa6, 0x056d0, 0x04ae0, 0x0a9d4, 0x0a2d0, 0x0d150, 0x0f252,//2090-2099
            0x0d520                                                                                  //2100
    };
    // 农历中文支持
    private static final String[] gan =
            {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] zhi =
            {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    private static final String[] chineseZodiac =
            {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
    private static final String[] dayLater =
            {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
    private static final String[] dayPre = {"初", "十", "二", "三", "廿"};
    private static final String[] chineseMonthName =
            {"腊", "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "腊"};
    private static final String[] monthSize = {"小", "大"};
    private static final String strYear = "年";
    private static final String strMonth = "月";
    private static final String strLeap = "闰";

    private static int differentDaysByMillisecond(Date date2)
    {
        Date date1 = new Date(0, 0, 1);

        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }
    /**
     * 公历转农历主函数
     *
     * @param year  公历年
     * @param month 公历月
     * @param day   公历日
     * @return 农历信息表字符串数组数组 , 失败返回空字符串数组
     */
    public String[] getLunarDateInfo(int year, int month, int day)
    {

        // 越界直接返回
        if (year > yearUpper || year < yearBase) return new String[]{"", "", "", ""};
        int total=differentDaysByMillisecond(new Date(year-yearBase,month-1,day));
        // 正常算法
        // 1900年为庚子年
        int ganIndex = 6;   // 天干
        int zhiIndex = 0;   // 地支，生肖
        int monthIndex = 1; // 月份
        int sizeIndex = 0;  // 月份大小
        boolean isLeapMonth = false;
        String strDay;  // 农历日
        // 1900年1月特殊处理
        if (total < 30)
        {
            ganIndex = 5;
            zhiIndex = 11;
            monthIndex = 12;
            sizeIndex = 1;
        } else
        {
            // 从1900-1-31开始计算
            total -= 30;
            int yearIndex = 0;
            while (true)
            {
                int daysInYear = _getDayNum(lunarInfo[yearIndex]);
                if (total >= daysInYear)
                {
                    total -= daysInYear;
                    ganIndex = (ganIndex + 1) % 10;
                    zhiIndex = (zhiIndex + 1) % 12;
                } else
                {
                    break;
                }
                yearIndex++;
            }
            int monthTemp = 1;
            int leapMonth = getLeapMonth(lunarInfo[yearIndex]);
            int[] monthSize = getMonthSize(lunarInfo[yearIndex], leapMonth);
            while (true)
            {
                int monthDays = (monthSize[monthTemp - 1] == 0) ? 29 : 30;
                if (total >= monthDays)
                {
                    total -= monthDays;
                    monthTemp = monthTemp % 12 + 1;
                } else
                {
                    sizeIndex = monthSize[monthTemp - 1];
                    break;
                }
            }
            monthIndex = (leapMonth == 0 || monthTemp <= leapMonth) ? monthTemp : monthTemp - 1;
            if (monthIndex == leapMonth && monthTemp == leapMonth + 1) isLeapMonth = true;
        }

        // 处理农历日
        int remainder = total % 10; // 余数
        int quotient = total / 10;  // 商
        System.out.println("total:"+total+";余数："+remainder+";商："+quotient);
        if (quotient == 0 || quotient == 3)
        {
            strDay = dayPre[0] + dayLater[remainder];
        } else
        {
            if (remainder == 9)
            {
                strDay = dayPre[quotient + 1] + dayLater[remainder];
            } else
            {
                strDay = dayPre[quotient * quotient] + dayLater[remainder];
            }
        }
        // 构造结果数组
        String[] info = new String[]{
                gan[ganIndex] + zhi[zhiIndex] + strYear,
                ((isLeapMonth) ? strLeap : "")
                        + chineseMonthName[monthIndex] + strMonth + monthSize[sizeIndex],
                strDay,
                chineseZodiac[zhiIndex]
        };
        for (String s:info
             ) {
            System.out.println(s);
        }

        return info;
    }

    /**
     * 获取该农历年的总天数
     *
     * @param info 农历年的16进制记录
     * @return 总天数 int, 失败返回-1
     */
    public static int getDayNum(long info)
    {
        return _getDayNum(info);
    }

    /**
     * 计算农历年的总天数的私有函数
     * @param info
     * @return
     */
    private static int _getDayNum(long info)
    {
        int sum = 0;
        int leapMonth = getLeapMonth(info);
        int[] monthSize = getMonthSize(info, leapMonth);
        for (int s : monthSize) sum += (s == 0) ? 29 : 30;
        return sum;
    }
    /**
     * 获取该农历年的闰月是哪月（1-12）
     *
     * @param info 该农历年的16进制记录
     * @return 返回该年的闰月
     */
    private static int getLeapMonth(long info)
    {
        return (int) info % 16;
    }

    /**
     * 获取该农历年的的每月月份大小 1：大，0：小
     *
     * @param info      该农历年的16进制记录
     * @param leapMonth 该农历年的闰月
     * @return int[] {1, 0, 1, 1, 0.....} 有闰月13个数据，否则12个
     */
    private static int[] getMonthSize(long info, int leapMonth)
    {
        int[] monthSize;
        int flag = (int) info >> 16;
        //if (leapMonth == 0)
        //此错误单独出现时 都错了
        /*FAULT## FAILURE INDUCING CODE */
        if (leapMonth != 0)
        {
            monthSize = new int[12];
        } else
        {
            monthSize = new int[13];
            monthSize[leapMonth] = flag;
        }
        info >>= 4;
        //for (int i = 0; i < 12; i++, info >>= 1)
        //此错误单独出现时 003 006 b002 b004 b010 b011
        /*FAULT## FAILURE INDUCING CODE */
        for (int i = 1; i < 12; i++, info >>= 1)
        {
            if (leapMonth > 0 && (12 - i) > leapMonth)
            {
                monthSize[12 - i] = (int) info % 2;
            } else
            {
                monthSize[12 - i - 1] = (int) info % 2;
            }
        }

        return monthSize;
    }


}