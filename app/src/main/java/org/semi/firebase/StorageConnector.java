package org.semi.firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.semi.util.MathUtils;

public class StorageConnector {
    private static final long MAX_SIZE = 1024 * 1024;
    private static StorageConnector instance;
    private StorageConnector(){}

    public void getBitmap(String path, final int desiredWidth , final IResult<Bitmap> result) {
        if(path == null || path.trim().equals("") || desiredWidth <= 0) {
            return;
        }
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference reference = storage.getReference().child(path);
        reference.getBytes(MAX_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if(bytes == null) {
                    result.onResult(null);
                } else {
                    int numBytes = bytes.length;
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    //get Width
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(bytes, 0, numBytes, opts);
                    //resample image
                    opts.inJustDecodeBounds = false;
                    opts.inSampleSize = MathUtils.getSample(desiredWidth, opts.outWidth) + 1;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, numBytes, opts);
                    result.onResult(bitmap);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                result.onFailure(e);
            }
        });
    }

    public static StorageConnector getInstance() {
        if(instance == null) {
            instance = new StorageConnector();
        }
        return instance;
    }
}
