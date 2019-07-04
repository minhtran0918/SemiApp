package org.semi.fragment;


import android.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.squareup.picasso.Picasso;

import org.semi.R;
import org.semi.utils.Contract;
import org.semi.utils.DialogUtils;
import org.semi.utils.SignInUtils;
import org.semi.views.UserAddStoreActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {

    private static MeFragment mMeFragmentInstance;
    private View mRootView;
    private MutableLiveData<Boolean> mIsAuthenticated = new MutableLiveData<>();
    private int RC_SIGN_IN = 1789;
    private Button mBtnLogin;
    private CircleImageView mImgAvatar;
    private TextView mTxtUserName;

    public static MeFragment getInstance() {
        if (mMeFragmentInstance == null) {
            mMeFragmentInstance = new MeFragment();
        }
        return mMeFragmentInstance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_me, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        if (SignInUtils.userAuthenticated()) {
            mIsAuthenticated.setValue(true);
        } else {
            mIsAuthenticated.setValue(false);
        }
        mImgAvatar = mRootView.findViewById(R.id.img_me_avatar_user);
        mTxtUserName = mRootView.findViewById(R.id.txt_me_username);
        mBtnLogin = mRootView.findViewById(R.id.btn_me_login);
        mBtnLogin.setOnClickListener(mOnClickListener);

        TextView txt_select_rating, txt_select_send_email, txt_show_infor, txt_add_store;
        txt_select_rating = mRootView.findViewById(R.id.txt_me_select_rating);
        txt_select_send_email = mRootView.findViewById(R.id.txt_me_select_send_email);
        txt_show_infor = mRootView.findViewById(R.id.txt_me_select_show_infor_dev);
        txt_add_store = mRootView.findViewById(R.id.txt_me_select_add_store);
        txt_select_rating.setOnClickListener(mOnClickListener);
        txt_select_send_email.setOnClickListener(mOnClickListener);
        txt_show_infor.setOnClickListener(mOnClickListener);
        txt_add_store.setOnClickListener(mOnClickListener);
        mIsAuthenticated.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    mImgAvatar.setImageResource(R.drawable.me_ic_user_empty_white_24);
                    Picasso.get().load(SignInUtils.getCurrentUser().getPhotoUrl()).into(mImgAvatar);
                    mTxtUserName.setText(SignInUtils.getCurrentUser().getDisplayName());
                    mBtnLogin.setText("Đăng xuất");
                } else {
                    mImgAvatar.setImageResource(R.drawable.me_ic_user_empty_white_24);
                    mTxtUserName.setText("Người dùng");
                    mBtnLogin.setText("Đăng nhập");
                }
            }
        });


    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_me_login:
                    if (!mIsAuthenticated.getValue()) {
                        startActivityForResult(SignInUtils.intentCallLogin(), RC_SIGN_IN);
                    } else {
                        SignInUtils.userLogout();
                        mIsAuthenticated.setValue(false);
                    }
                    break;
                case R.id.txt_me_select_add_store:
                    Intent intent = new Intent(getActivity(), UserAddStoreActivity.class);
                    startActivity(intent);
                    break;
                case R.id.txt_me_select_rating:
                    DialogUtils.showSnackBar(getActivity(), mRootView, "Ứng dụng chưa được đăng tải trên CHPlay");
                    break;
                case R.id.txt_me_select_send_email:
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:minhtran0918@gmail.com"));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Thư góp ý cho ứng dụng Semi");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Nội dung góp ý / báo lỗi cho ứng dụng Semi:\n");

                    if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(emailIntent);
                    }
                    break;
                case R.id.txt_me_select_show_infor_dev:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Semi\n\nTìm kiếm hàng hóa & cửa hàng")
                            .setMessage("Đây là nội dung")
                            .setPositiveButton("ĐÓNG", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                mIsAuthenticated.setValue(true);
                return;
            }

            if (response == null) {
                mIsAuthenticated.setValue(false);
                return;
            } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                DialogUtils.showSnackBar(null, mRootView, "Kết nối có vấn đề, xin vui lòng kiểm tra internet của bạn");
            } else {
                Log.e(Contract.TAG, "Sign-in error: ", response.getError());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
