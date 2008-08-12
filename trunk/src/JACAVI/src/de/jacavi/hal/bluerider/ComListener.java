package com_port;

public interface ComListener 
{
	void msgReceived(Message m, int index);
	void fixDataReceived(byte b,int index);
}
