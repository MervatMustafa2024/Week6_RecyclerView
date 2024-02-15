package com.example.week6_recyclerview.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ChatRoomViewModel  extends ViewModel
{
    public MutableLiveData<ArrayList<String>> messages = new MutableLiveData< >();
}
