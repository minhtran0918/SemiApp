package org.semi.dialog;

import android.app.Activity;
import android.app.Dialog;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import org.semi.R;

public class InfoDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private TextView infoTextView;

    public InfoDialog(@NonNull Activity activity, String message) {
        super(activity);
        setContentView(R.layout.dialog_info);
        final AppCompatButton closeButton = findViewById(R.id.closeButton);
        infoTextView = findViewById(R.id.messageTextView);
        infoTextView.setText(message);
        closeButton.setOnClickListener(this);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeButton:
                dismiss();
                break;
        }
    }

    public void setMessage(String message) {
        infoTextView.setText(message);
    }
}
