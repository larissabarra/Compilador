/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Larissa
 */
public class Syntax {

    private Lexer lexer;
    
    int tok = 0;
    Token t;
           
    public Syntax(Lexer lexer){
        this.lexer = lexer;
        t = null;
    }
    
    public void scan(){
        advance();
        program();
    }
    
    private void program() {
        switch (tok) {
            case Tag.PROGRAM:
                eat(Tag.PROGRAM); decl_list(); eat(Tag.AC); stmt_list(); eat(Tag.FC);
                break;
            default:
                error("Erro na linha " + Lexer.line + ": Programa deve ser iniciado com operador program");
        }
    }

    private void decl_list() {
        //As linhas abaixo foram comentadas porque assumimos que podem haver programas
        //em que não é necessário declarar nenhuma variável, portanto, na gramática,
        //a notação [] foi interpretada como {}.
        //decl();
        //eat(Tag.PONTO_VIRGULA);
        
        while (tok != Tag.AC) {
            decl();
            eat(Tag.PONTO_VIRGULA);
        }
    }

    private void decl() {
        switch (tok) {
            case Tag.ID:
                ident_list(); eat(Tag.DOIS_PONTOS); type();
                break;
            default:
                error("Erro na linha " + Lexer.line + ": Erro na declaração de variável.");
        }
    }

    private void ident_list() {
        eat(Tag.ID);
        
        while (tok == Tag.VIRGULA) {
            eat(Tag.VIRGULA);
            eat(Tag.ID);
        }
    }

    private void type() {
        switch (tok) {
            case Tag.INT:
                eat(Tag.INT);
                break;
            case Tag.FLOAT:
                eat(Tag.FLOAT);
                break;
            default:
                error("Erro na linha " + Lexer.line + ": Tipo de dado não reconhecido.");
        }
    }

    private void stmt_list() {
        stmt();
        eat(Tag.PONTO_VIRGULA);
        
        while (tok != Tag.FC) {
            switch(tok){
                case Tag.ID:
                case Tag.IF:
                case Tag.REPEAT:
                case Tag.SCAN:
                case Tag.PRINT:
                    stmt();
                    eat(Tag.PONTO_VIRGULA);
                    break;
                default:
                    error("Erro na linha " + Lexer.line + ": Comando mal-formulado.");
            }            
        }
    }
    
    private void stmt() {
        switch(tok){
            case Tag.ID:
                eat(Tag.ID); eat(Tag.ATRIB); simple_expr();
                break;
            case Tag.IF:
                eat(Tag.IF); condition(); eat(Tag.AC); stmt_list(); eat(Tag.FC); if_stmt();
                break;
            case Tag.REPEAT:
                eat(Tag.REPEAT); stmt_list(); eat(Tag.UNTIL); condition();
                break;
            case Tag.SCAN:
                eat(Tag.SCAN); eat(Tag.AP); eat(Tag.ID); eat(Tag.FP);
                break;
            case Tag.PRINT:
                eat(Tag.PRINT); eat(Tag.AP); writable(); eat(Tag.FP);
                break;
            default: error("Erro na linha " + Lexer.line + ": Comando mal-formulado.");
        }
    }
    
    private void simple_expr() {
        //TODO: tratar se for lambda
        // simple-expr não gera lambda
        term(); simple_exprprime();
    }
    
    private void term() {
        factor_a(); termprime();
    }
    
    private void termprime() {
        switch(tok){
            case Tag.MULT:
            case Tag.DIV:
            case Tag.AND:
                mulop(); factor_a(); termprime();
                break;
            //Lambda
            case Tag.ADD:
            case Tag.SUB:
            case Tag.OR:
            case Tag.FP:
            case Tag.EQ:
            case Tag.GT:
            case Tag.GE:
            case Tag.LT:
            case Tag.LE:
            case Tag.DIF:
            case Tag.AC:
            case Tag.PONTO_VIRGULA:
                // Não faz nada porque é lambda
                break;
            default:
                error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }

    }
    
    private void factor_a() {
        switch(tok) {
            case Tag.NOT:
                eat(Tag.NOT); factor();
                break;
            case Tag.SUB:
                eat(Tag.SUB); factor();
                break;
            default: factor();
        }
    }
    
    private void factor() {
        switch(tok) {
            case Tag.ID:
                eat(Tag.ID);
                break;
            case Tag.AP:
                eat(Tag.AP); expression(); eat(Tag.FP);
                break;
            default: constant();
        }
    }
    
    private void constant() {
        switch(tok) {
            case Tag.INT_NUM:
                eat(Tag.INT_NUM);
                break;
            case Tag.FLOAT_NUM:
                eat(Tag.FLOAT_NUM);
                break;
            case Tag.LITERAL:
                eat(Tag.LITERAL);
                break;
            default: error("Erro na linha " + Lexer.line + ": Constante mal-formulada.");
        }
    }
    
    private void simple_exprprime() {
        switch(tok){
            case Tag.ADD:
            case Tag.SUB:
            case Tag.OR:
                addop(); term(); simple_exprprime();
                break;
            // Lambda
            case Tag.FP:
            case Tag.EQ:
            case Tag.GT:
            case Tag.GE:
            case Tag.LT:
            case Tag.LE:
            case Tag.DIF:
            case Tag.AC:
            case Tag.PONTO_VIRGULA:
                // Não faz nada porque é lambda
                break;
            default:
                error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }
    }
    
    private void addop() {
        switch(tok) {
            case Tag.ADD:
                eat(Tag.ADD);
                break;
            case Tag.SUB:
                eat(Tag.SUB);
                break;
            case Tag.OR:
                eat(Tag.OR);
                break;
            default: error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }
    }
    
    private void mulop() {
        switch(tok) {
            case Tag.MULT:
                eat(Tag.MULT);
                break;
            case Tag.DIV:
                eat(Tag.DIV);
                break;
            case Tag.AND:
                eat(Tag.AND);
                break;
            default: error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }
    }
    
    private void relop() {
        switch(tok) {
            case Tag.EQ:
                eat(Tag.EQ);
                break;
            case Tag.GE:
                eat(Tag.GE);
                break;
            case Tag.GT:
                eat(Tag.GT);
                break;
            case Tag.LE:
                eat(Tag.LE);
                break;
            case Tag.LT:
                eat(Tag.LT);
                break;
            case Tag.DIF:
                eat(Tag.DIF);
                break;
            default: error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }
    }
    
    private void writable() {
        simple_expr();
    }
    
    //coloquei esse método inútil só pra ficar mais legível mesmo
    private void condition() {
        expression();
    }
    
    private void expression() {
        simple_expr(); expressionprime();
    }
    
    private void expressionprime() {
        switch(tok){
            case Tag.EQ:
            case Tag.GT:
            case Tag.GE:
            case Tag.LT:
            case Tag.LE:
            case Tag.DIF:
                relop(); simple_expr(); expressionprime();
                break;
            // Lambda
            case Tag.AC:
            case Tag.PONTO_VIRGULA:
            case Tag.FP:
                // Não faz nada porque é lambda
                break;
            default:
                error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }
    }
    
    private void if_stmt() {
        if(tok == Tag.ELSE) {
            eat(Tag.ELSE); eat(Tag.AC); stmt_list(); eat(Tag.FC);
        }
    }
    
    void advance() {
        try {
            t = lexer.scan();
            tok = t != null ? t.tag : 0;//1;//getToken(); //lê próximo token
        } catch (IOException ex) {
            //Logger.getLogger(Syntax.class.getName()).log(Level.SEVERE, null, ex);
            error("Erro na linha " + Lexer.line + ": Token não reconhecido.");
        }
    }

    void eat(int t) {
        if (tok == t) {
            advance();
        } else {
            error("Erro na linha " + Lexer.line + ": Token esperado (" + t + ") - Token recebido (" + tok + ")");
        }
    }

    private void error(String erro) {
        System.out.println(erro);
    }
}
