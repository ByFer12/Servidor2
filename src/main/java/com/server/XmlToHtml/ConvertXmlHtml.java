package com.server.XmlToHtml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ConvertXmlHtml {
    private static Document doc;

    public static void convertXmlToHtml() {
        try {
            File xml = new File("PaginasWeb/paginas.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(xml);
            doc.getDocumentElement().normalize();
            NodeList listaNodos = doc.getElementsByTagName("SITIO");
            //procesamos cada etiqueta SITIO y lo compararemos con su ID
            for (int i = 0; i < listaNodos.getLength(); i++) {
                Node nodo = listaNodos.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element siteElement = (Element) nodo;
                    procesarSitio(siteElement);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void procesarSitio(Element site) throws IOException {
        String idSite = site.getAttribute("ID");

        File carpeta = new File("Sitios/"+idSite);
        carpeta.mkdir();
        NodeList lista = site.getElementsByTagName("pagina");
        for (int i = 0; i < lista.getLength(); i++) {
            Node paginaNode = lista.item(i);
            if (paginaNode.getNodeType() == Node.ELEMENT_NODE) {
                Element pagina = (Element) paginaNode;
                procesarPagina(idSite, pagina);
            }
        }

    }

    private static void procesarPagina(String idSite, Element pagina) throws IOException {
        String idPage = pagina.getAttribute("ID").substring(1);
        File htmlFile = new File("Sitios/"+idSite + "/" + idPage + ".html");
        FileWriter htmlWriter = new FileWriter(htmlFile);

        String titulo = obtenerContenido(pagina, "TITULO");
        String eti = obtenerEtiquetas(pagina, "etiqueta", idPage).toString();
        eti = eti.substring(0, eti.length() - 2);

        htmlWriter.write("<!DOCTYPE html>\n");
        htmlWriter.write("<html>\n");
        htmlWriter.write("<head>\n");
        htmlWriter.write("<title>" + escapeHtml(titulo) + "</title>\n");
        htmlWriter.write("<meta name=\"tags\" content=\"" + eti + " \">");
        htmlWriter.write("</head>\n");
        htmlWriter.write("<body>\n");

        // Procesar componentes de la página actual
        NodeList componenteList = pagina.getElementsByTagName("componentes");
        for (int i = 0; i < componenteList.getLength(); i++) {
            Node componenteNode = componenteList.item(i);
            if (componenteNode.getNodeType() == Node.ELEMENT_NODE) {
                Element componenteElement = (Element) componenteNode;
                String idCOm = componenteElement.getAttribute("ID");
                Node padre = componenteElement.getParentNode();
                Element padree = (Element) padre;
                String idPadre = padree.getAttribute("ID").substring(1);
                if (idPadre.equals(idPage)) {

                    procesarComponente(htmlWriter, componenteElement);
                }
            }
        }

        // Cerrar etiquetas HTML de la página actual
        htmlWriter.write("</body>\n");
        htmlWriter.write("</html>\n");

        // Cerrar el archivo HTML de la página actual
        htmlWriter.close();
    }

    private static void procesarComponente(FileWriter writer, Element componenteElement) throws IOException {
        String clase = obtenerContenido(componenteElement, "CLASE");

        String texto = obtenerContenido(componenteElement, "TEXTO");

        String color = obtenerContenido(componenteElement, "COLOR");
        String align = obtenerContenido(componenteElement, "ALINEACION");
        String src = obtenerContenido(componenteElement, "ORIGEN");
        String anch = obtenerContenido(componenteElement, "ANCHO");
        String alto = obtenerContenido(componenteElement, "ALTURA");
        String COLOR = "color:" + color + ";";
        if (align.equals("CENTRAR")) {
            align = "center";
        } else if (align.equals("IZQUIERDA")) {
            align = "left";
        } else if (align.equals("DERECHA")) {
            align = "right";
        } else if (align.equals("JUSTIFICAR")) {
            align = "justify";
        }
        String ALIGN = "text-align:" + align + ";";

        String pad = obtenerContenido(componenteElement, "PADRE");
        String eti = obtenerContenido(componenteElement, "ETIQUETA");
        if (color == "") {
            COLOR = "";
        } else if (align == "") {
            ALIGN = "";
        }
        String total = COLOR + " " + ALIGN;
        // Generar HTML según la clase del componente
        if ("TITULO".equals(clase)) {
            writer.write("<h1 style=\"" + total + "\">" + escapeHtml(texto) + "</h1>\n");
        }
        if ("PARRAFO".equals(clase)) {
            writer.write("<p style=\"" + total + "\">" + escapeHtml(texto) + "</p>");
        }

        if ("IMAGEN".equals(clase)) {
            writer.write("<div style=\"" + ALIGN + "\" >\n");
            writer.write("<img src=\"" + src + "\" height=\"" + alto + "px\"    width=\"" + anch + "px\"/>");
            writer.write("</div>");
        }
        if ("VIDEO".equals(clase)) {
            writer.write("<video src=\"" + src + "\" controls height=\"" + alto + "px\"    width=\"" + anch + "px\"/>");
            writer.write("</video>");
        }
        if ("MENU".equals(clase)) {
            writer.write("<nav>");
            writer.write("<ul>");
            procesarListaMenu(pad, eti, writer);
            writer.write("</ul>");
            writer.write("</nav>");
        }
    }

    private static void procesarListaMenu(String padrePag, String eti, FileWriter writer) throws IOException {
        ArrayList<Element> hijasPag = new ArrayList<>();
        NodeList listaNodos = doc.getElementsByTagName("SITIO");
        //procesamos cada etiqueta SITIO y lo compararemos con su ID
        for (int i = 0; i < listaNodos.getLength(); i++) {
            Node nodo = listaNodos.item(i);
            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                Element siteElement = (Element) nodo;
                String sitioId = siteElement.getAttribute("ID");
                // Obtener la lista de nodos <pagina> bajo <paginas> de este <SITIO>
                NodeList paginaList = siteElement.getElementsByTagName("pagina");
                for (int j = 0; j < paginaList.getLength(); j++) {
                    Node paginaNode = paginaList.item(j);
                    if (paginaNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element paginaElement = (Element) paginaNode;
                        String idPadre = paginaElement.getAttribute("ID");
                        if (eti.isEmpty()) {
                            if (idPadre.equals(padrePag)) {
                                NodeList listaHijas = paginaElement.getElementsByTagName("pagina");
                                for (int k = 0; k < listaHijas.getLength(); k++) {
                                    Node hijo = listaHijas.item(k);
                                    if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                                        Element paginaHija = (Element) hijo;

                                        hijasPag.add(paginaHija);
                                    }
                                }
                                break;
                            }
                        } else {
                            NodeList listaHijas = paginaElement.getElementsByTagName("etiqueta");
                            for (int k = 0; k < listaHijas.getLength(); k++) {
                                Node hijo = listaHijas.item(k);
                                if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                                    Element paginaHija = (Element) hijo;

                                    hijasPag.add(paginaHija);
                                }
                            }
                        }
                    }
                }
                break;
            }
        }

        for (Element pag : hijasPag) {
            String pagS = pag.getAttribute("ID").substring(1);
            NodeList hija = pag.getElementsByTagName("TITULO");
            Node nodo = hija.item(0);
            String titulo = nodo.getTextContent();
            writer.write("<a href=\"" + pagS + ".html\" style=\"background-color:#FF5733; text-decoration: none; color:#ffffff; padding: 6px; border-radius:3px \"> Ir a la pagina: " + titulo + "</a><br><hr style=\"text-align: left; margin-left: 0; border: 3px solid #ccc;  width: 20%;\">");
        }


    }

    private static String obtenerContenido(Element pagina, String titulo) {
        NodeList nodos = pagina.getElementsByTagName(titulo);
        if (nodos.getLength() > 0) {
            Node nodo = nodos.item(0);
            return nodo.getTextContent();
        }
        return "";
    }

    private static StringBuilder obtenerEtiquetas(Element pagina, String etiquetas, String idPage) {
        StringBuilder eti = new StringBuilder();
        NodeList nodos = pagina.getElementsByTagName(etiquetas);
        for (int i = 0; i < nodos.getLength(); i++) {
            Node nodo = nodos.item(i);
            Node padre = nodo.getParentNode();
            Node ab = padre.getParentNode();
            Element abu = (Element) ab;
            String idPadre = abu.getAttribute("ID");
            idPadre = idPadre.substring(1);
            if (idPage.equals(idPadre)) {
                eti.append(nodo.getTextContent() + ", ");
            }
        }

        return eti;
    }

    private static String escapeHtml(String str) {
        // Método de escape para evitar inyecciones de HTML
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
