package wholesalemarket_LMP;

import jade.core.Agent;
import wholesalemarket_LMP.simul.SupplierData;
import wholesalemarket_LMP.simul.GridData;
import wholesalemarket_LMP.simul.ProducerData;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

public class Wholesale_InputData {

    private int totalBus = -1;
    private int totalBranch = -1;

    private DefaultListModel producer_Names;
    private int totalProducers = -1;
    private String producers_TotalNames = "";

    private DefaultListModel supplier_Names;
    private DefaultListModel LConsumer_Names;
    private DefaultListModel Consumer_Names;
    private int totalSuppliers = -1;
    private int totalLConsumer = -1;
    private int totalConsumer = -1;
    private String suppliers_TotalNames = "";
    private String LConsumer_TotalNames = "";
    private String Consumer_TotalNames = "";

    private ArrayList<GridData> GRID;
    private ArrayList<ProducerData> PRODUCER;
    private ArrayList<SupplierData> SUPPLIER;
    
    private Agent market;

    private GridGlobalParameters frame_GRID;
    private BranchesInputParameters frame_GRID_Branches;
    private Producer_InputParameters frame_PRODUCER;
    private Supplier_InputParameters frame_SUPPLIER;

    private static double VOLTAGE_BASE;
    private static double S_POWER_BASE;

    public static boolean editHour = false;

    public Wholesale_InputData(Agent Market) {
        market = Market;
        initJFrames();
        VOLTAGE_BASE = 10;
        S_POWER_BASE = 100;
    }

    public void setVoltageBase(double _voltageBase) {
        VOLTAGE_BASE = _voltageBase;
    }

    public static double getVoltageBase() {
        return VOLTAGE_BASE;
    }

    public void setSPowerBase(double _sPowerBase) {
        S_POWER_BASE = _sPowerBase;
    }

    public static double getSPowerBase() {
        return S_POWER_BASE;
    }
    
    public final void initJFrames() {
        frame_GRID = new GridGlobalParameters(this);
        frame_GRID.setVisible(false);

        frame_GRID_Branches = new BranchesInputParameters(this);
        frame_GRID_Branches.setVisible(false);

        /*if (_allDay) {
            frame_PRODUCER = new Producer_InputParameters(this);
            frame_PRODUCER.setVisible(false);

            frame_SUPPLIER = new Supplier_InputParameters(this);
            frame_SUPPLIER.setVisible(false);
        }*/
    }

    public void activeFrame_GRID(boolean _changeHour) {
        editHour = _changeHour;
        //frame_GRID.editableHour(false);
        frame_GRID.setVisible(true);
        //initJFrames();
    }

    public void activeFrame_BRANCHES() {
        frame_GRID_Branches.startBranchFrame();
        frame_GRID_Branches.setVisible(true);
    }

    public void createFrame_Producer_Supplier() {
        frame_PRODUCER = new Producer_InputParameters(market,this);
        frame_PRODUCER.startProducerFrame();
        frame_PRODUCER.setVisible(false);
        frame_SUPPLIER = new Supplier_InputParameters(this);
        frame_SUPPLIER.startSupplierFrame();
        frame_SUPPLIER.setVisible(false);
    }

    public void activeFrame_PRODUCER() {
        frame_PRODUCER = new Producer_InputParameters(market,this);
        frame_PRODUCER.startProducerFrame();
        frame_PRODUCER.setVisible(true);
    }

    public void activeFrame_SUPPLIER() {
        frame_SUPPLIER = new Supplier_InputParameters(this);
        frame_SUPPLIER.startSupplierFrame();
        frame_SUPPLIER.setVisible(true);
    }

    public int getTotalBus() {
        return totalBus;
    }

    public int getTotalBranch() {
        return totalBranch;
    }

    public DefaultListModel getProducerList() {
        return producer_Names;
    }

    public DefaultListModel getSupplierList() {
        return supplier_Names;
    }

    public String getProducerNames() {
        return producers_TotalNames;
    }

    public String getSupplierNames() {
        return suppliers_TotalNames;
    }
    
     public String getLConsumerNames() {
        return  LConsumer_TotalNames;
    }
     
      public String getConsumerNames() {
        return Consumer_TotalNames;
    }

      public int getTotalLConsumer() {
        return totalLConsumer;
    }
      
        public int getTotalConsumer() {
        return totalConsumer;
    }
      
    public int getTotalProducer() {
        return totalProducers;
    }

    public int getTotalSupplier() {
        return totalSuppliers;
    }

    public void setTotalBus(int _totalBus) {
        totalBus = _totalBus;
    }

    public void setTotalBranch(int _totalBranch) {
        totalBranch = _totalBranch;
    }

    public void setProducerAgentName(DefaultListModel _producerNames) {
        producer_Names = _producerNames;
        totalProducers = producer_Names.size();
        producers_TotalNames = producer_Names.toString();
    }

    public void setSupplierAgentName(DefaultListModel _supplierNames) {
        supplier_Names = _supplierNames;
        totalSuppliers = supplier_Names.size();
        suppliers_TotalNames = supplier_Names.toString();
    }
    
     public void setLConsumerAgentName(DefaultListModel _LConsumerNames) {
        LConsumer_Names = _LConsumerNames;
        totalLConsumer = LConsumer_Names.size();
        LConsumer_TotalNames = LConsumer_Names.toString();
    }
     
          public void setConsumerAgentName(DefaultListModel _ConsumerNames) {
        Consumer_Names = _ConsumerNames;
        totalConsumer = Consumer_Names.size();
        Consumer_TotalNames = Consumer_Names.toString();
    }

    public String[] splitAgentTotalNames(String _names, int _num) {
        String aux = _names.substring(_names.indexOf("[") + 1, _names.indexOf("]"));
        String[] agentNames = aux.split(", ", _num);
        return agentNames;
    }

    public void setGRID_List(ArrayList<GridData> _GRID) {
        GRID = _GRID;
    }

    public ArrayList<GridData> getGRID_List() {
        return GRID;
    }

    public void setPRODUCER_List(ArrayList<ProducerData> _PRODUCER) {
        PRODUCER = _PRODUCER;
    }

    public ArrayList<ProducerData> getPRODUCER_List() {
        return PRODUCER;
    }

    public void setSUPPLIER_List(ArrayList<SupplierData> _SUPPLIER) {
        SUPPLIER = _SUPPLIER;
    }

    public ArrayList<SupplierData> getSUPPLIER_List() {
        return SUPPLIER;
    }
}
