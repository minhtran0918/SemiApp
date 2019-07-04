package org.semi.firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.semi.utils.MathUtils;
import org.semi.utils.StringUtils;

public class StorageConnector {
    private static final long MAX_SIZE = 1024 * 1024;
    private static StorageConnector instance;

    public void getBitmap(String path, final int desiredWidth, final IResult<Bitmap> result) {
        if (path == null || path.trim().equals("") || desiredWidth <= 0) {
            return;
        }
        String path_first_img = StringUtils.getListPath(path).get(0);
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference reference = storage.getReference().child(path_first_img);
        reference.getBytes(MAX_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if (bytes == null) {
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
        if (instance == null) {
            instance = new StorageConnector();
        }
        return instance;
    }

    //path: "store/0Rt1k9ow9nqJwcChO0ic/HxVAYoj.jpg"
    //how to use
    //getREference.getDownloadUrl().addOnSuccessListener... -> Add picasso inside onsucces to download
    public static StorageReference getReference(String path) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // Create a reference with an initial file path and name
        //StorageReference pathReference = storageRef.child(Contract.FIREBASE_STORAGE_ROOT_BUCKET + path);
        StorageReference pathReference = storageRef.child(path);
        return pathReference;
    }

}
