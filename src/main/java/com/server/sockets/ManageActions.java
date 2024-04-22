package com.server.sockets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;


public class ManageActions {
    LocalDate fechaActual = LocalDate.now();
    ArrayList<String>ides=new ArrayList<>();
    public void newSitio(String acction, Map<String, String> param) {
        ServidorSer.messege = "Tipo de accion realizada \"" + acction + "\"";
        String hooa;
        ServidorSer.mensajes.add(ServidorSer.messege);
        try {
            File archivo = new File("PaginasWeb/paginas.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);
            // Crear el elemento raíz <acciones>

            Element raiz = doc.getDocumentElement();

            Element sitio = doc.createElement("SITIO");
            sitio.setAttribute("ID", param.get("ID"));
            raiz.appendChild(sitio);
            // Crear el elemento <accion> y agregar atributo nombre
            Element datosUsuario = doc.createElement("datosUsuario");
            sitio.appendChild(datosUsuario);


            // Agregar los elementos <parametro> dentro de <parametros>
            //en esta parte me falta extraer los parametros en oaram oara ver que elementos van aqui a excepcion de ID
            if (param.containsKey("USUARIO_CREACION")) {
                agregarParametro(doc, datosUsuario, "USUARIO_CREACION", param.get("USUARIO_CREACION"));
            } else {
                agregarParametro(doc, datosUsuario, "USUARIO_CREACION", ServidorSer.usuario);
            }
            if (param.containsKey("FECHA_CREACION")) {
                agregarParametro(doc, datosUsuario, "FECHA_CREACION", param.get("FECHA_CREACION"));
            } else {
                agregarParametro(doc, datosUsuario, "FECHA_CREACION", fechaActual.toString());
            }
            if (param.containsKey("FECHA_MODIFICACION")) {
                agregarParametro(doc, datosUsuario, "FECHA_MODIFICACION", param.get("FECHA_MODIFICACION"));
            } else {
                agregarParametro(doc, datosUsuario, "FECHA_MODIFICACION", fechaActual.toString());
            }
            if (param.containsKey("USUARIO_MODIFICACION")) {
                agregarParametro(doc, datosUsuario, "USUARIO_MODIFICACION", param.get("USUARIO_MODIFICACION"));
            } else {
                agregarParametro(doc, datosUsuario, "USUARIO_MODIFICACION", ServidorSer.usuario);
            }
            Element paginas = doc.createElement("paginas");
            //Element indexPage=doc.createElement("pagina");
            //indexPage.setAttribute("ID","index");
            //paginas.appendChild(indexPage);
            //paginas.appendChild(doc.createTextNode("HOla"));
            sitio.appendChild(paginas);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(archivo);
            transformer.transform(source, result);
            String mensaje = "Sitio web creado correctamente, archivo xml creado";
            ServidorSer.mensajes.add(mensaje);

        } catch (ParserConfigurationException | IOException e) {
            String mensaje = "Error al procesar el crear el archivo xml: " + e.getMessage();
            ServidorSer.mensajes.add(mensaje);


        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para agregar un elemento <parametro> con un nombre y valor dado
    public void deleteSitio(String acction, Map<String, String> param) {
        ServidorSer.messege = "Tipo de accion realizada \"" + acction + "\"";
        ServidorSer.mensajes.add(ServidorSer.messege);
        String ID = param.get("ID");
        File file = new File("PaginasWeb/paginas.xml");
        if (file.delete()) {
            String mensaje = "El Sitio web se ha eliminado correctamente, archivo xml Borrado";
            ServidorSer.mensajes.add(mensaje);
        } else {
            String mensaje = "Sitio web No se ha podido borrar";
            ServidorSer.errors.add(mensaje);
        }
    }

    public void newPage(String acction, Map<String, String> param, Map<Integer, String> tag) {
        boolean sit = false;

        ServidorSer.messege = "Tipo de accion realizada \"" + acction + "\"";
        ServidorSer.mensajes.add(ServidorSer.messege);
        int num = 0;
        File archivo = new File("PaginasWeb/paginas.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            if (archivo.exists()) {
                Document doc = dBuilder.parse(archivo);
                doc.getDocumentElement().normalize();
                NodeList listSite = doc.getElementsByTagName("SITIO");
                for (int i = 0; i < listSite.getLength(); i++) {
                    Node siteNode = listSite.item(i);
                    if (siteNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element sitioooo = (Element) siteNode;
                        String idSitio = sitioooo.getAttribute("ID");
                        if (idSitio.equals(param.get("SITIO"))) {
                            sit = true;

                            if (!insertarPagina(param.get("PADRE"), sitioooo, param, tag)) {
                                Element pagins = (Element) sitioooo.getElementsByTagName("paginas").item(0);


                                Element pagina = doc.createElement("pagina");
                                pagina.setAttribute("ID", param.get("ID"));

                                Element parametro = doc.createElement("parametros");

                                pagina.appendChild(parametro);
                                //agregarParametro(doc,parametro,"ID",param.get("ID"));
                                agregarParametro(doc, parametro, "SITIO", param.get("SITIO"));
                                agregarParametro(doc, parametro, "PADRE", param.get("PADRE"));
                                if (param.containsKey("TITULO")) {
                                    agregarParametro(doc, parametro, "TITULO", param.get("TITULO"));
                                } else {
                                    num++;
                                    agregarParametro(doc, parametro, "TITULO", "Titulo" + num);
                                }
                                if (param.containsKey("USUARIO_CREACION")) {
                                    agregarParametro(doc, parametro, "USUARIO_CREACION", param.get("USUARIO_CREACION"));
                                } else {

                                    agregarParametro(doc, parametro, "USUARIO_CREACION", ServidorSer.usuario);
                                }
                                if (param.containsKey("FECHA_CREACION")) {
                                    agregarParametro(doc, parametro, "FECHA_CREACION", param.get("FECHA_CREACION"));
                                } else {

                                    agregarParametro(doc, parametro, "FECHA_CREACION", fechaActual.toString());
                                }
                                if (param.containsKey("FECHA_MODIFICACION")) {
                                    agregarParametro(doc, parametro, "FECHA_MODIFICACION", param.get("FECHA_MODIFICACION"));
                                } else {

                                    agregarParametro(doc, parametro, "FECHA_MODIFICACION", fechaActual.toString());
                                }
                                if (param.containsKey("USUARIO_MODIFICACION")) {
                                    agregarParametro(doc, parametro, "USUARIO_MODIFICACION", param.get("USUARIO_MODIFICACION"));
                                } else {

                                    agregarParametro(doc, parametro, "USUARIO_MODIFICACION", ServidorSer.usuario);
                                }
                                Element etiquetas = doc.createElement("etiquetas");
                                pagina.appendChild(etiquetas);
                                for (int j = 0; j < tag.size(); j++) {
                                    Element etiqeuta = doc.createElement("etiqueta");
                                    etiqeuta.appendChild(doc.createTextNode(tag.get(j)));
                                    etiquetas.appendChild(etiqeuta);
                                }
                                pagins.appendChild(pagina);
                                TransformerFactory transformer = TransformerFactory.newInstance();
                                Transformer tr = transformer.newTransformer();
                                DOMSource src = new DOMSource(doc);
                                StreamResult res = new StreamResult(new File("PaginasWeb/paginas.xml"));
                                tr.transform(src, res);
                                ServidorSer.messege = "Pagina editado exitosamente";
                                ServidorSer.mensajes.add(ServidorSer.messege);
                                break;

                            } else {

                            }
                        }
                    }
                }
                if (!sit) {
                    String err = "El sitio web al que desea acceder no existe";
                    ServidorSer.errors.add(err);
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean insertarPagina(String padre, Element sitio, Map<String, String> param, Map<Integer, String> tag) {
        boolean correct = false;
        try {
            File archivo = new File("PaginasWeb/paginas.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);
            doc.getDocumentElement().normalize();
            Element pagins = (Element) sitio.getElementsByTagName("paginas").item(0);
            NodeList paginasList = doc.getElementsByTagName("pagina");
            for (int i = 0; i < paginasList.getLength(); i++) {
                Node paginaNode = paginasList.item(i);
                if (paginaNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element paginaElement = (Element) paginaNode;
                    String currentId = paginaElement.getAttribute("ID");
                    Node nodoPadre = paginaElement.getParentNode();
                    Node nodeAb = nodoPadre.getParentNode();
                    Element sit = (Element) nodeAb;
                    String citioID = sit.getAttribute("ID");
                    if (currentId.equals(padre) && citioID.equals(param.get("SITIO"))) {
                        Element nuevaPagina = doc.createElement("pagina");
                        nuevaPagina.setAttribute("ID", param.get("ID"));

                        Element parametros = doc.createElement("parametros");
                        nuevaPagina.appendChild(parametros);
                        agregarParametro(doc, parametros, "SITIO", param.get("SITIO"));
                        agregarParametro(doc, parametros, "PADRE", padre);


                        if (param.containsKey("TITULO")) {
                            agregarParametro(doc, parametros, "TITULO", param.get("TITULO"));
                        } else {

                            agregarParametro(doc, parametros, "TITULO", "Titulo nuevo");
                        }
                        if (param.containsKey("USUARIO_CREACION")) {
                            agregarParametro(doc, parametros, "USUARIO_CREACION", param.get("USUARIO_CREACION"));
                        } else {

                            agregarParametro(doc, parametros, "USUARIO_CREACION", ServidorSer.usuario);
                        }
                        if (param.containsKey("FECHA_CREACION")) {
                            agregarParametro(doc, parametros, "FECHA_CREACION", param.get("FECHA_CREACION"));
                        } else {

                            agregarParametro(doc, parametros, "FECHA_CREACION", fechaActual.toString());
                        }
                        if (param.containsKey("FECHA_MODIFICACION")) {
                            agregarParametro(doc, parametros, "FECHA_MODIFICACION", param.get("FECHA_MODIFICACION"));
                        } else {

                            agregarParametro(doc, parametros, "FECHA_MODIFICACION", fechaActual.toString());
                        }
                        if (param.containsKey("USUARIO_MODIFICACION")) {
                            agregarParametro(doc, parametros, "USUARIO_MODIFICACION", param.get("USUARIO_MODIFICACION"));
                        } else {

                            agregarParametro(doc, parametros, "USUARIO_MODIFICACION", ServidorSer.usuario);
                        }

                        Element etiquetas = doc.createElement("etiquetas");
                        nuevaPagina.appendChild(etiquetas);
                        for (int j = 0; j < tag.size(); j++) {
                            Element etiqueta = doc.createElement("etiqueta");
                            etiqueta.appendChild(doc.createTextNode(tag.get(j)));
                            etiquetas.appendChild(etiqueta);
                        }
                        //paginaNode.getParentNode().appendChild(nuevaPagina);
                        paginaElement.appendChild(nuevaPagina);

                        // Guardar el documento XML modificado
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(doc);
                        StreamResult result = new StreamResult(new File("PaginasWeb/paginas.xml"));
                        transformer.transform(source, result);
                        correct = true;
                        System.out.println("Página insertada exitosamente.");
                        return correct;
                    }
                }
            }
            if (!correct) {
                String err = "ELa pagina al que desea acceder no existe";
                ServidorSer.errors.add(err);
            } else {
                String err = "Pagina agregada exitosamente";
                ServidorSer.mensajes.add(err);
            }
            System.out.println("No se encontró ninguna página con el ID " + padre);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return correct;
    }

    public void deletePage(String acction, Map<String, String> param) {
        boolean bol = false;
        ServidorSer.messege = "Tipo de accion realizada \"" + acction + "\"";
        ServidorSer.mensajes.add(ServidorSer.messege);
        try {
            File archivo = new File("PaginasWeb/paginas.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);
            doc.getDocumentElement().normalize();

            NodeList paginas = doc.getElementsByTagName("pagina");
            for (int i = 0; i < paginas.getLength(); i++) {
                Node paginaNodo = paginas.item(i);
                if (paginaNodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element paginaElemento = (Element) paginaNodo;
                    String atributoID = paginaElemento.getAttribute("ID");
                    System.out.println("ESTA ES LA PAGINA A ELIMINAR CON ID = " + atributoID);
                    if (atributoID.equals(param.get("ID"))) {
                        paginaNodo.getParentNode().removeChild(paginaNodo);
                        bol = true;
                    }
                }
            }
            if (!bol) {
                String error = "La pagina que desea aliminar no existe";
                ServidorSer.errors.add(error);
            } else {
                String mes = "Pagina Borrada exitosamente";
                ServidorSer.mensajes.add(mes);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(archivo);
            transformer.transform(source, result);

        } catch (Exception e) {

        }
    }

    public void newComponent(String action, Map<String, String> param, Map<String, String> attr) {
        boolean sit = false;
        ServidorSer.messege = "Tipo de accion realizada \"" + action + "\"";
        ServidorSer.mensajes.add(ServidorSer.messege);
        int num = 0;
        File archivo = new File("PaginasWeb/paginas.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            if (archivo.exists()) {
                Document doc = dBuilder.parse(archivo);
                doc.getDocumentElement().normalize();
                NodeList listSite = doc.getElementsByTagName("pagina");
                for (int i = 0; i < listSite.getLength(); i++) {
                    Node siteNode = listSite.item(i);
                    if (siteNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element sitioooo = (Element) siteNode;
                        String idSitio = sitioooo.getAttribute("ID");
                        if (idSitio.equals(param.get("PAGINA") )) {
                            if(!ides.contains(param.get("ID"))){

                            sit = true;
                            Element pagina = doc.createElement("componentes");
                            pagina.setAttribute("ID", param.get("ID"));
                            Element parametro = doc.createElement("parametros");
                            pagina.appendChild(parametro);
                            ides.add(param.get("ID"));
                            //agregarParametro(doc,parametro,"ID",param.get("ID"));
                            agregarParametro(doc, parametro, "PAGINA", param.get("PAGINA"));
                            agregarParametro(doc, parametro, "CLASE", param.get("CLASE"));
                            Element atributos = doc.createElement("atributos");
                            pagina.appendChild(atributos);
                            for (Map.Entry<String, String> attri : attr.entrySet()) {
                                Element atributo = doc.createElement(attri.getKey());
                                atributo.appendChild(doc.createTextNode(attri.getValue()));
                                atributos.appendChild(atributo);
                            }
                            sitioooo.appendChild(pagina);
                            TransformerFactory transformer = TransformerFactory.newInstance();
                            Transformer tr = transformer.newTransformer();
                            DOMSource src = new DOMSource(doc);
                            StreamResult res = new StreamResult(new File("PaginasWeb/paginas.xml"));
                            tr.transform(src, res);
                            ServidorSer.messege = "Pagina editado exitosamente";
                            ServidorSer.mensajes.add(ServidorSer.messege);
                            break;
                        }else{
                                String err = "El ID del componente ya existe para esta pagina";
                                ServidorSer.errors.add(err);
                            }
                        }
                    }
                }
                if (!sit) {
                    String err = "La pagina web al que desea acceder no existe";
                    ServidorSer.errors.add(err);
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteComponente(String action, Map<String, String> param) {
        boolean bol = false;
        ServidorSer.messege = "Tipo de accion realizada \"" + action + "\"";
        ServidorSer.mensajes.add(ServidorSer.messege);
        try {
            File archivo = new File("PaginasWeb/paginas.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);
            doc.getDocumentElement().normalize();

            NodeList paginas = doc.getElementsByTagName("componentes");
            for (int i = 0; i < paginas.getLength(); i++) {
                Node paginaNodo = paginas.item(i);
                if (paginaNodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element paginaElemento = (Element) paginaNodo;
                    Node padre=paginaElemento.getParentNode();
                    Element pag=(Element)padre;
                    String idPag=pag.getAttribute("ID");
                    String atributoID = paginaElemento.getAttribute("ID");
                    System.out.println("ESTA ES LA PAGINA A ELIMINAR COMPONENTE CON ID = " + atributoID);
                    if (atributoID.equals(param.get("ID"))&&idPag.equals(param.get("PAGINA"))) {
                        paginaNodo.getParentNode().removeChild(paginaNodo);
                        bol = true;
                    }
                }
            }
            if (!bol) {
                String error = "La pagina que desea aliminar no existe";
                ServidorSer.errors.add(error);
            } else {
                String mes = "Pagina Borrada exitosamente";
                ServidorSer.mensajes.add(mes);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(archivo);
            transformer.transform(source, result);

        } catch (Exception e) {

        }
    }

    public void modifyComponent(String action, Map<String,String>params, Map<String, String> attr){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File("PaginasWeb/paginas.xml"));
            String idPag=params.get("PAGINA");
            Node paginaNode = buscarPaginaPorID(doc, idPag);
            if (paginaNode != null) {
                // Obtener los nodos de componentes de la página
                NodeList componentesList = paginaNode.getChildNodes();

                // Buscar el componente con el ID deseado y realizar la modificación
                for (int i = 0; i < componentesList.getLength(); i++) {
                    Node componenteNode = componentesList.item(i);
                    if (componenteNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element componenteElement = (Element) componenteNode;
                        String componenteID = componenteElement.getAttribute("ID");

                        if (componenteID.equals(params.get("ID"))) {
                            Element parametros=(Element)componenteElement.getElementsByTagName("parametros").item(0);
                            Element param=(Element) parametros.getElementsByTagName("CLASE").item(0);
                            param.setTextContent(params.get("CLASE"));

                            // Realizar la modificación del componente
                            // Por ejemplo, cambiar el texto del componente
                            Element atributos = (Element) componenteElement.getElementsByTagName("atributos").item(0);
                            Element textoElement = (Element) atributos.getElementsByTagName("TEXTO").item(0);
                            textoElement.setTextContent(attr.get("TEXTO"));
                            if(attr.containsKey("ALINEACION") && !attr.get("ALINEACION").isEmpty()){
                                Element alineacion = (Element) atributos.getElementsByTagName("ALINEACION").item(0);
                                alineacion.setTextContent(attr.get("ALINEACION"));
                            }
                            if(attr.containsKey("COLOR") && !attr.get("COLOR").isEmpty()){
                                Element color = (Element) atributos.getElementsByTagName("COLOR").item(0);
                                color.setTextContent(attr.get("COLOR"));
                            }
                            if(attr.containsKey("ORIGEN") && !attr.get("ORIGEN").isEmpty()){
                                Element origen = (Element) atributos.getElementsByTagName("ORIGEN").item(0);
                                origen.setTextContent(attr.get("ORIGEN"));
                            }
                            if(attr.containsKey("ALTURA") && !attr.get("ALTURA").isEmpty()){
                                Element altura = (Element) atributos.getElementsByTagName("ALTURA").item(0);
                                altura.setTextContent(attr.get("ALTURA"));
                            }
                            if(attr.containsKey("ANCHO") && !attr.get("ANCHO").isEmpty()){
                                Element ancho = (Element) atributos.getElementsByTagName("ANCHO").item(0);
                                ancho.setTextContent(attr.get("ANCHO"));
                            }
                        }
                    }
                }

                // Guardar los cambios de vuelta al archivo XML
                guardarCambios(doc,"PaginasWeb/paginas.xml");
                System.out.println("Componente modificado exitosamente.");
            } else {
                System.out.println("No se encontró la página con ID: " + idPag);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private static void guardarCambios(Document doc, String filePath) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }
    // Método para buscar una página por su ID
    private static Node buscarPaginaPorID(Document doc, String idPagina) {
        NodeList paginas = doc.getElementsByTagName("pagina");
        for (int i = 0; i < paginas.getLength(); i++) {
            Node paginaNode = paginas.item(i);
            if (paginaNode.getNodeType() == Node.ELEMENT_NODE) {
                Element paginaElement = (Element) paginaNode;
                String paginaID = paginaElement.getAttribute("ID");
                if (paginaID.equals(idPagina)) {
                    return paginaNode;
                }
            }
        }
        return null;
    }

    private static void agregarParametro(Document doc, Element datosUsuario, String nombre, String valor) {
        Element parametro = doc.createElement(nombre);
        parametro.appendChild(doc.createTextNode(valor));
        datosUsuario.appendChild(parametro);
    }

    private static String convertirDocumentATexto(Document doc) {
        try {
            javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom.DOMSource(doc);
            java.io.StringWriter writer = new java.io.StringWriter();
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(writer);
            javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (javax.xml.transform.TransformerException e) {
            e.printStackTrace();
            return null;
        }
    }


}
