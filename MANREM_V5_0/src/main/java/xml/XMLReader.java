/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author Paulo Bonifacio
 */
public  class               XMLReader {
    private File ObjectFile;
    private AgentName AllData;
    
    public ArrayList<String>            AgentNameByType (String type) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException{
        //, File xmlPath
        File xmlFile = new File("files/Config/AgentList.xml");
        XMLParser parser = new XMLParser();
        ArrayList<NameType> agentnames = parser.parseXml(new FileInputStream(xmlFile));
        ArrayList<String> listNames = new ArrayList<>();
        for (int i=0; i < agentnames.size(); i ++){
            if (agentnames.get(i).getType() == null ? type == null : agentnames.get(i).getType().equals(type)){
                listNames.add(agentnames.get(i).getName());
                
            }
        }   
        return listNames;
    }
    
    public ArrayList<String>            ProposalsByType (String type)throws FileNotFoundException, ParserConfigurationException, SAXException, IOException{
        File xmlFile = new File("files/Config/Proposals.xml");
        XMLParser parser = new XMLParser();
        ArrayList<NameType> proposalnames = parser.parseXml(new FileInputStream(xmlFile));
        ArrayList<String> listProposals = new ArrayList<>();
        for (int i=0; i < proposalnames.size(); i ++){
            if (proposalnames.get(i).getType() == null ? type == null : proposalnames.get(i).getType().equals(type)){
                listProposals.add(proposalnames.get(i).getName());
                
            }
        }   
        return listProposals;
    }
    
    public ArrayList<String>            VotMethodsByType (String type)throws FileNotFoundException, ParserConfigurationException, SAXException, IOException{
        File xmlFile = new File("files/Config/Voting.xml");
        XMLParser parser = new XMLParser();
        ArrayList<NameType> proposalnames = parser.parseXml(new FileInputStream(xmlFile));
        ArrayList<String> listProposals = new ArrayList<>();
        for (int i=0; i < proposalnames.size(); i ++){
            if (proposalnames.get(i).getType() == null ? type == null : proposalnames.get(i).getType().equals(type)){
                listProposals.add(proposalnames.get(i).getName());
                
            }
        }   
        return listProposals;
    }
     
    public ArrayList<String>            TeamStrategiesByType (String type)throws FileNotFoundException, ParserConfigurationException, SAXException, IOException{
        File xmlFile = new File("files/Config/Strategies.xml");
        XMLParser parser = new XMLParser();
        ArrayList<NameType> proposalnames = parser.parseXml(new FileInputStream(xmlFile));
        ArrayList<String> listProposals = new ArrayList<>();
        for (int i=0; i < proposalnames.size(); i ++){
            if (proposalnames.get(i).getType() == null ? type == null : proposalnames.get(i).getType().equals(type)){           
                listProposals.add(proposalnames.get(i).getName());
                
            }
        }   
        return listProposals;
    }
    
    
    
    
    //<editor-fold defaultstate="collapsed" desc="comment">
    /*    public ArrayList<String>           ConfigFile (String type)throws FileNotFoundException, ParserConfigurationException, SAXException, IOException{
     * //File xmlFile = new File("files\\"+ type +"\\"+ agentName +".xml");
     * XMLParser parser = new XMLParser();
     * ArrayList<ConfigRead> cfg = parser.parseXml(new FileInputStream(ObjectFile));
     * ArrayList<String> listAtributes = new ArrayList<>();
     * for (int i=0; i < cfg.size(); i ++){
     * if (cfg.get(i).getType() == null ? type == null : cfg.get(i).getType().equals(type)){
     * //System.out.println(agentnames.get(i).getName());
     * listAtributes.add(cfg.get(i).getName());
     *
     * }
     * }
     * return listAtributes;
     * }
     * // Attribute - Prices
     * public ArrayList<String>           readPrcAtt (String type)throws FileNotFoundException, ParserConfigurationException, SAXException, IOException{
     * //File xmlFile = new File("files\\"+ type +"\\"+ agentName +".xml");
     * XMLParser parser = new XMLParser();
     * ArrayList<ConfigRead> cfg = parser.parseXml(new FileInputStream(ObjectFile));
     * ArrayList<String> listAtributes = new ArrayList<>();
     * for (int i=0; i < cfg.size(); i ++){
     * if (cfg.get(i).getPrice() == null ? type == null : cfg.get(i).getPrice().equals(type)){
     * System.out.println(cfg.get(i).getName());
     * listAtributes.add(cfg.get(i).getName());
     *
     * }
     * }
     * return listAtributes;
     * }
     * // Attribute - Volumes
     * public ArrayList<String>           readVolAtt (String type)throws FileNotFoundException, ParserConfigurationException, SAXException, IOException{
     * //File xmlFile = new File("files\\"+ type +"\\"+ agentName +".xml");
     * XMLParser parser = new XMLParser();
     * ArrayList<ConfigRead> cfg = parser.parseXml(new FileInputStream(ObjectFile));
     * ArrayList<String> listAtributes = new ArrayList<>();
     * for (int i=0; i < cfg.size(); i ++){
     * if (cfg.get(i).getType() == null ? type == null : cfg.get(i).getType().equals(type)){
     * //System.out.println(agentnames.get(i).getName());
     * listAtributes.add(cfg.get(i).getName());
     *
     * }
     * }
     * return listAtributes;
     * }*/
    //</editor-fold>
        
        
        
      public ArrayList        agentRead(String agentName, String type) throws FileNotFoundException, IOException{
        File xmlSource      = new File("files/"+ type +"/"+ agentName + "/" + agentName + ".xml");
        AgentParser parser  = new AgentParser();
        ArrayList aname     = parser.parseAgent(new FileInputStream(xmlSource));
        
        /* try {
         * SAXParserFactory parserFactor   =   SAXParserFactory.newInstance();
         * SAXParser   parser  =   parserFactor.newSAXParser();
         * AgentParserHandler handler  =   new AgentParserHandler();
         * parser.parse(xmlSource, handler);
         * 
         * } catch (ParserConfigurationException | SAXException | IOException ex) {
         * Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, ex);
         * }*/
        
        
        
        
        return aname;
        
        
    }  
    
    public                              XMLReader(String agentName, String type){
        
        ObjectFile = new File("files/"+ type +"/"+ agentName + "/" + agentName + ".xml");
        
        
        
    }
}
