package org.semi.utils;

import android.app.Activity;
import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import org.semi.R;
import org.semi.dialog.InfoDialog;

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

    public static void showSnackBar(Activity activity, View v, String message) {
        if (activity != null) {
            v = activity.findViewById(android.R.id.content);
        }

        final Snackbar snackbar = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        final View view = snackbar.getView();
        final TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, MyApp.getContext().getResources().getDimension(R.dimen.snack_bar_text_size));
        snackbar.show();
    }
}
