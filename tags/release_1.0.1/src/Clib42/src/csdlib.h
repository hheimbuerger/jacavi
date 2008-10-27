/*
** file : csdlib.h
** author : Benjamin Kleinhens
** project : Carrera
** team : 42
** 
** Carrera Sensor Detection Library
** 
** This library waits for incomming sensor data on on
** the serial port ttyS0 and evaluates them. 
** When a car is detected by the sensor data evaluation 
** a callback to the specified method is triggered.
** 
** The sensor data evaluation is running in an own thread.
** So it's neccessary to link the pthread library when 
** too when linking the csdlib.
*/


#ifndef SENSOR_H
#define SENSOR_H

// Maximal number of supported sensors.
#define MAX_SENSOR 16

/* ERROR Codes for wrong initialization
*/
// One of the Arguments is invalid
#define SD_INVALID_ARGUMENT	-1 
// Accessing ttyS0 or the specified device failed.
#define SD_DEVICE_ACCESS_FAILED	-2
// Thread or locks could not get certainly initialized 
#define SD_SYNCHRONIZE_FAILED	-3

/* Callback function
** This function is called when a car was detected on a sensor.
*/
void (*callback)( int carId, int sensor );

/* This initializes the csdlib
** It opens the /dev/ttyS0 device for reading incomming sensor data,
** and starts a pthread for the evaluation. 
** Returns 0 on success, otherwise one of the ERROR Codes.
*/
int initializeSensorDetection( void (*callback_fnctl)( int, int ) );

/* Releases the csd lib.
*/
void releaseSensonrDetection();


#endif

