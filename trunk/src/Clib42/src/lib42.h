/*
** 	file : lib42.h
** 	author : Benjamin Kleinhens
** 	project : Carrera
** 	team : 42
** 
********************************************************************************
** 
** int initLib42( int mode );
** - Initializes the lib42. Must be called before using 
** - Should only called once, otherwise behavior is undefined.
** 
** void releaseLib42();
** - Releases the lib42. Must be called to free all 
** - allocated resources by initLib42
**
**
********************************************************************************
** 
** void	setSpeed( int carId, int speed )
** - Set's the speed of the specified car to the given speed value.
** 
** int setSpeedUp( int carId )
** - Increments the speed of the specified car by one. 
** - MAXSPEED is not crossed.
** 
** int setSpeedDown (int carId )
** - Decrements the speed of the specified car by one.
** - oes not fall below zero.
**
** void	fullBreak( int carId )
** - Sets the speed of the specified car to zero.
** 
** int getSpeed( int carId )
** - Returns the speed of the specified car.
** - Result will be 0 when carId is not valid.
** 
** 
******************************************************************************** 
** 
** 
** void	setSwitch( int carId, int value )
** - Set the Switch flag of the specified car to the given Value
** - Should be 1 to trigger the switch oherwise 0.
** 
** int toggleSwitch( int carId );
** - Toggles the switch state of the specified car.
** 
** int getSwitch( int carId )
** - Returns the switch value of the specified car.
** 
** 
********************************************************************************
** 
** 
** void	setPitstop( int carId, int pitstop  )
** - Sets the pitstop flag of the specified car.
** - Should be 1 to simulate pitstop behavior otherwise 0.
**
** int togglePitstop( int carId )
** - Toggles the pitstp flag of the specified car.
**
** int getPitstop( int carId )
** - Returns the pitstop flag of the specified car.
**
**
********************************************************************************
**
**
** void	activatePacecar()
** - Activates the pacecar. Can also be called to restart 
** - the pacecar after it was send to the box
**
** void deactivatePacecar()
** - Deactivates the pacecar.
** 
** void pacecar2box()
** - sends the pacecar to the pit stop lane.
** 
** void setPacecarSwitch( int value )
** - Sets the switch trigger for the pacecar.
** - If value is 1 the switch is triggered.
** 
** int togglePacecarSwitch()
** - Toggles the value of the pacecar switch trigger.
** - If trigger is 1 it is set to 0 and vice versa.
** 
** void setPcPitstop( int pitstop )
** - Sets the pitstop signal for the pacecar.
**
** int isPacecarActive()
** - Returns 1 if pacecar is active otherwise 0.
**
** int getPcSwitch()
** - Returns 1 if switch trigger for pacecar is active
** - otherwise it will return 0
**
**
********************************************************************************
**
**
** void activateFuel()
** - Activate the fuel level for all cars, including th pacecar
** 
** void deactivateFuel();
** - Deactivates the fuel level for all cars, including the pacecar.
**
**
********************************************************************************
**
**
** void programmCar( int carId )
** - programms all cars on the race-track to the given Id.
** - carId must be in the range of [ 0, 5 ] 
**
** void programmSpeed( int value )
** - Programms the speed scalation of all cars on the race-track.
** - value must be in the range of [ 1, 10 ].
**
** 
** void programmBreak( int value )
** - Programms the break scalation of all cars on the race-track.
** - value must be in the range of [ 1, 10 ].
**
** 
** void programmFuel( int value )
** - Programms the fuel scalation of all cars on the race-track.
** - value must be in the range of [ 1 10 ].
**
**
********************************************************************************
**
**
** Car* getCar( int carId )
** - Returns the Car struct of the car with the specified Id.
** - When carID is invalid getCar will return NULL
** 
** void resetCars()
** - Set the Speed of all cars to 0.
**
** int getSensor( int carId )
** - Returns the id of the last sensor that was detected
** - for the specified car. Valid Sensor id is in the 
** - the range of [ 1, 16 ]. When result is 0 no sensor 
** - was detected for this car yet.
**  
** 
** int getLapCount( int carId )
** - Reteruns the Lapcount of the specified car.
** - lib42 must be initialized with LIB42_LAP_COUNT to use this feature.
** 
** 
********************************************************************************
**
**	LIB42_COLLISION_DETECTION
**		
**	* track
**	# sensor
**	x switch
** 	> driving direction
**	
**	
**	  start			    end
** 	>*#*****x*******************#*> OUT
**	         ***
**	            ***>****	
**		            ***
**	>*#*************************#*> IN
**	  start			    end
**
**
** void initCriticalSection( CriticalSection* cs, int cin_start, int cin_end, int cout_start, int cout_end )
** - Initializes a critical section struct
** - Set the sensorids according to the graphical description above
** 
** 
** int addCriticalSection( CriticalSection* cs )
** - adds critical section to the prevent collisions in the specified partof the race-track
** - To use this feature you must initialize lib42 with LIB42_COLLISION_DETECTION
**
**
********************************************************************************
**
**	LIB42_LAP_COUNT
**	
** void addLapMarker( int* lm )
** - This adds any numbers of sensor ids, that should act as a lap mark.
** - When a car passes one of the sensors his lap count is increased
** - When addLapMarker is called all older lap marks (if exist) are removed.
** - To use this feature you must initialize lib42 with LIB42_LAP_COUNT
** - lm must point to an array of integers which is terminated with a 0 value.
**
**
********************************************************************************
**
**	LIB42_SENSOR_QUEUEING
**
** int pollSensorQueue( Sensor* sens )
** - Initializes the pased sensor struct with the next
** - detected sensor values. When initialization was succssfull
** - pollSensorQueue will return 1, otherwise 0.
** - lib42 must be initialized with LIB42_SENSOR_QUEUEING
** - to use this feature.
**
**
*/




#ifndef LIB42_h
#define LIB42_H


/* Define LIB42 modes
*/
#define LIB42_COLLISION_DETECTION	0x00000001
#define LIB42_LAP_COUNT			0x00000002
#define LIB42_SENSOR_QUEUEING		0x00000004
#define LIB42_SENSOR_LOGGING		0x00000008


/* OTHER USEFULL CONSTANT VALUES
*/
#define MAXCRITICAL 4
#define CRITICAL 1
#define SAVE 0

#define MAXCARS 6
#define MAXSPEED 15

#define TTYS0 "/dev/ttyS0"
#define TTYS1 "/dev/ttyS1"

#define MAX_QUEUE_LEN 100

typedef unsigned char byte;


// HANDLE LIB42
int 	initLib42( int mode );
void 	releaseLib42();


typedef struct _car_ {
	byte carId;		// The id of the car / controller [0,5]
	byte speed;		// The current speed of the car [0,15]
	byte switcher;		// The switch trigger [0,1]
	byte fuel;		// Is fuel active [0,1]
	byte pitstop;		// Simulate pitstop [0,1]

	byte oldSpeed;		// Old speed after a slow down in critical section
	byte lapCount;		// The lap count off the car
	byte sensor;		// The last detected sensor
} Car;
 


// HANDLE CAR SPEED
void	setSpeed( int carId, int speed );
int 	setSpeedUp( int carId );
int 	setSpeedDown (int carId );
void	fullBreak( int carId );
int 	getSpeed( int carId );



// HANDLE SWITCH
void 	setSwitch( int carId, int value );
int 	toggleSwitch( int carId );
int 	getSwitch( int carId );



// HANDLE PACE CAR
void	activatePacecar();
void	deactivatePacecar();
void	pacecar2box();
void	setPacecarSwitch( int value );
int	togglePacecarSwitch();
void	setPcPitstop( int pitstop );
int	isPacecarActive();
int 	getPcSwitch();



// HANDLE PIT STOP
void	setPitstop( int carId, int pitstop  );
int 	togglePitstop( int carId ); 
int 	getPitstop( int carId );



// HANDLE FUEL FOR ALL CARS
void 	activateFuel();
void 	deactivateFuel();



// PROGRAMM CARS
void programmCar( int carId );
void programmSpeed( int value );
void programmBreak( int value );
void programmFuel( int valu );




// OTHER CAR METHODS
Car*	getCar( int carId );
void 	resetCars();
int 	getSensor( int carId );
int	getLapCount( int carId );


// See the initCriticalSection desctiption for detailed explanation
typedef struct _critical_section_ {
	byte critical_in_start;		// sensor id 
	byte critical_in_end;		// sensor id 
	byte critical_out_start;	// sensor id 
	byte critical_out_end;		// sensor id 
	byte critical_out;		// out counter for collision detetion
	byte critical_in;		// in counter for collision detection
	Car* restricted[MAXCARS];	// Currently restricted cars in this section
} CriticalSection;


// ADD CRITICAL SECTIONS
void 	initCriticalSection( CriticalSection* cs, int cin_start, int cin_end, int cout_start, int cout_end );
int 	addCriticalSection( CriticalSection* cs );



// INIT LAP MARKERS 
void 	addLapMarker( int* lm );


typedef struct _sensor_ {
	byte id;		// Id of the sensor
	byte carId;		// Detected car
} Sensor;


// HANDLE QUEUE ACCESS
int	pollSensorQueue( Sensor* sens );



#endif

