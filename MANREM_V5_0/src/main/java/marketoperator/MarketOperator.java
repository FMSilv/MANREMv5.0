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
import javax.swing.JOptionPane;
import personalassistant.PersonalAssistant;

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

//        buy_gui = new Init(this);
    }
    
    public class MessageManager extends CyclicBehaviour {
        
        //Message mask for all messages
        MessageTemplate mt_ontology = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), 
                MessageTemplate.MatchProtocol("no_protocol"));
        
        
//
//        int step = 0;
//        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("hello_protocol"));
////        MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("no_protocol"));

        @Override
        public void action() {
            
            ACLMessage msg = myAgent.receive(mt_ontology);
            if (msg != null) {
                if (msg.getOntology().equals("market_ontology")) {
                    
                    MarketOntology market_ontology = new MarketOntology();
                    market_ontology.resolve(msg);
                    
                }
            
            
//
////            ACLMessage msg2 = myAgent.receive(mt2);
//            switch (step) {
//
//                case 0:
//
//                    ACLMessage msg = myAgent.receive(mt);
//                    if (msg != null) {
//                        if (msg != null) {
//
//                            String[] content_information = msg.getContent().split(";");
//                            mo_gui = new MarketOperatorGUI(MO);
//                            mo_gui.updateLog1("Market Operator is connected");
////                                setOpponent(new AID(content_information[0], AID.ISLOCALNAME));
//
//                            step = 1;
//
//                        } else {
//                            block();
//                            break;
//                        }
//
//                    } else {
//                        block();
//                        break;
//                    }
//
//                case 1:
//                    mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("no_protocol")), MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
//                    msg = myAgent.receive(mt);
//                    if (msg != null) {
//                        if (msg != null) {
//                            if (msg.getOntology().equals("market_ontology")) {
//
////                                setNegotiationContract(msg.getContent());
//                                deal = msg.getContent();
//                                step = 2;
//
//                            }
//                        } else {
//                            block();
//                            break;
//                        }
//
//                    } else {
//                        block();
//                        break;
//                    }
//
//                case 2:
//                    mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("no_protocol")), MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
//                    msg = myAgent.receive(mt);
//                    if (msg != null) {
//                        if (msg != null) {
//                            if (msg.getOntology().equals("market_ontology")) {
//
//                                ACLMessage msg_exist = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
//
////                                         int a=proposal(msg);
//                                int a = 0;
//                                if (a == 0) {
//                                    mo_gui.updateLog1(deal + "\n");
//                                    msg_exist.setContent("Deal Accepted");
//                                    msg_exist.setOntology("marketoperator_ontology");
//                                    msg_exist.setProtocol("negotiation_protocol");
////                    msg_exist.addReceiver(pmanager.system_agent);
//                                    msg_exist.addReceiver(msg.getSender());
//                                    MO.send(msg_exist);
//                                    msg_exist = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
//                                    msg_exist.setContent(deal);
//                                    msg_exist.setOntology("market_ontology");
//                                    msg_exist.setProtocol("no_protocol");
//                                    msg_exist.addReceiver(system_agent);
//                                    MO.send(msg_exist);
//                                    msg_exist.setContent(msg.getContent());
//                                    MO.send(msg_exist);
//                                } else {
//                                    msg_exist = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
//                                    msg_exist.setOntology("marketoperator_ontology");
//                                    msg_exist.setProtocol("negotiation_protocol");
//                                    msg_exist.setContent("Deal Rejected");
//                                    msg_exist.addReceiver(msg.getSender());
//                                    MO.send(msg_exist);
//                                }
//
//                                step = 3;
//                                break;
//                            }
//                        } else {
//                            block();
//                            break;
//                        }
//
//                    } else {
//                        block();
//                        break;
//                    }
//
//                case 3:
////                       mo_gui.dispose();
//                    block();
//                    break;
//            }
        }

//        private int proposal(ACLMessage msg) {
//            String[] choices4 = {"Accept", "Reject"};
//            String s = msg.getContent();
//            int a = JOptionPane.showOptionDialog(mo_gui, msg.getContent(), "Market Operator: New Bilateral Contract deal Received", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices4, null);
//
//            return a;
//        }

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
                
                if(msg.getContent().contains("Start Simulation")){
                    if(msg.getContent().contains("SMPsym")){
                        results = DAController.SMPsymsimulation();
                    }else if(msg.getContent().contains("SMPasym")){
                        results = DAController.SMPasymsimulation();    
                    }else if(msg.getContent().contains("LMP")){
                        
                    }else if(msg.getContent().contains("OTC")){
                        
                    }
                    
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
