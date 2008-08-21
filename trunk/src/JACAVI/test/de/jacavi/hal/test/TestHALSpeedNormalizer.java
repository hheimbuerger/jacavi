package de.jacavi.hal.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.jacavi.hal.HALSpeedNormalizer;



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

        int resultNormalize = HALSpeedNormalizer.normalizeSpeed(speed, TestGlobals.lib42MaxHalSpeed);

        int resultDenormalize = HALSpeedNormalizer.denormalizeSpeed(resultNormalize, TestGlobals.lib42MaxHalSpeed);

        Assert.assertEquals(speed, resultDenormalize);

    }

    @Test
    public void testNormalizeSpeedBluerider() {
        int speed = 70;

        int resultNormalize = HALSpeedNormalizer.normalizeSpeed(speed, TestGlobals.blueriderMaxHalSpeed);

        int resultDenormalize = HALSpeedNormalizer.denormalizeSpeed(resultNormalize, TestGlobals.blueriderMaxHalSpeed);

        Assert.assertEquals(speed, resultDenormalize);

    }

}
