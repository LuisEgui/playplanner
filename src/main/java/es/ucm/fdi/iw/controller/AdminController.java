package es.ucm.fdi.iw.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.ucm.fdi.iw.model.Mensaje;
import es.ucm.fdi.iw.model.Partido;
import es.ucm.fdi.iw.model.Transferable;
import es.ucm.fdi.iw.model.User;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 *  Site administration.
 *
 *  Access to this end-point is authenticated - see SecurityConfig
 */
@Controller
@RequestMapping("admin")
public class AdminController {

	private static final Logger log = LogManager.getLogger(AdminController.class);

    @Autowired
	private EntityManager entityManager;

	@ResponseStatus(
		value=HttpStatus.FORBIDDEN,
		reason="No se puede banear a un administrador.")  
	public static class BanearAdminException extends RuntimeException {}

	@GetMapping("/")
    public String index(Model model) {
        List<Mensaje> reports = entityManager.createNamedQuery("Mensaje.noLeidosReportes", Mensaje.class).getResultList();

		log.info("Generating message list for user {} ({} messages)", 
				"ADMIN", reports.size());
        
        //model.addAttribute("reports", reports);
        model.addAttribute("reports", reports);
        return "admin";
    } 

    /**
	 * Devuelve todos los reportes
	 */
	@GetMapping(path = "received", produces = "application/json")
	@Transactional // para no recibir resultados inconsistentes
	@ResponseBody  // para indicar que no devuelve vista, sino un objeto (jsonizado)
	public List<Mensaje.Transfer> retrieveReports(HttpSession session) {

		List<Mensaje> reports =  entityManager.createNamedQuery("Mensaje.reports", Mensaje.class).getResultList();

		log.info("Generating message list for user {} ({} messages)", 
				"ADMIN", reports.size());

		return  reports.stream().map(Transferable::toTransfer).collect(Collectors.toList());
	}
	
    @PostMapping("/banUser")
	@ResponseBody
	@Transactional
	public String banUser(@RequestBody JsonNode o, Model model, HttpSession session)  {
		
		String username = o.get("username").asText();
		
		User banned = entityManager.createNamedQuery("User.byUsername", User.class)
		.setParameter("username", username)
		.getSingleResult();

		if(banned.hasRole(User.Role.ADMIN)) {
			//TODO Bloquear tambien boton en vista si se quiere banear a admin
			throw new BanearAdminException();
		}

		banned.setEnabled(false);

		return "{\"result\": \"usuario baneado.\"}";
	}	

	@PostMapping("/marcarLeido")
       @ResponseBody
       @Transactional
       public String marcarLeido(HttpServletResponse response, @RequestBody JsonNode o, Model model, HttpSession session)  {

		Long idMsj = o.get("idMsj").asLong();

		Mensaje msj = entityManager.find(Mensaje.class, idMsj);

		if(msj == null) {
				response.setStatus(400);
				return "";
		}

		msj.setDateRead(LocalDateTime.now());
		return "{\"result\": \"ok.\"}";
    }
}