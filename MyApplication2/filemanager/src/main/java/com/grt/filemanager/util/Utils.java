package com.grt.filemanager.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;


import com.grt.filemanager.constant.SettingConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static int appBarHeight = 0;
    private static String DIS_CHANNEL = "";

    public static String convertStreamToString(InputStream is)
            throws IOException {
        return convertStreamToString(is, "UTF-8");
    }

    public static String convertStreamToString(InputStream is, String encoding)
            throws IOException {
        return convertStreamToString(is, encoding, "\n");
    }

//    //是否定制版
//    public static boolean isSpecial() {
//        return BuildConfig.CUSTOM;
//    }

    public static String convertStreamToString(InputStream is, String encoding,
                                               String enterChar) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = null;
            InputStreamReader inReader;

            try {
                inReader = new InputStreamReader(is, encoding);
            } catch (UnsupportedEncodingException e) {
                inReader = new InputStreamReader(is, "UTF-8");
            }

            try {
                reader = new BufferedReader(inReader);
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(enterChar);
                }
            } finally {
                is.close();
                inReader.close();
                if (reader != null) {
                    reader.close();
                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public static String getUUIDStr() {
        String before = UUID.randomUUID().toString();
        return before.replaceAll("-", "");
    }

    public static String setUniqueDeviceId(Context context) {
        String deviceId = null;
        try {
            TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = telMgr.getDeviceId();
            if (TextUtils.isEmpty(deviceId) || deviceId.equals("000000000000000")
                    || deviceId.equals("00000000000000")) {

                deviceId = Settings.Secure.ANDROID_ID;
                if (TextUtils.isEmpty(deviceId) || "android_id".equals(deviceId)) {
                    if (Build.VERSION.SDK_INT >= 9) {
                        deviceId = Build.SERIAL;
                    }

                    if (TextUtils.isEmpty(deviceId) || deviceId.equals("unknown")) {
                        deviceId = getUUIDStr();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (deviceId == null) {
                deviceId = getUUIDStr();
            }
            PreferenceUtils.setPrefString(context, SettingConstant.DEVICE_ID, deviceId);
        }
        return deviceId;
    }

    public static String getUniqueDeviceId(Context context) {
        String deviceId = PreferenceUtils.getPrefString(context, SettingConstant.DEVICE_ID, null);
        if (deviceId == null) {
            deviceId = setUniqueDeviceId(context);
        }
        return deviceId;
    }

    public static String getCodeMd5(String deviceCode, String codeMd5) {
        String res = deviceCode + codeMd5;
        return getMD5(res);
    }

    public static String getMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }



    public static String getDisChannelByAsset(Context context) {
        if (TextUtils.isEmpty(DIS_CHANNEL)) {
            try {
                InputStream in = context.getAssets().open("channel.txt");
                DIS_CHANNEL = Utils.convertStreamToString(in, "UTF-8", "");
            } catch (IOException e) {
                DIS_CHANNEL = "test";
            }
        }
        return DIS_CHANNEL;
    }

    //当前版本是否免费
    public static boolean isFreeVersion() {
        return true;
    }

    public static boolean isDisChannelAmazon() {
        return DIS_CHANNEL.equals("amazon");
    }

    public static boolean isDisChannelGooglePlay() {
        return DIS_CHANNEL.equals("e_s_googleplay");
    }

    public static boolean isChinaChannel() {
        return DIS_CHANNEL.startsWith("c_");
    }


    public static String getRandomSix() {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < 6; i++) {
            result += random.nextInt(10);
        }
        return result;
    }

    public static String string2md5By16(String plainText) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte aB : b) {
                i = aB;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isMobileNb(String mobiles) {
        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            screenHeight = dm.heightPixels;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            screenWidth = dm.widthPixels;
        }
        return screenWidth;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
