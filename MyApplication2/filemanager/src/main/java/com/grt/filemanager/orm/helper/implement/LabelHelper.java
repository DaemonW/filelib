package com.grt.filemanager.orm.helper.implement;

import android.content.Context;
import android.content.res.TypedArray;


import com.grt.filemanager.R;
import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.orm.dao.Label;
import com.grt.filemanager.orm.dao.base.LabelDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

public class LabelHelper extends BaseHelper<Label, Long> {

    private static final Context mContext = ApiPresenter.getContext();
    private static final TypedArray color = mContext.getResources().obtainTypedArray(R.array.label_icon);

    public LabelHelper(AbstractDao dao) {
        super(dao);
    }

//    public void insertSql() {
//        boolean result = false;
//        try {
//            Label cur = queryBuilder().where(LabelDao.Properties.LabelType.eq(DataConstant.LABEL_DEFAUL_FAV_NAME),
//                    LabelDao.Properties.LabelColor.eq(DataConstant.LABEL_DEFAUL_FAV_COLOR)).unique();
//
//            if (cur != null) {
//                return;
//            }
//
//            Label label = new Label();
//            label.setLabelType(DataConstant.LABEL_DEFAUL_FAV_NAME);
//            label.setLabelColor(DataConstant.LABEL_DEFAUL_FAV_COLOR);
//            label.setLastModified(System.currentTimeMillis());
//            save(label);
//            result = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (result) {
//                Label cur = queryBuilder().where(LabelDao.Properties.LabelType.eq(DataConstant.LABEL_DEFAUL_FAV_NAME),
//                        LabelDao.Properties.LabelColor.eq(DataConstant.LABEL_DEFAUL_FAV_COLOR)).unique();
//                if (cur != null) {
//                    long lableId = cur.getId();
//                    List<File> files = new ArrayList<>();
//                    try {
//                        Cursor beforFiles = DBCore.getDaoSession().getDatabase().query(DataConstant.TABLENAME_FAVORITE, new String[]{MediaStore.Files.FileColumns.DATA}, null, null, null, null, null);
//                        if (beforFiles != null && beforFiles.getCount() > 0) {
//                            while (beforFiles.moveToNext()) {
//                                String data = beforFiles.getString(0);
//                                if (data != null) {
//                                    files.add(new File(data));
//                                }
//                            }
//                            beforFiles.close();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    for (File file : files) {
//                        LabelFile labelFile = new LabelFile();
//                        labelFile.setLabelId(lableId);
//                        labelFile.setPath(file.getPath());
//                        labelFile.setName(file.getName());
//                        labelFile.setSize(file.length());
//                        labelFile.setLastModified(file.lastModified());
//                        labelFile.setIsFolder(file.isDirectory());
//                        labelFile.setMimeType(FileUtils.getMiMeType(file.getName()));
//                        try {
//                            DbUtils.getLabelFileHelper().save(labelFile);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//    }

    /**
     * 判断标签名称是否存在
     *
     * @param labelName
     * @return
     */
    public boolean LabelNameDuplicate(String labelName, int color) {
        QueryBuilder names = queryBuilder().where(LabelDao.Properties.LabelType.eq(labelName));
        QueryBuilder query = queryBuilder().where(LabelDao.Properties.LabelType.eq(labelName), LabelDao.Properties.LabelColor.eq(color));
        return query.count() > 0 || names.count() > 0;
    }

    /**
     * 获取标签颜色
     *
     * @param labelId
     * @return
     */
    public int queryColor(long labelId) {
        Label label = queryBuilder().where(LabelDao.Properties.Id.eq(labelId)).unique();
        int colorPos = label.getLabelColor();
        if (colorPos != -1) {
            colorPos = colorPos - 1;
        }

        if (colorPos == -1) {
            return -1;
        }
        return color.getResourceId(colorPos, 0);

    }

}
