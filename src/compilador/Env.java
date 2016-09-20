/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Larissa
 */
public class Env {

    private Hashtable table; //tabela de símbolos do ambiente
    protected Env prev; //ambiente imediatamente superior

    public Env(Env n) {
        table = new Hashtable(); //cria a TS para o ambiente
        prev = n; //associa o ambiente atual ao anterior
    }
    
    /* Este método insere uma entrada na TS do ambiente */
    /* A chave da entrada é o Token devolvido pelo analisador léxico */
    /* Id é uma classe que representa os dados a serem armazenados na TS para */
    /* identificadores */
    public void put(Token w, Id i) {
        
    /*    if(w instanceof Word)
            table.put(((Word) w).getLexeme(), i);
        else if(w instanceof NumInt)
            table.put(((NumInt) w).value, i);
        else if(w instanceof NumFloat)
            table.put(((NumFloat) w).value, i);
        else
            table.put(w.nome, i);*/
        
        table.put(w, i);
    }
    
    /* Este método retorna as informações (Id) referentes a determinado Token */
    /* O Token é pesquisado do ambiente atual para os anteriores */
    public Id get(Token w) {
        for (Env e = this; e != null; e = e.prev) {
            //Id found = (Id) e.table.get(w.nome);
            
            Id found = (Id) e.table.get(w);
            if (found != null) { //se Token existir em uma das TS
                return found;
            }
        }
        return null; //caso Token não exista em uma das TS
    }
    
    /* Este método retorna as informações (Id) referentes a determinado Token */
    /* O Token é pesquisado do ambiente atual para os anteriores */
    public Token getKey(String s) {
        
        Token key = null;
        
        for (Env e = this; e != null; e = e.prev) {
            for(Object it : e.table.keySet()){
                if(it instanceof Token){
                    if(it instanceof Word){
                        if(s.equals(((Word)it).getLexeme())){
                            key = (Word) it;
                            return key;
                        }
                    }
                    else if(it instanceof NumInt){
                        try{
                            if(Integer.valueOf(s) == ((NumInt)it).value){
                                    key = (NumInt) it;
                                    return key;
                            }
                        }
                        catch(NumberFormatException ex){
                            
                        }
                        
                    }
                    else if(it instanceof NumFloat){
                        try{
                            if(Float.valueOf(s) == ((NumFloat)it).value){
                                key = (NumInt) it;
                                return key;
                            }
                        }
                        catch(NumberFormatException ex){
                            
                        }
                    }
                    else{
                        if(s.equals(((Token)it).nome)){
                            key = (Token) it;
                            return key;
                        }
                    }
                }
            }
            
        }
        return null; //caso Token não exista em uma das TS
    }
    
    public void imprime(){
        System.out.println("\n***TABELA DE SÍMBOLOS***");
        Set set = table.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            System.out.println("<" + entry.getKey() + ", " + ((Id)entry.getValue()).toString() + ">");
         }
    }
}
