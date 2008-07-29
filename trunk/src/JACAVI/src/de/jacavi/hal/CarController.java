package de.jacavi.hal;

/* fro: this interface should be as simple as possible to control a car
 * 
 */
public interface CarController {
    //car controll
    void setSpeed(int carID,int speed);
	int getSpeed(int carID);
	int getSwitch(int carID);
	int toggleSwitch(int carID);
	void fullBreak( int carID );
    
    // HANDLE PIT STOP
    void setPitstop( int carId, int pitstop);
    int togglePitstop( int carId ); 
    int getPitstop( int carId );
    
    
//##################fro: other not car specific methods
    
    // HANDLE FUEL FOR ALL CARS
    void activateFuel();
    void deactivateFuel();

    /* Methods to programm cars
     *TODO: what is all this
     */
    void programmCar(int carID);
    void programmSpeed( int value );
    void programmBreak( int value );
    void programmFuel( int valu );
    void resetCars();

}
