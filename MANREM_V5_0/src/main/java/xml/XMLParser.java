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

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class            XMLParser {
    
    public ArrayList                parseXml(InputStream in){
		//Create a empty link of users initially
        ArrayList<NameType> nametype = new ArrayList<>();
		try {
			//Create default handler instance
			XMLParserHandler handler = new XMLParserHandler();
			
			//Create parser from factory
			XMLReader parser = XMLReaderFactory.createXMLReader();
			
			//Register handler with parser
			parser.setContentHandler(handler);

			//Create an input source from the XML input stream
			InputSource source = new InputSource(in);
			
			//parse the document
			parser.parse(source);
			
			//populate the parsed users list in above created empty list; You can return from here also.
			nametype = handler.getUsers();

		} catch (SAXException | IOException e) {} finally {}
                
		return nametype;
	}
    

}
