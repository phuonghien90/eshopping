package com.hien.eshopping.test;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = { "com.hoiio.service.callcenter.unittest.stepdefs" },
        tags = {"@unittest"},
        features = { "classpath:features" })
public class UnitTest {

    @BeforeClass
    public static void setup() throws Exception {
    }

    @AfterClass
    public static void teardown() throws Exception {
    }
}