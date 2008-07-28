/*
** 	file : lib42.c
** 	author : Benjamin Kleinhens
** 	project : Carrera
** 	team : 42
** 
*/

/*/ Include system libraries.
*/
#include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <ncurses.h>
#include <pthread.h>


/*/ Include local header files.
*/
#include "carrera.h"
#include "lib42.h"
#include "csdlib.h"


/*/ Define local data structures and types
*/

typedef struct _entry_ {
	void* data;
	struct _entry_* next;
} Entry;




/*/ Prototype definitions for local methods
*/
static void initialCars();
static int validatecarId(int carId);
static void writeCar( Car* car );
static void setAllFuel( byte fuel );

// Methods for mode handling
static void handleSensorDetection( int carId, int sensor );
static void checkCriticalSection( CriticalSection* cs, int carId, int sensor );
static void doLapCount( int carId, int sens );
static void logSensor( int carId, int sens );
static void add2Queue( int carId, int sens );

// Methods for queue handling
static void addSensor2Queue( Sensor* sens );
static Sensor* createSensor( int carId, int sens );
static void freeQueue();


/*/ Global data definitions 
*/
Car** cars;
byte isCritical[MAXCARS];

static CriticalSection* critical[MAXCRITICAL];
static int fd = -1;

static int* lapmarker = NULL;

// Pace Car
static byte pc_active = 0;
static byte pc_back2box = 0;
static byte pc_fuel = 1;
static byte pc_switch = 0;
static byte pc_pitstop = 0;




// Mode Flags 
static int COLLISION_DETECTION = 0;
static int LAP_COUNT = 0;
static int SENSOR_QUEUEING = 0;
static int SENSOR_LOGGING = 0;


pthread_mutex_t LOCK;	
pthread_mutex_t QLOCK;

// Variables for queue handling
Entry* qhead = NULL;
Entry* qtail = NULL;
int qlen = 0;

// Variables for logging
FILE* ostream;




/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
**	Following methods are defined in carrera.h 
**
*/


void sendPowerMsg( int power ) {

	unsigned char data[4] = { 'b', 0, 0, 0 };
	data[1] = ( power ) ? 0 : 1;
	write( fd, data, 4 );
	usleep(0);

}


void writeProgrammingMsg( int param, int value ) 
{

	int i = 0; 
	for ( i = 0; i < 8; i++ ) {

		byte carId = i;

		unsigned char data[4] = {'p',carId,value,0};

		data[2] |= (param << 4);


	
		write( fd, &data[0], 4 );


		usleep(0);
	}
}



void sendPacecarMsg(  ) {

	byte freigabe = 1;
	unsigned char data[4] = {'g',7,pc_fuel,0};

	
	data[2] |= ( pc_active ) ? BIT02 : BIT00;
	data[2] |= ( pc_back2box ) ? BIT00 : BIT03;
	data[2] |= ( freigabe ) ? BIT04 : BIT00;
	data[2] |= ( pc_pitstop ) ? BIT05 : BIT00;
	data[2] |= ( pc_switch ) ? BIT06 : BIT00;


	write( fd, &data[0], 4 );
	usleep(0);
}


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
**	The following section contains some usefull local methods 
**
*/

/* addSensor2Queue
** - This method adds a sensor struct to the end of the queue
** - It chould only be called when lib42  was initialized with LIB42_SENSOR_QUEUEING
** - when queue gets longer then MAX_QUEUE_LEN the head element will be removed.
*/ 
static void addSensor2Queue( Sensor* sens ) {



	pthread_mutex_lock( &QLOCK );

	Entry* e = (Entry*) malloc( sizeof(Entry) );
	if( e == NULL ) {
		pthread_mutex_unlock( &QLOCK );
		return;
	}
	e->data = (void*) sens;

	if( qtail == NULL ) {
		qtail = e;
		qhead = e;
	} else {
		qtail->next = e;
	}

	qlen++;

	while( MAX_QUEUE_LEN < qlen ){
		qhead = qhead->next;
		qlen--;
	}
	pthread_mutex_unlock( &QLOCK );



}

/* freeQueue
** - Removes all data from the queue and free's the memory
*/
static void freeQueue() {
	pthread_mutex_lock( &QLOCK );
		Entry* e = qhead;
		while( e != NULL ) {
			Entry* t = e;
			e = e->next;
			free( t->data );
			free( t );
		}
	pthread_mutex_unlock( &QLOCK );
}

/* createSensor
** - create a new sensor struct and initializes it with the
** - given values. 
*/
static Sensor* createSensor( int carId, int sens ) {

	

	Sensor* s = (Sensor*) malloc( sizeof( Sensor ) );
	if( s == NULL ) {
		printw(  "could not allocate mem for sensor \n" );
		refresh();
		return NULL;
	}

	s->id = sens;
	s->carId = carId;
	return s;
}




/* doLapCount
** - Checks whether the sensor is one of the lap marks and increases
** - the lap count of the detected car. Lib43 must be initialized
** - with LIB42_LAP_COUNT to make use of this.
*/
static void doLapCount( int carId, int sens ) {
	
	int* p = lapmarker;
	while( *p != 0 ) {
		if( *p == sens ) {
			cars[carId]->lapCount++;
		}
	
	}

}


/* logSensor
** Logs all detected sensor values to a file.
** Activate whis feature with LIB42_SENSOR_LOGGING
*/
static void logSensor( int carId, int sens ){

	fprintf( ostream, "%d\t%d\t%d\n", carId, sens, cars[carId]->sensor );
	fflush( ostream );

}

/* add2Queue
** - Create a new sensor struct for the spcified data and 
** - adds it to the queue.
*/
static void add2Queue( int carId, int sens ) {
	addSensor2Queue( createSensor( carId, sens ) );
}


/* checkCriticalSection
** Checks if the car enteres a the specified critical section and
** react on the current situation in this section.
*/ 
static void checkCriticalSection( CriticalSection* cs, int carId, int sens ) 
{ 

	if( sens == cs->critical_in_start ){
		isCritical[carId] = CRITICAL;
		fprintf( ostream, " critical in start %d\n", cs->critical_in );
		if( cs->critical_in ){

			int i;
			for( i = 0; i < MAXCARS; i++ ) {
				if ( cs->restricted[i] == NULL ) break;
			}
			cs->restricted[i] = cars[carId];
			cars[i]->oldSpeed = cars[carId]->speed;
			cars[carId]->speed = 0 ;
			writeCar( cars[carId] );
		} 

		cs->critical_out++;

	} else if ( sens == cs->critical_out_start ) {
		isCritical[carId] = CRITICAL;
			fprintf( ostream, " critical out start %d\n", cs->critical_out );

		if( cs->critical_out ){
			cars[carId]->switcher = 0;
			writeCar( cars[carId] );
		} else if( cars[carId]->switcher ) {
			cs->critical_in++;
		}

	} else if ( sens == cs->critical_in_end ) {
		isCritical[carId] = SAVE;
		fprintf( ostream, " critical in end \n");

		if( cars[carId]->sensor == cs->critical_in_start ){
			cs->critical_out--;
		} else if( cars[carId]->sensor == cs->critical_out_start ) {
			cs->critical_in--;
			int i;
			for( i = 0; i < MAXCARS; i++ ) {
				if( cs->restricted[i] == NULL ) break;
				byte cid = cs->restricted[i]->carId;
				cars[cid]->speed = cars[i]->oldSpeed;
				writeCar( cars[cid] );
				cs->restricted[i] = NULL;
			}

		}

	} else if ( sens == cs->critical_out_end ) {
		isCritical[carId] = SAVE;
		fprintf( ostream, " critical out end \n");

	} else if ( sens == ( (cs->critical_in_end + 2)%(MAX_SENSOR+1) ) ) {
		isCritical[carId] = SAVE;
		if( cars[carId]->sensor == cs->critical_in_start ) {
			
			cs->critical_out--;
		
		} else if ( cars[carId]->sensor == cs->critical_out_start ) {
			cs->critical_in--;
		}

	}


	refresh();

}


/* writeCar
** - sends the car data to the scheduler.
*/
static void writeCar( Car* car ) 
{

	unsigned char data[4] = {'s',car->carId,car->speed,0};

	unsigned char switcher = car->switcher ? 0 : (1 << 7);
	unsigned char fuel = car->switcher << 6;
	unsigned char pitstop = car->pitstop << 5;

	data[2] |= switcher;
	data[2] |= fuel;
	data[2] |= pitstop;
	
	write( fd, &data[0], 4 );


	/* When no delay is created after write, 
	** directly following write calls will fail.
	** It seems to suffice waiting 0 ms.
	** Probably the delay created by the syscall is enough.
	*/
	usleep(0);
}


/* setAllFuel
** - Set's the fuel values of all cars ( including the pacecar )
** - th the specified value fuel.
*/
static void setAllFuel( byte fuel ) {

	int i;
	for( i = 0; i < MAXCARS; i++ ){
		cars[i]->fuel = fuel;
	}
	pc_fuel = fuel;
}


/* validatecarId
** Checks wheter a carid is valid and wheter changes are allowed
** in the current state. Returns 1 if everything is okay otherwise 0.
*/
int validatecarId(int carId) 
{
	if(carId >= 0 && carId <= MAXCARS-1 && !isCritical[carId] )
		return 1;
	return 0;
}







/* initialCars
** Initializes the car array 
*/
static void initialCars() 
{
	cars = (Car**) malloc(sizeof(Car*) * MAXCARS);
	int i;
		
	for( i=0; i<MAXCARS; ++i ) {
		cars[i] = (Car*) malloc(sizeof(Car));
		cars[i]->carId = i;
		cars[i]->speed = 0;
		cars[i]->switcher = 0;
		cars[i]->fuel = 1;
		cars[i]->pitstop = 0;
		cars[i]->sensor = 0;
		cars[i]->oldSpeed = 0;
		cars[i]->lapCount = 0;
	}
}



/* handleSensorDetection
** Handles the sensor detection depending on the specified
** initialization mode flags. 
*/
static void handleSensorDetection( int carId, int sens ) {

	carId--;


	//	printw( "HANDLE SENSOR %d CAR %d\n", sens, carId  );
	//	refresh();

	pthread_mutex_lock( &LOCK );


	if( carId < 0 || carId >= MAXCARS ) {
		printw( "WARNIG illeagle car\n" );
		refresh();
		return;
	
	}



		if( COLLISION_DETECTION ) {
			int i;
			for( i = 0; i < MAXCRITICAL; i++ ) {
				if( NULL == critical[i] ) continue;
				checkCriticalSection( critical[i], carId, sens );
			}
	
		}
	
		if( LAP_COUNT ) {
			doLapCount( carId, sens );
		}
	
		if( SENSOR_QUEUEING ) {
			add2Queue( carId, sens );
		}
	
		if( SENSOR_LOGGING ) {
			logSensor( carId, sens );
		}
		
	
		cars[carId]->sensor = sens;

	pthread_mutex_unlock( &LOCK );



}





/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
**	In the following section all interface methods defined in lib42.h are implemented
**
*/




int initLib42( int mode  ) {

	initialCars();

	ostream = fopen( "log", "w" );
	if( ostream == NULL ) {
		printf("could not open file \n");
		return -1;
	}
	fprintf( ostream, "CARID\tCURRENT SENSOR\tLAST SENSOR\n" );

	fd = open( TTYS1, O_WRONLY );
	if( fd < 0 ) {
		fclose( ostream );
		printf( "could not open device \n" );
		return -1;
	}

	int csd = initializeSensorDetection( handleSensorDetection );
	if( csd ) {
		close( fd );
		return -1;
	}


	if( pthread_mutex_init( &LOCK, NULL ) ) {
		close( fd );
		releaseSensonrDetection();
		return -1;
	}

	if( pthread_mutex_init( &QLOCK, NULL ) ) {
		close( fd );
		releaseSensonrDetection();
		return -1;
	}

	int i;
	for( i = 0; i < MAXCARS; i ++ ) {
		isCritical[i] = SAVE;
	}

	
	COLLISION_DETECTION = ( mode & LIB42_COLLISION_DETECTION ); 
	SENSOR_QUEUEING = ( mode & LIB42_SENSOR_QUEUEING );	
	SENSOR_LOGGING = ( mode & LIB42_SENSOR_LOGGING );
	LAP_COUNT = ( mode & LIB42_LAP_COUNT );
	

	for( i = 0; i< MAXCRITICAL; i++ ) {
		critical[i] = NULL;
	}


	return 0;
}

void releaseLib42() 
{
	
	int i;
	resetCars();
	
	
	releaseSensonrDetection();
	freeQueue();
	pthread_mutex_destroy( &LOCK );
	pthread_mutex_destroy( &QLOCK );

	for(i=0; i<MAXCARS; ++i) {
		free( cars[i] );
	}
	free(cars);
	fclose(ostream);
	close(fd);
}



void setSpeed( int carId, int speed ) {

	if(validatecarId(carId) && speed<=MAXSPEED) {
		cars[carId]->speed = speed;
		writeCar( cars[carId] );
	}

}

void	fullBreak( int carId ) {
	setSpeed( carId, 0 );
}

int setSpeedUp( int carId ) {

	if(validatecarId(carId)) {
		if((cars[carId]->speed + 1) <= MAXSPEED) {
			cars[carId]->speed++;
			writeCar( cars[carId] );
			return cars[carId]->speed;
		}
		return cars[carId]->speed;
	}
	
	return -1;
}

int setSpeedDown(int carId)
{
	if(validatecarId(carId)) {
		if((cars[carId]->speed - 1) >= 0) {
			cars[carId]->speed--;
			writeCar( cars[carId] );
			return cars[carId]->speed;
		}
		return cars[carId]->speed;
	}
	
	return -1;
}

void setSwitch(int carId, int value) 
{
	if(validatecarId(carId)){
		cars[carId]->switcher = value;
		writeCar( cars[carId] );
	}
}

int toggleSwitch(int carId) 
{
	if(validatecarId(carId)) {
		if(cars[carId]->switcher) {
			cars[carId]->switcher = 0;
		} else {
			cars[carId]->switcher = 1;
		}
		writeCar( cars[carId] );	
		return cars[carId]->switcher;
	}
	
	return 0;
}


int togglePitstop(int carId) 
{
	if(validatecarId(carId)) {
		if(cars[carId]->pitstop) {
			cars[carId]->pitstop = 0;
		} else {
			cars[carId]->pitstop = 1;
		}
		writeCar( cars[carId] );	
		return cars[carId]->pitstop;
	}
	
	return 0;
}


int getPitstop( int carId )
{
	if(validatecarId(carId))
		return cars[carId]->pitstop;	
	return 0;
}

int getSensor( int carId ) 
{

	int sens = -1;

	sens = cars[carId]->sensor;
	
	return sens;
	
}

int getSpeed(int carId) 
{
	if(validatecarId(carId))
		return cars[carId]->speed;
	return -1;
}

int getSwitch(int carId) 
{
	if(validatecarId(carId))
		return cars[carId]->switcher;
		
	return 0;
}

Car* getCar(int carId) 
{
	if(validatecarId(carId))
		return cars[carId];
		
	return NULL;
}



void activatePacecar() {

	pc_active = 1;
	pc_back2box = 0;
	sendPacecarMsg();

}


void pacecar2box() {
	
	pc_back2box = 1;
	sendPacecarMsg();

}

void setPacecarSwitch( int switcher ) {

	pc_switch = switcher;
	sendPacecarMsg();

}

int togglePacecarSwitch() {

	pc_switch = ( pc_switch ) ? 0 : 1;
	sendPacecarMsg();
	return pc_switch;

}

void setPcPitstop( int pitstop ) {

	pc_pitstop = pitstop;
	sendPacecarMsg();

}

void setPitstop( int carId, int pitstop ) {

	if(validatecarId(carId)){
		cars[carId]->pitstop = pitstop;
		writeCar( cars[carId] );
	}

}



void activateFuel() {
	setAllFuel( 1 );
}

void deactivateFuel() {
	setAllFuel( 0 );
}




void resetCars() 
{
	int i;
	for(i=0; i<MAXCARS; ++i) {

		cars[i]->speed = 0;
		cars[i]->switcher = 0;
		cars[i]->fuel = 0;
		writeCar( cars[i] );	
		usleep( 1000 );
		printf("Reseting Car %d!\n", i);
	}
}






void initCriticalSection( CriticalSection* cs, int cin_start, int cin_end, int cout_start, int cout_end ){

	int i;
	for( i = 0; i < MAXCARS; i++ ) {
		cs->restricted[i] = NULL;
	}
	
	cs->critical_in = 0;
	cs->critical_out = 0;
	
	cs->critical_in_start  = cin_start;
	cs->critical_in_end    = cin_end;
	cs->critical_out_start = cout_start;
	cs->critical_out_end   = cout_end;

}


int addCriticalSection( CriticalSection* cs ) {

	int i;
	for( i = 0; i < MAXCRITICAL; i++ ) {
		if( NULL == critical[i] ){
			critical[i] = cs;
			return 1;
		}
	}
	return 0;

}

void addLapMarker( int* lm ){

	lapmarker = lm;

}


int pollSensorQueue( Sensor* sens ) {

	pthread_mutex_lock( &QLOCK );


	if( qhead == NULL ){
		pthread_mutex_unlock( &QLOCK );
		return 0;
	}

	Entry* t;

	t = qhead;
	qhead = qhead->next;
	if( NULL == qhead ) {
		qtail = NULL;
	}

	sens->id = ((Sensor*)t->data)->id;
	sens->carId = ((Sensor*)t->data)->carId;

	free( t->data );
	free( t );
	
	qlen--;
	pthread_mutex_unlock( &QLOCK );
	return 1;

}


void powerOff() {
	sendPowerMsg(0);
}

void powerOn() {
	sendPowerMsg(1);
}


void programmCar( carId ) {

	Car car = {
		.carId = carId,
		.switcher = 0,
		.fuel = 0,
		.speed = 0,
	};

	// init car state
	writeCar( &car );
	usleep(500000);


	// First click
	car.switcher = 1;
	writeCar( &car );
	usleep(200000);

	car.switcher = 0;
	writeCar( &car );
	usleep(200000);

	// Second click
	car.switcher = 1;
	writeCar( &car );
	usleep(200000);

	car.switcher = 0;
	writeCar( &car );
	usleep(200000);

	// Turn off the power supply to simulate that the 
	// car has left the race-track
	powerOff();
	usleep(500000);

	// Activate power supply agains
	powerOn();
	usleep(200000);

	// First click
	car.switcher = 1;
	writeCar( &car );
	usleep(200000);

	car.switcher = 0;
	writeCar( &car );
	usleep(200000);

	// Second click
	car.switcher = 1;
	writeCar( &car );
	usleep(200000);

	car.switcher = 0;
	writeCar( &car );
	usleep(200000);



}


void programmSpeed( int value ) {
	writeProgrammingMsg( PSPEED, value );
}


void programmBreak( int value ) {
	writeProgrammingMsg( PBREAK, value );
}


void programmFuel( int value ) {
	writeProgrammingMsg( PFUEL, value );
}



