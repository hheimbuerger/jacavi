/*
** 	file : carrera.h
** 	author : Benjamin Kleinhens
** 	project : Carrera
** 	team : 42
** 
** 
********************************************************************************
** 
** sendPowerMsg
** - send e message to the controll power suply
** - if power is 0 power supply will be turned off
** - otherwise power supply will be turned on
** - Power supply is active by default
** 
** writeProgrammingMsg
** - sends a programming message
** - param must  be one of the following values:
** - PSPEED, PBREAK, PFUEL, PNONE
** - value must be a positive integer in the range [1,10]
**
** sendPacecarMsg
** - sends out a message to the pacecar 
*/


#ifndef CARRERA_H
#define CARRERA_H

// DEFINE BITMAKS FOR THE FIRST 8 BITS
#define BIT08 0x00000080
#define BIT07 0x00000040
#define BIT06 0x00000020
#define BIT05 0x00000010
#define BIT04 0x00000008
#define BIT03 0x00000004
#define BIT02 0x00000002
#define BIT01 0x00000001
#define BIT00 0x00000000

// DEFINE VALID PROGRAMMING MSG PARAMETER
#define PSPEED 0x00
#define PBREAK 0X01
#define PFUEL  0x02
#define PNONE  0x04



void sendPowerMsg( int power );
void writeProgrammingMsg( int param, int value );
void sendPacecarMsg( void );




#endif
