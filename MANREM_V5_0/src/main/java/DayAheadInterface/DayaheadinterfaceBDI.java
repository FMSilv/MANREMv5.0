package DayAheadInterface;

import services.chatService.ChatService;
import services.chatService.IChatService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.clock.IClockService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import personalassistant.MarketParticipants;
import personalassistant.PersonalAssistantBDI;
import personalassistant.PersonalAssistantGUI;
import wholesalemarket_LMP.Pricing_Mechanism_Form;
import wholesalemarket_LMP.Wholesale_InputData;
import wholesalemarket_SMP.SMP_Market_Controller;

/**
 *
 * @author Filipe Silvério
 */
@Agent
@Description("DayaheadinterfaceBDI agent. <br>")
@Arguments
(value={
	@Argument(name="chatOn", description="dayaheadinterfaceBDI.chatOn", clazz=String.class, defaultvalue="\"0\""), 
})
@RequiredServices
({
	@RequiredService(name="clockservice", type=IClockService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="chatservices", type=IChatService.class, multiple=true, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, dynamic=true))
})
@ProvidedServices
({
	@ProvidedService(type=IChatService.class, implementation=@Implementation(ChatService.class))
})
public class DayaheadinterfaceBDI{
    
	private MarketParticipants participants;
	
	@AgentArgument
	protected String chatOn;
    
    private String[][] producerOffers = new String[23][2];
    private String[][] buyerOffers = new String[23][2];
    
    /*
     * Pricing Mechanism GUI variables
     */
    private PersonalAssistantGUI mo_gui;
    private Wholesale_InputData lmpMode;
    private SMP_Market_Controller smpMode;
   
    public DayaheadinterfaceBDI(){
        
    }
    
	@AgentCreated
	public void init() {
		
	}
    
    /*
     * Pricing Mechanism GUI constructor
     */
    public DayaheadinterfaceBDI(PersonalAssistantGUI _market, Wholesale_InputData _lmpMode, SMP_Market_Controller _smpMode){
        mo_gui = _market;
        lmpMode = _lmpMode;
        smpMode = _smpMode;
    }
    

    public void pricingMechanismForm(String mode){
        Pricing_Mechanism_Form priceMechanism = new Pricing_Mechanism_Form(mo_gui, lmpMode, smpMode);
        
        priceMechanism.setVisible(true);
        
        switch(mode){
            case "DayAheadMarket":
                priceMechanism.editWindow_options(true);
                break;
            case "IntraDayMarket":
                priceMechanism.editWindow_options(false);
                break;
            default:
                break;
        }
    }
    
    /*
    * Call GenCo Data GUI
    */
    public void chooseParticipants(PersonalAssistantBDI market, boolean isProducer, boolean isDayAhead, boolean isSMP, boolean isOTC){
        participants = new MarketParticipants(market, isProducer, isDayAhead, isSMP, isOTC);
        participants.setVisible(true);
    }
    
    /*
    * Method to store producer offer values
    */
    public void storeProducerOffers( String[][] _producerOffer ){
        this.producerOffers = _producerOffer;
    }
    
    /*
    * Method to store buyer offer values
    */
    public void storeBuyerOffers( String[][] _buyerOffer ){
        this.buyerOffers = _buyerOffer;
    }
    
    /*
    * Send producer offer values
    */
    public String[][] sendProducerOffer(){
        return this.producerOffers;
    }
    
    /*
    * Send buyer offer values
    */
    public String[][] sendBuyerOffer(){
        return this.buyerOffers;
    }
    
    
}
