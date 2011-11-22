package Clases;

import java.util.ArrayList;
import Clases.Parser.*;

/* @author Leonardo Gutiérrez Ramírez <leogutierrezramirez.gmail.com> */
/* Nov 20, 2011 */
public class Parser {
    
    public static enum TokenType {
       ENDFILE, ERROR, IF, THEN, ELSE, END, REPEAT, UNTIL, READ, WRITE, ID, NUM, ASSIGN, EQ, LT, PLUS,
       MINUS, TIMES, OVER, LPARENT, RPARENT, SEMI, POW, COMA, MOD, LEQ, GEQ, GT, NEQ, NOT, COMP, AND, OR
    };
   
    public static enum NodeKind {
        StmtK, ExpK
     };
    
    public static enum StmtKind {
        IfK, RepeatK, AssignK, ReadK, WriteK
    };
    
    public static enum ExpKind {
        OpK, ConstK, IdK
    };
    
    // Enumeración para verificación de Tipo.
    public static enum ExpType {
        Void, Integer, Boolean
    };
    
    public static void indent() {
        // indentno declarada más abajo.
        indentno += 2;
    }
    
    public static void unindent() {
        indentno -= 2;
    }
    
    public static void printSpaces() {
        for(int i = 0; i < indentno; i++)
            // Dirigir a un flujo...
            System.out.print(" ");
    }
    
    public static void printTree(NodoArbol tree) {
        
        int i;
        indent();
        while(tree != null) {
            printSpaces();
            if(tree.nodeKind == NodeKind.StmtK) {
                switch(tree.stmt) {
                    case IfK:
                        System.out.println("If");
                        break;
                    
                    case RepeatK:
                        System.out.println("Repeat");
                        break;
                    case AssignK:
                        System.out.println("Asignado a: " + tree.nombre);
                        break;
                    case ReadK:
                        System.out.println("Read: " + tree.nombre);
                        break;
                    case WriteK:
                        System.out.println("Write");
                        break;
                    default:
                        System.out.println("Nodo desconocido");
                }
            } else if(tree.nodeKind == NodeKind.ExpK) {
                
                switch(tree.exp) {
                    case OpK:
                        System.out.print("op: ");
                        printToken(tree.op, "\\0");
                        break;
                    case ConstK:
                        System.out.println("const: " + tree.valor);
                        break;
                    case IdK:
                        System.out.println("id: " + tree.nombre);
                        break;
                    default:
                        System.out.println("Nodo desconocido");
                        break;
                }
                
            } else {
                System.out.println("Nodo desconocido");
            }
            
            // Llamar recursivamente a la función.
            for(int j = 0; j < MAXCHILDREN; j++)
                printTree(tree.child[j]);
            
            tree = tree.sibling;
            
        }
        
        unindent();
        
    }
    
    private ArrayList<Lexema> palabras = null;   
    public static final int MAXRESERVED = 8;
    public static final int MAXCHILDREN = 3;
    public static int indentno = 0;
    private int indice = 0;
    
    // Java Beans a esto.
    public static TokenType token;              // El token actual.
    public static String tokenString;
    // JavaBeans a esto.
    public static int lineno = 0;
   
   // Just JavaBeans conventions...

   public Parser() {
       palabras = null;
       indice = 0;
   }
    
   public Parser(ArrayList<Lexema> palabras) {
       this.palabras = palabras;
       indice = 0;
   }
   
   public void setLexemas(ArrayList<Lexema> palabras) {
       this.palabras = palabras;
   }
   
   public ArrayList<Lexema> getListaLexemas() {
       return palabras;
   }
   
   // Por si uncluyeramos un flag para ver el proceso.
   /***************************************************************************/
   public static void printToken(TokenType token, String tokenString) {
       switch(token) {
           case IF:
           case THEN:
           case ELSE:
           case END:
           case REPEAT:
           case UNTIL:
           case READ:
           case WRITE:
               // Escribir en un archivo.
               System.out.println("palabra reservada: " + tokenString);
               break;
           case ASSIGN:
               System.out.println(":=");
               break;
           case LT:
               System.out.println("<");
               break;
           case EQ:
               System.out.println("==");
               break;
           case LPARENT:
               System.out.println("(");
               break;
           case RPARENT:
               System.out.println(")");
               break;
           case SEMI:
               System.out.println(";");
               break;
           case PLUS:
               System.out.println("+");
               break;
           case MINUS:
               System.out.println("-");
               break;
           case TIMES:
               System.out.println("*");
               break;
           case OVER:
               System.out.println("/");
               break;
           case ENDFILE:
               System.out.println("EOF");
               break;
           case NUM:
               System.out.println("Num, val=" + tokenString);
               break;
           case ID:
               System.out.println("name=" + tokenString);
               break;
           case ERROR:
               System.out.println("Error: " + tokenString);
               break;
           default:
               System.out.println("Deconocido=" + token);
               break;
       }
   }  // No Importante por ahora...
   /***************************************************************************/
   
   public NodoArbol newStmtNode(StmtKind kind) {
       NodoArbol t = new NodoArbol();
       if(t == null) {
           System.out.println("Error, no hay memoria");
           // Código para salir del método o el programa
       } else {
           for(int i = 0; i < MAXCHILDREN; i++)
               t.child[i] = null;
           t.sibling = null;
           
           t.nodeKind = NodeKind.StmtK;
           t.stmt = kind;
            // lineno debe ser la variable de número de línea global a usar en todo el proyecto.
           t.lineno = lineno;
       }
       
       return t;
   }
   
   public NodoArbol newExpNode(ExpKind kind) {
       NodoArbol t = new NodoArbol();
       if(t == null) {
           System.out.println("Error, no hay memoria");
           // Código para salir del método o el programa
       } else {
           for(int i = 0; i < MAXCHILDREN; i++)
               t.child[i] = null;
           
           t.sibling = null;
           t.nodeKind = NodeKind.ExpK;
           t.exp = kind;
           t.lineno = lineno;
           t.type = ExpType.Void;
           
       }
       return t;
   }
   
   public static void syntaxError(String mensaje) {
       System.out.printf("Error de sintaxis en la línea: %d: %s", lineno, mensaje);
       // Código para salir.
   }
   
   public void match(TokenType expected) {
       if(token == expected) {
           
           token = getToken();
           
       } else {
           
           syntaxError("Nodo no esperado --> ");
           printToken(expected, tokenString);
           System.out.print("      ");
           
       }
   }
   
   NodoArbol stmt_sequence() {
       
       NodoArbol t = statement();
       NodoArbol p = t;
       
       while(/*(token != TokenType.ENDFILE) && (token != TokenType.END) &&  */(token != TokenType.ELSE) && (token != TokenType.UNTIL) && (indice < this.palabras.size())) {
           
           NodoArbol q;
           match(TokenType.SEMI);
           q = statement();
           if(q != null) {
               if(t == null)
                   t = p = q;
               else {       /* Ahora p ya no puede ser NULL */
                   p.sibling = q;
                   p = q;
               }
                   
           }
       }
       
       return t;
       
   }
   
   public NodoArbol statement() {
       NodoArbol t = null;
       switch(token) {
           case IF:
               
               t = if_stmt();
               break;
               
           case REPEAT:
               
               t = repeat_stmt();
               break;
               
           case ID:
               
               t = assign_stmt();
               break;
               
           case READ:
               
               t = read_stmt();
               break;
               
           case WRITE:
               
               t = write_stmt();
               break;
               
           default:
               syntaxError("Token inesperado --> ");
               // Abstraer tokenString.
               printToken(token, tokenString);
               token = getToken();
               break;
       }
       return t;
   }
   
   NodoArbol if_stmt() {
       NodoArbol t = newStmtNode(StmtKind.IfK);
       match(TokenType.IF);
       if(t != null)
           t.child[0] = exp();
       match(TokenType.THEN);
       if(t != null)
           t.child[1] = stmt_sequence();
       if(token == TokenType.ELSE) {
           match(TokenType.ELSE);
           if(t != null)
               t.child[2] = stmt_sequence();
       }
       
       // Cuidado Ver cómo va a terminar el IF
       match(TokenType.END);
       return t;
       
   }
   
   public NodoArbol repeat_stmt() {
       NodoArbol t = newStmtNode(StmtKind.RepeatK);
       match(TokenType.REPEAT);
       if(t != null)
           t.child[0] = stmt_sequence();
       match(TokenType.UNTIL);
       if(t != null)
           t.child[1] = exp();
       
       return t;
       
   }
   
   public NodoArbol assign_stmt() {
       
       NodoArbol t = newStmtNode(StmtKind.AssignK);
       if((t != null) && (token == TokenType.ID))
           // Cuidado con esto!
           t.nombre = tokenString;
       match(TokenType.ID);
       match(TokenType.ASSIGN);
       if(t != null)
           t.child[0] = exp();
           
       return t;
   }
   
   public NodoArbol read_stmt() {
       NodoArbol t = newStmtNode(StmtKind.ReadK);
       match(TokenType.READ);
       if((t != null) && (token == TokenType.ID))
           t.nombre = tokenString;
       match(TokenType.ID);
       return t;
   }
   
   public NodoArbol write_stmt() {
       NodoArbol t = newStmtNode(StmtKind.WriteK);
       match(TokenType.WRITE);
       if(t != null)
           t.child[0] = exp();
       return t;
   }
   
   public NodoArbol exp() {
       NodoArbol t = simple_exp();
       if((token == TokenType.LT) || (token == TokenType.EQ)) {
           NodoArbol p = newExpNode(ExpKind.OpK);
           
           if(p != null) {
               p.child[0] = t;
               p.op = token;
               t = p;
           }
           
           match(token);
           if(t != null)
               t.child[1] = simple_exp();
           
       }
       return t;
   }
   
   public NodoArbol simple_exp() {
       NodoArbol t = term();
       while((token == TokenType.PLUS) || (token == TokenType.MINUS)) {
           NodoArbol p = newExpNode(ExpKind.OpK);
           if(p != null) {
               p.child[0] = t;
               p.op = token;
               t = p;
               match(token);
               t.child[1] = term();
           }
       }
       return t;
   }
   
   public NodoArbol term() {
       NodoArbol t = factor();
       while((token == TokenType.TIMES) || (token == TokenType.OVER)) {
           NodoArbol p = newExpNode(ExpKind.OpK);
           if(p != null) {
               p.child[0] = t;
               p.op = token;
               t = p;
               match(token);
               p.child[1] = factor();
           }
       }
       return t;
   }
   
   public NodoArbol factor() {
       NodoArbol t = null;
       switch(token) {
           case NUM:
               t = newExpNode(ExpKind.ConstK);
               if((t != null) && (token == TokenType.NUM))
                   t.valor = Integer.parseInt(tokenString);
               match(TokenType.NUM);
               break;
           case ID:
               t = newExpNode(ExpKind.IdK);
               if((t != null) && (token == TokenType.ID))
                   t.nombre = tokenString;
               match(TokenType.ID);
               break;
           case LPARENT:
               match(TokenType.LPARENT);
               t = exp();
               match(TokenType.RPARENT);
               break;
           default:
               syntaxError("Token inesperado ---> ");
               printToken(token, tokenString);
               token = getToken();
               break;
       }
       return t;
   }
   
   public TokenType getToken() {
       
       tokenString = palabras.get(indice).getValor();
       token = palabras.get(indice).getTipoToken();
       lineno = palabras.get(indice).getLineNo();
       
       return palabras.get(indice++).getTipoToken();
       
   }
   
   public NodoArbol parse() {
       
       NodoArbol t = null;
       
       token = getToken();
       
       t = stmt_sequence();
       
       return t;
   }
   // Solo para ir viendo el comportamiento...
   public void recorrer() {
       for(int i = 0; i < this.palabras.size(); i++)
           System.out.println(this.palabras.get(i).getValor() + ": " + this.palabras.get(i).getLineNo() + " - " + 
                   this.palabras.get(i).getTipoToken()
                   );
   }   
}

// TreeNode. Nuestro prototipo de nodo.
/*
class NodoArbol {
    
    public NodoArbol []child; // Maximo de tres hijos MAXHIJOS
    public NodoArbol sibling;   // El nodo hermano
    int lineno;                 // Número de línea donde se encuentra el nodo
    
    NodeKind nodeKind;    // Sentencia o expresión 
    StmtKind stmt; // Tipo de sentencia
    ExpKind exp;  // Tipo de expresion
    TokenType op;      // Tipo de operador
    int valor;
    String nombre;
    ExpType type;         // Para verificación de tipo de expresiones

}
*/