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