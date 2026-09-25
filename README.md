Android - Mi Formación CTMA
Semana 3 - Pruebas de Software y Scrum
Casos de Uso
Este documento contiene los casos de uso desarrollados para la actividad de pruebas de software de la Semana 3.
CU-01 - Confirmar entrega
Actor principal
Mensajero
Objetivo
Confirmar que una orden fue entregada correctamente y registrar la evidencia correspondiente.
Precondiciones
•
El mensajero está autenticado.
•
La orden está asignada al mensajero.
•
La orden está disponible para entrega.
Flujo principal
1.
El mensajero selecciona una orden asignada.
2.
El sistema muestra la información de la orden.
3.
El mensajero inicia la confirmación de entrega.
4.
El mensajero registra una fotografía de la entrega.
5.
El mensajero identifica al receptor.
6.
El sistema valida que la información requerida esté completa.
7.
El sistema cambia el estado de la orden a ENTREGADA.
8.
El sistema registra la fecha de entrega.
9.
El sistema registra la auditoría.
10.
El sistema muestra un mensaje de confirmación.
Flujos alternos y excepciones
A1 - Sin fotografía
1.
El mensajero intenta confirmar la entrega.
2.
No se proporciona una fotografía.
3.
El sistema detecta que falta la evidencia.
4.
El sistema rechaza la confirmación.
5.
La orden permanece en su estado anterior.
A2 - Receptor no identificado
1.
El mensajero intenta confirmar la entrega.
2.
No identifica al receptor.
3.
El sistema detecta la información faltante.
4.
El sistema rechaza la confirmación.
5.
La orden no cambia a ENTREGADA.
CU-02 - Registrar observación de entrega fallida
Actor principal
Mensajero
Objetivo
Registrar el motivo por el cual una entrega no pudo realizarse.
Precondiciones
•
El mensajero está autenticado.
•
Existe una orden en proceso de entrega.
•
La entrega no pudo realizarse.
Flujo principal
1.
El mensajero selecciona la orden.
2.
Selecciona la opción Registrar entrega fallida.
3.
El sistema muestra el campo para ingresar la observación.
4.
El mensajero escribe el motivo de la entrega fallida.
5.
El sistema valida que la observación tenga mínimo 10 caracteres.
6.
El sistema guarda la observación.
7.
El sistema registra la incidencia.
8.
El sistema muestra un mensaje de confirmación.
Flujos alternos y excepciones
A1 - Observación menor a 10 caracteres
1.
El mensajero escribe una observación de menos de 10 caracteres.
2.
El sistema valida la longitud.
3.
El sistema rechaza el registro.
4.
El sistema muestra un mensaje indicando que se requieren mínimo 10 caracteres.
5.
La observación no se guarda.
A2 - Observación vacía
1.
El mensajero intenta guardar la entrega fallida sin escribir una observación.
2.
El sistema detecta que el campo está vacío.
3.
El sistema rechaza el registro.
4.
El sistema solicita completar la observación.
Evidencias
Las evidencias de los casos de uso estarán organizadas en:
evidencias/semana-3/casos-de-uso/
CU-01 - Confirmar entrega
•
Flujo principal.
•
Caso alterno sin fotografía.
•
Caso alterno sin receptor identificado.
CU-02 - Registrar observación de entrega fallida
•
Flujo principal.
•
Caso alterno con observación menor a 10 caracteres.
•
Caso alterno con observación vacía.
Autor
Esteban Bedoya Rojo
Responsabilidad: Casos de Uso y evidencias
Actividad: Semana 3 - Pruebas de Software y Scrum
