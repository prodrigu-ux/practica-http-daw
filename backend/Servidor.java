import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Year;

public class Servidor {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/procesar", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String nombre = "Sin nombre";
            String correo = "Sin correo";
            int edad = 0;

            if (query != null) {
                for (String param : query.split("&")) {
                    String[] par = param.split("=");
                    if (par.length == 2) {
                        String clave = par[0];
                        String valor = URLDecoder.decode(par[1], StandardCharsets.UTF_8);
                        if (clave.equals("nombre")) nombre = valor;
                        if (clave.equals("edad")) edad = Integer.parseInt(valor);
                        if (clave.equals("correo")) correo = valor;
                    }
                }
            }

            String nombreMayus = nombre.toUpperCase();
            String estadoEdad = (edad >= 18) ? "Mayor de edad" : "Menor de edad";
            int anioNacimiento = Year.now().getValue() - edad;

            String respuestaHtml = "<html><body style='font-family: Arial, sans-serif; padding: 30px;'>"
                    + "<h2>Datos procesados por el Servidor Java</h2>"
                    + "<p><b>Usuario:</b> " + nombreMayus + "</p>"
                    + "<p><b>Edad:</b> " + edad + " años (" + estadoEdad + " - nacido aprox. en " + anioNacimiento + ")</p>"
                    + "<p><b>Correo registrado:</b> " + correo + "</p>"
                    + "<br><a href='javascript:history.back()'>Volver al formulario</a>"
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