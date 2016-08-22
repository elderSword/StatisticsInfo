
package statistics.huijin.com.statisticsinfo;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 应用后台打开次数，时间
 * 
 * @author huijinh
 */
public class LocalMobileDataUtil {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");

    public static final String TAG = LocalMobileDataUtil.class.getSimpleName();
    public static HashMap<String, StatisticsEvent> mPkgs;
    private static UsageStatsManager mUsmManager;
    public static final String TAG_U = "StatsTest_Usage";
    public static final String TAG_E = "StatsTest_Events";
    private static List<StatisticsEvent> eventList;

    public static void printCurrentUsageStatus(final Context context) {
        try {
            //按需添加
//            if (!isConnected(context))
//                return;
            mUsmManager = getUsageStatsManager(context);
            if (getUsageList().isEmpty()) {
                try {
                    //如果是系统应用调用一下代码直接赋予权限
                    AppOpsManager mAppOpsManager = (AppOpsManager) context
                            .getSystemService(Context.APP_OPS_SERVICE);
                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                            context.getPackageName(),
                            PackageManager.GET_PERMISSIONS);
//                    mAppOpsManager.setMode(43, packageInfo.applicationInfo.uid,
//                            context.getPackageName(), AppOpsManager.MODE_ALLOWED);

                    //非系统应用跳转到系统设置界面手动勾选
                    ((MainActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent mPermision = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                mPermision.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(mPermision);
                            }
                        });
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            printEvents();
            printResult();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印最后过滤的结果
     */
    private static void printResult() {
        Iterator iter = mPkgs.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String pkgName = (String)entry.getKey();
            StatisticsEvent event = (StatisticsEvent)entry.getValue();
            Log.e(TAG_E,"packageName:"+pkgName+"  count:"+event.getCount());
        }
    }

    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }

    /**
     * 判断今天是否有应用使用
     * @return
     */
    private static List<UsageStats> getUsageList() {
        long endTime = System.currentTimeMillis();
        long startTime = DateUtil.getTimesmorning();
        List<UsageStats> usageList = mUsmManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        return usageList;
    }

    /**
     * 只查询当天的应用打开事件
     * @throws NoSuchFieldException
     */
    public static void printEvents() throws NoSuchFieldException {

        long endTime = System.currentTimeMillis();
        long startTime = DateUtil.getTimesmorning();
        Log.d(TAG_U, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG_U, "Range end:" + dateFormat.format(endTime));

        UsageEvents events = mUsmManager.queryEvents(startTime, endTime);
        if(eventList!=null){
            eventList.clear();
        }
        eventList = new ArrayList<StatisticsEvent>();
        while (events.hasNextEvent()) {
            UsageEvents.Event e = new UsageEvents.Event();
            events.getNextEvent(e);
            StatisticsEvent event = null;
            if (e != null && e.getEventType() == 1) {
                event = new StatisticsEvent();
                event.setClazz(e.getClassName());
                event.setPkgname(e.getPackageName());
                event.setLasttime(e.getTimeStamp());
                event.setType(e.getEventType());
                eventList.add(event);
                Log.d(TAG_U, "Event:----lasttime = " + DateUtils.formatSameDayTime(e.getTimeStamp(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM)
                        + "---- pkgname = " + e.getPackageName()
                        + "----class = " + e.getClassName()
                        + "----type =" + e.getEventType());
            }
        }
        printEventsMine();
    }


    /**
     * 对查询出来所有的事件进行过滤
     * @throws NoSuchFieldException
     */
    public static void printEventsMine() throws NoSuchFieldException {
        HashMap<String, String> pkgNames = new HashMap<String, String>();
        if(mPkgs!=null){
            mPkgs.clear();
        }
        mPkgs = new HashMap<String, StatisticsEvent>();
        for(int i=0;i<eventList.size();i++){
            StatisticsEvent e = eventList.get(i);
            String packageName = e.getPkgname();
            //判断所有事件中当前如果为launcher时去判断上一个事件,上一个事件不是launcher就手动添加到mPkgs，并且打开次数+1
            if(packageName.contains("launcher")&&i>0){
                StatisticsEvent e1 = eventList.get(i-1);
                String pkgname = e1.getPkgname();
                if(!pkgname.contains("launcher")){
                        e1.setCount(mPkgs.get(pkgname)==null?1:mPkgs.get(pkgname).getCount() + 1);
                        mPkgs.put(pkgname, e1);
                        Log.d(TAG,"Event:-"+e1.getPkgname()+"count"+e1.getCount());
                    }
            }

            //如果为最后一个事件，不是launcher就做上面相同的操作
            if(i==eventList.size()-1){
                if(!packageName.contains("launcher")){
                    e.setCount(mPkgs.get(packageName)==null?1:mPkgs.get(packageName).getCount() + 1);
                    mPkgs.put(packageName, e);
                    Log.d(TAG,"last  Event:-"+e.getPkgname()+"count"+e.getCount());
                }
            }
        }
    }

    //检查网络
    public static boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取活动的网络连接信息
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        // 判断
        if (info == null) {
            // 当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
            return false;
        } else {
            // 当前有已激活的网络连接，但是否可用还需判断
            return info.isAvailable();
        }
    }


}
