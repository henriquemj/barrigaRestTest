package br.ce.wcaquino.rest.tests;

import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

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
	
	// Email: teste2023@teste.com.br
	// senha: 123456
	
	@Test
	public void deveIncluirContaComSucesso() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "teste2023@teste.com.br");
		login.put("senha", "123456");
		
		String token = given()
				.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
		
		given()
			.header("Authorization", "JWT " + token)
			.body("{ \"nome\": \"conta qualquer\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
	;
	}
}
