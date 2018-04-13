package com.grt.filemanager.orm.helper.implement;

import android.content.Context;

import com.grt.filemanager.orm.dao.AppFolder;
import com.grt.filemanager.orm.dao.base.DBCore;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.greenrobot.dao.AbstractDao;

public class AppFolderHelper extends BaseHelper<AppFolder, Long> {

    public AppFolderHelper(AbstractDao dao) {
        super(dao);
    }

    public void initAppIconData(Context context) {
        if (this.count() <= 0) {
            insertSql(context);
        }
    }

    public void insertSql(Context context) {
        BufferedReader reader = null;
        InputStream in = null;
        try {
            in = context.getAssets().open("appfolders.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            String tempString = null;

            DBCore.getDaoSession().getDatabase().beginTransaction();

            while ((tempString = reader.readLine()) != null) {
                if(tempString.length()>0){
                    DBCore.getDaoSession().getDatabase().execSQL(tempString);
                }
            }

            DBCore.getDaoSession().getDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }

           DBCore.getDaoSession().getDatabase().endTransaction();
        }
    }


}
