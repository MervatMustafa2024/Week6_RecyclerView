package com.example.week6_recyclerview.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.week6_recyclerview.data.ChatMessage;
import com.example.week6_recyclerview.databinding.DetailsLayoutBinding;

public class MessageDetailsFragment extends Fragment {

    ChatMessage selected;
    public MessageDetailsFragment(ChatMessage m)
    {
        selected=m;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        DetailsLayoutBinding binding=DetailsLayoutBinding.inflate(inflater);

       binding.messageText.setText(selected.getMessage());
        binding.timeText.setText(selected.getTimeSent());
        binding.sendReceiveText.setText(selected.isSentButton()?"Send":"Receive");
       binding.databaseText.setText( "id=" + selected.id);
binding.closeButton.setOnClickListener(e->{

    getActivity().getSupportFragmentManager().beginTransaction().remove(MessageDetailsFragment.this).commit();
});
        return binding.getRoot();
    }
}
