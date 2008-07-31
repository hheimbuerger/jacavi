#include "de_jacavi_hal_nat_JNativeCsdLib.h"
#include "csdlib.h"
#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>


JavaVM * jvm = NULL;
jclass cls;
jmethodID callbackID;

static void doCallback(int carID,int sensor);



static void doCallback(int carID,int sensor)
{
	JNIEnv* env;
	if (jvm != NULL) {
		if ((*jvm)->AttachCurrentThread(jvm, (void **)&env, NULL) >= 0) {
			(*env)->CallStaticVoidMethod(env, cls, callbackID, carID, sensor);
		}
	}
}


/*
 * Class:     de_jacavi_hal_nat_JNativeCsdLib
 * Method:    initSensorDetection
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_jacavi_hal_nat_JNativeCsdLib_initSensorDetection(JNIEnv *env, jobject o)
{
	//get the vm and cache it local
	(*env)->GetJavaVM(env,&jvm);
	//get the jclass
	cls = (*env)->GetObjectClass(env, o);
	//get the java callback method
	callbackID = (*env)->GetStaticMethodID(env, cls, "callback", "(II)V");
	//register c callback function
	return initializeSensorDetection(doCallback);
}


/*
 * Class:     de_jacavi_hal_nat_JNativeCsdLib
 * Method:    releaseSensorDetection
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_jacavi_hal_nat_JNativeCsdLib_releaseSensorDetection(JNIEnv *env, jobject o)
{
	releaseSensonrDetection();
}

