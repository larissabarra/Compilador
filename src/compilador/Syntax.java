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
        eat(Tag.PROGRAM);
        decl_list();
        eat(Tag.AC);
        stmt_list();
        eat(Tag.FC);
    }

    private void decl_list() {
        boolean ok = false;
        
        decl();
        eat(Tag.PONTO_VIRGULA);
        
        while (tok != Tag.AC && !lexer.isEOF()) {
            decl();
            ok = eat(Tag.PONTO_VIRGULA);
            
            if(!ok){
                advance();
            }
        }
    }

    private boolean decl() {
        boolean ok = false;
        
        ok = ident_list();
        ok = ok && eat(Tag.DOIS_PONTOS);
        ok = ok && type();
        
        return ok;
    }

    private boolean ident_list() {
        boolean ok = false;
        
        ok = eat(Tag.ID);
        
        while (tok == Tag.VIRGULA) {
            ok = ok && eat(Tag.VIRGULA);
            ok = ok && eat(Tag.ID);
            
            if(!ok){
                tok = Tag.DOIS_PONTOS;
                break;
            }
        }
        
        return ok;

    }

    private boolean type() {
        boolean ok = false;
        
        switch (tok) {
            case Tag.INT:
                ok = eat(Tag.INT);
                break;
            case Tag.FLOAT:
                ok = eat(Tag.FLOAT);
                break;
            default:
                error("Erro na linha " + Lexer.line + ": Tipo de dado não reconhecido.");
        }
        
        return ok;
    }

    private void stmt_list() {
        boolean ok = false;
        boolean existeProximo = true;
        
        stmt();
        eat(Tag.PONTO_VIRGULA);
        
        while (tok != Tag.FC && tok != Tag.UNTIL && !lexer.isEOF()) {
            switch(tok){
                case Tag.ID:
                case Tag.IF:
                case Tag.REPEAT:
                case Tag.SCAN:
                case Tag.PRINT:
                    ok = stmt();
                    if(!ok){
                        existeProximo = proximoComando();
                        if(existeProximo){
                            ok = eat(Tag.PONTO_VIRGULA);
                        }
                    }
                    else{
                        ok = ok && eat(Tag.PONTO_VIRGULA);
                    }
                    break;
                default:
                    error("Erro na linha " + Lexer.line + ": Comando mal-formulado.");
            }

            if(!existeProximo){
                tok = Tag.FC;
                break;
            }
        }
        
    }
    
    private boolean stmt() {
        boolean ok = false;
        
        switch(tok){
            case Tag.ID:
                ok = eat(Tag.ID); 
                ok = ok && eat(Tag.ATRIB); 
                ok = ok && simple_expr();
                break;
            case Tag.IF:
                ok = eat(Tag.IF); 
                ok = ok && condition(); 
                ok = ok && eat(Tag.AC); 
                stmt_list(); 
                ok = ok && eat(Tag.FC); 
                ok = ok && if_stmt();
                break;
            case Tag.REPEAT:
                ok = eat(Tag.REPEAT); 
                stmt_list(); 
                ok = ok && eat(Tag.UNTIL); 
                ok = ok && condition();
                break;
            case Tag.SCAN:
                ok = eat(Tag.SCAN); 
                ok = ok && eat(Tag.AP); 
                ok = ok && eat(Tag.ID); 
                ok = ok && eat(Tag.FP);
                break;
            case Tag.PRINT:
                ok = eat(Tag.PRINT); 
                ok = ok && eat(Tag.AP); 
                ok = ok && writable(); 
                ok = ok && eat(Tag.FP);
                break;
            default: error("Erro na linha " + Lexer.line + ": Comando mal-formulado.");
        }
        
        return ok;
    }
    
    private boolean simple_expr() {
        boolean ok = false;

        //TODO: tratar se for lambda
        // simple-expr não gera lambda
        ok = term();
        ok = ok && simple_exprprime();
        
        return ok;
    }
    
    private boolean term() {
        boolean ok = false;
        
        ok = factor_a(); 
        ok = ok && termprime();
        
        return ok;
    }
    
    private boolean termprime() {
        boolean ok = false;
        
        switch(tok){
            case Tag.MULT:
            case Tag.DIV:
            case Tag.AND:
                ok = mulop();
                ok = ok && factor_a();
                ok = ok && termprime();
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
                return true;
            default:
                error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }
        
        return ok;

    }
    
    private boolean factor_a() {
        boolean ok =  false;
        
        switch(tok) {
            case Tag.NOT:
                ok = eat(Tag.NOT); 
                ok = ok && factor();
                break;
            case Tag.SUB:
                ok = eat(Tag.SUB); 
                ok = ok && factor();
                break;
            default: 
                ok = factor();
        }
        
        return ok;
    }
    
    private boolean factor() {
        boolean ok = false;
        switch(tok) {
            case Tag.ID:
                ok = eat(Tag.ID);
                break;
            case Tag.AP:
                ok = eat(Tag.AP);
                ok = ok && expression();
                ok = ok && eat(Tag.FP);
                break;
            default: 
                ok = constant();
        }
        
        return ok;
    }
    
    private boolean constant() {
        boolean ok = false;
        
        switch(tok) {
            case Tag.INT_NUM:
                ok = eat(Tag.INT_NUM);
                break;
            case Tag.FLOAT_NUM:
                ok = eat(Tag.FLOAT_NUM);
                break;
            case Tag.LITERAL:
                ok = eat(Tag.LITERAL);
                break;
            default: error("Erro na linha " + Lexer.line + ": Constante mal-formulada.");
        }
        
        return ok;
    }
    
    private boolean simple_exprprime() {
        boolean ok = false;
        
        switch(tok){
            case Tag.ADD:
            case Tag.SUB:
            case Tag.OR:
                ok = addop();
                ok = ok && term();
                ok = ok && simple_exprprime();
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
                return true;
            default:
                error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }
        
        return ok;
    }
    
    private boolean addop() {
        boolean ok = false;
        
        switch(tok) {
            case Tag.ADD:
                ok = eat(Tag.ADD);
                break;
            case Tag.SUB:
                ok = eat(Tag.SUB);
                break;
            case Tag.OR:
                ok = eat(Tag.OR);
                break;
            default: error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }
        
        return ok;
    }
    
    private boolean mulop() {
        boolean ok = false;
        
        switch(tok) {
            case Tag.MULT:
                ok = eat(Tag.MULT);
                break;
            case Tag.DIV:
                ok = eat(Tag.DIV);
                break;
            case Tag.AND:
                ok = eat(Tag.AND);
                break;
            default: error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }
        
        return ok;
    }
    
    private boolean relop() {
        boolean ok = false;
        
        switch(tok) {
            case Tag.EQ:
                ok = eat(Tag.EQ);
                break;
            case Tag.GE:
                ok = eat(Tag.GE);
                break;
            case Tag.GT:
                ok = eat(Tag.GT);
                break;
            case Tag.LE:
                ok = eat(Tag.LE);
                break;
            case Tag.LT:
                ok = eat(Tag.LT);
                break;
            case Tag.DIF:
                ok = eat(Tag.DIF);
                break;
            default: error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }
        
        return ok;
    }
    
    private boolean writable() {
        boolean ok = false;
        
        ok = simple_expr();
        
        return ok;
    }
    
    //coloquei esse método inútil só pra ficar mais legível mesmo
    private boolean condition() {
        boolean ok = false;
        
        ok = expression();
        
        return ok;
    }
    
    private boolean expression() {
        boolean ok = false;
        
        ok = simple_expr(); 
        ok = ok && expressionprime();
        
        return ok;
    }
    
    private boolean expressionprime() {
        boolean ok = false;
        
        switch(tok){
            case Tag.EQ:
            case Tag.GT:
            case Tag.GE:
            case Tag.LT:
            case Tag.LE:
            case Tag.DIF:
                ok = relop(); 
                ok = ok && simple_expr(); 
                ok = ok && expressionprime();
                break;
            // Lambda
            case Tag.AC:
            case Tag.PONTO_VIRGULA:
            case Tag.FP:
                // Não faz nada porque é lambda
                return true;
            default:
                error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }
        
        return ok;
    }
    
    private boolean if_stmt() {
        boolean ok = true;
        
        if(tok == Tag.ELSE) {
            ok = eat(Tag.ELSE);
            ok = ok && eat(Tag.AC);
            stmt_list();
            ok = ok && eat(Tag.FC);
        }
        
        return ok;
    }
    
    boolean advance() {
        try {
            t = lexer.scan();
            tok = t != null ? t.tag : 0;//1;//getToken(); //lê próximo token
            if(tok == 0 && !lexer.isEOF()){
                error("[LEXICO] Erro na linha " + Lexer.line + ": Token não reconhecido.");
                //t = lexer.scan();
                return false;
            }
            return true;
        } catch (IOException ex) {
            //Logger.getLogger(Syntax.class.getName()).log(Level.SEVERE, null, ex);
            error("Erro na linha " + Lexer.line + ": Token não reconhecido.");
            return false;
        }
    }

    boolean eat(int t) {
        if (tok == t) {
            return advance();
        } else {
            error("Erro na linha " + Lexer.line + ": Token esperado (" + t + ") - Token recebido (" + tok + ")");
            return false;
        }
    }

    private void error(String erro) {
        System.out.println(erro);
    }
    
    private boolean proximoComando(){
        boolean existeProximo = false;
        
        while(tok != Tag.PONTO_VIRGULA && !lexer.isEOF()){
            advance();
        }
        
        existeProximo = tok == Tag.PONTO_VIRGULA;
        
        return existeProximo;
    }
}
