#include "de_jacavi_hal_lib42_NativeLib42.h"
#include "lib42.h"

/*
 * Class:     de_jacavi_hal_nat_JNativeLib42
 * Method:    initLib42
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_lib42_NativeLib42_initLib42(JNIEnv *env, jobject o, jint mode)
{

	int retVal=0;
	switch(mode)
	{
		case 1:
			retVal=initLib42(LIB42_COLLISION_DETECTION);
			break;
		case 2:
			retVal=initLib42(LIB42_LAP_COUNT);
			break;
		case 4:
			break;
			retVal=initLib42(LIB42_SENSOR_QUEUEING);
		case 8:
			retVal=initLib42(LIB42_SENSOR_LOGGING);
			break;
	}
	return retVal;
}

/*
 * Class:     de_jacavi_hal_nat_JNativeLib42
 * Method:    setSpeed
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_setSpeed(JNIEnv *env, jobject o, jint carID, jint speed)
{
	setSpeed(carID,speed);
}

/*
 * Class:     de_jacavi_hal_nat_JNativeLib42
 * Method:    getSpeed
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_lib42_NativeLib42_getSpeed(JNIEnv *env, jobject o, jint carID)
{
	return getSpeed(carID);

}

/*
 * Class:     de_jacavi_hal_nat_JNativeLib42
 * Method:    toggleSwitch
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_lib42_NativeLib42_toggleSwitch(JNIEnv *env, jobject o, jint carID)
{
	return toggleSwitch(carID);
}

/*
 * Class:     de_jacavi_hal_nat_JNativeLib42
 * Method:    getSwitch
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_lib42_NativeLib42_getSwitch(JNIEnv *env, jobject o, jint carID)
{
	return getSwitch(carID);
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    fullBreak
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_fullBreak(JNIEnv *env, jobject o, jint carID)
{
	fullBreak(carID);
}


/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    releaseLib42
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_releaseLib42(JNIEnv *env, jobject o)
{
	releaseLib42();
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    activatePacecar
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_activatePacecar(JNIEnv *env, jobject o)
{
	activatePacecar();
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    deactivatePacecar
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_deactivatePacecar(JNIEnv *env, jobject o)
{
	deactivatePacecar();
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    pacecar2box
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_pacecar2box(JNIEnv *env, jobject o)
{
	pacecar2box();
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    setPacecarSwitch
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_setPacecarSwitch(JNIEnv *env, jobject o, jint value)
{
	setPacecarSwitch(value);
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    togglePacecarSwitch
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_lib42_NativeLib42_togglePacecarSwitch(JNIEnv *env, jobject o)
{
	return togglePacecarSwitch();
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    setPcPitstop
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_setPcPitstop(JNIEnv *env, jobject o, jint pitstop)
{
	setPcPitstop(pitstop);
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    isPacecarActive
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_lib42_NativeLib42_isPacecarActive(JNIEnv *env, jobject o)
{
	return isPacecarActive();
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    getPcSwitch
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_lib42_NativeLib42_getPcSwitch(JNIEnv *env, jobject o)
{
	return getPcSwitch();
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    setPitstop
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_setPitstop(JNIEnv *env, jobject o, jint carID, jint pitstop)
{
	setPitstop(carID,pitstop);
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    togglePitstop
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_lib42_NativeLib42_togglePitstop(JNIEnv *env, jobject o, jint carID)
{
	return togglePitstop(carID);
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    getPitstop
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_lib42_NativeLib42_getPitstop(JNIEnv *env, jobject o, jint carID)
{
	return getPitstop(carID);
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    activateFuel
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_activateFuel(JNIEnv *env, jobject o)
{
	activateFuel();
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    deactivateFuel
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_deactivateFuel(JNIEnv *env, jobject o)
{
	deactivateFuel();
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    programmCar
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_programmCar(JNIEnv *env, jobject o, jint carID)
{
	programmCar(carID);
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    programmSpeed
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_programmSpeed(JNIEnv *env, jobject o, jint value)
{
	programmSpeed(value);
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    programmBreak
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_programmBreak(JNIEnv *env, jobject o, jint value)
{
	programmBreak(value);
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    programmFuel
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_programmFuel(JNIEnv *env, jobject o, jint value)
{
	programmFuel(value);
}

/*
 * Class:     de_jacavi_hal_lib42_NativeLib42
 * Method:    resetCars
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_lib42_NativeLib42_resetCars(JNIEnv *env, jobject o)
{
	resetCars();
}



