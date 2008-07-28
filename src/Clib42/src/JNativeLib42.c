#include "de_jacavi_hal_lib42_NativeLib42.h"
#include "lib42.h"

/*
 * Class:     de_jacavi_hal_nat_JNativeLib42
 * Method:    initLib42
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_nat_JNativeLib42_initLib42(JNIEnv *env, jobject o, jint i)
{
	//TODO
	return initLib42(LIB42_SENSOR_LOGGING);
}

/*
 * Class:     de_jacavi_hal_nat_JNativeLib42
 * Method:    setSpeed
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_nat_JNativeLib42_setSpeed(JNIEnv *env, jobject o, jint carID, jint speed)
{
	setSpeed(carID,speed);
}

/*
 * Class:     de_jacavi_hal_nat_JNativeLib42
 * Method:    getSpeed
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_nat_JNativeLib42_getSpeed(JNIEnv *env, jobject o, jint carID)
{
	return getSpeed(carID);

}

/*
 * Class:     de_jacavi_hal_nat_JNativeLib42
 * Method:    toggleSwitch
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_nat_JNativeLib42_toggleSwitch(JNIEnv *env, jobject o, jint carID)
{
	return toggleSwitch(carID);
}

/*
 * Class:     de_jacavi_hal_nat_JNativeLib42
 * Method:    getSwitch
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_nat_JNativeLib42_getSwitch(JNIEnv *env, jobject o, jint carID)
{
	return getSwitch(carID);
}


JNIEXPORT void JNICALL Java_de_jacavi_hal_nat_JNativeLib42_programmCar(JNIEnv *env, jobject o, jint carID)
{
	programmCar(carID);
}



