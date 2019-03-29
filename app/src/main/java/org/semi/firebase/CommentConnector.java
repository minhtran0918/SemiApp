package org.semi.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.semi.object.Comment;
import org.semi.object.DBContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentConnector {
    private static CommentConnector instance;

    public void getComments(String storeId, final IResult<List<Comment>> result) {
        Map<String, Object> data = new HashMap<>();
        data.put(CFContract.GetComments.STORE_ID, storeId);
        FirebaseFunctions.getInstance().getHttpsCallable(CFContract.GetComments.NAME)
                .call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) httpsCallableResult.getData();
                        List<Comment> comments = new ArrayList<>();
                        if(mapList.size() > 0) {
                            for(Map<String, Object> map : mapList) {
                                Comment comment = new Comment();
                                comment.setId((String) map.get(DBContract.ID));
                                comment.setComment((String) map.get(DBContract.Comment.COMMENT));
                                comment.setTime(getTimestamp(map, DBContract.Comment.TIME));
                                comment.setEditTime(getTimestamp(map, DBContract.Comment.EDIT_TIME));
                                comment.setStoreId((String) map.get(DBContract.Comment.STORE_ID));
                                comment.setUserDisplayName((String) map.get(DBContract.Comment.USER_DISPLAY_NAME));
                                comment.setUserPhotoURL((String) map.get(DBContract.Comment.USER_PHOTO_URL));
                                comments.add(comment);
                            }
                        }
                        result.onResult(comments);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onFailure(e);
                    }
                });
    }

    public void postComment(String storeId, String comment, float rating, final IResult<Timestamp> result) {
        Map<String, Object> data = new HashMap<>();
        data.put(CFContract.PostComment.STORE_ID, storeId);
        data.put(CFContract.PostComment.COMMENT, comment);
        data.put(CFContract.PostComment.USER_RATING, rating);
        FirebaseFunctions.getInstance().getHttpsCallable(CFContract.PostComment.NAME)
                .call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        Map<String, Object> map = (Map<String, Object>) httpsCallableResult.getData();
                        if (map != null) {
                            result.onResult(getTimestamp(map, DBContract.Comment.EDIT_TIME));
                        } else {
                            result.onResult(null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onFailure(e);
                    }
                });
    }

    private Timestamp getTimestamp(Map<String, Object> map, String key) {
        Map<String, Object> timestampMap = (Map<String, Object>) map.get(key);
        long seconds = ((Number)timestampMap.get(DBContract.Comment.TIME_SEC)).longValue();
        int nanoseconds = ((Number)timestampMap.get(DBContract.Comment.TIME_NANO)).intValue();
        return new Timestamp(seconds, nanoseconds);
    }

    public static CommentConnector getInstance() {
        if(instance == null) {
            instance = new CommentConnector();
        }
        return instance;
    }
}
