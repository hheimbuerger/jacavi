package de.jacavi.hal.bluerider;

public class ComException extends Exception 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static int FAILSAFE = 0;
	
	private int reason;
	
	public ComException(int reason) 
	{
		this.reason = reason;
		// TODO Auto-generated constructor stub
	}
	
	public int getReason()
	{
		return reason;
	}
}
