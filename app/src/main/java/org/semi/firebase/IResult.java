package org.semi.firebase;

import android.support.annotation.NonNull;

public interface IResult<T> {
    public void onResult(T result);
    public void onFailure(@NonNull Exception exp);
}
