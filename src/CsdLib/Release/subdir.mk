################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../JNativeCsdLib.c \
../csdlib.c 

OBJS += \
./JNativeCsdLib.o \
./csdlib.o 

C_DEPS += \
./JNativeCsdLib.d \
./csdlib.d 


# Each subdirectory must supply rules for building sources it contributes
%.o: ../%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I/usr/lib/jvm/java-6-sun-1.6.0.00/include -I/usr/lib/jvm/java-6-sun-1.6.0.00/include/linux -O0 -g3 -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


