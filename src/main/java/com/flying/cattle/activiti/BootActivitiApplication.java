package com.flying.cattle.activiti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan({"com.flying.cattle.modeler.editor","com.flying.cattle.activiti.config","com.flying.cattle.modeler"})
@EntityScan(basePackages = {"com.flying.cattle.system.entity"})

public class BootActivitiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootActivitiApplication.class, args);
	}

}
