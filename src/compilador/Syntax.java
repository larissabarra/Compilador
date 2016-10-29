/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

/**
 *
 * @author Larissa
 */
public class Syntax {

    int tok = 0;

    private void program() {
        switch (tok) {
            case Tag.PROGRAM:
                eat(Tag.PROGRAM); decl_list(); eat(Tag.AC); stmt_list(); eat(Tag.FC);
                break;
            default:
                error();
        }
    }

    private void decl_list() {
        if (tok == Tag.ID) {
            while (tok != Tag.AC) {
                decl();
                eat(Tag.PONTO_VIRGULA);
            }
        } else {
            error();
        }
    }

    private void decl() {
        switch (tok) {
            case Tag.ID:
                ident_list(); type();
                break;
            default:
                error();
        }
    }

    private void ident_list() {
        while (tok == Tag.ID) {
            eat(Tag.ID);
            if (tok == Tag.FLOAT || tok == Tag.INT) {
                break;
            }
            //verifica antes pra não dar o erro se vier tipo esperando vírgula
            eat(Tag.VIRGULA);
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
                error();
        }
    }

    private void stmt_list() {
        while (tok != Tag.FC) {
            stmt();
            eat(Tag.PONTO_VIRGULA);
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
            default: error();
        }
    }
    
    private void simple_expr() {
        //TODO: tratar se for lambda
        term(); simple_exprprime();
    }
    
    private void term() {
        factor_a(); termprime();
    }
    
    private void termprime() {
        //TODO: tratar se for lambda
        mulop(); factor_a(); termprime();
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
            default: error();
        }
    }
    
    private void simple_exprprime() {
        //TODO: tratar se for lambda
        addop(); term(); simple_exprprime();
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
            default: error();
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
            default: error();
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
            default: error();
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
        //TODO: tratar se for lambda
        relop(); simple_expr(); expressionprime();
    }
    
    private void if_stmt() {
        if(tok == Tag.ELSE) {
            eat(Tag.ELSE); eat(Tag.AC); stmt_list(); eat(Tag.FC);
        }
    }
    
    void advance() {
        tok = 1;//getToken(); //lê próximo token
    }

    void eat(int t) {
        if (tok == t) {
            advance();
        } else {
            error();
        }
    }

    private void error() {
        System.out.println("Deu erro");
    }
}
