package ueh.edu.vn.md.micro4nerds.data.local.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

import ueh.edu.vn.md.micro4nerds.data.local.SQLiteHelper;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;

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

                list.add(new CartItem(productId, name, price, imageUrl, quantity));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    public void addToCart(CartItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(SQLiteHelper.TABLE_CART,
                new String[]{SQLiteHelper.COL_CART_QUANTITY},
                SQLiteHelper.COL_CART_PRODUCT_ID + " = ?",
                new String[]{item.getProductId()},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int currentQuantityIndex = cursor.getColumnIndex(SQLiteHelper.COL_CART_QUANTITY);
            int currentQuantity = 0;
            if (currentQuantityIndex != -1) currentQuantity = cursor.getInt(currentQuantityIndex);

            ContentValues updateValues = new ContentValues();
            updateValues.put(SQLiteHelper.COL_CART_QUANTITY, currentQuantity + item.getQuantity());

            db.update(SQLiteHelper.TABLE_CART, updateValues, SQLiteHelper.COL_CART_PRODUCT_ID + " = ?", new String[]{item.getProductId()});
            cursor.close();
        } else {
            ContentValues insertValues = new ContentValues();
            insertValues.put(SQLiteHelper.COL_CART_PRODUCT_ID, item.getProductId());
            insertValues.put(SQLiteHelper.COL_CART_NAME, item.getProductName());
            insertValues.put(SQLiteHelper.COL_CART_PRICE, item.getProductPrice());
            insertValues.put(SQLiteHelper.COL_CART_IMAGE_URL, item.getImageUrl());
            insertValues.put(SQLiteHelper.COL_CART_QUANTITY, item.getQuantity());

            db.insert(SQLiteHelper.TABLE_CART, null, insertValues);
        }

        db.close();
    }

    public void updateQuantity(String productId, int newQuantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COL_CART_QUANTITY, newQuantity);
        db.update(SQLiteHelper.TABLE_CART, values, SQLiteHelper.COL_CART_PRODUCT_ID + " = ?", new String[]{productId});
        db.close();
    }

    public void removeFromCart(String productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(SQLiteHelper.TABLE_CART, SQLiteHelper.COL_CART_PRODUCT_ID + " = ?", new String[]{productId});
        db.close();
    }

    public void clearCart() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(SQLiteHelper.TABLE_CART, null, null);
        db.close();
    }
}
