package com.y34h1a.reactivelazyloading.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.y34h1a.reactivelazyloading.R;
import com.y34h1a.reactivelazyloading.model.User;
import com.y34h1a.reactivelazyloading.util.RxBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final RxBus bus;

    private final List<User> items = new ArrayList<>();

    public UserAdapter(RxBus bus){
        this.bus = bus;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_item, parent, false);
            return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bindContent(items.get(position));

        boolean lastPositionReached = position == items.size() - 1;
        if (lastPositionReached) {
            bus.send(new PageEvent());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<User> items) {
        this.items.addAll(items);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvEmailId)
        TextView tvEmailId;

        UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindContent(User user) {
            tvName.setText(user.getName());
            tvEmailId.setText(user.getEmail());
        }
    }

    public static class PageEvent {}
}
