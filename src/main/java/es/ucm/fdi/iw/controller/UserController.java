package es.ucm.fdi.iw.controller;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Mensaje;
import es.ucm.fdi.iw.model.Transferable;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;
import es.ucm.fdi.iw.model.Court;
import es.ucm.fdi.iw.model.Partido;
import es.ucm.fdi.iw.model.Juega;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestMethod;
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
import java.time.LocalDateTime;
import java.time.temporal.*;
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
		User u = getRequester(session);

		List<Mensaje> mensajes = new ArrayList<Mensaje>();

		//Recorrer todos los partidos en los que participa, buscando mensajes no leidos.
		//Usuario normal solo puede recibir mensajes a traves de chats de partido.
		for(Juega j: u.getJuega()) {
			mensajes.addAll(entityManager.createNamedQuery("Mensaje.noLeidos", Mensaje.class)
			.setParameter("matchId", j.getPartido().getId()).getResultList());
		}

		if(u.isAdmin()) {
			mensajes.addAll(null);//unreadREports)
		}

		log.info("Generating message list for user {} ({} messages)",
				u.getUsername(), mensajes.size());
		return  mensajes.stream().map(Transferable::toTransfer).collect(Collectors.toList());
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
		boolean pertenece = p.getJuega(sender) != null;
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

	//Provisional para obtener listados de partidos
	@GetMapping("/verPartidos") 
	public String verPartidos(Model model) { 
		List<Partido> partidos = entityManager
		.createNamedQuery("Partido.partidosAbiertos", Partido.class)
		.setParameter("fechaActual", LocalDateTime.now())
		.getResultList();

		model.addAttribute("partidos", partidos);
		return "listadoPartidos";
	}

	@GetMapping("/filtermatches")
	public String filter(Model model) {
		return "filtermatches";
	}

	@GetMapping("/match/{id}")
	@Transactional
    public String getMatch(@PathVariable long id, Model model, HttpSession session) {
        Partido p = entityManager.find(Partido.class, id);
		User requester = getRequester(session);
		Juega j = p.getJuega(requester);

		if(j == null && !requester.isAdmin()) {
			model.addAttribute("error", "No perteneces al partido");
			return "errorAux";
		}
		
        model.addAttribute("partido", p);

		List<Mensaje> mensajes = new ArrayList<Mensaje>();
		//Quedarnos con los mensajes que no son reportes
		for(Mensaje m: p.getMensajes()) {
			if(!m.isReport()) mensajes.add(m);
		}

		model.addAttribute("mensajes_chat", mensajes);
		if(j != null) j.setUltimoAcceso(LocalDateTime.now());
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
	public String crearPartido(Model model) {
		return "createMatch";
	}

	@PostMapping("/crearPartido")
	@Transactional
	public String crearPartido(HttpServletResponse response,
	@RequestParam("pista") Long idPista,
	@RequestParam("inicio") String inicio, Integer maxParticipantes,
	Model model, HttpSession session) throws IOException{

		User requester = getRequester(session);
		boolean error = false;

		//Comprobar que la pista existe y tomarla
        Court pista = entityManager.find(Court.class, idPista);
		if(pista == null) {
			model.addAttribute("error", "La pista no existe.");
			error = true;
		}

		//Comprobar que el maximo de participantes especificado no supera al maximo de participantes de la pista.
		if(maxParticipantes > pista.getMaxp()) {
			model.addAttribute("error", "El numero maximo de participantes específicado supera al de la pista.");
			error = true;
		}

		// Conversion de las fechas
		LocalDateTime inicioDateTime = LocalDateTime.parse(inicio);
		LocalDateTime finDateTime = inicioDateTime.plusHours(Partido.DURACION_PARTIDOS_HORAS);

		//Comprobar que no se sobrepasa el dia
		if(finDateTime.getDayOfYear() != inicioDateTime.getDayOfYear() || finDateTime.getYear() != inicioDateTime.getYear()) {
			model.addAttribute("error", "La reserva cubre dos dias o mas, eso no esta permitido");
			error = true;
		}

		//Comprobar que la fecha de inicio es posterior al momento actual
		if(inicioDateTime.isBefore(LocalDateTime.now())) {
			model.addAttribute("error", "La fecha de inicio debe ser posterior al momento actual.");
			error = true;
		}

		//Comprobar que la hora de inicio es anterior a la de fin
		if(!inicioDateTime.isBefore(inicioDateTime)) {
			model.addAttribute("error", "La fecha de inicio debe ser anterior a la de fin.");
			error = true;
		}
		
		//Comprobar que son horas en punto
		if(inicioDateTime.getMinute() != 0 || inicioDateTime.getSecond() != 0 || inicioDateTime.getNano() != 0) {
			model.addAttribute("error", "No se pueden reservar horas que no sean en punto.");
			error = true;
		}
		
		//Comprobar que el horario es valido para la pista
		int horaInicioPartido = inicioDateTime.getHour();
		int horaFinPartido = finDateTime.getHour();

		if(horaInicioPartido < pista.getApertura() || horaFinPartido > pista.getCierre()) {
			model.addAttribute("error", "Horas reservadas no validas para la pista. Horario de la pista: "
			 + pista.getApertura() + " - " + pista.getCierre());

			error = true;
		}

		//Comprobar que no hay ya un partido en esa pista a esa hora.
		Partido conflicto = entityManager
		.createNamedQuery("Partido.conflicto", Partido.class)
		.setParameter("courtId", pista.getId())
		.setParameter("fechaInicio", inicioDateTime)
		.setParameter("fechaFin", finDateTime)
		.getSingleResult();
		
		if(conflicto != null) {
			model.addAttribute("error", "Ya hay un partido en ese horario en esa pista: " + conflicto.getInicio() + " - " + conflicto.getFin());
			error = true;
		} 
		else if(!error) {
			//Crear partido
			Partido partido = new Partido();
			partido.setPista(pista);
			partido.setInicio(inicioDateTime);
			partido.setFin(finDateTime);
			partido.setPrivate(false);
			partido.setChatToken(generateRandomBase64Token(12));
			partido.setMaxp(maxParticipantes);
			entityManager.persist(partido);

			//El creador juega en el partido
			Juega juega = new Juega();
			juega.setPartido(partido);
			juega.setUser(requester);
			entityManager.persist(juega);
			entityManager.flush();

			//Hacer que el creador se suscriba al chat del partido
			suscribirA(session, partido.getChatToken());
		}
		return "endMatch";
	}

	@PostMapping("/joinMatch")
	@Transactional
	@ResponseBody
	public String unirseAPartido(HttpServletResponse response,
	@RequestBody JsonNode o,
	Model model, HttpSession session) throws IOException{

		Long idPartido = o.get("idPartido").asLong();
		User requester = getRequester(session);
		Partido partido = entityManager.find(Partido.class, idPartido);
		Integer num_participantes = partido.getJuega().size();
		
		//Comprobar que no es admin
		if(requester.hasRole(User.Role.ADMIN)) {
			response.setStatus(400);
			return "Eres administrador";
		}

		//Comprobar que no pertenece ya al partido
		if(partido.getJuega(requester) != null) {
			response.setStatus(400);
			return "Ya perteneces al partido";
		}

		//Comprobar que el partido no esta cerrado ni ha terminado
		if(partido.getEstado().equals(Partido.Estado.CERRADO) ||
		partido.getEstado().equals(Partido.Estado.TERMINADO)) {
			response.setStatus(400);
			return "El partido esta cerrado o ya ha terminado";
		}

		//Meter al jugador en el partido
		Juega juega = new Juega();
		juega.setPartido(partido);
		juega.setUser(requester);
		entityManager.persist(juega);
		entityManager.flush();

		num_participantes++;

		//El jugador se suscribe al chat del partido
		suscribirA(session, partido.getChatToken());

		//Si se ha llegado al maximo de participantes actualizamos el estado del partido a CERRADO
		if(num_participantes == partido.getMaxp()) {
			partido.setEstado(Partido.Estado.CERRADO);
		}

		return "{\"result\":".concat(num_participantes.toString()).concat( " }");
	}

	private User getRequester(HttpSession session) {
		User requester = (User)session.getAttribute("u");
		requester = entityManager.find(User.class, requester.getId());
		return requester;
	}

	private void suscribirA(HttpSession session, String chat_token) {
		String topics = session.getAttribute("topics").toString();
		topics.concat(",").concat(chat_token);
		session.setAttribute("topics", topics);
	}
}