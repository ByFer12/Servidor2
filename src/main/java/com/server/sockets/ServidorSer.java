
package com.server.sockets;

import com.server.consult.VisitSite;
import com.server.xmlflexcup.XmlAnalyzer;
import com.server.xmlflexcup.parser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServidorSer implements Runnable{
    ManageActions actionsss;
    private Socket con;
    public static String usuario;
    public static String messege;
    public static ArrayList<String>errors=new ArrayList<>();
    public static ArrayList<String>mensajes=new ArrayList<>();
    private static  ArrayList<String>idesComponents=new ArrayList<>();
    private String error;

    public ServidorSer(Socket con){
        this.con=con;
        actionsss=new ManageActions();
    }
    
    @Override
    public void run(){
        try {
            ObjectInputStream entrada=new ObjectInputStream(con.getInputStream());
            ObjectOutputStream salida=new ObjectOutputStream(con.getOutputStream());
            messege=(String)entrada.readObject();
            usuario=(String) entrada.readObject();
            messege=messege.trim();
            System.out.println("Usuario que esta enviando: "+usuario);
            boolean isSQL=false;
            if(!messege.startsWith("<")){
                VisitSite.procesVisitSite(messege);
                System.out.println("Si");
                isSQL=true;
            }
            if(!isSQL) {
                if (messege.endsWith(">")) {
                    // System.out.println(newMessege);
                    XmlAnalyzer flex = new XmlAnalyzer(new BufferedReader(new StringReader(messege)));
                    parser p = new parser(flex);
                    p.parse();

                    if (parser.erroresSintacticos.isEmpty() && XmlAnalyzer.erroresLexicos.isEmpty()) {
                        validar(messege);
                    }
                } else {
                    error = "Error en la sintaxis, falta cierre de > en el ultimo tag";
                    System.out.println(error);
                    errors.add(error);
                }
                salida.writeObject(errors);
                salida.writeObject(parser.erroresSintacticos);
                salida.writeObject(XmlAnalyzer.erroresLexicos);
                salida.writeObject(mensajes);
                errors.clear();
                parser.erroresSintacticos.clear();
                XmlAnalyzer.erroresLexicos.clear();
                mensajes.clear();
                con.close();
            }
        } catch (IOException |ClassNotFoundException  ex) {
            ex.printStackTrace();
            System.out.println("No se encontro clientes...");
        } catch (Exception e) {
           e.printStackTrace();
        }
    }


    public void validar(String XML){
        String patron="^[-_$][a-zA-Z0-9_$-]*";
        try {
            // Creo un DocumentBuilder para procesar el XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder=dbFactory.newDocumentBuilder();
            Document doc= dbBuilder.parse(new InputSource(new StringReader(XML)));

            // Creao un XPathFactory para hacer consultas XPath
            XPathFactory xpathFactory=XPathFactory.newInstance();
            XPath xpath=xpathFactory.newXPath();

            // Obtener la lista de nodos de acción
            XPathExpression accionExpresion= xpath.compile("//*[translate(local-name(), 'ACCION', 'accion') = 'accion']");
            NodeList accionNodos=(NodeList) accionExpresion.evaluate(doc, XPathConstants.NODESET);
            System.out.println("Numero de acciones: "+accionNodos.getLength());

            // Iterar sobre los nodos de acción
            for (int i = 0; i < accionNodos.getLength(); i++) {
                Node acctionNodo=accionNodos.item(i);
                String nombreAccion=acctionNodo.getAttributes().getNamedItem("nombre").getNodeValue();
                System.out.println("Nombre de la accion: "+nombreAccion);

                //Procesamos parametros
                Map<String, String>parametros=getParametros(xpath, doc, acctionNodo);
                System.out.println("Parametros: ");
                for(Map.Entry<String, String>entidad:parametros.entrySet()){
                    System.out.println(" "+entidad.getKey()+" = "+entidad.getValue());
                }

                //Procesamos Etiquetas
                Map<Integer, String>tags=getTags(xpath, doc, acctionNodo);
                System.out.println("Etiquetas: ");
                for (Map.Entry<Integer,String>eti:tags.entrySet()) {
                    System.out.println(" "+eti.getValue());
                }


                //Procesar Atributos
                Map<String,String> attr=getAtributos(xpath, doc, acctionNodo);
                System.out.println("Atributos: ");
                for (Map.Entry<String,String>att:attr.entrySet()){
                    System.out.println(" "+att.getKey()+" "+att.getValue());
                }

                System.out.println(" ");
                if(!parser.isError){
                    System.out.println("se esta procesando....................");
                    procesar(nombreAccion, parametros,tags,attr);
                }

            }
                   }catch (Exception e){
                    error=e.getMessage();
            System.out.println(error);
            errors.add(error);
                    //e.printStackTrace();
        }
    }


    private static Map<String,String> getParametros(XPath xpath, Document doc, Node nodo) throws Exception{
        Map<String, String> parameters = new HashMap<>();
        XPathExpression parameterExpression = xpath.compile("*[local-name()='parametros']/*[local-name()='parametro']");
        NodeList parameterNodes = (NodeList) parameterExpression.evaluate(nodo, XPathConstants.NODESET);
        for (int j = 0; j < parameterNodes.getLength(); j++) {
            Node parameterNode = parameterNodes.item(j);
            String parameterName = parameterNode.getAttributes().getNamedItem("nombre").getNodeValue();
            String parameterValue = parameterNode.getTextContent();
            int index = parameterValue.indexOf("]");
            int index2 = parameterValue.indexOf("[");
            parameterValue = parameterValue.substring(index2+1, index);
            parameters.put(parameterName, parameterValue);
        }
        return parameters;
    }

    private static Map<Integer,String> getTags(XPath xpath, Document doc, Node nodo) throws Exception{
        Map<Integer, String> etiquetas=new HashMap<>();
        XPathExpression expEtiquetas=xpath.compile("*[local-name()='etiquetas']/*[local-name()='etiqueta']");
        NodeList listaEtiquetas=(NodeList) expEtiquetas.evaluate(nodo, XPathConstants.NODESET);
        for (int i = 0; i < listaEtiquetas.getLength(); i++) {
            String etiqueta=listaEtiquetas.item(i).getAttributes().getNamedItem("valor").getNodeValue();
            etiquetas.put(i,etiqueta);
        }
        return etiquetas;
    }

    private static  Map<String, String> getAtributos(XPath xpath,Document doc, Node nodo)throws Exception{
        Map<String, String> attr=new HashMap<>();
        XPathExpression expAtributos=xpath.compile("*[local-name()='atributos']/*[local-name()='atributo']");
        NodeList listaAttr=(NodeList) expAtributos.evaluate(nodo,XPathConstants.NODESET);
        for (int i = 0; i < listaAttr.getLength(); i++) {
            Node nodoAttr=listaAttr.item(i);
            String clave=nodoAttr.getAttributes().getNamedItem("nombre").getNodeValue();
            String valor = nodoAttr.getTextContent();
            int index = valor.indexOf("]");
            int index2 = valor.indexOf("[");
            valor = valor.substring(index2+1, index);
            attr.put(clave,valor);
        }
        return attr;
    }

    public void procesar(String accion, Map<String, String>parametros,Map<Integer, String>tags,Map<String, String>attr){
        switch (accion){
            case "NUEVO_SITIO_WEB":
                String v=parametros.get("ID");
                String r="\n+";
                if(attr.isEmpty() &&tags.isEmpty()){
                    if(parametros.containsKey("ID")&&!v.matches(r)){
                    actionsss.newSitio(accion,parametros);
                    }else{
                        error="El ID debe ser obligatorio";
                        System.out.println(error);
                        errors.add(error);
                    }
                }else{
                    error="Al crear sitios no debes mandar atributos ni etiquetas ";
                    System.out.println(error);
                    errors.add(error);
                }
                break;
            case "BORRAR_SITIO_WEB":
                System.out.println("Estamos en borrar");
                String a=parametros.get("ID");
                String b="\n+";
                if(attr.isEmpty() &&tags.isEmpty()){
                    if(parametros.containsKey("ID")&&!a.matches(b)){
                        if(parametros.size()==1){

                            actionsss.deleteSitio(accion,parametros);
                        }else{
                            error="Solo se necesita el ID, no mas parametros";
                            System.out.println(error);
                            errors.add(error);
                        }
                    }else{
                        error="El ID debe ser obligatorio por tratarse de Borrar sitio";
                        System.out.println(error);
                        errors.add(error);
                    }
                }else{
                    error="Al Borrar sitios no debes mandar atributos ni etiquetas ";
                    System.out.println(error);
                    errors.add(error);
                }
                break;
            case "NUEVA_PAGINA":
                    String valor=parametros.get("ID");
                    String reg="\n+";
                    if(parametros.containsKey("ID")&&parametros.containsKey("SITIO") &&parametros.containsKey("PADRE")&& !valor.matches(reg)&&!parametros.get("SITIO").isEmpty()&&!parametros.get("PADRE").isEmpty()){
                        actionsss.newPage(accion,parametros,tags);
                    }else{
                        error="El ID, SITIO y PADRE deben ser obligatorio";
                        System.out.println(error);
                        errors.add(error);
                    }
                System.out.println("Aqui se ejecuta la Creacion del nueva pagina  web-------------"+parametros.get("ID")+"---------------------------------------------------------3");
                break;
            case "BORRAR_PAGINA":
                System.out.println("Estamos en borrar");
                String c=parametros.get("ID");
                String d="\n+";
                if(attr.isEmpty() &&tags.isEmpty()){
                    if(parametros.containsKey("ID")&&!c.matches(d)){
                        if(parametros.size()==1){
                            System.out.println("Borrando pagina web");
                            actionsss.deletePage(accion,parametros);
                        }else{
                            error="Solo se necesita el ID, no mas parametros";
                            System.out.println(error);
                            errors.add(error);
                        }
                    }else{
                        error="El ID debe ser obligatorio por tratarse de Borrar sitio";
                        System.out.println(error);
                        errors.add(error);
                    }
                }else{
                    error="Al Borrar sitios no debes mandar atributos ni etiquetas ";
                    System.out.println(error);
                    errors.add(error);
                }
                break;
            case "MODIFICAR_PAGINA":
                System.out.println("Aqui se ejecuta la Modificacion de  pagina  web----------------------------------------------------------------------5");
                break;
            case "AGREGAR_COMPONENTE":
                String val=parametros.get("ID").trim();
                String re="\n+";
                if(parametros.containsKey("ID")&&parametros.containsKey("PAGINA") &&parametros.containsKey("CLASE")&& !val.matches(re)&&!parametros.get("PAGINA").isEmpty()&&!parametros.get("CLASE").isEmpty()){

                        System.out.println("\nAgregamos componente");
                        idesComponents.add(val);
                        actionsss.newComponent(accion,parametros,attr);

                }else{
                    error="El ID, PAGINA y CLASE deben ser obligatorio";
                    System.out.println(error);
                    errors.add(error);
                }
                break;
            case "BORRAR_COMPONENTE":
                System.out.println("Estamos en borrar");
                String e=parametros.get("ID");
                String f="\n+";
                if(attr.isEmpty() &&tags.isEmpty()){
                    if(parametros.containsKey("ID")&&!e.matches(f) && parametros.containsKey("PAGINA")){
                        if(parametros.size()==2){
                            System.out.println("Borrando componente web");
                            actionsss.deleteComponente(accion,parametros);
                        }else{
                            error="Solo se necesita el ID y PAGINA, no mas parametros";
                            System.out.println(error);
                            errors.add(error);
                        }
                    }else{
                        error="El ID y PAGINA deben ser obligatorios por tratarse de Borrar Componente";
                        System.out.println(error);
                        errors.add(error);
                    }
                }else{
                    error="Al Borrar sitios no debes mandar atributos ni etiquetas ";
                    System.out.println(error);
                    errors.add(error);
                }
                break;
            case "MODIFICAR_COMPONENTE":
                String g=parametros.get("ID").trim();
                String h="\n+";
                if(parametros.containsKey("ID")&&parametros.containsKey("PAGINA") &&parametros.containsKey("CLASE")&& !g.matches(h)&&!parametros.get("PAGINA").isEmpty()&&!parametros.get("CLASE").isEmpty()){

                        System.out.println("\nAgregamos componente");
                        idesComponents.add(g);
                        actionsss.modifyComponent(accion,parametros,attr);

                }else{
                    error="El ID, PAGINA y CLASE deben ser obligatorio";
                    System.out.println(error);
                    errors.add(error);
                }
                break;
        }

    }
    
}
