package br.ce.wcaquino.rest.tests;

import org.junit.Test;
import org.junit.Before;

import br.ce.wcaquino.rest.core.BaseTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

public class BarrigaTest extends BaseTest {
	
	private String TOKEN;
	
	@Before
	public void login() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "teste2023@teste.com.br");
		login.put("senha", "123456");
		
		TOKEN = given()
				.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
		
	}
	
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
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{ \"nome\": \"conta qualquer\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
	;
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{ \"nome\": \"conta alterada\"}")
		.when()
			.put("/contas/1571019")
		.then()
		.log().all()
			.statusCode(200)
			.body("nome", is("conta alterada"))
	;
  }
	
	@Test
	public void naoDeveInserirContaMesmoNome() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{ \"nome\": \"conta alterada\"}")
		.when()
			.post("/contas")
		.then()
		.log().all()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
	;
  }
	
	@Test
	public void deveInserirMovimentacaoSucesso() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(1571019);
//		mov.setUsuario_id(usuario_id);
		mov.setDescricao("Descricao da movimentacao");
		mov.setEnvolvido("Envolvido na mov");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2000");
		mov.setData_pagamento("01/01/2010");
		mov.setValor(100f);
		mov.setStatus(true);
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
		.log().all()
			.statusCode(201)
	;
  }
}
