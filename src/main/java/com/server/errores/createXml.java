package com.server.errores;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class createXml {

    public static void createXml() {
        try {
            // Crear la carpeta "PaginasWeb" si no existe
            File carpeta = new File("PaginasWeb");
            if (!carpeta.exists()) {
                carpeta.mkdir();
            }

            // Crear el archivo "sitios.xml" dentro de la carpeta "PaginasWeb"
            File archivo = new File("PaginasWeb/paginas.xml");
            if (!archivo.exists()) {
                archivo.createNewFile();


                // Crear el documento XML
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder;
                try {
                    dBuilder = dbFactory.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    System.err.println("Error al crear el DocumentBuilder: " + e.getMessage());
                    return;
                }
                Document doc = dBuilder.newDocument();

                // Crear el elemento raíz "sitiosWeb"
                Element raiz = doc.createElement("sitiosWeb");
                doc.appendChild(raiz);

                // Guardar el documento XML
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer;
                try {
                    transformer = transformerFactory.newTransformer();
                } catch (TransformerException e) {
                    System.err.println("Error al crear el Transformer: " + e.getMessage());
                    return;
                }
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(archivo);
                transformer.transform(source, result);

                System.out.println("Archivo XML creado exitosamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
