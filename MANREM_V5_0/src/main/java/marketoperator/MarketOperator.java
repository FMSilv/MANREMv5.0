/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package marketoperator;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author Hugo
 */
public class MarketOperator extends Agent {

    public MarketOperatorGUI mo_gui;
    public MarketOperatorGUI MO_gui;
    public MarketOperator MO = this;
    AID system_agent = new AID("PersonalAssistant", AID.ISLOCALNAME);
    public String deal;
    public DayAheadController DAController = new DayAheadController();

    @Override
    protected void setup() {
        this.addBehaviour(new MessageManager());
    }
    
    public class MessageManager extends CyclicBehaviour {
        
        //Message mask for all messages
        MessageTemplate mt_ontology = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("no_protocol"));

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(mt_ontology);
            if (msg != null) {
                if (msg.getOntology().equals("market_ontology")) {
                    MarketOntology market_ontology = new MarketOntology();
                    market_ontology.resolve(msg);
                }
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
                String results = null;
                
                if(msg.getContent().contains("Start Simulation"))
                {
                    if(msg.getContent().contains("SMPsym"))
                    {
                        results = DAController.SMPsymsimulation();
                    }
                    else if(msg.getContent().contains("SMPasym"))
                    {
                    	results = DAController.SMPasymsimulation();    
                    }
                    else if(msg.getContent().contains("LMP")){}
                    else if(msg.getContent().contains("OTC")){}
                    send_results(results);
                }
            }

            private void resolveCFP(ACLMessage msg){

            }

        }
    }

    public void send_results(String content){
        ACLMessage info_msg = new ACLMessage(ACLMessage.INFORM);

        info_msg.setContent(content);
        info_msg.setOntology("market_ontology");
        info_msg.setProtocol("no_protocol");
        info_msg.addReceiver(system_agent);
        send(info_msg);
    }
    
    
}
