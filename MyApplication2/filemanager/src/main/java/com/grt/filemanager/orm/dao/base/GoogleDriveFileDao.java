package com.grt.filemanager.orm.dao.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grt.filemanager.orm.dao.GoogleDriveFile;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "GOOGLE_DRIVE_FILE".
*/
public class GoogleDriveFileDao extends AbstractDao<GoogleDriveFile, Long> {

    public static final String TABLENAME = "GOOGLE_DRIVE_FILE";

    /**
     * Properties of entity GoogleDriveFile.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Path = new Property(2, String.class, "path", false, "PATH");
        public final static Property Size = new Property(3, Long.class, "size", false, "SIZE");
        public final static Property MimeType = new Property(4, String.class, "mimeType", false, "MIME_TYPE");
        public final static Property LastModified = new Property(5, Long.class, "lastModified", false, "LAST_MODIFIED");
        public final static Property IsFolder = new Property(6, Boolean.class, "isFolder", false, "IS_FOLDER");
        public final static Property AccountId = new Property(7, Integer.class, "accountId", false, "ACCOUNT_ID");
        public final static Property ParentId = new Property(8, String.class, "parentId", false, "PARENT_ID");
        public final static Property FileId = new Property(9, String.class, "fileId", false, "FILE_ID");
    };


    public GoogleDriveFileDao(DaoConfig config) {
        super(config);
    }
    
    public GoogleDriveFileDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GOOGLE_DRIVE_FILE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"PATH\" TEXT," + // 2: path
                "\"SIZE\" INTEGER," + // 3: size
                "\"MIME_TYPE\" TEXT," + // 4: mimeType
                "\"LAST_MODIFIED\" INTEGER," + // 5: lastModified
                "\"IS_FOLDER\" INTEGER," + // 6: isFolder
                "\"ACCOUNT_ID\" INTEGER," + // 7: accountId
                "\"PARENT_ID\" TEXT," + // 8: parentId
                "\"FILE_ID\" TEXT UNIQUE );"); // 9: fileId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GOOGLE_DRIVE_FILE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, GoogleDriveFile entity) {
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
 
        String mimeType = entity.getMimeType();
        if (mimeType != null) {
            stmt.bindString(5, mimeType);
        }
 
        Long lastModified = entity.getLastModified();
        if (lastModified != null) {
            stmt.bindLong(6, lastModified);
        }
 
        Boolean isFolder = entity.getIsFolder();
        if (isFolder != null) {
            stmt.bindLong(7, isFolder ? 1L: 0L);
        }
 
        Integer accountId = entity.getAccountId();
        if (accountId != null) {
            stmt.bindLong(8, accountId);
        }
 
        String parentId = entity.getParentId();
        if (parentId != null) {
            stmt.bindString(9, parentId);
        }
 
        String fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindString(10, fileId);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public GoogleDriveFile readEntity(Cursor cursor, int offset) {
        GoogleDriveFile entity = new GoogleDriveFile( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // path
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // size
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // mimeType
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // lastModified
            cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0, // isFolder
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // accountId
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // parentId
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // fileId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, GoogleDriveFile entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPath(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSize(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setMimeType(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setLastModified(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setIsFolder(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
        entity.setAccountId(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setParentId(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setFileId(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(GoogleDriveFile entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(GoogleDriveFile entity) {
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
