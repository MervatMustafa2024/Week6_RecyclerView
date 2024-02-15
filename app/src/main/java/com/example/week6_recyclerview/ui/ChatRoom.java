package com.example.week6_recyclerview.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week6_recyclerview.R;
import com.example.week6_recyclerview.data.ChatRoomViewModel;
import com.example.week6_recyclerview.databinding.RoomChatBinding;
import com.example.week6_recyclerview.databinding.SentMessageBinding;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatRoom extends AppCompatActivity {
    RoomChatBinding binding;
    ChatRoomViewModel chatModel ;
    ArrayList<String> messages = new ArrayList<>();

    RecyclerView.Adapter<MyRowHolder> myAdpter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_chat);

        binding= RoomChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);


        messages = chatModel.messages.getValue();
        if(messages == null)
        {
            chatModel.messages.postValue( messages = new ArrayList<String>());
        }




        binding.recyclerView.setAdapter(myAdpter=new RecyclerView.Adapter<MyRowHolder>(){

            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                SentMessageBinding binding= SentMessageBinding.inflate(getLayoutInflater());
                return new MyRowHolder(binding.getRoot());

            }
            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position){
                String obj = messages.get(position);
                holder.messageText.setText(obj);
                holder.timeText.setText("");
            }
            @Override
            public int getItemCount(){
                return messages.size();

            }
            @Override
            public  int getItemViewType(int position){
                return 0;
            }



    });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.sendButton.setOnClickListener(click->{

            messages.add(binding.inputText.getText().toString());
            myAdpter.notifyItemInserted(messages.size()-1);
            binding.inputText.setText("");

        });

    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            messageText=itemView.findViewById(R.id.message);
            timeText=itemView.findViewById(R.id.time);
        }
    }
}