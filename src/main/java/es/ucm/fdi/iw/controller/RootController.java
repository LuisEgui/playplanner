package es.ucm.fdi.iw.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.model.User;

/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {

    @Autowired
	private EntityManager entityManager;

    @Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger log = LogManager.getLogger(RootController.class);

	@GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

	@GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/register")
    public String register(Model model) {
        return "register"; 
    }

    @PostMapping("/register")
	@Transactional
	public String registerUser(@RequestParam("name") String name, @RequestParam("surname") String surname,
	@RequestParam("username") String username, @RequestParam("password") String password, Model model) {

        ArrayList<User> users = (ArrayList<User>) entityManager.createNamedQuery("User.byUsername", User.class)
                            .setParameter("username", username)
                            .getResultList();	
        if(!users.isEmpty()) {
            model.addAttribute("error", "El username ya existe, por favor elige otro.");
            return "register";
        }

		User user = new User();
		user.setFirstName(name);
		user.setRoles("USER");
        user.setEnabled(true);
		user.setUsername(username);
        user.setLastName(surname);
        user.setPassword(passwordEncoder.encode(password));

        entityManager.persist(user);

        return "login";
	}
}