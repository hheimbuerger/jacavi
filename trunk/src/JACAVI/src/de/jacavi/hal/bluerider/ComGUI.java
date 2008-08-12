package com_port;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;


import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//Diese Klasse und deren innere Klassen stellt den Statusbildschirm für den Bluerider

public class ComGUI extends JFrame implements WindowListener
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6624155764807388768L;
	private JPanel input,output,status;
	private JProgressBar[] bar_fix_in, bar_fix_out;
	private JProgressBar resp_time,resp_max,resp_min;


	//Die GForceGUI enthält die mittlere Komponente des Status-Frames.
	//Hier werden die G-Kräfte visualisiert und hier kann das Licht
	//ein und ausgeschalten werden.
	private class GforceGUI extends JPanel implements ActionListener, ItemListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		int x,y;
		
		int offsetX,offsetY;
		double offsetYval,offsetXval;
		double xval,yval;
		double xmax,ymax;
		Button breset;
        JCheckBox cb_l1, cb_l2;
		

		
		public GforceGUI() {
			// TODO Auto-generated constructor stub
			
			super();
			x=64;
			y=64;
			
			offsetX = 0;
			offsetY = 0;
			offsetYval = 0;
			offsetXval = 0;
			
			xval = 0;
			yval = 0;
			xmax = 0;
			ymax=0;
			

			this.setLayout(null);
			breset = new Button("Kalibrieren");
			breset.setBounds(30, 245, 128, 25);
			breset.addActionListener(this);
			this.add(breset);
			
			cb_l1 = new JCheckBox("Vorne",true);
			cb_l1.setName("L1");
			cb_l1.addItemListener(this);
			cb_l1.setBounds(20,270, 128, 25);
			this.add(cb_l1);
			cb_l2 = new JCheckBox("Hinten",true);
			cb_l2.setName("L2");
			cb_l2.addItemListener(this);
			cb_l2.setBounds(20,295, 128, 25);
			this.add(cb_l2);
			
			
		}
		

		public void setYCoord(int y)
		{
			yval = ((((double)y-128)/127)*5*-1) -offsetYval;
			if(Math.abs(yval) > Math.abs(ymax))
				ymax = yval;
			
			this.y = (((y-128)/2)*4) - offsetY;
			
			this.repaint();
			
		}
		public void setXCoord(int x)
		{
			xval = ((((double)x-128)/127)*5) -offsetXval;
			if(Math.abs(xval) > Math.abs(xmax))
				xmax = xval;
	
			this.x = (((x-128)/2)*4) - offsetX;
			this.repaint();
			
		}
		
		protected void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D)g;
			
			g2.clearRect(0, 0, 200, 300);
			
			
			g2.setStroke(new BasicStroke(1.0f));
			
			g2.drawString("Beschleunigung", 30, 25);
			g2.setColor(Color.white);
			g2.fillRect(30, 30, 128,128 );
			g2.setColor(Color.black);
			g2.drawRect(30, 30, 128,128 );
						
			
			g2.setStroke(new BasicStroke(3.0f));
			g.setColor(Color.blue);
			g.drawLine(94, 94, 94+x, 94);
			
			g.setColor(Color.red);
			g.drawLine(94, 94, 94, 94+y);
			
			g2.setColor(Color.green);
			g2.drawLine(94, 94, 94+x, 94+y);
			g2.fillOval(94+x-4, 94+y-4, 8, 8);
			
			g2.setColor(Color.black);
			g2.drawString("G-Kräfte", 30, 180);
			g2.drawString("X-Achse:  "+(new DecimalFormat("00.00")).format(xval)+"g", 30, 195);
			g2.drawString("Y-Achse:  "+(new DecimalFormat("00.00")).format(yval)+"g", 30, 210);
			g2.drawString("X-MAX:    "+(new DecimalFormat("00.00")).format(xmax)+"g", 30, 225);
			g2.drawString("Y-MAX:    "+(new DecimalFormat("00.00")).format(ymax)+"g", 30, 240);	
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			offsetX = x;
			offsetY = y;
			offsetYval = yval;
			offsetXval = xval;
			xmax=0;
			ymax=0;

//			System.out.println(offsetX + "  " + offsetY + "  "+offsetXval+"  "+offsetYval);
			
		}


		@Override
		public void itemStateChanged(ItemEvent e) 
		{

			JCheckBox cb = (JCheckBox)e.getItem();
			
			Message m = new Message();
			
			ComManager cm = ComManager.getInstanceOfCM();
			if(cb.getName().equals("L1"))
			{
				if(cb.isSelected())
				{
					m.header = 1;
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
					m.header = 1;
					m.payload[0] = 1;
					try {
						cm.sendMessage(m, ComManager.MSG_0);
					} catch (ComException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			else if(cb.getName().equals("L2"))
			{
				if(cb.isSelected())
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
					m.header = 2;
					m.payload[0] = 1;
					try {
						cm.sendMessage(m, ComManager.MSG_0);
					} catch (ComException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
			
		}
		
	}
	
	private GforceGUI gforce;
	JTextArea log;
	
	
	public ComGUI() 
	{
		this.addWindowListener(this);
		this.setAlwaysOnTop(true);
		
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(500,500));
		
		this.setTitle("Blue Rider Status Monitor");
				
		gforce = new GforceGUI();
		gforce.setBackground(Color.blue);
		gforce.setMinimumSize(new Dimension(75,75));
		this.add(gforce,BorderLayout.CENTER);
		
		input = new JPanel();
		input.setLayout(new GridLayout(30,1));
		this.add(input,BorderLayout.EAST);
		
		output = new JPanel();
		output.setLayout(new GridLayout(30,1));
		this.add(output,BorderLayout.WEST);
		
		
		//Status
		status = new JPanel();
		status.setLayout(new GridLayout(3,1));
		
		resp_time = new JProgressBar(0,250);
		resp_time.setStringPainted(true);
		resp_time.setString("N.A.");
		
		status.add(new JLabel("RESPONSE TIME NOW"));
		status.add(resp_time);
		
		resp_max = new JProgressBar(0,250);
		resp_max.setStringPainted(true);
		resp_max.setValue(0);
		resp_max.setString("N.A.");
		resp_max.setForeground(Color.red);
	
		
		status.add(new JLabel("RESPONSE TIME MAXIMUM "));
		status.add(resp_max);
		
		resp_min = new JProgressBar(0,250);
		resp_min.setStringPainted(true);
		resp_min.setValue(0);
		resp_min.setString("N.A.");
		resp_min.setForeground(Color.green);
		
		status.add(new JLabel("RESPONSE TIME MINIMUM "));
		status.add(resp_min);
		
		this.add(status,BorderLayout.NORTH);
		
		
		this.add(status,BorderLayout.NORTH);
			
		//Log fenster
		
		JPanel log_pan = new JPanel();
		log_pan.setMinimumSize(new Dimension(500,80));
		
		log = new JTextArea(5,40);
		log.setEditable(false);
		JScrollPane scrollLOG = new JScrollPane(log,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		log_pan.add(scrollLOG);
		this.add(log_pan,BorderLayout.SOUTH);
		
		//FIX-Data
		bar_fix_in = new JProgressBar[8];
		bar_fix_out = new JProgressBar[8];
		for(int i = 0; i<8;i++)
		{
			bar_fix_in[i] = new JProgressBar(0,255);
			bar_fix_in[i].setStringPainted(true);
			input.add(new JLabel("FIX_DATA_"+i));
			input.add(bar_fix_in[i]);
			
			bar_fix_out[i] = new JProgressBar(0,255);
			bar_fix_out[i].setStringPainted(true);
			output.add(new JLabel("FIX_DATA_"+i));
			output.add(bar_fix_out[i]);
		}	
	}
	
	public void setFixDataInput(int index, int val)
	{
		if(index > 7 || index < 0)
			return;
		
		bar_fix_in[index].setValue(val);
		
		if(index == 1 || index ==2 || index ==3)
		{
			//System.out.println("index"+ index+":"+val);
			
			if(index == 2)
			{
				gforce.setYCoord(val);
			}
			else if(index == 1)
			{
				gforce.setXCoord(val);
			}
			
		}
	}
	
	public void setFixDataOutput(int index, int val)
	{
		if(index > 7 || index < 0)
			return;
		
		bar_fix_out[index].setValue(val);
	}
	
	public void setResponseTime(int val)
	{
		if(resp_min.getValue() == 0)
		{
			resp_min.setValue(val);
			resp_min.setString(val+"ms");
			
			resp_max.setValue(val);
			resp_max.setString(val+"ms");
			
			System.out.println("initialized with "+val);
		}
		else if(resp_min.getValue() > val)
		{
			resp_min.setValue(val);
			resp_min.setString(val+"ms");
		}
		else if(resp_max.getValue() < val)
		{
			resp_max.setValue(val);
			resp_max.setString(val+"ms");
		}
		
		resp_time.setValue(val);
		resp_time.setString(val+"ms");
	}
	
	public synchronized void printLog(String t)
	{
		SimpleDateFormat formatter = new SimpleDateFormat ("HH:mm:ss,SSS ");
		java.util.Date currentTime = new java.util.Date();

		String text = formatter.format(currentTime)+ ": "+t;
		log.append(text +"\n");
	}
	
	public void resetBars()
	{
		
	}

	@Override
	public void windowActivated(WindowEvent e) {

		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		

		
	}

	@Override
	public void windowClosing(WindowEvent e) {

		System.exit(0);
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}
