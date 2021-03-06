package com.example.multimedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SendTextActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private MessageReceiver messageReceiver;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private Uri imageUri;
    private ImageView picture;
    private EditText sendtext;
    List<String> messageslist= new ArrayList<>();
    String phone;
    ListView messagesView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_text);

        Intent intent = getIntent();
        String[] data = intent.getStringExtra("data").split("\n");
        phone=data[1].replaceAll(" ","");

        TextView contact=(TextView)findViewById(R.id.textView);
        sendtext = (EditText) findViewById(R.id.edit_text);

        Button send = (Button) findViewById(R.id.send);
        Button takephoto = (Button) findViewById(R.id.takephoto);
        Button chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);
        picture = (ImageView) findViewById(R.id.picture);

        //????????????
        contact.setText(data[0]+":    "+data[1]);
        messagesView=(ListView)findViewById(R.id.messages);
        adapter =new ArrayAdapter<String>(this,android.R.layout. simple_list_item_1,messageslist);
        messagesView.setAdapter(adapter);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_SMS }, 1);
        } else {
            getSmsInPhone();
        }
        //??????????????????
        getSms();

        //??????????????????
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SendTextActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SendTextActivity.this, new String[]{Manifest.permission.SEND_SMS}, 2);
                } else {
                    sendMessage();
                }
            }
        });

        //????????????
        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(SendTextActivity.this,
                            "com.example.multimedia.fileprovider", outputImage);
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }
                // ??????????????????
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });

        //????????????????????????
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SendTextActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SendTextActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 3);
                } else {
                    openAlbum();
                }
            }
        });
    }

    //????????????????????????
    private void getSms() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        messageReceiver = new MessageReceiver();
        //????????????????????????
        intentFilter.setPriority(100);
        registerReceiver(messageReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //?????????????????????
        unregisterReceiver(messageReceiver);
    }

    //?????????????????????
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            //??????????????????
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            //?????????????????????
            String address = messages[0].getOriginatingAddress();

            String fullMessage = "";
            for (SmsMessage message : messages) {
                //??????????????????
                fullMessage += message.getMessageBody();
            }
            //????????????,??????????????????Android??????????????????????????????
//            abortBroadcast();
            messageslist.add(fullMessage);
            adapter =new ArrayAdapter<String>(context,android.R.layout. simple_list_item_1,messageslist);
            messagesView.setAdapter(adapter);
        }
    }

    //????????????
    private void sendMessage(){
        try {
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage(phone,null,sendtext.getText().toString(),null,null);
            messageslist.add(sendtext.getText().toString());
            adapter =new ArrayAdapter<String>(SendTextActivity.this,android.R.layout. simple_list_item_1,messageslist);
            messagesView.setAdapter(adapter);
            Toast.makeText(getApplicationContext(), "?????????????????????",
                    Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),
                    "???????????????????????????",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    //???????????????????????????
    @SuppressLint("LongLogTag")
    public void getSmsInPhone() {
        String SMS_URI_ALL = "content://sms/";

        Cursor cur = null;

        try {
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
             cur = getContentResolver().query(uri, projection, "address=?", new String[]{phone}, "date asc");// ????????????????????????

            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");

                do {
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int intType = cur.getInt(index_Type);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date date = new Date(longDate);
                    String strDate = dateFormat.format(date);

                    String strType = "";
                    if (intType == 1) {
                        strType = "?????????";
                    } else if (intType == 2) {
                        strType = "?????????";
                    } else {
                        strType = "null";
                    }
                    messageslist.add("[???????????????"+strAddress+" ?????????"+strDate+"]\n"
                            +"???????????????"+strbody+"\n"+strType+"\n");
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                }
            } else {
                messageslist.add("????????????????????????");
            } // end if
//            messageslist.add("?????????getSmsInPhone???");
        } catch (SQLiteException ex) {
            Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO://??????????????????
                if (resultCode == RESULT_OK) {

                    try {
//                        displayImage(getContentResolver().openInputStream(imageUri));
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // ???????????????????????????
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4?????????????????????????????????????????????
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4??????????????????????????????????????????
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    //????????????
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // ????????????
    }

    //??????????????????
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);//
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // ?????????document?????????Uri????????????document id??????
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // ????????????????????????id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // ?????????content?????????Uri??????????????????????????????
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // ?????????file?????????Uri?????????????????????????????????
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // ??????????????????????????????
    }

    //??????????????????
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // ??????Uri???selection??????????????????????????????
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //????????????
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
        }
    }

    //???????????????
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSmsInPhone();
                }
                else {
                    Toast.makeText(this, "??????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage();
                } else {
                    Toast.makeText(this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}