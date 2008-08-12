package de.jacavi.hal.bluerider;

public interface ComListener 
{
	void msgReceived(Message m, int index);
	void fixDataReceived(byte b,int index);
}
