-- insert admin (username a, password aa)
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (1, TRUE, 'ADMIN,USER', 'a',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (2, TRUE, 'USER', 'b',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');

INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (3, TRUE, 'USER', 'c',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');

INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (4, TRUE, 'USER', 'd',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');

INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (5, TRUE, 'USER', 'e',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');


INSERT INTO IWCourt (id, nombre, localizacion, apertura, cierre, tipo)
VALUES (1, 'Cancha baloncesto San Pol de Mar', 'Calle de San Pol de Mar, 11', 8, 20, 'Baloncesto');

INSERT INTO IWCourt (id, nombre, localizacion, apertura, cierre, tipo)
VALUES (2, 'Pistas de tenis del Centro Deportivo Municipal Concepción', 'C. Hermanos de Pablo, 44, Cdad. Lineal, Madrid', 8, 20, 'Tenis');

INSERT INTO IWCourt (id, nombre, localizacion, apertura, cierre, tipo)
VALUES (3, 'Cancha baloncesto Parque de la Paz', 'Parque de la Paz, C. la Paz, 4, Alcorcón', 8, 20, 'Baloncesto');

INSERT INTO IWCourt (id, nombre, localizacion, apertura, cierre, tipo)
VALUES (4, 'Pista central tenis, Club de Campo Villa de Madrid', 'Moncloa - Aravaca, 28040 Madrid', 8, 20, 'Tenis');

INSERT INTO Partido (id, fin, inicio, is_private, pista_id, chat_token) 
VALUES (1, '2024-11-11T10:00', '2024-11-11T08:00', false, 1, 'XhbWy-uPBI1N81IT');

--Participaciones en partido
INSERT INTO IWJUEGA (id, partido_id, user_id) VALUES (1, 1, 2);
INSERT INTO IWJUEGA (id, partido_id, user_id) VALUES (2, 1, 3);
INSERT INTO IWJUEGA (id, partido_id, user_id) VALUES (3, 1, 4);

-- Mensajes de chat
INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (1, '2024-11-11T19:00', '2024-11-11T18:00', false, 'Hola a todos!', 1, null, 2);

INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (2, '2024-11-11T20:00', '2024-11-11T19:00', false, 'Holaa!', 1, null, 4);

-- Insertar reportes
INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (3, '2024-11-11T19:00', '2024-11-11T18:00', true, 'El usuario pepe esta spameando el chat', 1, null, 2);

INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (4, '2024-11-11T20:00', '2024-11-11T19:00', true, 'Esto es otro reporte', 1, null, 3);

-- start id numbering from a value that is larger than any assigned above
ALTER SEQUENCE "PUBLIC"."GEN" RESTART WITH 1024;