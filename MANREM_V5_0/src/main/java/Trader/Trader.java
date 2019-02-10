/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Trader;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author João de Sá. Based on previous Work
 */
public abstract class Trader extends Agent{
    //Variables that are common for every Agent Type
    public AgentData information;
    
    
    //Methods that are common for every Agent Type
    
    // setup method for the Agent, initializes the MessageManager and starts
    // executing first phase.
    @Override
    protected void setup() {
        this.information = new AgentData();
        this.addBehaviour(new MessageManager());
        executePhase(0);
    }
    
    
    class MessageManager extends CyclicBehaviour {
        
        //Message mask for initialization messages <----------------------------------------------
        MessageTemplate mt_hello = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), 
                MessageTemplate.MatchProtocol("hello_protocol"));
        
        
        //Message mask for all subsequent messages
        MessageTemplate mt_ontology = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), 
                MessageTemplate.MatchProtocol("no_protocol"));
        
        
        
        @Override
        public void action() {
            ACLMessage hello_msg = myAgent.receive(mt_hello);
            
            ACLMessage msg = myAgent.receive(mt_ontology);
            
            if (hello_msg != null) {
                
                if (hello_msg.getOntology().equals("market_ontology")) {

                    MarketOntology market_ontology = new MarketOntology();
                    market_ontology.resolve_hello(hello_msg);
                    
                }
            
            }
            if ( msg != null){
                if (msg.getOntology().equals("market_ontology")) {
                    MarketOntology market_ontology = new MarketOntology();
                    market_ontology.resolve(msg);
                    
                }
                
            }
    }
    
        
    class MarketOntology {
        private void resolve_hello(ACLMessage msg) {

        }
        
        private void resolve(ACLMessage msg) {
            
        }
    }
        
    }

    abstract public void executePhase(int phase);
    
}
