package com.grt.filemanager.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.grt.filemanager.R;
import com.grt.filemanager.constant.SettingConstant;

import java.util.Locale;

/**
 * 设置语言工具类
 */
public class LanguageUtils {

    private static boolean needUpdate = false;

    private LanguageUtils() {
    }

    public static void setNeedUpdate(boolean needUpdate) {
        LanguageUtils.needUpdate = needUpdate;
    }

    public static void checkNeedUpdate(Context context) {
        if (needUpdate) {
            setLocale(context);
            needUpdate = false;
        }
    }

    public static String getLanguage() {
        String cur = Locale.getDefault().getLanguage();
        if (cur.equals("zh")) {
            cur = "cn";
        }

        String country = Locale.getDefault().getCountry();
        if (country.equals("CN")) {
            cur = "cn";
        } else if (country.equals("TW")) {
            cur = "tw";
        }

        return cur;
    }

    public static boolean isChinaBySystem() {
        String language = getLanguage();
        return language.equals("cn") || language.equals("zh");
    }

    public static void setLocale(Context context) {
        try {
            if (context == null) {
                return;
            }

            Resources resources = context.getResources();
            if (resources != null) {
                Configuration config = resources.getConfiguration();
                if (Build.VERSION.SDK_INT >= 24) {
                    config.setLocales(null);
                    resources.updateConfiguration(config, null);
                }
                config.locale = getLanguageSetting();
                resources.updateConfiguration(config, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Locale getLanguageSetting() {
        String languageToLoad;
        String countryToLoad = null;
        Locale locale;

        int languageResId = PreferenceUtils.getPrefInt(SettingConstant.SETTING_LANGUAGE,
                R.string.language_default);
        if (languageResId == R.string.language_default) {
            languageToLoad = Locale.getDefault().getLanguage();
            countryToLoad = Locale.getDefault().getCountry();

        } else if (languageResId == R.string.language_usa) {
            languageToLoad = "en";

        } else if (languageResId == R.string.setting_language_portuguese_brasil) {
            languageToLoad = "pt";
            countryToLoad = "BR";

        } else if (languageResId == R.string.setting_language_portuguese_portugal) {
            languageToLoad = "pt";
            countryToLoad = "PT";

        } else if (languageResId == R.string.setting_language_tip) {
            languageToLoad = "zh";
            countryToLoad = "CN";

        } else if (languageResId == R.string.setting_language_fanti) {
            languageToLoad = "zh";
            countryToLoad = "TW";

        } else if (languageResId == R.string.setting_language_italiano) {
            languageToLoad = "it";
            countryToLoad = "IT";

        } else if (languageResId == R.string.setting_language_japan) {
            languageToLoad = "ja";
            countryToLoad = "JP";

        } else if (languageResId == R.string.setting_language_korean) {
            languageToLoad = "ko";
            countryToLoad = "KR";

        } else if (languageResId == R.string.setting_language_russian) {
            languageToLoad = "ru";
            countryToLoad = "RU";

        } else if (languageResId == R.string.setting_language_spanish) {
            languageToLoad = "es";
            countryToLoad = "ES";

        } else if (languageResId == R.string.setting_language_french) {
            languageToLoad = "fr";
            countryToLoad = "FR";

        } else if (languageResId == R.string.setting_language_eg) {
            languageToLoad = "ar";
            countryToLoad = "EG";

        } else if (languageResId == R.string.setting_language_id) {
            languageToLoad = "in";
            countryToLoad = "ID";

        } else if (languageResId == R.string.setting_language_thai) {
            languageToLoad = "th";
            countryToLoad = "TH";

        } else if (languageResId == R.string.setting_language_german) {
            languageToLoad = "de";
            countryToLoad = "DE";

        } else if (languageResId == R.string.setting_language_nl) {
            languageToLoad = "nl";
            countryToLoad = "NL";

        } else if (languageResId == R.string.setting_language_polish) {
            languageToLoad = "pl";
            countryToLoad = "PL";

        } else if (languageResId == R.string.setting_language_ua) {
            languageToLoad = "uk";
            countryToLoad = "UA";

        } else if (languageResId == R.string.setting_language_my) {
            languageToLoad = "ms";
            countryToLoad = "MY";

        } else {
            languageToLoad = Locale.getDefault().getLanguage();
            countryToLoad = Locale.getDefault().getCountry();
            if (null == languageToLoad) {
                languageToLoad = "zh";
                countryToLoad = "CN";
            }
        }

        if (countryToLoad != null) {
            locale = new Locale(languageToLoad, countryToLoad);
        } else {
            locale = new Locale(languageToLoad);
        }

        return locale;
    }


    public static String getLanguageAndCountry() {
        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();

        return (language + "-" + country).toLowerCase();
    }

}
