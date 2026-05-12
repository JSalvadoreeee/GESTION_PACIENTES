package com.SaludPlus.gestion_pacientes;

import com.SaludPlus.gestion_pacientes.model.Role;
import com.SaludPlus.gestion_pacientes.model.User;
import com.SaludPlus.gestion_pacientes.repository.UserRepository;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling 
public class GestionPacientesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionPacientesApplication.class, args);
    }

    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SaludPlus - Gestión de Pacientes")
                .version("1.0")
                .description("Microservicio encargado de la administración y registro de pacientes de la clínica SaludPlus."))
            .addSecurityItem(new SecurityRequirement().addList("SaludPlusSecurity"))
            .components(new Components().addSecuritySchemes("SaludPlusSecurity", new SecurityScheme()
                .name("SaludPlusSecurity")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")));
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                // He puesto "admin123" para que sea más seguro, pero puedes dejarlo como gustes
                User admin = new User(null, "admin", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN);
                userRepository.save(admin);
                System.out.println("Usuario admin creado con éxito para SaludPlus.");
            }
        };
    }
}