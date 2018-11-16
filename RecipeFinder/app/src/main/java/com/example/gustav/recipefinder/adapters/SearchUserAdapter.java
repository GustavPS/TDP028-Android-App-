package com.example.gustav.recipefinder.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.classes.User;
import android.view.View;

import java.util.List;

public class SearchUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<User> list;

    private OnItemClicked onClick;
    public interface OnItemClicked {
        void onItemClicked(int position);
    }
    public void setOnClick(OnItemClicked onClick) { this.onClick=onClick; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        LinearLayout layout;
        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            layout = v.findViewById(R.id.layout);
        }
    }

    public SearchUserAdapter(List<User> userList) { this.list = userList; }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_user_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder vH = (ViewHolder) holder;
        User user = list.get(position);
        vH.name.setText(user.getName());

        vH.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() { return this.list.size(); }
}
