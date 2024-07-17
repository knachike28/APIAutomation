package com.Test;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class ApiRequest {

    @BeforeClass
    public void setup() {
        baseURI = "https://httpbin.org";
    }

    @Test
    public void testGetRequest() {
        Response response = get("/get");
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void testPostRequest() {
        String requestBody = "{ \"name\": \"test\" }";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post("/post");

        System.out.println("Response Body: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200);

        String nameValue = response.jsonPath().getString("json.name");
        Assert.assertNotNull(nameValue, "The 'name' field is null");
        Assert.assertTrue(nameValue.contains("test"));
    }

    @Test
    public void testDelayedResponse() {
        int delayTime = 3; // delay time in seconds
        Response response = get("/delay/" + delayTime);
        Assert.assertEquals(response.getStatusCode(), 200);
        // Optionally, check the response time
        Assert.assertTrue(response.getTime() >= delayTime * 1000);
    }

    @Test
    public void testNegativeScenario() {
        Response response = get("/status/404");
        Assert.assertEquals(response.getStatusCode(), 404);

    }

    @Test
    public void testUnauthorizedAccess() {
        Response response = get("/basic-auth/user/passwd");
        Assert.assertEquals(response.getStatusCode(), 401);
    }

    @DataProvider(name = "statusCodes")
    public Object[][] createStatusCodes() {
        return new Object[][] { { 200 }, { 404 }, { 500 } };
    }

    @Test(dataProvider = "statusCodes")
    public void testParameterizedStatusCodes(int statusCode) {
        Response response = get("/status/" + statusCode);
        Assert.assertEquals(response.getStatusCode(), statusCode);
    }
}