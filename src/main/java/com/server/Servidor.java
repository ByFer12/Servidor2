/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.server;

import com.server.XmlToHtml.ConvertXmlHtml;
import com.server.errores.createXml;
import com.server.servidorWeb.WebServer;
import com.server.sockets.ServidorSer;
import com.server.xmlflexcup.XmlAnalyzer;
import com.server.xmlflexcup.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author fer
 */
public class Servidor {

    public static void main(String[] args) throws Exception {
       //convirtiendo el xml a html
        ConvertXmlHtml.convertXmlToHtml();
        //iniciando el servidor
            WebServer.ServerPage();

        try {
            createXml.createXml();
            ServerSocket  server=new ServerSocket(10000);
            while(true){
                Socket cliente=server.accept();
                Thread hilo=new Thread(new ServidorSer(cliente));
                hilo.start();
            }
        } catch (IOException  ex) {
            ex.printStackTrace();
        }
    }
    public static void pruebass(){
        String entrada=
                "<acciones>\n" +
                        "<accion nombre=\"NUEVA_PAGINA\">\n" +
                        "<parametros>\n" +
                        "\n" +
                        "<parametro nombre=\"ID\">\n" +
                        "[productos]\n" +
                        "</parametro>\n" +
                        "\n" +
                        "</parametros>\n" +
                        "<etiquetas>\n" +
                        "<etiqueta valor=\"etiqueta1\"/>\n" +
                        "<etiqueta valor=\"etiqueta2\"/>\n" +
                        "</etiquetas>\n" +
                        "</accion>\n" +
                        "<accion nombre=\"MODIFICAR_PAGINA\">\n" +
                        "<parametros>\n" +
                        "<parametro nombre=\"ID\">\n" +
                        "[producto]\n" +
                        "</parametro>\n" +
                        "<parametro nombre=\"TITULO\">\n" +
                        "[nuevo titulo]\n" +
                        "</parametro>\n" +
                        "</parametros>\n" +
                        "<etiquetas>\n" +
                        "<etiqueta valor=\"nueva_etiqueta1\"/>\n" +
                        "<etiqueta valor=\"nueva_etiqueta2\"/>\n" +
                        "</etiquetas>\n" +
                        "</accion>\n" +
                        "<accion nombre=\"AGREGAR_COMPONENTE\">\n" +
                        "<parametros>\n" +
                        "<parametro nombre=\"ID\">\n" +
                        "[comp-1]\n" +
                        "</parametro>\n" +
                        "<parametro nombre=\"PAGINA\">\n" +
                        "[productos]\n" +
                        "</parametro>\n" +
                        "<parametro nombre=\"CLASE\">\n" +
                        "[TITULO]\n" +
                        "</parametro>\n" +
                        "</parametros>\n" +
                        "<atributos>\n" +
                        "<atributo nombre=\"TEXTO\">\n" +
                        "[Este es el texto que aparece en el titulo :) ]\n" +
                        "</atributo>\n" +
                        "<atributo nombre=\"COLOR\">\n" +
                        "[#5A5A5A]\n" +
                        "</atributo>\n" +
                        "</atributos>\n" +
                        "</accion>\n" +
                        "<acciones>";

        XmlAnalyzer flex=new XmlAnalyzer(new BufferedReader(new StringReader(entrada)));
        parser p=new parser(flex);
        try {
            p.parse();
            System.out.println("Analisis realizado...");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
