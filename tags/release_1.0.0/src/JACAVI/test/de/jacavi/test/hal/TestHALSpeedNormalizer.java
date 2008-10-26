package de.jacavi.test.hal;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.jacavi.hal.SlotCarThrustAdjuster;



/**
 * @author fro
 */
public class TestHALSpeedNormalizer {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testNormalizeSpeedLib42() {

        int speed = 80;

        int resultNormalize = SlotCarThrustAdjuster.normalizeThrust(speed, TestGlobals.lib42MaxHalSpeed);
        System.out.println("" + resultNormalize);

        Assert
                .assertTrue("" + resultNormalize, resultNormalize <= TestGlobals.lib42MaxHalSpeed
                        && resultNormalize >= 0);

        int resultDenormalize = SlotCarThrustAdjuster.denormalizeThrust(resultNormalize, TestGlobals.lib42MaxHalSpeed);

        Assert.assertEquals(speed, resultDenormalize);

    }

    @Test
    public void testNormalizeSpeedBluerider() {
        int speed = 70;

        int resultNormalize = SlotCarThrustAdjuster.normalizeThrust(speed, TestGlobals.blueriderMaxHalSpeed);
        Assert.assertTrue("" + resultNormalize, resultNormalize <= TestGlobals.blueriderMaxHalSpeed
                && resultNormalize >= 0);

        int resultDenormalize = SlotCarThrustAdjuster
                .denormalizeThrust(resultNormalize, TestGlobals.blueriderMaxHalSpeed);

        Assert.assertEquals(speed, resultDenormalize);

    }

}
