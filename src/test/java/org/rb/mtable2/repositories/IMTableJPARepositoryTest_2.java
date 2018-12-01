package org.rb.mtable2.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rb.mtable2.Mtable2SpringThymeleafApplication;
import org.rb.mtable2.model.MTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Mtable2SpringThymeleafApplication.class,webEnvironment = WebEnvironment.RANDOM_PORT)

public class IMTableJPARepositoryTest_2 {

	//private static final String REST_SERVICE_URI = "http://localhost:8080/api";
	private static final String REST_SERVICE_URI = "/api";

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFindAllMTables() {
		
		//RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("unchecked")
		ResponseEntity<MTable[]> response = restTemplate.getForEntity(REST_SERVICE_URI+"/tables/", MTable[].class);
         MTable[] tables = response.getBody();
        System.out.println("Result: "+tables.toString());
	}

	

}
