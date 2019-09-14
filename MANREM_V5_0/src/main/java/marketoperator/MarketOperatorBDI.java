/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package marketoperator;

import jadex.bdiv3.IBDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.annotation.ServiceTrigger;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import services.chatService.ChatService;
import services.chatService.IChatService;
import services.messageServices.IMessageService;

/**
 *
 * @author Filipe Silvério
 */
@Agent
@Description("MarketOperatorBDI agent. <br>")
@Arguments
(value={
	@Argument(name="chatOn", description="marketOperatorBDI.chatOn", clazz=String.class, defaultvalue="\"0\"")
})
@RequiredServices
({
	@RequiredService(name="clockservice", type =IClockService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="chatservices", type=IChatService.class, multiple=true, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, dynamic=true))
})
@ProvidedServices
({
	@ProvidedService(name="msgser", type=IMessageService.class, implementation=@Implementation(IBDIAgent.class)),
	@ProvidedService(type=IChatService.class, implementation=@Implementation(ChatService.class))
})
public class MarketOperatorBDI{

    @Agent
    protected IInternalAccess agent;
	
    @AgentFeature 
    protected IBDIAgentFeature bdiFeature;
    
	@AgentArgument
	protected String chatOn;
    
    DayAheadController DAController = new DayAheadController();
    
    @AgentBody
    public void init()
    {

    }
    
	@Plan(trigger=@Trigger(service=@ServiceTrigger(type=IMessageService.class)))
	public class iMessageServiceTrigger
	{
	    @PlanPrecondition
	    public boolean checkPrecondition(Object[] params)
	    {
	    	return params[1].equals(agent.getComponentIdentifier().getLocalName());
	    }
	    
	    @PlanBody
	    public String body(Object[] params)
	    {
	    	System.out.println("Sender: "+params[0]+"/ Receiver: "+params[1]+"/ Message: "+params[2]);
	    	
	    	if(params[3].equals("market_ontology"))
	    	{
	    		 bdiFeature.adoptPlan(new MarketOntology(params));
	    	}
	    	return "Chegou a MarketOperatorBDI";
	    }
	}
    
    @Plan
	public class MarketOntology {
		
    	protected String sender;
    	protected String receiver;
    	protected String content;
    	protected String ontology;
    	protected String protocol;
    	protected String performative;
    	
    	public MarketOntology(Object[] params) {
    		sender = params[0].toString();
    		receiver = params[1].toString();
    		content = params[2].toString();
    		ontology = params[3].toString();
    		protocol = params[4].toString();
    		performative = params[5].toString();
    	}
    	
    	@PlanBody
    	public void resolve()
    	{
          if (performative.equals("INFORM")) {
        	  resolveInform(content);
	      }
	      if (performative.equals("CFP")) {
	          resolveCFP(content);
	      }
    	}
    	
      private void resolveInform(String messageContent)
      {
    	  String results = null;
	      if(messageContent.toLowerCase().indexOf("Start Simulation".toLowerCase()) != -1)
	      {
	          if(messageContent.toLowerCase().indexOf("SMPsym".toLowerCase()) != -1)
	          {
	              results = DAController.SMPsymsimulation();
	          }
	          else if(messageContent.toLowerCase().indexOf("SMPasym".toLowerCase()) != -1)
	          {
	              results = DAController.SMPasymsimulation();    
	          }
	          else if(messageContent.toLowerCase().indexOf("LMP".toLowerCase()) != -1)
	          {
	              
	          }
	          else if(messageContent.toLowerCase().indexOf("OTC".toLowerCase()) != -1)
	          {
	              
	          }
	          send_results(results);
	      }
      }
	  private void resolveCFP(String messageContent)
	  {
	  
	  }
    	
	}
    
    public void send_results(String content){
        sendMessage(agent.getComponentIdentifier().getLocalName(), "PersonalAssistantBDIAgent", content, "market_ontology", "no_protocol", "INFORM");
    }
 
    public void sendMessage(String sender, String receiver, String messageContent, String ontology, String protocol, String performative) {
        SServiceProvider.getServices(agent, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
        .addResultListener(new IntermediateDefaultResultListener<IMessageService>()
        {
	        public void intermediateResultAvailable(IMessageService ts)
	        {
	            ts.sendMessage(sender, receiver, messageContent, ontology, protocol, performative)
	                .addResultListener(new SwingResultListener<String>(new IResultListener<String>()
	            {
	                public void resultAvailable(String response) 
	                {
	                	if(null!=response) {
		                	System.out.println("Message Received: "+ response);
	                	}
	                }
	
	                public void exceptionOccurred(Exception exception)
	                {
	                    exception.printStackTrace();
	                }
	            }));
	        }
        });
    }
    

    
}
