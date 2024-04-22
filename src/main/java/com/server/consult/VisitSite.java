package com.server.consult;

import com.server.SqlFlexyCup.SqlLexer;
import com.server.SqlFlexyCup.parser;
import com.server.servidorWeb.WebServer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VisitSite {
        public static void procesVisitSite(String cadena){

            String entrada = cadena;
            String regex = "\"([^\"]*)\"";

            // Compilar la expresión regular
            Pattern pattern = Pattern.compile(regex);

            // Crear un matcher para la entrada
            Matcher matcher = pattern.matcher(entrada);

            // Lista para almacenar las palabras entre comillas
            List<String> palabras = new ArrayList<>();

            // Iterar sobre las coincidencias encontradas
            while (matcher.find()) {
                // Obtener la palabra entre comillas (grupo de captura 1)
                String palabra = matcher.group(1);
                palabras.add(palabra);
            }

            // Imprimir las palabras entre comillas encontradas
            System.out.println("Palabras encontradas entre comillas:");
            for (String palabra : palabras) {

                System.out.println("Pagina "+palabra+" Visitado "+WebServer.visitasPorSitio.get(palabra));
            }


        }
}
