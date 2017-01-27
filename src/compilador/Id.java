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
public class Id {
    private String name; //valor (nome do identificador ou valor da constante)
    private int type; //vem da tag
    private String bloco; //vai ter?
    private Object value;
    
    public Id(String name) {
        this.name = name;
    }
    
    public Id(String name, int type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBloco() {
        return bloco;
    }

    public void setBloco(String bloco) {
        this.bloco = bloco;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    public String toString(){
        return String.valueOf(type);
    }
}
