package com.example.gustav.recipefinder.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.classes.ProfileSetting;
import android.view.View;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProfileSetting> list;

    private OnItemClicked onClick;

    public interface OnItemClicked {
        void onItemClicked(int position);
    }

    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        LinearLayout layout;
        public ViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            text = v.findViewById(R.id.text);
            layout = v.findViewById(R.id.layout);
        }
    }

    public ProfileAdapter(List<ProfileSetting> settingList) { this.list = settingList; }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder vH = (ViewHolder) holder;
        ProfileSetting setting = list.get(position);
        vH.image.setImageBitmap(setting.getBitmap());
        vH.text.setText(setting.getText());

        vH.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }
}
