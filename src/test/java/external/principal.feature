Feature: Caso principal

  Scenario: crear partido ok
    Given call read('login.feature@login_b')
    * driver baseUrl + '/user/2'

    #Comprobar que lleva a la ventana de crear partido
    * click("a[id=a-crearPartido]")
    * match html('title') contains 'IW: Crear partido'

    #Comprobar que crea partido
    * input('#pista', '1')
    * input('#inicio', '11/06/002026 10:00 AM')
    * input('#fin', '11/06/002026 11:00 AM')
    * click("input[id=crear]")
    * match html('title') contains 'IW: Chat de partido'

  Scenario: partido
    #inicia sesion y entra a su perfil
    Given call read('login.feature@login_b')

    #pulsa el botón de filtrar partido
    And click("a[id=a-filtrarPartido]")
    And input('#fecha', '12/03/2024')
    And input('#hora-inicio', '15:30')
    And input('#hora-fin', '17:00')
    And input('#deporte', 'Baloncesto')
    And input('#localidad', 'Ciu')
    * click("input[id=buscarPartido]")