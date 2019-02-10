/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DayAheadInterface;

import jade.core.Agent;
import personalassistant.MarketParticipants;
import personalassistant.PersonalAssistant;
import personalassistant.PersonalAssistantGUI;
import wholesalemarket_LMP.Pricing_Mechanism_Form;
import wholesalemarket_LMP.Wholesale_InputData;
import wholesalemarket_SMP.SMP_Market_Controller;

/**
 *
 * @author Filipe
 */
public class Dayaheadinterface extends Agent{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MarketParticipants participants;
    
    private String[][] producerOffers = new String[23][2];
    private String[][] buyerOffers = new String[23][2];
    
    //private PersonalAssistant mo_pa;
    
    /*
     * Pricing Mechanism GUI variables
     */
    private PersonalAssistantGUI mo_gui;
    private Wholesale_InputData lmpMode;
    private SMP_Market_Controller smpMode;
    
    @Override
    protected void setup(){
        
    }
   
    public Dayaheadinterface(){
        
    }
    
    /*
     * Pricing Mechanism GUI constructor
     */
    public Dayaheadinterface(PersonalAssistantGUI _market, Wholesale_InputData _lmpMode, SMP_Market_Controller _smpMode){
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
    public void chooseParticipants(PersonalAssistant market, boolean isProducer, boolean isDayAhead, boolean isSMP, boolean isOTC){
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
