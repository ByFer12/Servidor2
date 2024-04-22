
/* codigo de usuario */
package com.server.xmlflexcup;

import java_cup.runtime.*;
import java.util.ArrayList;
%% //separador de area
%{
    public static ArrayList<String> erroresLexicos=new ArrayList();

  public static String mensajeErrorLexico;
%}
/* opciones y declaraciones de jflex */
%public
%class XmlAnalyzer
%cup
%line
%column

LineTerminator = \r|\n|\r\n

WhiteSpace = {LineTerminator} | [ \t\f]

/* integer literals */



%{

  private Symbol symbol(int type) {
    return new Symbol(type, yyline+1, yycolumn+1);
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline+1, yycolumn+1, value);
  }

  private void error(String message) {
    System.out.println("Error en linea line "+(yyline+1)+", columna "+(yycolumn+1)+" : "+message);
  }
%}

%% // separador de areas

<YYINITIAL>{

   [aA][cC][cC][iI][oO][nN]         { return symbol(sym.ACCION, yytext()); }
	[aA][cC][cC][iI][oO][nN][eE][sS]    { return symbol(sym.ACCIONES, yytext()); }
    "nombre" { return symbol(sym.NOMBRE,yytext()); }
     [pP][aA][rR][aA][mM][eE][tT][rR][oO] { return symbol(sym.PARAMETRO, yytext()); }
     [pP][aA][rR][aA][mM][eE][tT][rR][oO][sS] { return symbol(sym.PARAMETROS, yytext()); }
	[eE][tT][iI][qQ][uU][eE][tT][aA]  { return symbol(sym.ETIQUETA, yytext()); }
     [eE][tT][iI][qQ][uU][eE][tT][aA][sS] { return symbol(sym.ETIQUETAS, yytext()); }

     [aA][tT][rR][iI][bB][uU][tT][oO]  { return symbol(sym.ATRIBUTO, yytext()); }
	 [aA][tT][rR][iI][bB][uU][tT][oO][sS] { return symbol(sym.ATRIBUTOS, yytext()); }
     "valor"     {return symbol(sym.VALOR,yytext());}
	"="		{ return symbol(sym.IGUAL,yytext());}

    "/"		{ return symbol(sym.CIERRE,yytext());}

    ">"     {return symbol(sym.MAY,yytext());}
    "<"     {return symbol(sym.MEN,yytext());}

    \"[^\"]*\"  {return symbol(sym.STRING,yytext());}

	{WhiteSpace} 	{/* ignoramos */}
    \[[^\]]*\] { return  symbol(sym.TEXTO, yytext()); }




    [^] {System.out.println("Error lexico: en linea: "+yyline+ " Columna: "+yycolumn+" "+yytext());


        mensajeErrorLexico="Error Lexico, token no reconocido: "+yytext()+" en fila: "+yyline+" y columna: "+yycolumn;
        erroresLexicos.add(mensajeErrorLexico);

      }
      }