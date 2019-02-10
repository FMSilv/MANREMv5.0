/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

/**
 *
 * @author Paulo Bonifacio
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class AgentParser {
    
    ArrayList<AgentName>    agentname   =   new ArrayList();
    
    public ArrayList        parseAgent(InputStream in) throws IOException{
        try {
            AgentParserHandler  handler =   new AgentParserHandler();
            
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(handler);

			//Create an input source from the XML input stream
            InputSource source = new InputSource(in);
			
			//parse the document
            parser.parse(source);
			
			//populate the parsed users list in above created empty list; You can return from here also.
            agentname   =   handler.AllData;
            
        } catch (SAXException ex) {
            Logger.getLogger(AgentParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return agentname;
    }
    
}
