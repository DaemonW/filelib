package com.grt.filemanager.orm.dao.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grt.filemanager.orm.dao.FtpAccount;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "FTP_ACCOUNT".
*/
public class FtpAccountDao extends AbstractDao<FtpAccount, Long> {

    public static final String TABLENAME = "FTP_ACCOUNT";

    /**
     * Properties of entity FtpAccount.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Type = new Property(1, Integer.class, "type", false, "TYPE");
        public final static Property Serveraddress = new Property(2, String.class, "serveraddress", false, "SERVERADDRESS");
        public final static Property Port = new Property(3, Integer.class, "port", false, "PORT");
        public final static Property Encoding = new Property(4, String.class, "encoding", false, "ENCODING");
        public final static Property Connectmode = new Property(5, Integer.class, "connectmode", false, "CONNECTMODE");
        public final static Property Byname = new Property(6, String.class, "byname", false, "BYNAME");
        public final static Property Account = new Property(7, String.class, "account", false, "ACCOUNT");
        public final static Property Password = new Property(8, String.class, "password", false, "PASSWORD");
        public final static Property Anonymous = new Property(9, Integer.class, "anonymous", false, "ANONYMOUS");
    };


    public FtpAccountDao(DaoConfig config) {
        super(config);
    }
    
    public FtpAccountDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FTP_ACCOUNT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TYPE\" INTEGER," + // 1: type
                "\"SERVERADDRESS\" TEXT," + // 2: serveraddress
                "\"PORT\" INTEGER," + // 3: port
                "\"ENCODING\" TEXT," + // 4: encoding
                "\"CONNECTMODE\" INTEGER," + // 5: connectmode
                "\"BYNAME\" TEXT," + // 6: byname
                "\"ACCOUNT\" TEXT," + // 7: account
                "\"PASSWORD\" TEXT," + // 8: password
                "\"ANONYMOUS\" INTEGER);"); // 9: anonymous
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FTP_ACCOUNT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, FtpAccount entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(2, type);
        }
 
        String serveraddress = entity.getServeraddress();
        if (serveraddress != null) {
            stmt.bindString(3, serveraddress);
        }
 
        Integer port = entity.getPort();
        if (port != null) {
            stmt.bindLong(4, port);
        }
 
        String encoding = entity.getEncoding();
        if (encoding != null) {
            stmt.bindString(5, encoding);
        }
 
        Integer connectmode = entity.getConnectmode();
        if (connectmode != null) {
            stmt.bindLong(6, connectmode);
        }
 
        String byname = entity.getByname();
        if (byname != null) {
            stmt.bindString(7, byname);
        }
 
        String account = entity.getAccount();
        if (account != null) {
            stmt.bindString(8, account);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(9, password);
        }
 
        Integer anonymous = entity.getAnonymous();
        if (anonymous != null) {
            stmt.bindLong(10, anonymous);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public FtpAccount readEntity(Cursor cursor, int offset) {
        FtpAccount entity = new FtpAccount( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // type
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // serveraddress
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // port
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // encoding
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // connectmode
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // byname
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // account
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // password
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9) // anonymous
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, FtpAccount entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setType(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setServeraddress(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPort(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setEncoding(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setConnectmode(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setByname(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setAccount(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setPassword(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setAnonymous(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(FtpAccount entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(FtpAccount entity) {
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
