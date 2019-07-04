package org.semi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.semi.R;


public class SelectCategoryStoreDialog extends BottomSheetDialogFragment {

    private View mRootView;
    private BottomSheetListener mListener;
    private int mUserSelectCategory;

    public SelectCategoryStoreDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog d = super.onCreateDialog(savedInstanceState);
        // view hierarchy is inflated after dialog is shown
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                //this disables outside touch
                //d.getWindow().findViewById(R.id.touch_outside).setOnClickListener(null);
                //this prevents dragging behavior
                View content = d.getWindow().findViewById(R.id.design_bottom_sheet);
                ((CoordinatorLayout.LayoutParams) content.getLayoutParams()).setBehavior(null);
            }
        });
        return d;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement BottomSheetListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_select_category_store, container, false);
        initView(mRootView);
        return mRootView;
    }


    private void initView(View v) {

        RadioGroup selectCategory = mRootView.findViewById(R.id.btn_dialog_group_item);
        if (mUserSelectCategory != 0) {
            selectCategory.check(mUserSelectCategory);
            RadioButton radioButton = v.findViewById(mUserSelectCategory);
            radioButton.setFocusableInTouchMode(true);
            radioButton.requestFocus();
        }
        selectCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mListener.onSelectCategoryClicked(checkedId);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 350);
            }
        });
    }


    public interface BottomSheetListener {
        void onSelectCategoryClicked(int itemId);
    }

    public void setUserSelectCategory(int userSelectCategory) {
        this.mUserSelectCategory = userSelectCategory;
    }
}
