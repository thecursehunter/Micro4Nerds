package ueh.edu.vn.md.micro4nerds.data.local.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

import ueh.edu.vn.md.micro4nerds.data.local.SQLiteHelper;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;
import ueh.edu.vn.md.micro4nerds.data.model.Product;

public class CartDao {
    private final SQLiteHelper dbHelper;

    public CartDao(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public List<CartItem> getCartItems() {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_CART, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(SQLiteHelper.COL_CART_PRODUCT_ID);
                int nameIndex = cursor.getColumnIndex(SQLiteHelper.COL_CART_NAME);
                int quantityIndex = cursor.getColumnIndex(SQLiteHelper.COL_CART_QUANTITY);
                int priceIndex = cursor.getColumnIndex(SQLiteHelper.COL_CART_PRICE);
                int imgIndex = cursor.getColumnIndex(SQLiteHelper.COL_CART_IMAGE_URL);

                String productId = "";
                if (idIndex != -1) productId = cursor.getString(idIndex);

                String name = "";
                if (nameIndex != -1) name = cursor.getString(nameIndex);

                int quantity = 0;
                if (quantityIndex != -1) quantity = cursor.getInt(quantityIndex);

                double price = 0;
                if (priceIndex != -1) price = cursor.getDouble(priceIndex);

                String imageUrl = "";
                if (imgIndex != -1) imageUrl = cursor.getString(imgIndex);

                // SỬA LỖI: Đúng thứ tự: productId, productName, productPrice, imageUrl, quantity
                list.add(new CartItem(productId, name, price, imageUrl, quantity));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    public void addToCart(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 1. Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        Cursor cursor = db.query(SQLiteHelper.TABLE_CART,
                new String[]{SQLiteHelper.COL_CART_QUANTITY},
                SQLiteHelper.COL_CART_PRODUCT_ID + " = ?",
                new String[]{product.getId()},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // 2a. Nếu có, cập nhật số lượng
            int currentQuantityIndex = cursor.getColumnIndex(SQLiteHelper.COL_CART_QUANTITY);
            int currentQuantity = 0;
            if(currentQuantityIndex != -1) currentQuantity = cursor.getInt(currentQuantityIndex);

            ContentValues updateValues = new ContentValues();
            updateValues.put(SQLiteHelper.COL_CART_QUANTITY, currentQuantity + 1);

            db.update(SQLiteHelper.TABLE_CART, updateValues, SQLiteHelper.COL_CART_PRODUCT_ID + " = ?", new String[]{product.getId()});
            cursor.close();
        } else {
            // 2b. Nếu không, thêm mới
            ContentValues insertValues = new ContentValues();
            insertValues.put(SQLiteHelper.COL_CART_PRODUCT_ID, product.getId());
            insertValues.put(SQLiteHelper.COL_CART_NAME, product.getName());
            insertValues.put(SQLiteHelper.COL_CART_PRICE, product.getPrice());
            insertValues.put(SQLiteHelper.COL_CART_IMAGE_URL, product.getImageUrl());
            insertValues.put(SQLiteHelper.COL_CART_QUANTITY, 1);

            db.insert(SQLiteHelper.TABLE_CART, null, insertValues);
        }

        db.close();
    }
}
