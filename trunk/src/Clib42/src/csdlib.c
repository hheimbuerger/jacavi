/*
** file : csdlib.c
** author : Benjamin Kleinhens
** project : Carrera
** team : 42
** 
** Carrera Sensor Detection Library
** 
*/


/*/ Include system libraries.
*/
#include    <stdint.h> 
#include     <stdio.h>
#include    <unistd.h>
#include <sys/types.h>
#include  <sys/stat.h>
#include     <fcntl.h>
#include    <string.h>
#include   <pthread.h>


/*/ Include local header files.
*/
#include    "csdlib.h"



/*/ Define constant values
*/

#define MAX_CHANNELS  	MAX_SENSOR
#define MAX_TIMESTAMP 	0x3FFF

#define MAGIC 		0xDEAF
#define FIRST_MAGIC  	0xDE
#define SECOND_MAGIC 	0xAF

#define NOT_RECOGNIZED	0xFF

#define STREAM_LENGTH	6

#define DEVICE "/dev/ttyS0"



/*/ Define local data structures and types
*/
#define time_t		uint16_t

typedef unsigned char byte;

typedef struct _data_ {
 uint16_t	timestamp;
 uint16_t	state;
 uint16_t	magic;
} Data;


typedef struct _channel_ {
 time_t		time;
 time_t		timestamp;
 time_t		last_timestamp;
 time_t		first_timestamp;
 uint8_t	event;
} Channel;

uint8_t recognized[MAX_CHANNELS];



/*/ Prototype definitions for local methods
*/
inline time_t calc_timestamp_diff ( time_t time, time_t time0 );
static void analyse_data( Data* data );
void* analyse_stream( void* arg );
static void setRunning( uint8_t run );
static uint8_t isRunning( void );

/*/ Global data definitions 
*/

Channel 	channel[MAX_CHANNELS];

pthread_t 	thread;
pthread_mutex_t LOCK;	
int		file_handle;
uint8_t		running;



int initializeSensorDetection( void (*callback_fnctl)( int, int ) ){

	// Validate callback function
	if( NULL == callback_fnctl ) {
		return SD_INVALID_ARGUMENT;
	}

	// Open serial port 
	file_handle = open( DEVICE, O_RDONLY, O_NONBLOCK );
	if( file_handle < 0 ) {
		return SD_DEVICE_ACCESS_FAILED;
	}

	// initialize locking mechanism 
	if( pthread_mutex_init( &LOCK, NULL ) ) {
		close( file_handle );
		return SD_SYNCHRONIZE_FAILED;
	}

	
	
	uint8_t i;
	// Initialize channel data
	for( i = 0; i < MAX_CHANNELS; i++ ) {
		channel[i].time = 0;
		channel[i].timestamp = 0;
		channel[i].last_timestamp = 0;
		channel[i].first_timestamp = 0;
		channel[i].event = 0;
		recognized[i] = NOT_RECOGNIZED;
	}


	callback = callback_fnctl;
	running = 1;


	// start evaluation thread
	int tret = pthread_create( &thread, NULL, analyse_stream, &file_handle );
	if( tret ) {
		close(file_handle);
		pthread_mutex_destroy( &LOCK );
		return SD_SYNCHRONIZE_FAILED;
	}

	return 0;
}


void releaseSensonrDetection(){
	
	setRunning( 0 );
	pthread_join( thread, NULL );
	pthread_mutex_destroy( &LOCK );
	close( file_handle );

}



inline time_t calc_timestamp_diff ( time_t time, time_t time0 ) {
	return ( time >= time0 ) ? ( time - time0 ) : ( (MAX_TIMESTAMP - time0) + time );
}


static void analyse_data( Data* data ) {

	uint8_t i = 0;
	time_t time_dif;
	uint8_t carId;

	//printf( "MAGIC -> %d, TIME -> %d, STATE -> %d\n", data->magic, data->timestamp, data->state );

	if( data-> magic != MAGIC ) {
		printf("Invalid Magic Number %d\n", data->magic);
		return;
	}

	for( i = 0; i < MAX_CHANNELS; i++ ){
	
		if( data->state & ( 1 << (i) ) ) {

			channel[i].event++;
			time_dif = calc_timestamp_diff( data->timestamp, channel[i].timestamp );

		

			//printf("Timedif : %d when : timestamp-data -> %d and timestamp channel -> %d \n", time_dif, data->timestamp, channel[i].timestamp);

			if( time_dif >= 40 ) {
				
				channel[i].event = 0;
				channel[i].time = 0;
			} else {
				channel[i].time += time_dif;

				if( channel[i].event == 1 ) {

					channel[i].first_timestamp = data->timestamp;
					//recognized[i] = NOT_RECOGNIZED;
				} else if( channel[i].event == 0 ){

					// prefent floatingpoint exception
				} else if( calc_timestamp_diff( data->timestamp, channel[i].first_timestamp ) > 60 ) {

					carId = channel[i].time / channel[i].event /2;
				
					if ( recognized[carId-1] != i  ) {
						//printf("AUTOID = %d | CHAN: %d \n", carId, i );
						callback( (int) carId, (int) i+1 );
						recognized[carId-1] = i;
					}
					
				} 
			
				

			}

			channel[i].last_timestamp = channel[i].timestamp;
			channel[i].timestamp = data->timestamp;

		}
	}
}




void* analyse_stream( void* arg ) {

	Data data;
	byte stream[STREAM_LENGTH + 1];
	
	int fd = *( (int*) arg );

	while( isRunning() ) {
	
		if( read( fd, &stream[STREAM_LENGTH], 1 ) == 1 ) {
			if( stream[STREAM_LENGTH] == FIRST_MAGIC && stream[STREAM_LENGTH-1] == SECOND_MAGIC ) {
				memmove( &data, &stream[1], STREAM_LENGTH );
				analyse_data( &data );
			} else {
				memmove( stream, &stream[1], STREAM_LENGTH );
			}	
		}
	}

	return NULL;
}


static uint8_t isRunning(  ){
	uint8_t run;
	pthread_mutex_lock( &LOCK );
		run = running;	
	pthread_mutex_unlock( &LOCK );
	return run;
}



static void setRunning( uint8_t run ){
	pthread_mutex_lock( &LOCK );
		running = run;
	pthread_mutex_unlock( &LOCK );
}

