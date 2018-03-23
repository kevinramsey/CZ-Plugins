package com.melissadata.kettle.propertywebservice.testing;

/**
 * Created by Kevin on 2/12/2018.
 */
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
	public static void main(String[] args) {
		//Result result = JUnitCore.runClasses(SmartMoverTest.class);
		Result result = JUnitCore.runClasses(PropertyJunitTestSuite.class);

		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}

		System.out.println(result.wasSuccessful());
	}
}
