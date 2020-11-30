package com.blz.restapi;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blz.restapi.EmployeePayrollService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EmployeePayrollServiceTest {

	private static final IOService REST_IO = null;
	static EmployeePayrollService employeePayrollRESTService;

	@BeforeClass
	public static void createObj() {
		employeePayrollRESTService = new EmployeePayrollService();
	}

	@AfterClass
	public static void nullObj() {
		employeePayrollRESTService = null;
	}

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	private EmployeePayrollData[] getEmployeeList() {
		Response response = RestAssured.get("/employeepayroll");
		System.out.println("Employee Payroll entries in JsonServer :\n" + response.asString());
		EmployeePayrollData[] arrayOfEmployee = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
		return arrayOfEmployee;
	}

	private Response addEmployeeToJsonServer(EmployeePayrollData employeePayrollData) {
		String employeeJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(employeeJson);
		return request.post("/employeepayroll");
	}

	@Test
	public void givenNewEmployee_WhenAdded_ShouldMatchResponseCode201AndCount() {
		EmployeePayrollData[] arrayOfEmployee = getEmployeeList();
		employeePayrollRESTService = new EmployeePayrollService(Arrays.asList(arrayOfEmployee));

		EmployeePayrollData employeePayrollData = new EmployeePayrollData(0, "Mark", "M", 3000000.00, LocalDate.now());

		Response response = addEmployeeToJsonServer(employeePayrollData);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);

		employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
		employeePayrollRESTService.addEmployeeToPayroll(employeePayrollData, REST_IO);
		long entries = employeePayrollRESTService.countEntries(REST_IO);
		Assert.assertEquals(1, entries);
	}

	@Test
	public void givenMultipleEmployee_WhenAdded_ShouldMatch201ResponseAndCount() {
		EmployeePayrollData[] arrayOfEmployee = getEmployeeList();
		employeePayrollRESTService = new EmployeePayrollService(Arrays.asList(arrayOfEmployee));
		EmployeePayrollData[] arrayOfEmployeePayroll = {
				new EmployeePayrollData(0, "Abhi", "M", 3000000.00, LocalDate.now()),
				new EmployeePayrollData(0, "Latha", "F", 5000000.00, LocalDate.now()) };

		for (EmployeePayrollData employeePayrollData : arrayOfEmployeePayroll) {

			Response response = addEmployeeToJsonServer(employeePayrollData);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);

			employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
			employeePayrollRESTService.addEmployeeToPayroll(employeePayrollData, REST_IO);
		}
		long entries = employeePayrollRESTService.countEntries(REST_IO);
		Assert.assertEquals(3, entries);
	}
}
