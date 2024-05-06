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

INSERT INTO IWCourt (id, nombre, localizacion, apertura, cierre, tipo, maxp)
VALUES (1, 'Cancha baloncesto San Pol de Mar', 'Calle de San Pol de Mar, 11', 8, 20, 'Baloncesto', 10);

INSERT INTO IWCourt (id, nombre, localizacion, apertura, cierre, tipo, maxp)
VALUES (2, 'Pistas de tenis del Centro Deportivo Municipal Concepción', 'C. Hermanos de Pablo, 44, Cdad. Lineal, Madrid', 8, 20, 'Tenis', 4);

INSERT INTO IWCourt (id, nombre, localizacion, apertura, cierre, tipo, maxp)
VALUES (3, 'Cancha baloncesto Parque de la Paz', 'Parque de la Paz, C. la Paz, 4, Alcorcón', 8, 20, 'Baloncesto', 6);

INSERT INTO IWCourt (id, nombre, localizacion, apertura, cierre, tipo, maxp)
VALUES (4, 'Pista central tenis, Club de Campo Villa de Madrid', 'Moncloa - Aravaca, 28040 Madrid', 8, 20, 'Tenis', 4);

INSERT INTO Partido (id, inicio, fin, is_private, pista_id, chat_token, estado) 
VALUES (1, '2024-11-11T12:00', '2024-11-11T14:00', false, 1, 'XhbWy-uPBI1N81IT', 0);
INSERT INTO Partido (id, inicio, fin, is_private, pista_id, chat_token, estado) 
VALUES (2, '2024-11-11T18:00', '2024-11-11T20:00', false, 4, 'XhbWy-uPBI1N81IO', 0);
INSERT INTO Partido (id, inicio, fin, is_private, pista_id, chat_token, estado, result) 
VALUES (3, '2024-11-11T08:00', '2024-11-11T10:00', false, 2, 'u-EJC1pTzEo1Weon', 2, 'GANADO');
INSERT INTO Partido (id, inicio, fin, is_private, pista_id, chat_token, estado, result) 
VALUES (4, '2024-11-11T14:00', '2024-11-11T16:00', false, 3, 'YQRpm30p5y_r-TRk', 2, 'PERDIDO');

--Participaciones en partido
INSERT INTO IWJUEGA (id, partido_id, user_id, ultimo_acceso) VALUES (1, 1, 2, '2023-11-11T17:00');
INSERT INTO IWJUEGA (id, partido_id, user_id, ultimo_acceso) VALUES (2, 1, 3, '2023-11-11T17:00');
INSERT INTO IWJUEGA (id, partido_id, user_id, ultimo_acceso) VALUES (3, 1, 4, '2023-11-11T17:00');
INSERT INTO IWJUEGA (id, partido_id, user_id, ultimo_acceso) VALUES (4, 3, 2, '2023-11-11T17:00');
INSERT INTO IWJUEGA (id, partido_id, user_id, ultimo_acceso) VALUES (5, 4, 2, '2023-11-11T17:00');

-- Mensajes de chat
INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (1, null, '2024-03-03T18:00', false, 'Hola a todos!', 1, null, 2);

INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (2, null, '2024-03-03T19:00', false, 'Holaa!', 1, null, 4);

-- Insertar reportes
INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (3, '2024-03-03T19:00', '2024-03-03T18:00', true, 'El usuario pepe esta spameando el chat', 1, null, 2);

INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (4, null, '2024-03-03T19:00', true, 'Esto es otro reporte', 1, null, 3);

INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (5, null, '2024-03-03T19:00', true, 'Esto es otro reporte v2', 1, null, 3);

-- start id numbering from a value that is larger than any assigned above
ALTER SEQUENCE "PUBLIC"."GEN" RESTART WITH 1024;