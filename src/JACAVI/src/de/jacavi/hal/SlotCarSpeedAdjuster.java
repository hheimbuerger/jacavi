package de.jacavi.hal;

import de.jacavi.rcp.util.Check;



/**
 * Represents an central speed normalizer for the spezific HAL technologies
 * 
 * @author fro
 */
public class SlotCarSpeedAdjuster {

    /**
     * use this to adjust the speed given from the DeviceContoller (0-100) to an HAL specific speed
     * 
     * @param speed
     *            the speed given from an device (must be adjusted to the range 0-100
     * @param maxHALSpeed
     *            the max speed on the specific Hardware
     * @return int the hardware adjusted speed to set to the hardware
     */
    public static int normalizeSpeed(int deviceAdjustedSpeed, int maxHALSpeed) {
        Check.Require(deviceAdjustedSpeed >= 0 && deviceAdjustedSpeed <= 100,
                "speed may be between (included) 0 and 100");
        int tmpSpeed = deviceAdjustedSpeed * 100;
        int tmpMaxHALSpeed = maxHALSpeed * 100;
        int retVal = 0;
        retVal = (tmpSpeed / 100) * tmpMaxHALSpeed;
        return retVal / 100;
    }

    /**
     * use this to readjust the hardwarespecific speed to an speed of the range 0-100
     * 
     * @param speed
     *            the hardware specific speed
     * @param maxHALSpeed
     *            the max speed on the specific Hardware
     * @return int the 0-100 adjusted speed
     */
    public static int denormalizeSpeed(int specificHardwareAdjustedSpeed, int maxHALSpeed) {
        Check.Require(maxHALSpeed > 0, "maxHALSpeed may be greater that 0");
        int tmpSpeed = specificHardwareAdjustedSpeed * 100;
        int tmpMaxHALSpeed = maxHALSpeed * 100;
        int retVal = 0;
        retVal = (tmpSpeed / tmpMaxHALSpeed) * 100;
        return retVal / 100;
    }
}
