package de.jacavi.hal;

import de.jacavi.hal.lib42.NativeLib42Adapter;


public class FirstCarreraNativeLibraryFactory implements CarreraNativeLibraryFactory {

	@Override
	public CarreraControler initialiseCarreraLib(CarreraLibraryType type) {		
		
		CarreraControler halController=null;
		
		switch(type)
		{
			case lib42:
				halController=new NativeLib42Adapter();
			
			break;	
			
			case bluerider:
				//TODO
				break;
		}
		
		return halController;
	}

}