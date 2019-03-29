package org.semi.util;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import org.semi.R;
import org.semi.custom.InfoDialog;

public final class DialogUtils {
    private DialogUtils() {
    }

    public static void showAlert(Context context, String title, String message) {
        AlertDialog dgBuilder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.adg_position_button, null)
                .create();
        dgBuilder.show();
    }

    public static void showInfoDialog(Activity activity, String message) {
        new InfoDialog(activity, message).show();
    }
}
