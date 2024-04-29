package es.ucm.fdi.iw.controller;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Mensaje;
import es.ucm.fdi.iw.model.Transferable;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;
import lombok.Data;
import es.ucm.fdi.iw.model.Court;
import es.ucm.fdi.iw.model.Partido;
import es.ucm.fdi.iw.model.Juega;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *  User management.
 *
 *  Access to this end-point is authenticated.
 */
@Controller()
@RequestMapping("user")
public class UserController {

	private static final Logger log = LogManager.getLogger(UserController.class);

	@Autowired
	private EntityManager entityManager;

	@Autowired
    private LocalData localData;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

    /**
     * Exception to use when denying access to unauthorized users.
     * 
     * In general, admins are always authorized, but users cannot modify
     * each other's profiles.
     */
	@ResponseStatus(
		value=HttpStatus.FORBIDDEN, 
		reason="No eres administrador, y éste no es tu perfil")  // 403
	public static class NoEsTuPerfilException extends RuntimeException {}

	/**
	 * Encodes a password, so that it can be saved for future checking. Notice
	 * that encoding the same password multiple times will yield different
	 * encodings, since encodings contain a randomly-generated salt.
	 * @param rawPassword to encode
	 * @return the encoded password (typically a 60-character string)
	 * for example, a possible encoding of "test" is 
	 * {bcrypt}$2y$12$XCKz0zjXAP6hsFyVc8MucOzx6ER6IsC1qo5zQbclxhddR1t6SfrHm
	 */
	public String encodePassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

    /**
     * Generates random tokens. From https://stackoverflow.com/a/44227131/15472
     * @param byteLength
     * @return
     */
    public static String generateRandomBase64Token(int byteLength) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[byteLength];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token); //base64 encoding
    }

    /**
     * Landing page for a user profile
     */
	@GetMapping("{id}")
    public String index(@PathVariable long id, Model model, HttpSession session) {
        User target = entityManager.find(User.class, id);
        model.addAttribute("user", target);

		Long ganados = (Long) entityManager
					.createNamedQuery("Partido.ganados", Long.class)
					.setParameter("idUser", id)
					.getSingleResult();

		Long perdidos = (Long) entityManager
					.createNamedQuery("Partido.perdidos", Long.class)
					.setParameter("idUser", id)
					.getSingleResult();

		model.addAttribute("ganados", ganados);
		model.addAttribute("perdidos", perdidos);

		ArrayList<Partido> partidosJugados = (ArrayList<Partido>) entityManager
					.createNamedQuery("Partido.byUser", Partido.class)
					.setParameter("idUser", id)
					.getResultList();
	
		model.addAttribute("partidosJugados", partidosJugados);

        return "user";
    }

    /**
     * Alter or create a user
     */
	@PostMapping("/{id}")
	@Transactional
	public String postUser(
			HttpServletResponse response,
			@PathVariable long id, 
			@ModelAttribute User edited, 
			@RequestParam(required=false) String pass2,
			Model model, HttpSession session) throws IOException {

        User requester = (User)session.getAttribute("u");
        User target = null;
        if (id == -1 && requester.hasRole(Role.ADMIN)) {
            // create new user with random password
            target = new User();
            target.setPassword(encodePassword(generateRandomBase64Token(12)));
            target.setEnabled(true);
            entityManager.persist(target);
            entityManager.flush(); // forces DB to add user & assign valid id
            id = target.getId();   // retrieve assigned id from DB
        }
        
        // retrieve requested user
        target = entityManager.find(User.class, id);
        model.addAttribute("user", target);
		
		if (requester.getId() != target.getId() &&
				! requester.hasRole(Role.ADMIN)) {
			throw new NoEsTuPerfilException();
		}
		
		if (edited.getPassword() != null) {
            if ( ! edited.getPassword().equals(pass2)) {
                // FIXME: complain
            } else {
                // save encoded version of password
                target.setPassword(encodePassword(edited.getPassword()));
            }
		}		
		target.setUsername(edited.getUsername());
		target.setFirstName(edited.getFirstName());
		target.setLastName(edited.getLastName());

		// update user session so that changes are persisted in the session, too
        if (requester.getId() == target.getId()) {
            session.setAttribute("u", target);
        }

		return "user";
	}	

    /**
     * Returns the default profile pic
     * 
     * @return
     */
    private static InputStream defaultPic() {
	    return new BufferedInputStream(Objects.requireNonNull(
            UserController.class.getClassLoader().getResourceAsStream(
                "static/img/default-pic.jpg")));
    }

    /**
     * Downloads a profile pic for a user id
     * 
     * @param id
     * @return
     * @throws IOException
     */
    @GetMapping("{id}/pic")
    public StreamingResponseBody getPic(@PathVariable long id) throws IOException {
        File f = localData.getFile("user", ""+id+".jpg");
        InputStream in = new BufferedInputStream(f.exists() ?
            new FileInputStream(f) : UserController.defaultPic());
        return os -> FileCopyUtils.copy(in, os);
    }

    /**
     * Uploads a profile pic for a user id
     * 
     * @param id
     * @return
     * @throws IOException
     */
    @PostMapping("{id}/pic")
	@ResponseBody
    public String setPic(@RequestParam("photo") MultipartFile photo, @PathVariable long id, 
        HttpServletResponse response, HttpSession session, Model model) throws IOException {

        User target = entityManager.find(User.class, id);
        model.addAttribute("user", target);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&
				! requester.hasRole(Role.ADMIN)) {
            throw new NoEsTuPerfilException();
		}
		
		log.info("Updating photo for user {}", id);
		File f = localData.getFile("user", ""+id+".jpg");
		if (photo.isEmpty()) {
			log.info("failed to upload photo: emtpy file?");
		} else {
			try (BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(f))) {
				byte[] bytes = photo.getBytes();
				stream.write(bytes);
                log.info("Uploaded photo for {} into {}!", id, f.getAbsolutePath());
			} catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				log.warn("Error uploading " + id + " ", e);
			}
		}
		return "{\"status\":\"photo uploaded correctly\"}";
    }
    

    /**
     * Returns JSON with all received messages
     
    @GetMapping(path = "received", produces = "application/json")
	@Transactional // para no recibir resultados inconsistentes
	@ResponseBody  // para indicar que no devuelve vista, sino un objeto (jsonizado)
	public List<Mensaje.Transfer> retrieveMessages(HttpSession session) {
		long userId = ((User)session.getAttribute("u")).getId();		
		User u = entityManager.find(User.class, userId);
		List<Mensaje> mensajes = new ArrayList<Mensaje>();

		//Recorrer todos los partidos en los que participa, buscando mensajes no leidos
		for(Juega j: u.getJuega()) {
			mensajes.addAll(entityManager.createNamedQuery("Mensaje.noLeidos", Mensaje.class)
			.setParameter("matchId", j.getPartido().getId()).getResultList());
		}

		log.info("Generating message list for user {} ({} messages)", 
				u.getUsername(), mensajes.size());
		return  mensajes.stream().map(Transferable::toTransfer).collect(Collectors.toList());
	}
	*/
    
    /**
     * Returns JSON with count of unread messages 
    
	@GetMapping(path = "unread", produces = "application/json")
	@ResponseBody
	public String checkUnread(HttpSession session) {
		long userId = ((User)session.getAttribute("u")).getId();		
		User u = entityManager.find(User.class, userId);
		long unread = 0;

		//Recorrer todos los partidos en los que participa, buscando mensajes no leidos
		for(Juega j: u.getJuega()) {
			unread += entityManager.createNamedQuery("Mensaje.countUnread", Long.class)
			.setParameter("matchId", j.getPartido().getId())
			.getSingleResult();
		}
		
		session.setAttribute("unread", unread);
		return "{\"unread\": " + unread + "}";
    }*/
    
    /**
     * Posts a message to a match.
     * @param id of target user (source user is from ID)
     * @param o JSON-ized message, similar to {"message": "text goes here"}
     * @throws JsonProcessingException
     */
    @PostMapping("/match/{id}/msg")
	@ResponseBody
	@Transactional
	public String postMsg(@PathVariable long id, 
			@RequestBody JsonNode o, Model model, HttpSession session) 
		throws JsonProcessingException {
		
		String text = o.get("message").asText();
		Partido p = entityManager.find(Partido.class, id);
		User sender = entityManager.find(
				User.class, ((User)session.getAttribute("u")).getId());
		
		
		//Comprobar que el emisor pertenece al partido o bien es admin
		boolean pertenece = false;
		for(Juega j : sender.getJuega()) {
			if(j.getUser().getId() == sender.getId()) pertenece = true;
		}
		if(!pertenece && !sender.hasRole(Role.ADMIN))
			 throw new IllegalArgumentException("No perteneces al partido y no eres admin");

		// construye mensaje, lo guarda en BD
		Mensaje m = new Mensaje();
		m.setPartido(p);
		m.setSender(sender);
		m.setDateSent(LocalDateTime.now());
		m.setTexto(text);
		m.setReport(false);
		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit
		
		ObjectMapper mapper = new ObjectMapper();
		/*
		// construye json: método manual
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("from", sender.getUsername());
		rootNode.put("to", u.getUsername());
		rootNode.put("text", text);
		rootNode.put("id", m.getId());
		String json = mapper.writeValueAsString(rootNode);
		*/
		// persiste objeto a json usando Jackson
		String json = mapper.writeValueAsString(m.toTransfer());

		log.info("Sending a message to {} with contents '{}'", id, json);

		messagingTemplate.convertAndSend("/topic/" + p.getChatToken(), json);
		return "{\"result\": \"message sent.\"}";
	}	

	@GetMapping("/filtermatches")
	public String filter(Model model) {
		ArrayList<Court> pistas = (ArrayList<Court>) entityManager
				.createNamedQuery("Court.allCourt", Court.class)
				.getResultList();
		
		model.addAttribute("pistas", pistas);

		return "filtermatches";
	}

	@PostMapping("/filtermatches")
	@Transactional
	public String filter(HttpServletResponse response, @RequestParam("fecha") String fecha,
		@RequestParam("hora-inicio") String horaInicio, @RequestParam("hora-fin") String horaFin, 
		@RequestParam("deporte") String deporte, @RequestParam("localizacion") String localizacion, 
		Model model, HttpSession session) throws IOException {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime fechaInicio = LocalDateTime.parse(fecha + " " + horaInicio + ":00", formatter);
		LocalDateTime fechaFin = LocalDateTime.parse(fecha + " " + horaFin + ":00", formatter);
		
		ArrayList<Partido> partidosFiltrados = (ArrayList<Partido>) entityManager
										.createNamedQuery("Partido.filtrar", Partido.class)
										.setParameter("deporte", deporte)
										.setParameter("localizacion", localizacion)
										.setParameter("fechaInicio", fechaInicio)
										.setParameter("fechaFin", fechaFin)
										.getResultList();

		if(!partidosFiltrados.isEmpty()){
			model.addAttribute("partidos", partidosFiltrados);
		}
		else{
			model.addAttribute("error", "No encontramos partidos con esos filtros!! :(");
		}
		model.addAttribute("filtrado", true);

		return viewMatches(model);
	}

	@GetMapping("/match/{id}")
	@Transactional
    public String getMatch(@PathVariable long id, Model model, HttpSession session) {
        Partido p = entityManager.find(Partido.class, id);
        model.addAttribute("partido", p);

		List<Mensaje> mensajes = new ArrayList<Mensaje>();
		//Quedarnos con los mensajes que no son reportes
		for(Mensaje m: p.getMensajes()) {
			if(!m.isReport()) mensajes.add(m);
		}
		model.addAttribute("mensajes_chat", mensajes);

		//TODO marcar los mensajes como leidos por ese usuario

		/* 1. Ejecutar named query que tome todos los Leido por ese usuario de este chat
		 * 2. Iterar sobre todos los mensajes del partido y crear un nuevo leido para cada uno nuevo
		 */
		
        return "chatMatch";
    }

	@GetMapping("/endMatch")
	public String endMatch(Model model) {
		return "endMatch";
	}

	/*
	 * valoraciones
	 */
	@GetMapping("/valuation")
	public String valuation(Model model) {
		return "valuation";
	}

	@GetMapping("/crearPartido")
	public String crearPartido(@RequestParam(required = false) String deporte, @RequestParam(required = false) Long pistaId, 
	@RequestParam(required = false) String fecha, Model model) {
		ArrayList<Court> pistas;
		if (deporte != null) {
			model.addAttribute("deporteSeleccionado", deporte);

			pistas = (ArrayList<Court>) entityManager
				.createNamedQuery("Court.byTipoDeporte", Court.class)
				.setParameter("deporte", deporte)
				.getResultList();

			if(pistaId != null && fecha != null){
				model.addAttribute("pistaSeleccionada", pistaId);
				model.addAttribute("fechaSeleccionada", fecha);

				//Obtener horas libres
				model.addAttribute("horasDisponibles", verDisponibilidad(pistaId, fecha));
				model.addAttribute("mostrarHoras", true);
			}
			else {
				model.addAttribute("mostrarHoras", false);
			}
		}
		else {
			pistas = (ArrayList<Court>) entityManager
				.createNamedQuery("Court.allCourt", Court.class)
				.getResultList();
		}

		model.addAttribute("pistas", pistas);

		return "createMatch";
	}

	private List<String> verDisponibilidad(Long pistaId, String fecha) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse(fecha, formatter); 

		List<String> horasDisponibles = new ArrayList<>();

		List<Integer> horasOcupadas;
		horasOcupadas = (ArrayList<Integer>) entityManager
				.createNamedQuery("Partido.horasOcupadas", Integer.class)
				.setParameter("pistaId", pistaId)
				.setParameter("fecha", date.atStartOfDay())
				.setParameter("fechaMasUnDia", date.atStartOfDay().plusDays(1))
				.getResultList();

		log.info("horasOcupadas {}", horasOcupadas);
		Court c = entityManager.find(Court.class, pistaId);
		
		for(int i = c.getApertura(); i < c.getCierre(); i+=2){
			if(!horasOcupadas.contains(i)){
				horasDisponibles.add(String.format("%02d:00", i));
			}
		}
				
		return horasDisponibles;
	}

	@PostMapping("/crearPartido")
	@Transactional
	public String crearPartido(HttpServletResponse response,
	@RequestParam("pista") Long idPista,
	@RequestParam("inicio") String inicio,
	@RequestParam("horaSeleccionada") String horaSeleccionada,
	Model model, HttpSession session) throws IOException{

        User requester = (User)session.getAttribute("u");
        Court pista = entityManager.find(Court.class, idPista);

		//Crear partido
		Partido partido = new Partido();
		partido.setPista(pista);

		// Formatear la fecha y la hora
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime fechaInicio = LocalDateTime.parse(inicio + " " + horaSeleccionada + ":00", formatter);
		partido.setInicio(fechaInicio);

		// Calcular la fecha de fin sumándole dos horas a la fecha de inicio
		LocalDateTime fechaFin = fechaInicio.plusHours(2);
		partido.setFin(fechaFin);

		//NUEVO
		partido.setEstado("PREPARANDO");
		//NUEVO 

		partido.setPrivate(false);
		partido.setChatToken(generateRandomBase64Token(12));
		entityManager.persist(partido);
		
		//El creador juega en el partido
		Juega juega = new Juega();
		juega.setPartido(partido);
		juega.setUser(requester);
		entityManager.persist(juega);

		response.sendRedirect("/user/viewMatches");
		return "viewMatches";
	}

	@GetMapping("/viewMatches")
	public String viewMatches(Model model) {

		Boolean filtrado = (Boolean) model.getAttribute("filtrado");
    	if (filtrado == null || !filtrado) {
			List<Partido> partidos = entityManager
			.createNamedQuery("Partido.allPartidos", Partido.class)
			.getResultList();

			model.addAttribute("partidos", partidos);
		}
		else{
			model.addAttribute("filtrado", false);
		}

		return "viewMatches";
	}
}