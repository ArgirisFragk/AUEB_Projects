package com.example.edsui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import Event_Delivery_System.MultimediaFile;

public class ActiveTopicListAdapter extends RecyclerView.Adapter<ActiveTopicListAdapter.MyViewHolder>{

    private ArrayList<Event_Delivery_System.Message> history;
    private ActiveTopicListClickListener clickListener;

    public ActiveTopicListAdapter(ArrayList<Event_Delivery_System.Message> history,ActiveTopicListClickListener clickListener) {
        this.history = history;
        this.clickListener = clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        private TextView topicName;
        private TextView UserName;
        private TextView MessageText;
        private ImageView MessageImage;

        public MyViewHolder(final View view) {
            super(view);
            topicName = view.findViewById(R.id.MessageTopicName);
            UserName = view.findViewById(R.id.MessageUserName);
            MessageText = view.findViewById(R.id.MessageText);
            MessageImage = view.findViewById(R.id.MessageImage);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {clickListener.onClick(v,getAdapterPosition());}
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_topic_list_item, parent, false);
        return new MyViewHolder(itemview);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(history.get(position).getMessageType()) {
            holder.MessageImage.setVisibility(View.INVISIBLE);
            String msg = history.get(position).getTextValue();
            String[] items = msg.split("\\|");//0 -> TopicName, 1-> UserName, 2-> MessageText
            holder.topicName.setText(items[0]);
            holder.UserName.setText(items[1]);
            holder.MessageText.setText(items[2]);
        }
        else {
            holder.topicName.setText(history.get(position).getFileText().split("\\|")[0]);
            holder.UserName.setText(history.get(position).getFileText().split("\\|")[1]);
            String fileExtension = history.get(position).getFileText().split("\\|")[2];
            if(fileExtension.contains("image")) {
                holder.MessageText.setText("");
                MultimediaFile tmp = history.get(position).getFileValue();
                Bitmap bitmap = BitmapFactory.decodeByteArray(tmp.getMultimediaFile(),0,tmp.getMultimediaFile().length);
                holder.MessageImage.setImageBitmap(bitmap);
                holder.MessageImage.setMaxWidth(25);
                holder.MessageImage.setMaxHeight(25);
                holder.MessageImage.setVisibility(View.VISIBLE);
            }
            else {
                holder.MessageText.setText(Html.fromHtml("<i><b><u>Sent Video File</u></b></i>"));
            }



        }
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    public interface ActiveTopicListClickListener {
        void onClick(View v,int position);
    }
}
