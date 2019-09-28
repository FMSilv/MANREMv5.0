package dataStorageAssistant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import wholesalemarket_SMP.Simulation;

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
    			getLastSimulation();
    		}
    		else if(content.equals("SMPsym - Store simulation data"))
    		{
    			if(insertSimulationsData("SMPsym"))
    			{
        			String lastId = getLastID("SELECT I_ID FROM (SELECT I_ID FROM SIMULATIONS_HISTORY ORDER BY DATE DESC) WHERE ROWNUM = 1", "I_ID");
        			storeValuesManagement(lastId);
    			}
    		}
    		else if(content.equals("SMPasym - Store simulation data"))
    		{
    			if(insertSimulationsData("SMPasym"))
    			{
        			String lastId = getLastID("SELECT I_ID FROM (SELECT I_ID FROM SIMULATIONS_HISTORY ORDER BY DATE DESC) WHERE ROWNUM = 1", "I_ID");
        			storeValuesManagement(lastId);
    			}
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
    	
    	
    	
    	protected void getLastSimulation() {
    		
			String lastSimId = getLastID("SELECT SIM_ID FROM (SELECT DISTINCT SIM_ID FROM SIMULATIONS_DATA ORDER BY SIM_ID DESC) WHERE ROWNUM = 1", "SIM_ID");

    	    ArrayList<AgentData> sellers = new ArrayList<AgentData>();
    	    ArrayList<AgentData> buyers = new ArrayList<AgentData>();
			
    	    String[] sellerNames = getNames("SELECT DISTINCT NAME FROM SIMULATIONS_DATA WHERE SIM_ID = " + lastSimId + " AND TYPE = 'Producer'");
    	    String[] buyerNames = getNames("SELECT DISTINCT NAME FROM SIMULATIONS_DATA WHERE SIM_ID = " + lastSimId + " AND TYPE = 'Buyer'");
    		
//			String[][] allInfoLastSimulation = getLastSimulation_AllInfo("SELECT NAME, TYPE, VALUE, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20, D21, D22, D23, D24 FROM SIMULATIONS_DATA WHERE SIM_ID = "+lastSimId);
    		
			int id = 0;
			for(String sellerName : sellerNames) {
				ArrayList<Float> sellerPowerInfo = geSimulationsDataSingleRowInfo("SELECT D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20, D21, D22, D23, D24 FROM SIMULATIONS_DATA WHERE SIM_ID = " + lastSimId + " AND NAME = '"+ sellerName +"' AND VALUE = 'Power';");
				ArrayList<Float> sellerPriceInfo = geSimulationsDataSingleRowInfo("SELECT D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20, D21, D22, D23, D24 FROM SIMULATIONS_DATA WHERE SIM_ID = " + lastSimId + " AND NAME = '"+ sellerName +"' AND VALUE = 'Price';");

				AgentData agentData = new AgentData(sellerName, id, sellerPriceInfo, sellerPowerInfo);
				sellers.add(agentData);
				id++;
			}
			
			id = 0;
			for(String buyerName : buyerNames) {
				ArrayList<Float> buyerPowerInfo = geSimulationsDataSingleRowInfo("SELECT D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20, D21, D22, D23, D24 FROM SIMULATIONS_DATA WHERE SIM_ID = " + lastSimId + " AND NAME = '"+ buyerName +"' AND VALUE = 'Power';");
				ArrayList<Float> buyerPriceInfo = geSimulationsDataSingleRowInfo("SELECT D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20, D21, D22, D23, D24 FROM SIMULATIONS_DATA WHERE SIM_ID = " + lastSimId + " AND NAME = '"+ buyerName +"' AND VALUE = 'Price';");

				AgentData agentData = new AgentData(buyerName, id, buyerPriceInfo, buyerPowerInfo);
				buyers.add(agentData);
				id++;
			}

    	    
//	        sendMessage(agent.getComponentIdentifier().getLocalName(), AgentName, "Get Las Simulation", "market_ontology", "no_protocol", "INFORM");

            Simulation sim = new Simulation(buyers, sellers, false);
            sim.run(0, 23, sellerNames, buyerNames);
    	}
    	
    	
 	   protected String[][] getLastSimulation_AllInfo(String query){
		     Connection conn = null; 
		      Statement stmt = null;
		      String[][] resultSetArray = null;
		      try {  
			     Class.forName("org.h2.Driver");
			     conn = DriverManager.getConnection("jdbc:h2:file:"+System.getProperty("user.dir").replace("\\", "\\\\") + "\\\\database\\\\h2db"+"","root","root");
		         stmt = conn.createStatement(
                       ResultSet.TYPE_SCROLL_INSENSITIVE,
                       ResultSet.CONCUR_UPDATABLE); 
		         String sql = query; 
		         ResultSet rs = stmt.executeQuery(sql);
		         rs.last();
		         int rowsNumber = rs.getRow();
			     String[][] array = new String[rowsNumber][27];
			     rs.beforeFirst();
			     int i=0;
		         while(rs.next()) {
			        array[i][0] = rs.getString("NAME");array[i][1] = rs.getString("TYPE");array[i][2] = rs.getString("VALUE");
			        array[i][3] = rs.getString("D1");array[i][4] = rs.getString("D2");array[i][5] = rs.getString("D3");
			        array[i][6] = rs.getString("D4");array[i][7] = rs.getString("D5");array[i][8] = rs.getString("D6");
			        array[i][9] = rs.getString("D7");array[i][10] = rs.getString("D8");array[i][11] = rs.getString("D9");
			        array[i][12] = rs.getString("D10");array[i][13] = rs.getString("D11");array[i][14] = rs.getString("D12");
			        array[i][15] = rs.getString("D13");array[i][16] = rs.getString("D14");array[i][17] = rs.getString("D15");
			        array[i][18] = rs.getString("D16");array[i][19] = rs.getString("D17");array[i][20] = rs.getString("D18");
			        array[i][21] = rs.getString("D19");array[i][22] = rs.getString("D20");array[i][23] = rs.getString("D21");
			        array[i][24] = rs.getString("D22");array[i][25] = rs.getString("D23");array[i][26] = rs.getString("D24");
			        i++;
		          }
		         resultSetArray = array;
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
		      return resultSetArray;
	   }
    	
    	
	   protected ArrayList<Float> geSimulationsDataSingleRowInfo(String query){
		     Connection conn = null; 
		      Statement stmt = null;
		      ArrayList<Float> resultSetArray = null;
		      try {  
			     Class.forName("org.h2.Driver");
			     conn = DriverManager.getConnection("jdbc:h2:file:"+System.getProperty("user.dir").replace("\\", "\\\\") + "\\\\database\\\\h2db"+"","root","root");
		         stmt = conn.createStatement(
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE); 
		         String sql = query; 
		         ResultSet rs = stmt.executeQuery(sql);
		         rs.last();
		         ArrayList<Float> arrayList = new ArrayList<Float>();
			     rs.beforeFirst();
		         while(rs.next()) {
		        	 arrayList.add(Float.valueOf(rs.getString("D1").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D2").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D3").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D4").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D5").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D6").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D7").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D8").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D9").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D10").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D11").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D12").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D13").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D14").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D15").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D16").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D17").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D18").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D19").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D20").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D21").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D22").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D23").trim()).floatValue());
		        	 arrayList.add(Float.valueOf(rs.getString("D24").trim()).floatValue());
		          }
		         resultSetArray = arrayList;
		         rs.close();
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } catch(Exception ex) {
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
		      return resultSetArray;
	   }
 	   
 	   
 	   protected String[] getNames(String query){
		     Connection conn = null; 
		      Statement stmt = null;
		      String[] resultSetArray = null;
		      try {  
			     Class.forName("org.h2.Driver");
			     conn = DriverManager.getConnection("jdbc:h2:file:"+System.getProperty("user.dir").replace("\\", "\\\\") + "\\\\database\\\\h2db"+"","root","root");
		         stmt = conn.createStatement(
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE); 
		         String sql = query; 
		         ResultSet rs = stmt.executeQuery(sql);
		         rs.last();
		         int rowsNumber = rs.getRow();
			     String[] array = new String[rowsNumber];
			     rs.beforeFirst();
			     int i=0;
		         while(rs.next()) {
		        	 array[i] = rs.getString("NAME");
			        i++;
		          }
		         resultSetArray = array;
		         rs.close();
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } catch(Exception ex) {
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
		      return resultSetArray;
	   }
    	
    	
    	
    	
    	
    	
    	
    	
    	protected boolean insertSimulationsData(String simulationType) {
    		
    		boolean success = true;
    		
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
				success = false;
				JOptionPane.showMessageDialog(null, "Não foi possível registar os dados da simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);
				se.printStackTrace();
			} 
			catch(Exception e) 
			{
				success = false;
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
    		return success;
    	}
    	
    	
    	protected String getLastID(String query, String queryHeaderResult) {
    		
 		     Connection conn = null; 
		      Statement stmt = null;
		      String resultSetString = null;
		      try {
		         Class.forName("org.h2.Driver");
		         conn = DriverManager.getConnection("jdbc:h2:file:"+System.getProperty("user.dir").replace("\\", "\\\\") + "\\\\database\\\\h2db"+"","root","root");
		         stmt = conn.createStatement(
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE);
		         String sql = query;
		         ResultSet rs = stmt.executeQuery(sql);
		         while(rs.next()) {
		        	 resultSetString = rs.getString(queryHeaderResult);
		          }
		         rs.close();
		      } catch(SQLException se) {
		    	  JOptionPane.showMessageDialog(null, "Não foi possível obter o ID da ultima simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);

		         se.printStackTrace();
		      } catch(Exception ex) { 
		    	  JOptionPane.showMessageDialog(null, "Não foi possível obter o ID da ultima simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);
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
