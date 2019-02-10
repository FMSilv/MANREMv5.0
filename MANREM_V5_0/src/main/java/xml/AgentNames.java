/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

/**
 *
 * @author Paulo Bonifacio
 */
public class                    AgentNames {
    private String agentName;                                                   //  local agent name
    private String agentType;                                                   // type id *Buyer/Seller
    
    public String                           getName(){
        return agentName;
    }
    
    public String                           getType(){
        return agentType;
    }
    
    public void                             setAgentName(String agentName){
        this.agentName = agentName;
    }
    
    public void                             setAgentType(String agentType){
        this.agentType = agentType;
    }
                                                                                // provavelmente não é necessário
    @Override
    public String                           toString(){
        //StringBuilder sb = new StringBuilder();
        //sb.append("Name :").append(getName());
        //return sb.toString();
        return "Name : " + this.agentName + " Type : " + this.agentType; // + "\n"; 
    }
}
