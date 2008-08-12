package com_port;

public class Message 
{
	public byte header;
	public byte[] payload;
	public Message() 
	{
		payload = new byte[2];
		header = 0;
	}
}
