package dataStorageAssistant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

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
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import services.messageServices.IMessageService;

/**
*
* @author Filipe Silvério
*/
@Agent
@Description("DataStorageAssistantBDI agent. <br>")
@RequiredServices
({
	@RequiredService(name="clockservice", type=IClockService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
@ProvidedServices
({
	@ProvidedService(name="msgser", type=IMessageService.class, implementation=@Implementation(IBDIAgent.class)),
})
public class DataStorageAssistantBDI {

    @Agent
    protected IInternalAccess agent;
	
    @AgentFeature 
    protected IBDIAgentFeature bdiFeature;
	
	
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
	    	
   		 	bdiFeature.adoptPlan(new StoreData(params));
	    	
	    	return "Chegou a " + agent.getComponentIdentifier().getLocalName() + " Agent";
	    }
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
	
    
    @Plan
	public class StoreData {
		
    	protected String sender;
    	protected String receiver;
    	protected String content;
    	protected String ontology;
    	protected String protocol;
    	protected String performative;
    	
    	public StoreData(Object[] params) {
    		sender = params[0].toString();
    		receiver = params[1].toString();
    		content = params[2].toString();
    		ontology = params[3].toString();
    		protocol = params[4].toString();
    		performative = params[5].toString();
    	}
    	
    	@PlanBody
    	public void execute()
    	{
    		Connection conn = null; 
    		Statement stmt = null;
    		try 
    		{
    			Class.forName("org.h2.Driver");
    			conn = DriverManager.getConnection("jdbc:h2:file:D:\\Work\\eclipse\\workspace-fsilverio\\git\\MANREMv5.0.git\\MANREM_V5_0\\database\\h2db","root","root");
    			stmt = conn.createStatement();
    			String sql =  "INSERT INTO SIMULATIONS_DATA (RESULTS, DATE) VALUES ('"+ content +"' , '"+ new Timestamp(new Date().getTime()) +"')";  
    			stmt.executeUpdate(sql);
    			stmt.close();
    			conn.close();
			} 
			catch(SQLException se) 
			{
				se.printStackTrace(); 
			} 
			catch(Exception e) 
			{
				e.printStackTrace(); 
			} 
			finally 
			{
				try
				{ 
					if(stmt!=null) stmt.close(); 
				} 
				catch(SQLException se2) 
				{
					se2.printStackTrace(); 
				}
				try
				{ 
					if(conn!=null) conn.close(); 
				} 
				catch(SQLException se)
				{ 
					se.printStackTrace(); 
				} 
			}
    	}
    	
    	
    	
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
