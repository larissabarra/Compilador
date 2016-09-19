/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.util.Hashtable;

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
        table.put(w.nome, i);
    }
    
    /* Este método retorna as informações (Id) referentes a determinado Token */
    /* O Token é pesquisado do ambiente atual para os anteriores */
    public Id get(Token w) {
        for (Env e = this; e != null; e = e.prev) {
            Id found = (Id) e.table.get(w.nome);
            if (found != null) { //se Token existir em uma das TS
                return found;
            }
        }
        return null; //caso Token não exista em uma das TS
    }
}
