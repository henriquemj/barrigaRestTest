package br.ce.wcaquino.rest.tests;

import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;

import static io.restassured.RestAssured.given;

public class BarrigaTest extends BaseTest {
	
	@Test
	public void naoDeveAcessarAPISemToken() {
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
}
