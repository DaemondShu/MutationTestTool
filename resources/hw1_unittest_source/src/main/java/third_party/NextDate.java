
package third_party;



import java.util.ArrayList;


public class NextDate
{
    
    public static final String NO_LUNAR_INFO = "2";     
    public static final String SUCCESS = "1";           
    public static final String FAIL = "0";              
    
    private static final int weekBase = 1;
    private static final int yearBase = 1900;
    private static final int yearUpper = 2100;
    private static final int addInLeapYear = 366 % 7;
    private static final int addInNormalYear = 365 % 7;
    private static final int[][] calendarMonth = {
            {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31},
            {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}
    };
    
    private static final int validSuccess = 100;
    private static final int errDateOutOfRange = 101;
    private static final int errNoSuchDate = 102;
    
    private static final String errDateOutOfRangeMsg = "输入的日期超出应用可用范围";
    private static final String errNoSuchDateMsg = "输入的日期不存在";
    private static final String unknownErrorMsg = "未知的错误";


    private LunarUtil lunarUtil = new LunarUtil();

    public void setLunarUtil(LunarUtil lunarUtil)
    {
        this.lunarUtil = lunarUtil;
    }


    
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
        
        return result;
    }

    
    public static int validDate(int year, int month, int day)
    {
        return _validDate(year, month, day);
    }

    
    private String[] _getNextDateInfo(int year, int month, int day)
    {
        String[] info;
        int isLeapYear = isLeapYear(year) ? 1 : 0;
        int yearNext = year;
        int monthNext = month;
        int dayNext = day + 1;
        
        int total = 0;          

        
        if (dayNext > calendarMonth[isLeapYear][monthNext - 1])
        {
            dayNext = 1;
            monthNext++;
            if (monthNext > 12)
            {
                System.out.println("monNext: "+monthNext);
                monthNext = 1;
                yearNext++;
            }
        }






















        
        String[] lunarDateInfo = lunarUtil.getLunarDateInfo(yearNext, monthNext, dayNext);

        
        info = new String[]{
                yearNext + "",
                monthNext + "",
                dayNext + "",
               
                lunarDateInfo[0],
                lunarDateInfo[1],
                lunarDateInfo[2],
                lunarDateInfo[3]
        };

        return info;
    }

    
    private String[] _getNextNDateInfo(int year, int month, int day, int n)
    {
        n--;
        day += n;
        
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

    
    private static Boolean isLeapYear(int year)
    {
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
    }

    
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
