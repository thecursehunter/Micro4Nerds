package ueh.edu.vn.md.micro4nerds.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Micro4Nerds.db";
    private static final int DB_VERSION = 1;

    // Để public static để bên DAO gọi được tên bảng
    public static final String TABLE_PRODUCTS = "products_cache";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PRICE = "price";
    public static final String COL_IMAGE = "imageUrl";
    public static final String COL_DESC = "description";
    public static final String COL_STOCK = "stock";

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProductTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_ID + " TEXT PRIMARY KEY, " +
                COL_NAME + " TEXT, " +
                COL_PRICE + " REAL, " +
                COL_IMAGE + " TEXT, " +
                COL_DESC + " TEXT, " +
                COL_STOCK + " INTEGER)";
        db.execSQL(createProductTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }
}