/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.ArrayList;

/**
 *
 * @author Paulo Bonifacio
 */
public class            AgentParserHandler extends DefaultHandler {
    
    private Config      config;
    private Prices      prices;
    private Volumes     volumes;
    private AgentName   agentname;
    private String      attContent;
   
    //private ArrayList<Volumes>   VolData;
    //private ArrayList<Prices>    PrcData;
    private Prices              PrcData;
    private Volumes             VolData;
    private Config              CfgData;
    
    public  ArrayList<AgentName> AllData;
            
    
    public AgentParserHandler(){
        super();
        PrcData =   new Prices();           //ArrayList<>();
        //AllData =   new ArrayList<>();
        VolData =   new Volumes();          //ArrayList<>();
        CfgData =   new Config();
        AllData =   new ArrayList<>();
                                            // VolData =   new ArrayList<>;
    }
    
    /*    @Override
     * public void                        startDocument() throws SAXException {
     * //System.out.println("start of the document   : ");
     * }
     * 
     * @Override
     * public void                         endDocument() throws SAXException {
     * //System.out.println("end of the document document     : ");
     * }*/
    //@Override;
    //}
    
    /*    @Override
     * public void                        startDocument() throws SAXException {
     * //System.out.println("start of the document   : ");
     * }
     * 
     * @Override
     * public void                         endDocument() throws SAXException {
     * //System.out.println("end of the document document     : ");
     * }*/
    @Override
    public void                         startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        
        switch (qName){
            case "Agent":
                agentname   =   new AgentName();
                break;
            case "Config":
                config      =   new Config();
                break;
            case "Prices":
                prices      =   new Prices();
                break;
            case "Volumes":
                volumes     =   new Volumes();
                break;
                
         }    
        }
        
        
        //<editor-fold defaultstate="collapsed" desc="comment">
        //Push it in element stack
        /*        this.elementStack.push(qName);
         * //If this is start of 'user' element then prepare a new User instance and push it in object stack
         * if ("Agent".equals(qName) || "Proposal".equals(qName)|| "Method".equals(qName)) {
         * //New User instance
         * ConfigRead att = new ConfigRead();
         * 
         * //Set all required attributes in any XML element here itself
         * 
         * if(attributes != null && attributes.getLength() == 1) {
         * att.setType(attributes.getValue(qName));            //.getType(qName)); 0
         * 
         * }
         * this.objectStack.push(att);
         * 
         * }*/
        //</editor-fold>
    

    @Override
    public void                         endElement(String uri, String localName, String qName) throws SAXException
    {	
        
        switch (qName){
            case "Agent":
                agentname.setCfgData(CfgData);
                //agentname.setPrcData(prices);
                //agentname.setVolData(volumes);
                agentname.setPrcData(PrcData);
                agentname.setVolData(VolData);
                AllData.add(agentname);
                break;
            case "Name":
                agentname.setName(attContent);
                break;
            case "Type":
                agentname.setType(attContent);
                break;
            case "NumberAtributes":
                agentname.setNumberAtributes(attContent);
                break;
            case "Config":    
                CfgData = config;
                break;
            case "Strategy":
                //config.Strategy =   attContent;
                config.setStrategy(attContent);
                break;
            case "Protocol":
                //config.Protocol =   attContent;
                config.setProtocol(attContent);
                break;
            case "Preference":
                //config.Preferences =   attContent;
                config.setPreferences(attContent);
                break;
            case "Prices":
                //PrcData.add(prices);
                PrcData = prices;
                break;
            case "Price":
                prices.setPrice(attContent);
                break;
            case "pMax":
                prices.setpMax(attContent);
                break;
            case "pFlex":
                prices.setpFlex(attContent);
                break;
            case "Volumes":
                //VolData.add(volumes);
                VolData = volumes;
                break;
            case "Volume":
                volumes.setVolume(attContent);
                break;
            case "vMax":
                volumes.setvMax(attContent);
                break;
            case "vMin":
                volumes.setvMin(attContent);
                break;
            case "vFlex":
                volumes.setvFlex(attContent);
                break;
                       
        }
                
        //<editor-fold defaultstate="collapsed" desc="comment">
        /*        //Remove last added </user> element
         * this.elementStack.pop();
         * 
         * //User instance has been constructed so pop it from object stack and push in userList
         * if ("Agent".equals(qName) || "Proposal".equals(qName)|| "Method".equals(qName))
         * {
         * ConfigRead object = (ConfigRead) this.objectStack.pop();
         * this.newList.add(object);
         * }*/
        //</editor-fold>
    }

    /**
     * This will be called everytime parser encounter a value node
     * */
    @Override
    public void                         characters(char[] ch, int start, int length) throws SAXException
    {
        attContent = new String(ch, start, length).trim();
        
        //<editor-fold defaultstate="collapsed" desc="comment">
        /*if (value.length() == 0)
         * {
         * return; // ignore white space
         * }
         * 
         * //handle the value based on to which element it belongs
         * if ("localname".equals(currentElement()) || "Name".equals(currentElement()))
         * {
         * ConfigRead att = (ConfigRead) this.objectStack.peek();
         * att.setName(value);
         * //
         * }*/
        //</editor-fold>
                
                
    }
    
   
}
