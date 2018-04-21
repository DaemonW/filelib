package com.daemonw.filelib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 * 需要在Application中注册，为了要在程序启动器就监控整个程序。
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    //TAG
    public static final String TAG = "CrashHandler";
    //自定义Toast
    private static Toast mCustomToast;
    //提示文字
    private static String mCrashTip = "很抱歉,程序出现异常,即将退出.";
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler mCrashHandler;
    //程序的App对象
    public Application mApplication;
    //生命周期监听
    MyActivityLifecycleCallbacks mMyActivityLifecycleCallbacks = new MyActivityLifecycleCallbacks();
    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());
    //是否保存崩溃信息
    private boolean mDumpCrash;
    //是否重启APP
    private boolean mRestartApp;
    //重启APP时间
    private long mRestartDelay;
    //重启后的第一个Activity class文件
    private Class mClassOfFirstActivity;
    //是否已经toast
    private boolean hasToast;
    //消息处理handler
    private Handler mUIHandler;
    //dump处理时长
    private long dumpDuration;
    //crash listener
    private OnAppCrashListener mCrashListener;

    /**
     * 私有构造函数
     */
    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例 ,单例模式
     *
     * @return
     * @since V1.0
     */
    public static CrashHandler getInstance() {
        if (mCrashHandler == null) {
            synchronized (CrashHandler.class) {
                if (mCrashHandler == null) {
                    mCrashHandler = new CrashHandler();
                }
            }
        }
        return mCrashHandler;
    }

    public void setOnCrashListener(OnAppCrashListener onCrashListener) {
        mCrashListener = onCrashListener;
    }

    public void setCloseAnimation(int closeAnimation) {
        mMyActivityLifecycleCallbacks.sAnimationId = closeAnimation;
    }

    public static void setCustomToast(Toast customToast) {
        mCustomToast = customToast;
    }

    public static void setCrashTip(String crashTip) {
        mCrashTip = crashTip;
    }

    public void init(Application application, boolean dumpCrash, boolean restartApp, long restartDelay, Class classOfFirstActivity) {
        mRestartApp = restartApp;
        mRestartDelay = restartDelay;
        if(mRestartDelay<0){
            mRestartDelay=0;
        }
        mClassOfFirstActivity = classOfFirstActivity;
        initCrashHandler(application, dumpCrash);
    }

    public void init(Application application, boolean dumpCrash) {
        mRestartApp = false;
        mRestartDelay = 0;
        initCrashHandler(application, dumpCrash);
    }

    /**
     * 初始化
     *
     * @since V1.0
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initCrashHandler(Application application, boolean dumpCrash) {
        mDumpCrash = dumpCrash;
        mApplication = application;
        mUIHandler = new Handler(mApplication.getMainLooper());
        mApplication.registerActivityLifecycleCallbacks(mMyActivityLifecycleCallbacks);
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        boolean isExceptionHandled = handleException(ex);
        if (!isExceptionHandled && mDefaultHandler != null) {
            // 如果我们没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                if (dumpDuration < 3000) {
                    //给Toast留出时间
                    Thread.sleep(3000 - dumpDuration);
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "uncaughtException() InterruptedException:" + e);
            }

            if (mRestartApp) {
                //利用系统时钟进行重启任务
                AlarmManager am = (AlarmManager) mApplication.getSystemService(Context.ALARM_SERVICE);
                try {
                    Intent intent = new Intent(mApplication, mClassOfFirstActivity);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent restartIntent = PendingIntent.getActivity(mApplication, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    am.set(AlarmManager.RTC, System.currentTimeMillis() + mRestartDelay, restartIntent); // x秒钟后重启应用
                } catch (Exception e) {
                    Log.e(TAG, "first class error:" + e);
                }
            }

            mMyActivityLifecycleCallbacks.removeAllActivities();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            System.gc();

        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param throwable
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable throwable) {

        if (!hasToast) {
            hasToast = true;
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast;
                    if (mCustomToast == null) {
                        toast = Toast.makeText(mApplication, mCrashTip, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                    } else {
                        toast = mCustomToast;
                    }
                    toast.show();
                }
            });
        }

        if (throwable == null) {
            return false;
        }

        if (mDumpCrash) {
            long t1 = SystemClock.elapsedRealtime();
            // 收集设备参数信息
            HashMap<String, String> info = collectDeviceInfo(mApplication);
            // 保存日志文件
            String logFile = saveCrashInfo(info, throwable);
            if (mCrashListener == null) {
                mCrashListener = new OnAppCrashListener() {
                    @Override
                    public void onCrash(String log) {
                        Log.e(TAG, "save crash log in file: " + log);
                    }
                };
            }
            mCrashListener.onCrash(logFile);
            dumpDuration = SystemClock.elapsedRealtime() - t1;
        }

        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param app Applicaton context
     * @return device properties
     * @since V1.0
     */
    public HashMap<String, String> collectDeviceInfo(Application app) {
        HashMap<String, String> infos = new HashMap<>();
        try {
            PackageManager pm = app.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(app.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("VERSION_NAME", versionName);
                infos.put("VERSION_CODE", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "collectDeviceInfo() an error occured when collect package info NameNotFoundException:", e);
            return null;
        }
        Field[] fields = Build.class.getDeclaredFields();
        String fieldVal;
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object val = field.get(null);
                if (val instanceof Object[]) {
                    fieldVal = join((Object[]) val, ", ");
                } else {
                    fieldVal = val.toString();
                }
                infos.put(field.getName(), fieldVal);
            } catch (Exception e) {
                Log.e(TAG, "collectDeviceInfo() an error occured when collect crash info Exception:", e);
                return null;
            }
        }
        return infos;
    }

    /**
     * 保存错误信息到文件中
     *
     * @param throwable  uncatched exception
     * @param deviceInfo device properties
     * @return 文件名称
     */
    private String saveCrashInfo(HashMap<String, String> deviceInfo, Throwable throwable) {
        StringBuffer sb = new StringBuffer();
        sb.append("------------------------start------------------------------\n");
        sb.append("\nDevice Info: \n");
        for (Map.Entry<String, String> entry : deviceInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        sb.append("\nException: \n");
        sb.append(getCrashInfo(throwable));
        sb.append("\n------------------------end------------------------------");
        try {
            String time = formatter.format(new Date());
            String fileName = time + "-" + getAndroidID() + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = mApplication.getExternalFilesDir(null) + File.separator + "crash/";
                File dir = new File(path);
                if (!dir.exists()) dir.mkdirs();
                // 创建新的文件
                if (!dir.exists()) dir.createNewFile();

                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                // 答出log日志到控制台
                LogcatCrashInfo(path + fileName);
                fos.close();
                return path + fileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "saveCatchInfo2File() an error occured while writing file... Exception:");
        }
        return null;
    }

    private String getAndroidID() {
        return Settings.System.getString(mApplication.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 将捕获的导致崩溃的错误信息保存在sdcard 和输出到LogCat中
     *
     * @param fileName
     * @since V1.0
     */
    private void LogcatCrashInfo(String fileName) {
        if (!new File(fileName).exists()) {
            Log.e(TAG, "LogcatCrashInfo() log file doesn't exist");
            return;
        }
        FileInputStream fis = null;
        BufferedReader reader = null;
        String s = null;
        try {
            fis = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(fis, "utf-8"));
            while (true) {
                s = reader.readLine();
                if (s == null)
                    break;
                Log.e(TAG, s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally { // 关闭流
            closeStream(reader);
            closeStream(fis);
        }
    }

    /**
     * 得到程序崩溃的详细信息
     */
    public String getCrashInfo(Throwable ex) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        ex.printStackTrace(printWriter);
        printWriter.flush();
        return result.toString();
    }

    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String join(Object[] objs, String separator) {
        if (objs == null || objs.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(objs[0].toString());
        for (Object o : objs) {
            sb.append(separator).append(o.toString());
        }
        return sb.toString();
    }


    public interface OnAppCrashListener {
        void onCrash(String logFile);
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        private List<Activity> activities = new LinkedList<>();
        public int sAnimationId = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            addActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            removeActivity(activity);
        }

        /**
         * 添加Activity
         */
        public void addActivity(Activity activity) {
            if (activities == null) {
                activities = new LinkedList<>();
            }

            if (!activities.contains(activity)) {
                activities.add(activity);//把当前Activity添加到集合中
            }
        }

        /**
         * 移除Activity
         */
        public void removeActivity(Activity activity) {
            if (activities.contains(activity)) {
                activities.remove(activity);
            }

            if (activities.size() == 0) {
                activities = null;
            }
        }


        /**
         * 销毁所有activity
         */
        public void removeAllActivities() {
            for (Activity activity : activities) {
                if (null != activity) {
                    activity.finish();
                    activity.overridePendingTransition(0, sAnimationId);
                }
            }
        }
    }

}
