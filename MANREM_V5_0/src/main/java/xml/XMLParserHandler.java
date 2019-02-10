/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Paulo Bonifacio
 */
public class            XMLParserHandler extends DefaultHandler {
    
    private ArrayList   newList         =   new ArrayList<>();
    private Stack       elementStack    =   new Stack<>();
    private Stack       objectStack     =   new Stack<>();
    
    @Override
     public void                        startDocument() throws SAXException {
        //System.out.println("start of the document   : ");
    }

    @Override
    public void                         endDocument() throws SAXException {
        //System.out.println("end of the document document     : ");
    }

    @Override
    public void                         startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                                                                                //Push it in element stack
        this.elementStack.push(qName);
                                                                                //If this is start of 'user' element then prepare a new User instance and push it in object stack
        if ("Agent".equals(qName) || "Proposal".equals(qName)|| "Method".equals(qName)) {
                                                                                //New User instance
            NameType nametype = new NameType();
            
                                                                                //Set all required attributes in any XML element here itself
           
            if(attributes != null && attributes.getLength() == 1) {
            	nametype.setType(attributes.getValue(qName));            //.getType(qName)); 0
                
            }
            this.objectStack.push(nametype);
            
        }
    }

    @Override
    public void                         endElement(String uri, String localName, String qName) throws SAXException
    {	
    	//Remove last added </user> element
        this.elementStack.pop();

        //User instance has been constructed so pop it from object stack and push in userList
        if ("Agent".equals(qName) || "Proposal".equals(qName)|| "Method".equals(qName))
        {
            NameType object = (NameType) this.objectStack.pop();
            this.newList.add(object);
        }
    }

    /**
     * This will be called everytime parser encounter a value node
     * */
    @Override
    public void                         characters(char[] ch, int start, int length) throws SAXException
    {
        String value = new String(ch, start, length).trim();

        if (value.length() == 0)
        {
            return; // ignore white space
        }
        
        //handle the value based on to which element it belongs
        if ("localname".equals(currentElement()) || "Name".equals(currentElement()))
        {
            NameType nametype = (NameType) this.objectStack.peek();
            nametype.setName(value);
            //
        }else if("type".equals(currentElement()) || "Type".equals(currentElement())){
            NameType nametype = (NameType) this.objectStack.peek();
            nametype.setType(value);
        }
    }
    
    /**
     * Utility method for getting the current element in processing
     * */
    private Object                      currentElement()
    {
        return this.elementStack.peek();
    }
    
    //Accessor for userList object
    public ArrayList<NameType>        getUsers()
    {
    	return newList;
    }
    
    
}
