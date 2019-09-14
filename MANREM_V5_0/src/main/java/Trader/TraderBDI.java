/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Trader;

import jadex.bdiv3.IBDIAgent;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import services.chatService.ChatService;
import services.chatService.IChatService;
import services.messageServices.IMessageService;

/**
 *
 * @author Filipe Silvério. Based on previous Work
 */
@Agent
@ProvidedServices
({
	@ProvidedService(name="msgser", type=IMessageService.class, implementation=@Implementation(IBDIAgent.class)),
	@ProvidedService(type=IChatService.class, implementation=@Implementation(ChatService.class))
})
public abstract class TraderBDI{
	
    @Agent
    protected IInternalAccess agent;
	
    @AgentFeature 
    protected IBDIAgentFeature bdiFeature;
	
    //Variables that are common for every Agent Type
    public AgentData information;
    
    //Methods that are common for every Agent Type
    
    // setup method for the Agent, initializes the MessageManager and starts
    // executing first phase.
    @AgentBody
    protected void setup() {
        this.information = new AgentData();
//        this.addBehaviour(new MessageManager());
        executePhase(0);
    }
    
    
//    class MessageManager extends CyclicBehaviour {
//        
//        //Message mask for initialization messages <----------------------------------------------
//        MessageTemplate mt_hello = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("hello_protocol"));
//        
//        //Message mask for all subsequent messages
//        MessageTemplate mt_ontology = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("no_protocol"));
//        
//        
//        @Override
//        public void action() {
//            ACLMessage hello_msg = myAgent.receive(mt_hello);
//            
//            ACLMessage msg = myAgent.receive(mt_ontology);
//            
//            if (hello_msg != null) {
//                
//                if (hello_msg.getOntology().equals("market_ontology")) {
//
//                    MarketOntology market_ontology = new MarketOntology();
//                    market_ontology.resolve_hello(hello_msg);
//                    
//                }
//            
//            }
//            if ( msg != null){
//                if (msg.getOntology().equals("market_ontology")) {
//                    MarketOntology market_ontology = new MarketOntology();
//                    market_ontology.resolve(msg);
//                    
//                }
//                
//            }
//    }
//    
//    class MarketOntology {
//        private void resolve_hello(ACLMessage msg) {
//
//        }
//        
//        private void resolve(ACLMessage msg) {
//            
//        }
//    }
//        
//    }

    abstract public void executePhase(int phase);
    
}
