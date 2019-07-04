package org.semi.adapter;

import android.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.esafirm.imagepicker.model.Image;
import com.squareup.picasso.Picasso;

import org.semi.R;

import java.io.File;
import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.RecyclerViewHolder> {
    private ArrayList<Image> mListImage;
    private View.OnClickListener mOnItemClickListener;
    public MutableLiveData<Integer> countImage = new MutableLiveData<>();

    public ImageListAdapter() {
        mListImage = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_select_image, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
        Picasso.get()
                .load(new File(mListImage.get(i).getPath()))
                .resize(400, 400)
                .centerCrop()
                .into(recyclerViewHolder.img);
    }

    public void setListImage(ArrayList<Image> listImage) {
        this.mListImage = listImage;
        countImage.setValue(mListImage.size());
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    private void clearItemPosition(int position) {
        mListImage.remove(position);
        countImage.setValue(mListImage.size());
        notifyDataSetChanged();
    }

    public ArrayList<Image> getListImage() {
        return mListImage;
    }

    @Override
    public int getItemCount() {
        return mListImage.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageButton btn_clear;

        public RecyclerViewHolder(final View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imgview_select_image);
            btn_clear = itemView.findViewById(R.id.btn_select_image_clear);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
            btn_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Xác nhận xóa ảnh")
                            .setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clearItemPosition(getAdapterPosition());
                                }
                            }).show();
                }
            });
        }
    }
}
