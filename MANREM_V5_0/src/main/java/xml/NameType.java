/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

/**
 *
 * @author Paulo Bonifacio
 */
public class NameType {
    
    private String Name;                                                   //  local agent name
    private String Type;                                                   // type id *Buyer/Seller
    
    public String                           getName(){
        return Name;
    }
    
    public String                           getType(){
        return Type;
    }
    
    public void                             setName(String Name){
        this.Name = Name;
    }
    
    public void                             setType(String Type){
        this.Type = Type;
    }
                                                                                // provavelmente não é necessário
    @Override
    public String                           toString(){
        //StringBuilder sb = new StringBuilder();
        //sb.append("Name :").append(getName());
        //return sb.toString();
        return "Name : " + this.Name + " Type : " + this.Type; // + "\n"; 
    }
}

