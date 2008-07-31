package de.jacavi.hal.lib42;

public class NativeCsdLib {
	static
	{
		System.loadLibrary("CsdLib");
	}
	
	/*initialization*/
	public native int initSensorDetection();
	public native void releaseSensorDetection();
	
	
	//This is the fuction called by detecting a sensor
	public static void callback(int carID,int sensorID)
	{
		//TODO: 
	    System.out.println("callback: carID->" +carID + " sensorID->"+sensorID);
	}
}
