package br.ce.wcaquino.rest.tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;

public class ContasTest extends BaseTest {
	
	@BeforeClass
	public static void login() {
		System.out.println("Before Conta");
		Map<String, String> login = new HashMap<>();
		login.put("email", "teste2023@teste.com.br");
		login.put("senha", "123456");
		
		String TOKEN = given()
				.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
		
		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
		
		RestAssured.get("/reset").then().statusCode(200);
		
	}
	
	@Test
	public void deveIncluirContaComSucesso() {
		System.out.println("incluir");
		given()
			.body("{ \"nome\": \"Conta inserida\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			;
	}
	
	@Test
	public void AlterarContaComSucesso() {
		System.out.println("alterar");
		Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");
		
		given()
			.body("{ \"nome\": \"Conta alterada\" }")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
		.log().all()
			.statusCode(200)
			.body("nome", is("Conta alterada"))
	;
  }
	
	@Test
	public void naoDeveInserirContaMesmoNome() {
		given()
			.body("{ \"nome\": \"Conta mesmo nome\" }")
		.when()
			.post("/contas")
		.then()
		.log().all()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
	;
  }
	
	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
	}
}
