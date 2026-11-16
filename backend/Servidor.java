import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Servidor {
    public static void main(String[] args) throws IOException {
        // Levanta el servidor en el puerto 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Escucha las peticiones que lleguen a /procesar
        server.createContext("/procesar", exchange -> {
            String query = exchange.getRequestURI().getQuery(); // Ej: "nombre=Ana&edad=20"
            String nombre = "Sin nombre";
            int edad = 0;

            if (query != null) {
                for (String param : query.split("&")) {
                    String[] par = param.split("=");
                    if (par.length == 2) {
                        String clave = par[0];
                        String valor = URLDecoder.decode(par[1], StandardCharsets.UTF_8);
                        if (clave.equals("nombre")) nombre = valor;
                        if (clave.equals("edad")) edad = Integer.parseInt(valor);
                    }
                }
            }

            // Procesamiento de la información
            String nombreMayus = nombre.toUpperCase();
            String mensajeEdad = (edad >= 18) ? "eres mayor de edad" : "eres menor de edad";

            String respuestaHtml = "<html><body style='font-family: sans-serif;'>"
                    + "<h2>Respuesta procesada por Java</h2>"
                    + "<p>Hola <b>" + nombreMayus + "</b>, tienes " + edad + " años (" + mensajeEdad + ").</p>"
                    + "<a href='javascript:history.back()'>Volver al formulario</a>"
                    + "</body></html>";

            byte[] bytes = respuestaHtml.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });

        server.start();
        System.out.println("Servidor escuchando en http://localhost:8080");
    }
}