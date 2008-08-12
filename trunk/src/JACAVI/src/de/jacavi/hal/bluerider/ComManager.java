package com_port;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import gnu.io.SerialPort;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;





public class ComManager implements SerialPortEventListener, Runnable
{
	
	//Hilfsklasse für Bremslicht
	private class BrakeLight implements Runnable
	{
		public Boolean isActive;
		Message msg;
		
		
		public BrakeLight() {
			isActive = new Boolean(false);
			msg = null;
		}
		
		public void setLastMsg(Message m)
		{
			synchronized(isActive)
			{
				if(!isActive)
					this.msg = m;
			}
				
		}
		public void run()
		{
			isActive = true;
			ComManager cm = ComManager.getInstanceOfCM();
			Message m = new Message();
			for(int i= 0; i<8;i++)
			{
				try {
					Thread.sleep(70);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(i%2 == 0) // Licht einschalten
				{
					m.header = 2;
					m.payload[0] = 0;
					try {
						cm.sendMessage(m, ComManager.MSG_0);
					} catch (ComException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else //Licht ausschalten
				{
					m.header = 2;
					m.payload[0] = 1;
					try {
						cm.sendMessage(m, ComManager.MSG_0);
					} catch (ComException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}	
				}	
			}
			
			if(msg == null)
			{
				m.header = 2;
				m.payload[0] = 0;
				try {
					cm.sendMessage(m, ComManager.MSG_0);
				} catch (ComException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
			}
			else
			{
				try {
					cm.sendMessage(msg, ComManager.MSG_0);
				} catch (ComException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}
			}
			isActive = false;
		}
	}
	
	//Empfänger Thread
	private class Receiver implements Runnable
	{	
		private InputStream in;
		private ComManager myCMM;
		

		public Receiver(ComManager m, InputStream in) 
		{
			this.in = in;
			this.myCMM = m;
		}
		
		public void run()
		{
	        int byteCnt = 0;
			byte[] b = new byte[32];
			int b_cnt;
			byte[] buffer = new byte[32];
			
			long failsafe;
	        try
	        {          	
	        	while ( true)
	            {
	        		b_cnt = this.in.read(b);
	        		if(0 == b_cnt)
	        		{
	        			
	        			synchronized(myCMM.rx_lock)
	        			{

	        				failsafe = System.currentTimeMillis();
	        				//warte auf eingehenden Frame
	        				myCMM.rx_lock.wait(500);
	        				if((System.currentTimeMillis()-failsafe)>= 500)
	        				{
	        					if(myCMM.failsafe_time == 0)
	        					{
	        						myCMM.failsafe_time = System.currentTimeMillis()-500;
	        						myCMM.failsafe_reason = "TIMEOUT";
	        					}
	        					in.close();
	        					return;
	        				}
	        				continue;
	        			}
	        		}
	        		for(int i=0;i<b_cnt && byteCnt < 32;i++)
	        		{	
	        			buffer[byteCnt++] = b[i];
	        		}
	        		
	                if(byteCnt >= 32)
	                {
	                	byteCnt = 0;
	                	
	                	Message_Cont msc = getMsgCont(buffer);
	                	
	                	//CRC überprüfen
	                	if(!(msc.crc[0] == 0x1A && msc.crc[1] == 0x1B))
	                	{
	                		if(myCMM.failsafe_time == 0)
	                		{
	                			myCMM.failsafe_time = System.currentTimeMillis();
	                			myCMM.failsafe_reason = "CRC-ERROR";
	                		}
	                		return;
	                	}
	                	
	                	if(myCMM.failsafe_time != 0)
	                	{
	                		myCMM.cmg.printLog("Falisafe took "+(System.currentTimeMillis()-myCMM.failsafe_time)+"ms");
	                		myCMM.failsafe_time = 0;
	                		myCMM.failsafe_reason = null;
	                	}
	                	
	                	//Nachrichtenverteiler
	                	myCMM.distributeMessage(msc);
	                	
	                	//Response Time in GUI schreiben
	                	myCMM.cmg.setResponseTime((int)(System.currentTimeMillis() - myCMM.time));
	                	
	                	buffer = new byte[32];
	                	
	                	//System.out.println("habe empfangen");
	                	//Den Sender Thread aufwecken
	                	synchronized(this.myCMM.tx_lock)
	                	{
	                		this.myCMM.tx_lock.notify();
	                	}
	                	
	                }	
	            }
	        }
	        catch ( IOException e )
	        {
	            //e.printStackTrace();
	        } 
	        catch (InterruptedException e) 
	        {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}	
		}
		
		//wandelt Buffer in einen Message_Cont
		Message_Cont getMsgCont(byte[] buffer)
		{
			Message_Cont mc = new Message_Cont();
			Message m;
			
			//FIX_DATA
			mc.flagfield = buffer[0];
			for(int i=1;i<9;i++)
			{
				mc.fd[i-1] = buffer[i];
			}
			
			//MSGs
			int j = 0;
			for(int i=9;i<30;i = i+3)
			{
				m = new Message();
				m.header = buffer[i];
				m.payload[0] = buffer[i+1];
				m.payload[1] = buffer[i+2];
				mc.msg[j++] = m;
			}
			//CRC
			
			mc.crc[0] = buffer[30];
			mc.crc[1] = buffer[31];
			
			return mc;
		}
	}
	
	//Sender Thread
	private class Transceiver implements Runnable
	{
		OutputStream out;
		ComManager myCMM; 
		
		long failsafe;
		public Transceiver(ComManager m, OutputStream out) 
		{
			this.out  = out;
			this.myCMM = m;
		}
		
		public void run() 
		{
			while(true)
			{
				synchronized(this.myCMM.tx_lock)
				{
					try 
					{
						failsafe = System.currentTimeMillis();
					//wartet solange bis ihn einer aufweckt oder 500ms verstrichen sind
						this.myCMM.tx_lock.wait(500);
        				
        				if((System.currentTimeMillis()-failsafe)>= 500)
        				{
        					if(myCMM.failsafe_time == 0)
        					{
        						myCMM.failsafe_time = System.currentTimeMillis()-500;
        						myCMM.failsafe_reason = "TIMEOUT";
        					}
        					try {
								out.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}
        					return;
        				}
					} 
					catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
				if(!myCMM.rx.isAlive()) //falls der Empfänger Thread tot ist begehe auch Selbstmord
				{
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					return;		
				}
				//senden
				try 
				{
					this.out.write(getMsgBuffer()); 

					myCMM.time = System.currentTimeMillis();
					
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
			}	
		}
		
		//Hilfsfunktion um einen Frame zusammen zu stellen
		private byte[] getMsgBuffer()
		{
			Message m;
			byte[] buffer = new byte[32];
			
			//FIX_DATA
			synchronized(this.myCMM.fix_data_tx)
			{
				for(int i = 0; i<9;i++)
					buffer[i] = this.myCMM.fix_data_tx[i];
				
				this.myCMM.fix_data_tx[0] = 0;
			}
			
			//MSGs
			int j = 0;
			for(int i=9;i<30;i = i+3)
			{
				if(this.myCMM.send_queue.elementAt(j++).isEmpty())
				{
					buffer[i] = 0;
				}
				else
				{
					m = this.myCMM.send_queue.elementAt(j-1).poll();

					 buffer[i] = m.header;
					 buffer[i+1] = m.payload[0];
					 buffer[i+2] = m.payload[1];
					
				}
			}
			//CRC	
			
			buffer[30] = 0x1C;
			buffer[31] = 0x1D;
			
			return buffer;
		}
	}
	
	//Nachricht auf der "Leitung"
	private class Message_Cont
	{
		byte flagfield;
		byte[] fd;
		Message[] msg;
		byte crc[];
		public Message_Cont() 
		{
			fd = new byte[8];
			msg = new Message[7];
			for(int i = 0; i<7;i++)
				msg[i] = new Message();
			crc = new byte[2];
		}	
	}
	
	
	
	private Vector<ConcurrentLinkedQueue<ComListener>> listeners;
	private Vector<ConcurrentLinkedQueue<Message>> send_queue;
	
	double time;
	double failsafe_time;
	

	private byte[] fix_data_tx;

	public static int MSG_0 = 0;
	public static int MSG_1 = 1;
	public static int MSG_2 = 2;
	public static int MSG_3 = 3;
	public static int MSG_4 = 4;
	public static int MSG_5 = 5;
	public static int MSG_6 = 6;
	public static int FIX_0 = 7;
	public static int FIX_1 = 8;
	public static int FIX_2 = 9;
	public static int FIX_3 = 10;
	public static int FIX_4 = 11;
	public static int FIX_5 = 12;
	public static int FIX_6 = 13;
	public static int FIX_7 = 14;
	
	ComGUI cmg;
	
	private Lock tx_lock,rx_lock;
	
	private Thread rx,tx,respawn;
		
	static ComManager comMan = new ComManager(); 
	
	SerialPort serialPort;

	static BrakeLight breakL;
	
	String portName, failsafe_reason;
	
	boolean isOpen;
	
	
	public static ComManager getInstanceOfCM()
	{
		return comMan;
	}
	
	private ComManager() 
	{
		listeners = new Vector<ConcurrentLinkedQueue<ComListener>>();
		
		for(int i= 0; i<15;i++)
		{
			listeners.add(new ConcurrentLinkedQueue<ComListener>());
		}
				
		send_queue = new Vector<ConcurrentLinkedQueue<Message>>();	
		for(int i = 0; i<7;i++)
		{
			send_queue.add(new ConcurrentLinkedQueue<Message>());
		}
		
		tx_lock = new ReentrantLock();
		rx_lock = new ReentrantLock();
		
		fix_data_tx = new byte[9];
		
		cmg = new ComGUI();
		cmg.setVisible(true);
		
		respawn = null;
		
		failsafe_time = 0;
		failsafe_reason = null;
		isOpen = false;
		

	}
	
	public boolean connect(String portName) throws Exception
	{
        this.portName = portName;
		
       
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
  
            return false;
        }
        else
        {
            //CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
        	//System.out.println("vor open");

        
       	
        	//Thread.sleep(2000);
        	CommPort commPort = portIdentifier.open(portName,2000);
        	
           // System.out.println("nch open");
        	   	
            if ( commPort instanceof SerialPort )
            {

            	serialPort = (SerialPort) commPort;
 
                serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
             
            /*
                System.out.println(serialPort.getBaudRate());
                System.out.println(serialPort.getDataBits());
                System.out.println(serialPort.getStopBits());
                System.out.println(serialPort.getFlowControlMode());
                System.out.println(serialPort.getParity());
                */
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                
                rx = new Thread(new Receiver(this,in));
                tx = new Thread(new Transceiver(this,out));
                
               
                rx.start();   
                
                
                tx.start();
                Thread.sleep(10); //warten bis tx wait() gemacht hat
                synchronized(tx_lock)
                {
                	tx_lock.notify();
                }
                
                if(respawn == null)
                {
                	respawn = new Thread(this);
                	respawn.start();
                }
                
                
            }
            else
            {
                System.out.println("Error: Only serial ports are possible");
                return false;
            }
        } 
        return true;
	}
	

	//Nachrichten verteilen
	public void distributeMessage(Message_Cont m)
	{

		//System.out.println("Verteile");
		for(int i = 0; i<8;i++)
		{
			Iterator<ComListener> t = listeners.elementAt(i+7).iterator();
			if(((m.flagfield >> (7-i)) & (byte) 1) == 1 )
			{	
				
				//Die FIX_DATA progress bars updaten
				short a = (short)(0x00FF & m.fd[i]);	
				
				cmg.setFixDataInput(i, a);
				
				//
				try
				{
					while(t.hasNext())
					{
						((ComListener)t.next()).fixDataReceived(m.fd[i], i+7);
					}
				}
				catch (NoSuchElementException e)
				{
					
				}
			}
		}
		
		for(int i= 0; i<7;i++)
		{
			if(m.msg[i].header != 0)
			{
				Iterator<ComListener> t = listeners.elementAt(i).iterator();
				
				try
				{
					while(t.hasNext())
					{
						((ComListener)t.next()).msgReceived(m.msg[i],i);
					}
				}
				catch (NoSuchElementException e)
				{
					
				}
			}
		}
	}
	
	public void setFixData(int index, byte b) throws ComException
	{
		
		if(failsafe_reason != null)
		{
			throw new ComException(ComException.FAILSAFE);
		}
		
		if(index == FIX_0 && b==0)
		{
			if(breakL == null)
			{
				breakL = new BrakeLight();
			}
			synchronized(breakL.isActive)
			{
				if(!breakL.isActive)
				{
					(new Thread(breakL)).start();
				}
			}
		}
		synchronized(fix_data_tx)
		{
			fix_data_tx[(index-7)+1] = b;
			fix_data_tx[0] |= (1 << (7-(index-7)));
			
			//progress bars updaten
			short a = (short)(0x00FF & b);
	
			cmg.setFixDataOutput(index-7, a);
		}
	}
	
	public void sendMessage(Message m, int channel) throws ComException
	{
		if(failsafe_reason != null)
			throw new ComException(ComException.FAILSAFE);
		
		if(channel == MSG_0)
		{
			if(breakL == null)
			{
				breakL = new BrakeLight();
			}
			breakL.setLastMsg(m);
		}
		send_queue.elementAt(channel).add(m);
	}
	
	public void addComListener(ComListener cl,int channel)
	{		
		listeners.elementAt(channel).add(cl);
	}
	
	public void removeComListener(ComListener cl,int channel)
	{
		listeners.elementAt(channel).remove(cl);
	}

	@Override
	public void serialEvent(SerialPortEvent e)
	{
		synchronized(rx_lock)
		{
			rx_lock.notify();
		}
	}

	//Dieser Thread sorgt für die Wiederbelebung des Systems falls ein
	//Failsafe auftritt
	public void run() 
	{
		while(true)
		{
		try 
		{
				this.rx.join();
				System.out.println("rx tot");
				
				if(this.tx.isAlive())
				{
					this.tx.join();
				}
				for(int index = 7;index<15;index++)
				{	
					synchronized(fix_data_tx)
					{
						fix_data_tx[(index-7)+1] = 0;
						fix_data_tx[0] |= (1 << (7-(index-7)));
						
						//progress bars updaten
						short a = (short)(0x00FF & 0);
				
						cmg.setFixDataOutput(index-7, a);
					}
				}
				
				//System.out.println("tx tot");
				
				System.gc();
				//System.out.println("gc executed");
				
				CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
				System.out.println("aktueller Besitzer: "+portIdentifier.getCurrentOwner());
				//System.out.println(""+portIdentifier.);
				
				
				this.serialPort.close();
				//System.out.println("port close");
				
				
				cmg.printLog("FAILSAFE because of "+failsafe_reason);
				
				while(true)
				{
					try
					{
						//System.out.println("try to connect");
						if(connect(portName))
						{
							break;
						}
					}
					catch(Exception e){}
				}
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			}
		}
		
	}
}
