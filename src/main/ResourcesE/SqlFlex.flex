/* codigo de usuario */
package com.server.SqlFlexyCup;

import java_cup.runtime.*;
import java.util.LinkedList;

%% //separador de area

%{
    public static LinkedList<String> erroresLexicos=new LinkedList<>();
%}

/* opciones y declaraciones de jflex */
%public
%class SqlLexer
%cup
%line
%unicode
%column
LineTerminator = \r|\n|\r\n

WhiteSpace = {LineTerminator} | [ \t\f]

/* integer literals */
   STRING_LITERAL = \"[^\"]*\"




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

/* reglas lexicas */
<YYINITIAL>{

  {STRING_LITERAL}         {return symbol(sym.STR,yytext());}
	"consultar"  { return symbol(sym.COLSULT, yytext()); }
    "visitas_sitio" { return symbol(sym.VISITSITE,yytext()); }
    "visitas_pagina" { return symbol(sym.VISITPAGE,yytext()); }
    "paginas_populares"     {return symbol(sym.POPULAR,yytext());}
    "componente"  {return symbol(sym.COMPONENT,yytext());}
    "todo" {return symbol(sym.TODOS,yytext());}
    "parrafo" {return symbol(sym.PARRAFO,yytext());}
    "titulo" {return symbol(sym.TITULO,yytext());}
    "imagen" {return symbol(sym.IMG,yytext());}
    "video" {return symbol(sym.VID,yytext());}
    "menu" {return symbol(sym.MENU,yytext());}
    ","		{ return symbol(sym.COMA,yytext());}
    ";"     {return symbol(sym.PCOMA,yytext());}
   //{STRING_LITERAL}  {return symbol(sym.STR,yytext());}


	{WhiteSpace} 	{/* ignoramos */}

    [^] {System.out.println("Error lexico: "+yytext());
      String error="Error Lexico, token no reconocido: "+yytext()+" en fila: "+yyline+" y columna: "+yycolumn;
      erroresLexicos.add(error);

      }


      }