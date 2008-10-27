################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/JNativeLib42.c \
../src/csdlib.c \
../src/lib42.c 

OBJS += \
./src/JNativeLib42.o \
./src/csdlib.o \
./src/lib42.o 

C_DEPS += \
./src/JNativeLib42.d \
./src/csdlib.d \
./src/lib42.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I/usr/lib/jvm/java-6-sun-1.6.0.00/include -I/usr/lib/jvm/java-6-sun-1.6.0.00/include/linux -O0 -c -fpic -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


