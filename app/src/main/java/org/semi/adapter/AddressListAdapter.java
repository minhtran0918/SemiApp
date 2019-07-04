package org.semi.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import org.semi.R;
import org.semi.databases.model.AddressCity;
import org.semi.databases.model.AddressDistrict;
import org.semi.databases.model.AddressWard;

import java.util.ArrayList;
import java.util.List;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.AddressViewHolder> {

    private int mModeSelect;
    public List<AddressCity> mListCity = new ArrayList<>();
    private List<AddressDistrict> mListDistrict = new ArrayList<>();
    private List<AddressWard> mListWard = new ArrayList<>();

    private int sCityLastCheckedPos = -1;
    private int sDistrictLastCheckedPos = -1;
    private int sWardLastCheckedPos = -1;

    private View.OnClickListener mOnItemClickListener;

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list_address, viewGroup, false);
        return new AddressViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressListAdapter.AddressViewHolder addressViewHolder, int i) {
        if (mModeSelect == 1) {
            AddressCity city = mListCity.get(i);
            addressViewHolder.mAddressTextView.setText(city.toString());
            addressViewHolder.mAddressTextView.setChecked(i == sCityLastCheckedPos);
        } else if (mModeSelect == 2) {
            AddressDistrict district = mListDistrict.get(i);
            addressViewHolder.mAddressTextView.setText(district.toString());
            addressViewHolder.mAddressTextView.setChecked(i == sDistrictLastCheckedPos);
        } else if (mModeSelect == 3) {
            AddressWard ward = mListWard.get(i);
            addressViewHolder.mAddressTextView.setText(ward.toString());
            addressViewHolder.mAddressTextView.setChecked(i == sWardLastCheckedPos);
        }
    }

    @Override
    public int getItemCount() {
        if (mModeSelect == 1) {
            if (mListCity != null) {
                return mListCity.size();
            }
        } else if (mModeSelect == 2) {
            if (mListDistrict != null) {
                return mListDistrict.size();
            }
        } else if (mModeSelect == 3) {
            if (mListWard != null) {
                return mListWard.size();
            }
        }
        return 0;
    }

    public void setModeSelect(int modeSelect) {
        mModeSelect = modeSelect;
    }

    public void setListCity(List<AddressCity> listCity) {
        //refreshRecycleView();
        mListCity.addAll(listCity);
        notifyDataSetChanged();
    }

    public void setListDistrict(List<AddressDistrict> listDistrict) {
        refreshRecycleView();
        mListDistrict = listDistrict;
        notifyDataSetChanged();
    }

    public void setListWard(List<AddressWard> listWard) {
        refreshRecycleView();
        mListWard = listWard;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    //do Checked item
    public void changeCheckedState(int item_id) {

        int newPos = getIndexFromList(item_id);
        if (mModeSelect == 1) {
            if (sCityLastCheckedPos != newPos) {
                int oldPos = sCityLastCheckedPos;
                sCityLastCheckedPos = newPos;
                notifyItemChanged(oldPos);
                notifyItemChanged(sCityLastCheckedPos);
            }
        } else if (mModeSelect == 2) {
            if (sDistrictLastCheckedPos != newPos) {
                int oldPos = sDistrictLastCheckedPos;
                sDistrictLastCheckedPos = newPos;
                notifyItemChanged(oldPos);
                notifyItemChanged(sDistrictLastCheckedPos);
            }
        } else if (mModeSelect == 3) {
            if (sWardLastCheckedPos != newPos) {
                int oldPos = sWardLastCheckedPos;
                sWardLastCheckedPos = newPos;
                notifyItemChanged(oldPos);
                notifyItemChanged(sWardLastCheckedPos);
            }
        }
    }

    public int getIndexFromList(int id) {
        if (mModeSelect == 1) {
            for (int i = 0; i < mListCity.size(); i++) {
                if (mListCity.get(i).getId() == id) {
                    return i;
                }
            }
        } else if (mModeSelect == 2) {
            for (int i = 0; i < mListDistrict.size(); i++) {
                if (mListDistrict.get(i).getId() == id) {
                    return i;
                }
            }
        } else if (mModeSelect == 3) {
            for (int i = 0; i < mListWard.size(); i++) {
                if (mListWard.get(i).getId() == id) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void refreshRecycleView() {
        if (mModeSelect == 1) {
            if (!mListCity.isEmpty()) {
                mListCity.clear();
            }
        }
        if (mModeSelect == 2) {
            if (!mListDistrict.isEmpty()) {
                mListDistrict.clear();
            }
        }
        if (mModeSelect == 3) {
            if (!mListWard.isEmpty()) {
                mListWard.clear();
            }
        }
        notifyDataSetChanged();
    }

    public void filterList(List<AddressCity> filterList) {
        List<AddressCity> temp = filterList;
        mListCity.clear();
        mListCity.addAll(temp);
        notifyDataSetChanged();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {

        private CheckedTextView mAddressTextView;

        private AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            mAddressTextView = itemView.findViewById(R.id.txt_item_list_address);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
