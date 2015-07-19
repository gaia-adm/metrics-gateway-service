package com.hp.gaia.mgs.services;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by belozovs on 7/19/2015.
 */
public class MetricsCollectorServiceTest {

    private MetricsCollectorService mcs;

    @Before
    public void setUp() throws Exception {
        mcs = new MetricsCollectorService();

    }

    @Test
    public void testEscapedString() throws Exception {
        System.out.println(mcs.escapedString("\"my double quoted string part 1, part 2\""));
    }
}