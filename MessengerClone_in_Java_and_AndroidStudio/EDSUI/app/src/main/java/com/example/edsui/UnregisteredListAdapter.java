package com.example.edsui;
import Event_Delivery_System.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class UnregisteredListAdapter extends RecyclerView.Adapter<UnregisteredListAdapter.MyViewHolder>{

    private ArrayList<Event_Delivery_System.Topic> unregisteredTopics;
    private UnregisterdListClickListener clickListener;

    public UnregisteredListAdapter(ArrayList<Event_Delivery_System.Topic> unregisteredTopics, UnregisterdListClickListener clickListener) {
        this.unregisteredTopics = unregisteredTopics;
        this.clickListener = clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView topicName;

        public MyViewHolder(final View view) {
            super(view);
            topicName = view.findViewById(R.id.unregisteredTopicNameInList);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public UnregisteredListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.unregistered_topics_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UnregisteredListAdapter.MyViewHolder holder, int position) {
        String topicName = unregisteredTopics.get(position).getName();
        holder.topicName.setText(topicName);
    }

    @Override
    public int getItemCount() {
        return unregisteredTopics.size();
    }

    public interface UnregisterdListClickListener {
        void onClick(View v, int position);
    }
}
