package com.example.week6_recyclerview.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.week6_recyclerview.R;
import com.example.week6_recyclerview.data.ChatMessage;
import com.example.week6_recyclerview.data.ChatMessageDAO;
import com.example.week6_recyclerview.data.ChatRoomViewModel;
import com.example.week6_recyclerview.data.MessageDatabase;
import com.example.week6_recyclerview.databinding.ReceivedMessageBinding;
import com.example.week6_recyclerview.databinding.RoomChatBinding;
import com.example.week6_recyclerview.databinding.SentMessageBinding;
import com.google.android.material.snackbar.Snackbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatRoom extends AppCompatActivity {
    RoomChatBinding binding;
    ChatRoomViewModel chatModel ;
    ArrayList<ChatMessage> messages = new ArrayList<>();

    RecyclerView.Adapter<MyRowHolder> myAdpter;
    ChatMessageDAO mDAO;
    private int opID; // 1 for insert and 2 for delete
    private  ChatMessage deletedMessage;
    private int deletPostion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_chat);

       // setSupportActionBar(binding.toolbar);



        binding= RoomChatBinding.inflate(getLayoutInflater());

        setSupportActionBar(binding.toolbar);

        setContentView(binding.getRoot());

        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class,
                "Messages").build();
         mDAO = db.cmDAO();


        messages = chatModel.messages.getValue();


       if(messages == null)
        {
            chatModel.messages.setValue(messages = new ArrayList<>());
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                messages.addAll( mDAO.getAllMessages() ); //Once you get the data from database
                runOnUiThread( () -> binding.recyclerView.setAdapter( myAdpter )); //You can then load the RecyclerView
            });
        }




        binding.recyclerView.setAdapter(myAdpter=new RecyclerView.Adapter<MyRowHolder>(){

            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                if (viewType==0)
                {
                    SentMessageBinding binding= SentMessageBinding.inflate(getLayoutInflater());
                return new MyRowHolder(binding.getRoot());}
                else {
                    ReceivedMessageBinding binding= ReceivedMessageBinding.inflate(getLayoutInflater());
                    return new MyRowHolder(binding.getRoot());
                }

            }
            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position){
                ChatMessage obj = messages.get(position);
                holder.messageText.setText(obj.getMessage());
                holder.timeText.setText(obj.getTimeSent());

            }
            @Override
            public int getItemCount(){
                return messages.size();

            }
            @Override
            public  int getItemViewType(int position){
                ChatMessage obj = messages.get(position);
                if (obj.isSentButton()==true)

                return 0; // sender message
                else
                    return 1;// receiver message
            }



    });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.sendButton.setOnClickListener(click->{
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            ChatMessage obj=new ChatMessage(binding.inputText.getText().toString(),currentDateandTime,true);
            messages.add(obj);
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
            mDAO.insertMessage(obj);});
            myAdpter.notifyItemInserted(messages.size()-1);
            binding.inputText.setText("");
            opID=1;

        });
        binding.receiveButton.setOnClickListener(click->{
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            ChatMessage obj=new ChatMessage(binding.inputText.getText().toString(),currentDateandTime,false);
            messages.add(obj);
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                mDAO.insertMessage(obj);});
            myAdpter.notifyItemInserted(messages.size()-1);

            binding.inputText.setText("");
            opID=1;

        });

    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;



        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );
            itemView.setOnClickListener(e->{
                int position=getAbsoluteAdapterPosition();
                ChatMessage m=messages.get(position);

                builder.setMessage("Do you want to delete the message:" +messageText.getText());
                builder.setTitle("Question:");
                builder.setNegativeButton("NO", (dialog, cl)->{});
                builder.setPositiveButton( "Yes", (dialog, cl)->{
                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(() ->
                    {
                        mDAO.deleteMessage(m);});

                    messages. remove(position);
                    myAdpter.notifyItemRemoved(position);
                    Snackbar.make(e,"You deleted message #" , Snackbar.LENGTH_LONG)
                            .setAction (  "Undo", clk -> {

                                messages.add (position, m); myAdpter.notifyItemInserted(position);})
                            .show();

                  deletedMessage = m;
                  deletPostion=position;
                    opID=2;

                });
                builder.create().show();

            });
            messageText=itemView.findViewById(R.id.message);
            timeText=itemView.findViewById(R.id.time);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);

        if(item.getItemId() == R.id.item1){
            if(opID==1)
            {
                int position=messages.size()-1;
                ChatMessage m=messages.get(position);

                builder.setMessage("Do you want to delete the message:" +m.getMessage());
                builder.setTitle("Question:");
                builder.setNegativeButton("NO", (dialog, cl)->{});
                builder.setPositiveButton( "Yes", (dialog, cl)->{
                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(() ->
                    {
                        mDAO.deleteMessage(m);});
                    messages. remove(position);
                    myAdpter.notifyItemRemoved(position);

                });
                builder.create().show();

            }else if(opID==2){

                messages.add (deletPostion, deletedMessage); myAdpter.notifyItemInserted(deletPostion);
            }

        }  else if (item.getItemId() == R.id.item2) {

            Log.w("mervat", "Tost");

            builder.setMessage("This is the chat application : Version 1.0");
            builder.setTitle("Information");

            builder.setPositiveButton("OK", (dialog, cl) -> {
            });
            builder.create().show();

        }



        return true;
    }
    }