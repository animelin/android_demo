package com.example.sqllit_librarydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddBookActivity extends AppCompatActivity {
    private EditText E_name,E_author,E_price,E_pages,E_category_id,E_id;//编辑框
    private MyDatabaseHelper helper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addbook);
        final EditText e_name=findViewById(R.id.E_name);
        final EditText e_author=findViewById(R.id.E_author);
        final EditText e_price=findViewById(R.id.E_price);
        final EditText e_pages=findViewById(R.id.E_pages);
        final EditText e_category_id=findViewById(R.id.E_category_id);
        helper = new MyDatabaseHelper(this, "BookStore.db", null, 1);//dbName数据库名
        Button addBook = (Button) findViewById(R.id.insert_Add);
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name =e_name.getText().toString();
                String author =e_author.getText().toString();
                String price =e_price.getText().toString();
                String pages =e_pages.getText().toString();
                String category_id =e_category_id.getText().toString();
                db = helper.getWritableDatabase();//获取到了 SQLiteDatabase 对象
                ContentValues values = new ContentValues();
                values.put("name",name);
                values.put("author",author);
                values.put("price",price);
                values.put("pages",pages);
                values.put("category_id",category_id);
                db.insert("Book", null, values);
                values.clear();
                db.close();
                finish();
            }
        });
    }
}

