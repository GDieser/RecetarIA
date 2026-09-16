📱 RECETARiA - Tu Asistente en la Cocina - Equipo 7

Este repositorio contiene el proyecto final desarrollado por el equipo 7, con el cual se alcanzó el título de Técnico Universitario en Programación en la UTN. RECETARiA es una aplicación móvil Android que funciona como un asistente culinario inteligente. La solución utiliza inteligencia artificial para generar recetas personalizadas, permitiendo a los usuarios organizar su alimentación diaria, reducir el desperdicio de comida y disponer de sus preparaciones sin necesidad de conexión a internet.

### Funcionalidades principales

👤 Perfil de usuario y Onboarding

* Encuesta inicial de onboarding para relevar el contexto del usuario.


* Persistencia de datos personales que incluyen: grupo familiar, objetivos calóricos y restricciones alimentarias (como celiaquía, intolerancia a la lactosa, diabetes y colesterol alto).



🍳 Generación de recetas con IA

* Creación dinámica de recetas a través de la API de OpenAI basándose en los ingredientes disponibles en el hogar.


* Adaptación inteligente de las preparaciones para ajustarse al tiempo disponible, los conocimientos culinarios y los objetivos de salud del usuario.



💾 Gestión de recetas y Modo Offline

* Interfaz simple, clara e intuitiva diseñada para el uso cotidiano en dispositivos móviles.


* Sistema de guardado para recetas favoritas y un registro con el historial completo de las comidas generadas.


* Acceso offline garantizado, permitiendo consultar todas las recetas guardadas sin conexión a internet mediante almacenamiento local.



🛡️ Arquitectura y Seguridad

* Implementación de arquitectura cliente-servidor con un servidor intermedio (proxy) para evitar la comunicación directa de la app con servicios externos.


* Construcción dinámica de prompts en el backend y validación estricta de las respuestas devueltas por la IA.


* Protección de credenciales sensibles, aislando la clave de acceso a la API de OpenAI fuera de la aplicación cliente para maximizar la seguridad, controlar el consumo y facilitar la escalabilidad del sistema.



### 🧰 Tecnologías utilizadas

* Android Studio


* Java


* SQLite


* Arquitectura MVVM


* API de OpenAI


* JSON / HTTP



### 👨‍💻 Equipo 7

Este proyecto fue desarrollado como trabajo final por el equipo 7:

* Alegre Lucas
* Dieser German Jorge Esteban
* Hiebl Darian
