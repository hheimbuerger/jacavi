package de.jacavi.hal.lib42;

import de.jacavi.hal.lib42.NativeLib42;

public class NativeLib42Adapter implements Clib42CarreraController{

	private NativeLib42 lib42=null;
	
	public NativeLib42Adapter()
	{
		lib42=new NativeLib42();
		lib42.initLib42(/*mode*/0);
	}
	
	@Override
	public int getSpeed(int carID) {
		return lib42.getSpeed(carID);
	}

	@Override
	public int getSwitch(int carID) {
		return lib42.getSwitch(carID);
	}

	@Override
	public void setSpeed(int carID, int speed) {
		lib42.setSpeed(carID, speed);
	}

	@Override
	public int toggleSwitch(int carID) {
		return lib42.toggleSwitch(carID);
	}

    @Override
    public void fullBreak(int carID) {
        lib42.fullBreak(carID);
    }

    @Override
    public int getPitstop(int carId) {
       
        return lib42.getPitstop(carId);
    }

    @Override
    public void setPitstop(int carId, int pitstop) {
       lib42.setPitstop(carId, pitstop); 
    }

    @Override
    public int togglePitstop(int carId) {
        return lib42.togglePitstop(carId);
    }

    @Override
    public void activateFuel() {
        
        lib42.activateFuel();
         
    }

    @Override
    public void deactivateFuel() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void programmBreak(int value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void programmCar(int carID) {
        lib42.programmCar(carID);
    }

    @Override
    public void programmFuel(int valu) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void programmSpeed(int value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resetCars() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void activatePacecar() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deactivatePacecar() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getPcSwitch() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int isPacecarActive() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void pacecar2box() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPacecarSwitch(int value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPcPitstop(int pitstop) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int togglePacecarSwitch() {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
