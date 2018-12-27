package com.example.sougataghosh.seatsfff;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String profileImageUrl;

    DatabaseReference rf = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadImageToFirebaseStorage();
    }

    private void uploadImageToFirebaseStorage() {

//        Bitmap icon = getBitmapFromView(v);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.blue);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getApplicationContext()
                .getContentResolver(), icon, "Title", null);
        Uri uriProfileImage =  Uri.parse(path);

//        Uri uriProfileImage = getUriToResource(getApplicationContext(), R.drawable.blue);

        final StorageReference storageReferenceSeats = FirebaseStorage
                .getInstance()
                .getReference("seatlayoutinfo/"+System.currentTimeMillis()+".jpg");

        if (uriProfileImage != null){
            UploadTask uploadTask;
            uploadTask = storageReferenceSeats.putFile(uriProfileImage);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL

                    return storageReferenceSeats.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(getApplicationContext(), "seatlayoutinfo",
                                Toast.LENGTH_SHORT).show();

                        Uri downloadUri = task.getResult();
                        profileImageUrl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance()
                                .getReference("seatlayouturls");

                        String dateTime = DateFormat.getDateTimeInstance().format(new Date());
                        ref.child(dateTime).setValue(profileImageUrl);

                        Toast.makeText(getApplicationContext(), "seatlayoutinfoXXX",
                                Toast.LENGTH_SHORT).show();

                        String url = profileImageUrl;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);

//                        ins.setText(profileImageUrl);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }

    public static final Uri getUriToResource(@NonNull Context context,
                                             @AnyRes int resId)
            throws Resources.NotFoundException {
        /** Return a Resources instance for your application's package. */
        Resources res = context.getResources();
        /**
         * Creates a Uri which parses the given encoded URI string.
         * @param uriString an RFC 2396-compliant, encoded URI
         * @throws NullPointerException if uriString is null
         * @return Uri for this given uri string
         */
        Uri resUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resId)
                + '/' + res.getResourceTypeName(resId)
                + '/' + res.getResourceEntryName(resId));
        /** return uri */
        return resUri;
    }

//    public static Bitmap getBitmapFromView(View view) {



//        //Define a bitmap with the same size as the view
//        Log.i("HEIGHT: ", String.valueOf(view.getHeight()) );
//        Bitmap returnedBitmap = Bitmap.createBitmap(100, 100,Bitmap.Config.ARGB_8888);
//        //Bind a canvas to it
//        Canvas canvas = new Canvas(returnedBitmap);
//        //Get the view's background
////        Drawable bgDrawable =view.getBackground();
////        if (bgDrawable!=null)
////            //has background drawable, then draw it on the canvas
////            bgDrawable.draw(canvas);
////        else
////            //does not have background drawable, then draw white background on the canvas
////            canvas.drawColor(Color.WHITE);
//        // draw the view on the canvas
//        view.draw(canvas);
//        //return the bitmap
//        return returnedBitmap;
//    }
}

