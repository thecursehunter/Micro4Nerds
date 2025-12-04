package ueh.edu.vn.md.micro4nerds.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import ueh.edu.vn.md.micro4nerds.model.Product;

public class ProductDao {
    private final SQLiteHelper dbHelper;

    public ProductDao(Context context) {
        // Khởi tạo Helper tại đây
        dbHelper = new SQLiteHelper(context);
    }

    public void insertListProducts(List<Product> productList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // Xin quyền ghi từ Helper
        db.beginTransaction();
        try {
            // Gọi tên bảng từ class Helper
            db.delete(SQLiteHelper.TABLE_PRODUCTS, null, null);

            for (Product p : productList) {
                ContentValues values = new ContentValues();
                values.put(SQLiteHelper.COL_ID, p.getId());
                values.put(SQLiteHelper.COL_NAME, p.getName());
                values.put(SQLiteHelper.COL_PRICE, p.getPrice());
                values.put(SQLiteHelper.COL_IMAGE, p.getImageUrl());
                values.put(SQLiteHelper.COL_DESC, p.getDescription());
                values.put(SQLiteHelper.COL_STOCK, p.getStock());

                db.insert(SQLiteHelper.TABLE_PRODUCTS, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_PRODUCTS, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Product p = new Product();
                // Code map dữ liệu y hệt như cũ, chỉ thêm SQLiteHelper. phía trước tên cột
                int idIndex = cursor.getColumnIndex(SQLiteHelper.COL_ID);
                int nameIndex = cursor.getColumnIndex(SQLiteHelper.COL_NAME);
                int priceIndex = cursor.getColumnIndex(SQLiteHelper.COL_PRICE);
                int imgIndex = cursor.getColumnIndex(SQLiteHelper.COL_IMAGE);
                int descIndex = cursor.getColumnIndex(SQLiteHelper.COL_DESC);
                int stockIndex = cursor.getColumnIndex(SQLiteHelper.COL_STOCK);

                if (idIndex != -1) p.setId(cursor.getString(idIndex));
                if (nameIndex != -1) p.setName(cursor.getString(nameIndex));
                if (priceIndex != -1) p.setPrice(cursor.getDouble(priceIndex));
                if (imgIndex != -1) p.setImageUrl(cursor.getString(imgIndex));
                if (descIndex != -1) p.setDescription(cursor.getString(descIndex));
                if (stockIndex != -1) p.setStock(cursor.getInt(stockIndex));

                list.add(p);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}