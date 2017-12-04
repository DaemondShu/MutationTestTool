/**
 * 被测源代码来自﻿https://github.com/git-new/next_date.git 然后做了少改动。
 */
package third_party;

//import org.junit.experimental.theories.suppliers.TestedOn;

import java.util.ArrayList;


public class NextDate
{
    // 返回结果判定标志
    public static final String NO_LUNAR_INFO = "2";     // 农历越界
    public static final String SUCCESS = "1";           // 计算成功
    public static final String FAIL = "0";              // 计算失败
    // 公历用计算常量
    private static final int weekBase = 1;
    private static final int yearBase = 1900;
    private static final int yearUpper = 2100;
    private static final int addInLeapYear = 366 % 7;
    private static final int addInNormalYear = 365 % 7;
    private static final int[][] calendarMonth = {
            {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31},
            {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}
    };
    // 错误码
    private static final int validSuccess = 100;
    private static final int errDateOutOfRange = 101;
    private static final int errNoSuchDate = 102;
    // 错误信息
    private static final String errDateOutOfRangeMsg = "输入的日期超出应用可用范围";
    private static final String errNoSuchDateMsg = "输入的日期不存在";
    private static final String unknownErrorMsg = "未知的错误";


    private LunarUtil lunarUtil = new LunarUtil();

    public void setLunarUtil(LunarUtil lunarUtil)
    {
        this.lunarUtil = lunarUtil;
    }


    /**
     * 获取输入日期的后一天的公历和农历信息入口方法
     *
     * @param yearNow  输入年
     * @param monthNow 输入月
     * @param dayNow   输入日
     * @param n        向后天数
     * @return 后一天的日期信息或者错误信息列表
     */
    public ArrayList<String> getNextDateInfo(int yearNow, int monthNow, int dayNow, int n)
    {
        ArrayList<String> result = new ArrayList<>();
        switch (_validDate(yearNow, monthNow, dayNow))
        {
            case errDateOutOfRange:
                result.add(FAIL);
                result.add(errDateOutOfRangeMsg);
                break;
            case errNoSuchDate:
                result.add(FAIL);
                result.add(errNoSuchDateMsg);
                break;
            case validSuccess:
                if (dayNow == 31 && monthNow == 12 && yearNow == yearUpper) result.add(NO_LUNAR_INFO);
                else result.add(SUCCESS);
                String[] info;
                if (n <= 0) info = _getBackNDateInfo(yearNow, monthNow, dayNow, n);
                else if (n == 1) info = _getNextDateInfo(yearNow, monthNow, dayNow);
                else info = _getNextNDateInfo(yearNow, monthNow, dayNow, n);
                for (String s : info)
                {
                    result.add(s);
                }
                break;

        }
        //System.out.println(result.toString());
        return result;
    }

    /**
     * 判断日期是否有效
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static int validDate(int year, int month, int day)
    {
        return _validDate(year, month, day);
    }

    /**
     * 获取日期后一天信息主逻辑
     *
     * @return 后一天信息字符串数组
     */
    private String[] _getNextDateInfo(int year, int month, int day)
    {
        String[] info;
        int isLeapYear = isLeapYear(year) ? 1 : 0;
        int yearNext = year;
        int monthNext = month;
        int dayNext = day + 1;
        //int week = weekBase;
        int total = 0;          // 与1900-1-1的时间差,用于计算农历

        // 计算下一天的公历日期
        if (dayNext > calendarMonth[isLeapYear][monthNext - 1])
        {
            dayNext = 1;
            monthNext++;
            if (monthNext >= 12)
            {
                System.out.println("monNext: "+monthNext);
                monthNext = 1;
                yearNext++;
            }
        }

//        // 计算下一天星期
//        for (int y = yearBase; y < yearNext; y++)
//        {
//            if (isLeapYear(y))
//            {
//                week = (week + addInLeapYear) % 7;
//                total += 366;
//            } else
//            {
//                week = (week + addInNormalYear) % 7;
//                total += 365;
//            }
//        }
//        for (int m = 1; m < monthNext; m++)
//        {
//            week = (week + calendarMonth[isLeapYear][m - 1]) % 7;
//            total += calendarMonth[isLeapYear][m - 1];
//        }
//        week = (week + dayNext - 1) % 7;
//        total += dayNext - 1;

        // 根据公历日期获取农历日期
        String[] lunarDateInfo = lunarUtil.getLunarDateInfo(yearNext, monthNext, dayNext);

        // 更新后一天信息字符串数组
        info = new String[]{
                yearNext + "",
                monthNext + "",
                dayNext + "",
               // week + "",
                lunarDateInfo[0],
                lunarDateInfo[1],
                lunarDateInfo[2],
                lunarDateInfo[3]
        };

        return info;
    }

    /**
     * 获取往后n天的信息
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @param n     后n天
     * @return 后n天的字符串数组信息
     */
    private String[] _getNextNDateInfo(int year, int month, int day, int n)
    {
        n--;
        day += n;
        // 计算公历
        while (day > calendarMonth[isLeapYear(year) ? 1 : 0][month - 1])
        {
            day -= calendarMonth[isLeapYear(year) ? 1 : 0][month - 1];
            month++;
            if (month == 13)
            {
                year++;
                month = 1;
            }
        }
        return _getNextDateInfo(year, month, day);
    }

    /**
     * 获取某日前N天的信息
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @param n     相差天数-负数
     * @return 信息的字符串数组
     */
    private String[] _getBackNDateInfo(int year, int month, int day, int n)
    {
        day += n;
        day--;
        while (day < 0)
        {
            day += calendarMonth[isLeapYear(year) ? 1 : 0][(month + 10) % 12];
            month--;
            if (month == 0)
            {
                year--;
                month = 12;
            }
        }
        if (day == 0)
        {
            month--;
            if (month == 0)
            {
                year--;
                month = 12;
            }
            day = calendarMonth[isLeapYear(year) ? 1 : 0][month - 1];
        }
        return _getNextDateInfo(year, month, day);
    }

    /**
     * 判断某年是否是闰年
     *
     * @return 是否是闰年的结果
     */
    private static Boolean isLeapYear(int year)
    {
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
    }

    /**
     * 判断日期是否合法
     *
     * @return 错误码
     */
    private static int _validDate(int year, int month, int day)
    {
        if (year < 1900 || year > 2100)
        {
            return errDateOutOfRange;
        } else if (month > 12 || month < 1)
        {
            System.out.println(year + "-" + month + "-" + day);
            return errNoSuchDate;
        } else
        {
            int isLeapYear = isLeapYear(year) ? 1 : 0;
            if (day < 1 || day > calendarMonth[isLeapYear][month - 1])
            {
                System.out.println(year + "-" + month + "-" + day);
                return errNoSuchDate;
            }
        }
        return validSuccess;
    }

}