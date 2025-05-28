package br.edu.ifpb.GastoZen;

import br.edu.ifpb.model.User;
import br.edu.ifpb.service.UserService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "br.edu.ifpb")
public class GastoZenApplication {
    public static void main(String[] args) {
        // Cria o SpringApplication e força modo NON-WEB
        SpringApplication app = new SpringApplication(GastoZenApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);

        // Inicializa o contexto sem web-server
        ApplicationContext ctx = app.run(args);

        // Recupera o UserService do contexto
        UserService userService = ctx.getBean(UserService.class);

        // Cria um usuário de exemplo
        User exemplo = new User(
                "Teste Usuário",     // name
                99,                  // age
                1_234.56,            // salary
                "teste2@usuario.com", // email
                "9999-9999",         // phone
                "Administrador"      // occupation
        );

        // Tenta registrar — se já existir, captura a exceção e só loga
        try {
            User salvo = userService.registerUser(exemplo);
            System.out.println("Usuário registrado com sucesso: " + salvo.getEmail());
        } catch (Exception e) {
            System.err.println("Não foi possível registrar o usuário de teste: " + e.getMessage());
        }

        // Lista todos os usuários e imprime quantidade
        System.out.println("Total de usuários no Firebase: " + userService.getAllUsers().size());
    }
}
