package dataStorageAssistant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JOptionPane;

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
import marketoperator.DayAheadController;
import services.messageServices.IMessageService;
import wholesalemarket_SMP.AgentData;

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
    		if(content.equals("getLastSimulation")) {
    			String resultSetString = getLastSimulation();
    			JOptionPane.showMessageDialog(null, resultSetString, "INFO", JOptionPane.INFORMATION_MESSAGE);
    		}
    		else if(content.equals("SMPsym - Store simulation data"))
    		{
    			insertSimulationsData("SMPsym");
    			String lastId = getLastID();
    			storeValuesManagement(lastId);
    		}
    		else if(content.equals("SMPasym - Store simulation data"))
    		{
    			insertSimulationsData("SMPasym");
    			String lastId = getLastID();
    			storeValuesManagement(lastId);
    		}
    	}
    	
    	
    	protected void storeValuesManagement(String lastId) {
			DayAheadController dayAheadController= new DayAheadController();
			dayAheadController.readinputfile();
			for(AgentData buyer : dayAheadController.getBuyers()) {
				insertSimulationDataRow(Integer.parseInt(lastId), buyer.getName(), "Buyer", "Power", 
						buyer.getPower().get(0), buyer.getPower().get(1),
						buyer.getPower().get(2), buyer.getPower().get(3),
						buyer.getPower().get(4), buyer.getPower().get(5),
						buyer.getPower().get(6), buyer.getPower().get(7),
						buyer.getPower().get(8), buyer.getPower().get(9),
						buyer.getPower().get(10), buyer.getPower().get(11),
						buyer.getPower().get(12), buyer.getPower().get(13),
						buyer.getPower().get(14), buyer.getPower().get(15),
						buyer.getPower().get(16), buyer.getPower().get(17),
						buyer.getPower().get(18), buyer.getPower().get(19),
						buyer.getPower().get(20), buyer.getPower().get(21),
						buyer.getPower().get(22), buyer.getPower().get(23)
						);
				insertSimulationDataRow(Integer.parseInt(lastId), buyer.getName(), "Buyer", "Price", 
						buyer.getPrice().get(0), buyer.getPrice().get(1),
						buyer.getPrice().get(2), buyer.getPrice().get(3),
						buyer.getPrice().get(4), buyer.getPrice().get(5),
						buyer.getPrice().get(6), buyer.getPrice().get(7),
						buyer.getPrice().get(8), buyer.getPrice().get(9),
						buyer.getPrice().get(10), buyer.getPrice().get(11),
						buyer.getPrice().get(12), buyer.getPrice().get(13),
						buyer.getPrice().get(14), buyer.getPrice().get(15),
						buyer.getPrice().get(16), buyer.getPrice().get(17),
						buyer.getPrice().get(18), buyer.getPrice().get(19),
						buyer.getPrice().get(20), buyer.getPrice().get(21),
						buyer.getPrice().get(22), buyer.getPrice().get(23)
						);
			}
			for(AgentData seller : dayAheadController.getSellers()) {
				insertSimulationDataRow(Integer.parseInt(lastId), seller.getName(), "Producer", "Power", 
						seller.getPower().get(0), seller.getPower().get(1),
						seller.getPower().get(2), seller.getPower().get(3),
						seller.getPower().get(4), seller.getPower().get(5),
						seller.getPower().get(6), seller.getPower().get(7),
						seller.getPower().get(8), seller.getPower().get(9),
						seller.getPower().get(10), seller.getPower().get(11),
						seller.getPower().get(12), seller.getPower().get(13),
						seller.getPower().get(14), seller.getPower().get(15),
						seller.getPower().get(16), seller.getPower().get(17),
						seller.getPower().get(18), seller.getPower().get(19),
						seller.getPower().get(20), seller.getPower().get(21),
						seller.getPower().get(22), seller.getPower().get(23)
						);
				insertSimulationDataRow(Integer.parseInt(lastId), seller.getName(), "Producer", "Price", 
						seller.getPrice().get(0), seller.getPrice().get(1),
						seller.getPrice().get(2), seller.getPrice().get(3),
						seller.getPrice().get(4), seller.getPrice().get(5),
						seller.getPrice().get(6), seller.getPrice().get(7),
						seller.getPrice().get(8), seller.getPrice().get(9),
						seller.getPrice().get(10), seller.getPrice().get(11),
						seller.getPrice().get(12), seller.getPrice().get(13),
						seller.getPrice().get(14), seller.getPrice().get(15),
						seller.getPrice().get(16), seller.getPrice().get(17),
						seller.getPrice().get(18), seller.getPrice().get(19),
						seller.getPrice().get(20), seller.getPrice().get(21),
						seller.getPrice().get(22), seller.getPrice().get(23)
						);
			}
    	}
    	
    	
    	
    	protected String getLastSimulation() {
    		
  		     Connection conn = null; 
		      Statement stmt = null;
		      String resultSetString = null;
		      try {
		         Class.forName("org.h2.Driver");
		         conn = DriverManager.getConnection("jdbc:h2:file:"+System.getProperty("user.dir").replace("\\", "\\\\") + "\\\\database\\\\h2db"+"","root","root");
		         stmt = conn.createStatement(
                      ResultSet.TYPE_SCROLL_INSENSITIVE,
                      ResultSet.CONCUR_UPDATABLE);
		         String sql = "SELECT sim.RESULTS from SIMULATIONS_DATA sim order by DATE asc";
		         ResultSet rs = stmt.executeQuery(sql);
		         while(rs.next()) {
		        	 resultSetString = rs.getString("RESULTS");
		          }
		         rs.close();
		      } catch(SQLException se) {
		    	  JOptionPane.showMessageDialog(null, "Não foi possível obter os dados da ultima simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);

		         se.printStackTrace();
		      } catch(Exception ex) { 
		    	  JOptionPane.showMessageDialog(null, "Não foi possível obter os dados da ultima simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);
		         ex.printStackTrace(); 
		      } finally { 
		         try { 
		            if(stmt!=null) stmt.close();  
		         } catch(SQLException se2) { 
		         }
		         try { 
		            if(conn!=null) conn.close(); 
		         } catch(SQLException se) { 
		            se.printStackTrace(); 
		         }
		      }
		      return resultSetString;
    	}
    	
    	
    	
    	protected void insertSimulationsData(String simulationType) {
    		
    		Connection conn = null; 
    		Statement stmt = null;
    		try
    		{
    			Class.forName("org.h2.Driver");
    			conn = DriverManager.getConnection("jdbc:h2:file:"+System.getProperty("user.dir").replace("\\", "\\\\") + "\\\\database\\\\h2db"+"","root","root");
    			stmt = conn.createStatement();
    			String sql =  "INSERT INTO SIMULATIONS_HISTORY (TYPE, DATE) VALUES ('"+ simulationType +"' , '"+ new Timestamp(new Date().getTime()) +"')";  
    			stmt.executeUpdate(sql);
    			stmt.close();
    			conn.close();
			}
			catch(SQLException se) 
			{
				JOptionPane.showMessageDialog(null, "Não foi possível registar os dados da simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);
				se.printStackTrace();
			} 
			catch(Exception e) 
			{
				JOptionPane.showMessageDialog(null, "Não foi possível registar os dados da simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);
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
    	
    	
    	protected String getLastID() {
    		
 		     Connection conn = null; 
		      Statement stmt = null;
		      String resultSetString = null;
		      try {
		         Class.forName("org.h2.Driver");
		         conn = DriverManager.getConnection("jdbc:h2:file:"+System.getProperty("user.dir").replace("\\", "\\\\") + "\\\\database\\\\h2db"+"","root","root");
		         stmt = conn.createStatement(
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE);
		         String sql = "SELECT I_ID FROM (SELECT I_ID FROM SIMLULATIONS_HISTORY ORDER BY DATE DESC) WHERE ROWNUM = 1";
		         ResultSet rs = stmt.executeQuery(sql);
		         while(rs.next()) {
		        	 resultSetString = rs.getString("RESULTS");
		          }
		         rs.close();
		      } catch(SQLException se) {
		    	  JOptionPane.showMessageDialog(null, "Não foi possível obter os dados da ultima simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);

		         se.printStackTrace();
		      } catch(Exception ex) { 
		    	  JOptionPane.showMessageDialog(null, "Não foi possível obter os dados da ultima simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);
		         ex.printStackTrace(); 
		      } finally { 
		         try { 
		            if(stmt!=null) stmt.close();  
		         } catch(SQLException se2) { 
		         }
		         try { 
		            if(conn!=null) conn.close(); 
		         } catch(SQLException se) { 
		            se.printStackTrace(); 
		         }
		      }
		      return resultSetString;
    	}
    	
    	
    	
    	protected void insertSimulationDataRow(int lastId, String name, String type, String value, 
				float d1, float d2,
				float d3, float d4,
				float d5, float d6,
				float d7, float d8,
				float d9, float d10,
				float d11, float d12,
				float d13, float d14,
				float d15, float d16,
				float d17, float d18,
				float d19, float d20,
				float d21, float d22,
				float d23, float d24
				){
    		Connection conn = null; 
    		Statement stmt = null;
    		try
    		{
    			Class.forName("org.h2.Driver");
    			conn = DriverManager.getConnection("jdbc:h2:file:"+System.getProperty("user.dir").replace("\\", "\\\\") + "\\\\database\\\\h2db"+"","root","root");
    			stmt = conn.createStatement();
    			String sql =  "INSERT INTO SIMULATIONS_DATA (SIM_ID, NAME, TYPE, VALUE, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20, D21, D22, D23, D24) VALUES "
    					+ "('"+ lastId +"' , '"+ name +"', '"+ type +"', '"+ value +"', '"+ d1 +"'"
    							+ ", '"+ d2 +"', '"+ d3 +"', '"+ d4 +"', '"+ d5 +"', '"+ d6 +"', '"+ d7 +"', '"+ d8 +"'"
    									+ ", '"+ d9 +"', '"+ d10 +"', '"+ d11 +"', '"+ d12 +"', '"+ d13 +"', '"+ d14 +"'"
    									+ ", '"+ d15 +"', '"+ d16 +"', '"+ d17 +"', '"+ d18 +"', '"+ d19 +"', '"+ d20 +"'"
    									+ ", '"+ d21 +"', '"+ d22 +"', '"+ d23 +"', '"+ d24 +"')";  
    			stmt.executeUpdate(sql);
    			stmt.close();
    			conn.close();
			}
			catch(SQLException se) 
			{
				JOptionPane.showMessageDialog(null, "Não foi possível registar os dados da simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);
				se.printStackTrace();
			} 
			catch(Exception e) 
			{
				JOptionPane.showMessageDialog(null, "Não foi possível registar os dados da simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);
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
