package com.sheva.services;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.IOException;

import static com.sheva.db.DatabaseTestHelper.deleteAllData;

/**
 * Services test suite.
 *
 * Created by Sheva on 10/5/2016.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestFoodService.class, TestPersonService.class })
public class TestServicesSuite {
    @BeforeClass
    public static void setUp() throws IOException {
        deleteAllData();
    }
}
