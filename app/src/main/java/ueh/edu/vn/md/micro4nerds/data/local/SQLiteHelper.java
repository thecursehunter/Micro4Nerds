package ueh.edu.vn.md.micro4nerds.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Micro4Nerds.db";
    private static final int DB_VERSION = 2; // Đã tăng phiên bản DB

    // Bảng products_cache
    public static final String TABLE_PRODUCTS = "products_cache";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PRICE = "price";
    public static final String COL_IMAGE = "imageUrl";
    public static final String COL_DESC = "description";
    public static final String COL_STOCK = "stock";

    // Bảng cart
    public static final String TABLE_CART = "cart";
    public static final String COL_CART_ID = "id";
    public static final String COL_CART_PRODUCT_ID = "productId";
    public static final String COL_CART_NAME = "name";
    public static final String COL_CART_PRICE = "price";
    public static final String COL_CART_IMAGE_URL = "imageUrl";
    public static final String COL_CART_QUANTITY = "quantity";

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

        String createCartTable = "CREATE TABLE " + TABLE_CART + " (" +
                COL_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CART_PRODUCT_ID + " TEXT, " +
                COL_CART_NAME + " TEXT, " +
                COL_CART_PRICE + " REAL, " +
                COL_CART_IMAGE_URL + " TEXT, " +
                COL_CART_QUANTITY + " INTEGER)";
        db.execSQL(createCartTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }
}
