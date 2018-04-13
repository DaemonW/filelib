package com.grt.filemanager.orm.dao.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grt.filemanager.orm.dao.RecentOpen;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "RECENT_OPEN".
*/
public class RecentOpenDao extends AbstractDao<RecentOpen, Long> {

    public static final String TABLENAME = "RECENT_OPEN";

    /**
     * Properties of entity RecentOpen.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Path = new Property(2, String.class, "path", false, "PATH");
        public final static Property Size = new Property(3, Long.class, "size", false, "SIZE");
        public final static Property LastModified = new Property(4, Long.class, "lastModified", false, "LAST_MODIFIED");
        public final static Property MimeType = new Property(5, String.class, "mimeType", false, "MIME_TYPE");
        public final static Property IsFolder = new Property(6, Boolean.class, "isFolder", false, "IS_FOLDER");
        public final static Property ClickTime = new Property(7, Long.class, "clickTime", false, "CLICK_TIME");
    }


    public RecentOpenDao(DaoConfig config) {
        super(config);
    }
    
    public RecentOpenDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"RECENT_OPEN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"PATH\" TEXT UNIQUE ," + // 2: path
                "\"SIZE\" INTEGER," + // 3: size
                "\"LAST_MODIFIED\" INTEGER," + // 4: lastModified
                "\"MIME_TYPE\" TEXT," + // 5: mimeType
                "\"IS_FOLDER\" INTEGER," + // 6: isFolder
                "\"CLICK_TIME\" INTEGER);"); // 7: clickTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"RECENT_OPEN\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, RecentOpen entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(3, path);
        }
 
        Long size = entity.getSize();
        if (size != null) {
            stmt.bindLong(4, size);
        }
 
        Long lastModified = entity.getLastModified();
        if (lastModified != null) {
            stmt.bindLong(5, lastModified);
        }
 
        String mimeType = entity.getMimeType();
        if (mimeType != null) {
            stmt.bindString(6, mimeType);
        }
 
        Boolean isFolder = entity.getIsFolder();
        if (isFolder != null) {
            stmt.bindLong(7, isFolder ? 1L: 0L);
        }
 
        Long clickTime = entity.getClickTime();
        if (clickTime != null) {
            stmt.bindLong(8, clickTime);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public RecentOpen readEntity(Cursor cursor, int offset) {
        RecentOpen entity = new RecentOpen( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // path
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // size
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // lastModified
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // mimeType
            cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0, // isFolder
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7) // clickTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, RecentOpen entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPath(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSize(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setLastModified(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setMimeType(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setIsFolder(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
        entity.setClickTime(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(RecentOpen entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(RecentOpen entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
