package br.ce.wcaquino.rest.tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.tests.Movimentacao;
import br.ce.wcaquino.rest.utils.DataUtils;
import io.restassured.RestAssured;

public class MovimentacaoTest extends BaseTest {
	
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
	public void deveInserirMovimentacaoSucesso() {
		Movimentacao mov = getMovimentacaoValida();
		
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
		.log().all()
			.statusCode(201)
			;
	}
	
	@Test
	public void deveValidarCamposObrigatoriosMovimentacao() {
		given()
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
		.log().all()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório"
					))
		;
	} 
	
	@Test
	public void naoDeveInserirMovimentacaoComDataFutura() {
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao(DataUtils.getDataDiferencaDias(2));
		
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
		.log().all()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
	;
  }
	
	@Test
	public void naoDeveRemoverContaComMovimentacao() {
		Integer CONTA_ID = getIdContaPeloNome("Conta com movimentacao");
		
		given()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
		.log().all()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
	;
  }
	
	@Test
	public void deveRemoverMovimentacao() {
		Integer MOV_ID = getIdMovPelaDescricao("Movimentacao para exclusao");
		
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
		.log().all()
			.statusCode(204)
		;
  }
	
	
	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
	}
	
	public Integer getIdMovPelaDescricao(String desc) {
		return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
	}
	
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id2(getIdContaPeloNome("Conta para movimentacoes"));
	//	mov.setUsuario_id(usuario_id);
		mov.setDescricao("Descricao da movimentacao");
		mov.setEnvolvido("Envolvido na mov");
		mov.setTipo("REC");
		mov.setData_transacao(DataUtils.getDataDiferencaDias(-1));
		mov.setData_pagamento(DataUtils.getDataDiferencaDias(5));
		mov.setValor(100f);
		mov.setStatus(true);
		return mov;
	}
}
