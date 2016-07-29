package io.pivotal.poc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.NamingException;

@SpringBootApplication
public class BobbyApplication implements CommandLineRunner {

	@Autowired
	private MQGateway mqGateway;

	@Override
	public void run(String... strings) throws Exception {
		System.out.println(">>>>>>>>> sending");
		mqGateway.send("Hi tere...");
		System.out.println(">>>>>>>>> sent");
	}

	public static void main(String[] args) {
		SpringApplication.run(BobbyApplication.class, args);
	}
}



