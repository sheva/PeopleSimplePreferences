package com.sheva.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Services test suite.
 *
 * Created by Sheva on 10/5/2016.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestFoodService.class, TestPersonService.class })
public class TestServicesSuite {
}
