package com.server.servidorWeb;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServer {
    public static Server server;
    // Mapa para contar visitas por página
    private static Map<String, Integer> pageVisitCount = new HashMap<>();
    static int contar=0;
    public static Map<String, Integer> visitasPorSitio = new HashMap<>();
    // Mapa para contar visitas totales por sitio
    private static Map<String, Integer> siteVisitCount = new HashMap<>();
    public static void ServerPage() {

        String baseDir = "/home/tuxrex/NetBeansProjects/ProjectCompi/Servidor/Sitios";
        File f=new File(baseDir);
        File[]listFiles=f.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            System.out.println("Nombres: "+listFiles[i].getName());
        }
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Manejar solicitudes para /sitio1/*



        for (int i = 0; i < listFiles.length; i++) {
            final int currentIndex = i;
            final String sitioActual = listFiles[i].getName();

            server.createContext("/" + sitioActual + "", (exchange) -> {
                String path = exchange.getRequestURI().getPath();
                String sitioPath = baseDir + "/" + sitioActual + "" + path.replace("/" + sitioActual + "", "");

                // Incrementar el contador de visitas para el sitio actual
                visitasPorSitio.put(sitioActual, visitasPorSitio.getOrDefault(sitioActual, 0) + 1);
                int visitasSitioActual = visitasPorSitio.get(sitioActual);

                System.out.println("Pagina: " + sitioActual + " path: " + path + " Numero de veces visitados paginas : " + visitasSitioActual);
                enviarArchivo(exchange, sitioPath);
            });
        }


        // Manejar solicitudes para /sitio2/*






        server.start();
        System.out.println("Servidor iniciado en http://localhost:8000/");

    }

    private static void enviarArchivo(HttpExchange exchange, String filePath) throws IOException {
        Path file = Paths.get(filePath);
        if (Files.exists(file) && Files.isRegularFile(file)) {
            byte[] fileContent = Files.readAllBytes(file);
            exchange.sendResponseHeaders(200, fileContent.length);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(fileContent);
            responseBody.close();
        } else {
            String response = "404 Not Found";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(response.getBytes());
            responseBody.close();
        }
    }
}
