/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OTC;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import personalassistant.PersonalAssistant;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author João
 */
public class OTC_Market_Operator extends Agent {
    
    private int counter;
    private Results results;
    
    private ArrayList<Float> Volumes;
    private ArrayList<Float> Prices;
    
    
    private ArrayList<OTC_AgentData> buyers;
    private ArrayList<OTC_AgentData> sellers;
    private OTC_AgentData new_agent;
    
    
    @Override
    protected void setup(){
        
        System.out.println("LIGOU O AGENTE!!");
        
        counter = 0;
        
        buyers = new ArrayList<>();
        sellers = new ArrayList<>();
        
        this.addBehaviour(new MessageManager());
    }

    void send_results() {
        
        for(int i = 0; i < buyers.size(); i++){
            AID rec = new AID(buyers.get(i).getName(), AID.ISLOCALNAME);
        
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.setOntology("market_ontology");
            msg.setProtocol("no_protocol");
            msg.setContent("RESULTS!!");
            msg.addReceiver(rec);
        
            send(msg);
        }
        
        for(int i = 0; i < sellers.size(); i++){
            AID rec = new AID(sellers.get(i).getName(), AID.ISLOCALNAME);
        
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.setOntology("market_ontology");
            msg.setProtocol("no_protocol");
            msg.setContent("RESULTS!!");
            msg.addReceiver(rec);
        
            send(msg);
        }
    }
    
    
    
    
    public class MessageManager extends CyclicBehaviour {
        
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("no_protocol"));
        
        @Override
        public void action(){
            ACLMessage msg = myAgent.receive(mt);
            
            if (msg != null) {
                if (msg.getOntology().equals("market_ontology")) {

                    MarketOntology market_ontology = new MarketOntology();
                    market_ontology.resolve(msg);
                    
                }
            }
            else
                block();        
        }
      
    }
    
    class MarketOntology {
        
        private void resolve(ACLMessage msg) {
            if (msg.getPerformative() == ACLMessage.INFORM) {
                resolveInform(msg);
            }
            if (msg.getPerformative() == ACLMessage.CFP) {
                resolveCFP(msg);
            }
        }
        
        private void resolveInform(ACLMessage msg){
            try{
                storeData(msg.getContent());
            }
            catch(Exception ex){
                JOptionPane.showMessageDialog(null, "Error while processing data!!\n",
                                "Corrupted data", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private void resolveCFP(ACLMessage msg){
            
            counter = counter + 1;
            if(counter == 2){
                
                simulate();
                
            }
            
            
        }
        
    }
    
    public void storeData(String info){
        
        String[] data; 
        data = info.split(" ");
        boolean isSeller;
        boolean isOTC;
        boolean isPool;
        boolean volumebids;
        String Name;
        
        
        Volumes = new ArrayList<>();
        Prices = new ArrayList<>();
        
        if(data[0].equals("seller")){
            isSeller = true;
        }
        else
            isSeller = false;
        
        Name = data[1];
        
        if(data[2].equals("y")){
            isOTC = true;
        }    
        else{
            isOTC = false;
        }
        
        if(data[3].equals("y")){
            isPool = true;
        }
        else{
            isPool = false;
        }
        
        volumebids = true;
        for(int i = 4; i < data.length; i++){
            
            if(data[i].equals("end"))
                break;
            
            if(data[i].equals("volume_bids")){
                volumebids = true;
                continue;
            }    
            if(data[i].equals("price_bids")){
                volumebids = false;
                continue;
            }
            
            if(volumebids){
                Volumes.add(Float.parseFloat(data[i]));
            }
            else{
                Prices.add(Float.parseFloat(data[i]));
            }
            
        }
        
        int id = buyers.size() + sellers.size();
        new_agent = new OTC_AgentData(Name, id, Prices, Volumes, isOTC, isPool);
        
       
        if(isSeller){
                if(sellers.isEmpty())
                    sellers.add(new_agent);
                else{
                    for(int i = 0; i < sellers.size(); i++){
                        if(sellers.get(i).getName().equals(Name)){
                            JOptionPane.showMessageDialog(null, "Agent information already added!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                    }
                    sellers.add(new_agent);
                }
                    
            } else{
                if(buyers.isEmpty())
                    buyers.add(new_agent);
                else{
                    for(int i = 0; i < buyers.size(); i++){
                        if(buyers.get(i).getName().equals(Name)){
                            JOptionPane.showMessageDialog(null, "Agent information already added!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                    }
                    buyers.add(new_agent);
                }
            }
        
    }
    
    
    public void simulate(){
        
        // Goes through all the buyers
        for(int i = 0; i < buyers.size(); i++){
            // Goes through all the periods
                for(int j = 0; j < buyers.get(i).get_PowerOffers().size(); j++)
                    calculate_results(i, j, 0);
        }
        
        results = new Results(buyers, sellers, this);
        results.setVisible(true);
    }
    
    public void calculate_results(int buyer_index, int bid_index, float _total_volume){
        
        float max_volume = buyers.get(buyer_index).getPower().get(bid_index);
        float max_price = buyers.get(buyer_index).getPrice().get(bid_index);
        float lowest_price = Float.MAX_VALUE;
        float lowest_price_volume = Float.MIN_VALUE;
        float total_volume = _total_volume;
        
        int sellers_index = -1;
        
        float price;
        float volume;
          
        // For each seller recursively calculates the lowest offers that are acceptable,
        // that is, below price that buyer is willing to pay in that period, and 
        // until the maximum value the buyer is willing to buy is reached.
        
        for(int i = 0; i < sellers.size(); i++){
            volume = sellers.get(i).getPower().get(bid_index);
            price = sellers.get(i).getPrice().get(bid_index);
            
            if(price <= max_price && price < lowest_price && volume < max_volume){
                if((total_volume + volume) < max_volume && !sellers.get(i).getIsNegotiated_OTC()[bid_index]){
                    sellers_index = i;
                    lowest_price = sellers.get(i).getPeriodPrice(bid_index);
                }
            }
        }
        
        if(sellers_index != -1){
            sellers.get(sellers_index).getIsNegotiated_OTC()[bid_index] = true;
            volume = sellers.get(sellers_index).getPower().get(bid_index);
            total_volume = total_volume + volume;
                
            calculate_results(buyer_index, bid_index, total_volume);
        }
        
    }

}
