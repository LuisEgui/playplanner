Feature: Caso principal

  Scenario: crear partido ok
    Given call read('login.feature@login_b')
    * driver baseUrl + '/user/2'

    #Comprobar que lleva a la ventana de crear partido
    * click("a[id=a-crearPartido]")
    * match html('title') contains 'IW: Crear partido'

    #Comprobar que crea partido
    * click("input[id=tenis]")
    * click("select[id=pista]")
    * script("document.getElementById('pista2').selected = true")
    * input('#inicio', '11/06/2026')

    #Ver disponibilidad
    * click("input[id=ver]")

    #Seleccionar la franja de las 10:00
    * script("document.getElementById('10:00').click()")
    * click("input[id=crear]")

    * delay(1000)

    #Ir al chat del partido
    * driver baseUrl + '/user/match/975'
    * match html('title') contains 'IW: Chat de partido'

    #Enviar mensaje a partido
    * input('#message', 'Hola a todos!')
    * click("button[id=sendmsg]")
    * delay(1000)
    * match html('#contenedor-mensajes') contains 'Hola a todos!'

  Scenario: uinrse a partido
    #inicia sesion y entra a su perfil
    Given call read('login.feature@login_b')

    #pulsa el botón de filtrar partido
    And click("a[id=a-filtrarPartido]")
    And input('#fecha', '11/11/2024')
    And input('#hora-inicio', '18:00')
    And input('#hora-fin', '20:00')
    And input('#deporte', 'Tenis')
    And input('#localizacion', 'Moncloa - Aravaca, 28040 Madrid')
    * click("input[id=buscarPartido]")

    #Unirse al partido
    * script("document.getElementsByClassName('form-unirse').item(0).getElementsByClassName('unirse-btn').item(0).click()")
    * delay(1000)
    * match html(".partido") contains 'Te has unido correctamente al partido'