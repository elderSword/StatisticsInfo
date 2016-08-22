
package statistics.huijin.com.statisticsinfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final SimpleDateFormat sdfyyyy_MM_dd = new SimpleDateFormat(
            "yyyy-MM-dd");
    public static final SimpleDateFormat sdfyyyy_MM_dd_HH_mm_ss = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public static final SimpleDateFormat sdfyyyyMMddHHmmss = new SimpleDateFormat(
            "yyyyMMddHHmmss");

    public static final SimpleDateFormat sdfyyyy_MM_dd_HH_mm = new SimpleDateFormat(
            "yyyy年MM月dd日 HH:mm");
    public static final SimpleDateFormat sdfyyyy_MM_dd_HH = new SimpleDateFormat(
            "yyyy年MM月dd日 HH:00");
    public static final SimpleDateFormat yyyyMMddHms = new SimpleDateFormat("yyyyMMddHms");
    public static final SimpleDateFormat sdfHH_mm = new SimpleDateFormat(
            "HH:mm");
    public static final SimpleDateFormat sdfMM_dd = new SimpleDateFormat(
            "MM-dd");
    public static final SimpleDateFormat sdfyyyyMMddHHmmssfff = new SimpleDateFormat(
            "yyyyMMddHHmmssSSS");

    /**
     * return yyyy-MM-dd
     * 
     * @return
     */
    public static String getDate_yyyy_MM_dd() {
        return sdfyyyy_MM_dd.format(new Date());
    }

    public static String getDate_yyyy_MM_dd(Date data) {
        return sdfyyyy_MM_dd.format(data);
    }

    public static String getDate_yyyy_MM_dd(long data) {
        return sdfyyyy_MM_dd.format(data);
    }

    public static String getDate_HH_mm() {
        return sdfHH_mm.format(new Date());
    }

    /**
     * return yyyy_MM_dd_HH_mm_ss
     * 
     * @return
     */
    public static String getDate_yyyy_MM_dd_HH_mm() {
        return sdfyyyy_MM_dd_HH_mm.format(new Date());
    }

    public static String getDate_yyyyMMddHms() {
        return sdfyyyyMMddHHmmss.format(new Date());
    }

    public static String getDate_yyyyMMddHmsf() {
        return sdfyyyyMMddHHmmssfff.format(new Date());
    }

    /**
     * 由返回回来的毫秒数算出真正的时间
     * 
     * @param s
     * @return
     */
    public static String getDateString(long s, SimpleDateFormat format) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(s * 1000));
        Date dt = c.getTime();
        String str = format.format(dt);
        return str;
    }

    public static boolean isToday(long time){
        return getDate_yyyy_MM_dd().equals(getDate_yyyy_MM_dd(time));
    }

    // 获得当天0点时间
    public static long getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }

    // 获得昨天0点时间
    public static long getYesterdaymorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimesmorning()-3600*24*1000);
        return cal.getTime().getTime();
    }
}
