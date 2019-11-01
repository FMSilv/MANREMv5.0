package xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AddXmlNode {
	
	
    public void addNode(String agentName, String firstName, String lastName, String address, String phone, String type) throws Exception {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(System.getProperty("user.dir")+"\\src\\main\\java\\xml\\beliefs\\beliefs.xml");
        Element root = document.getDocumentElement();

        
        NodeList flowList = document.getElementsByTagName("marketInfo");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (agentName.equals(childNode.getNodeName())) {
                	childNode.getParentNode().removeChild(childNode);
                }
            }
        }
        
            Element newNode = document.createElement(agentName);
            newNode.setAttribute("id", String.valueOf((Math.random() * ((999999999 - 000000000) + 1)) + 000000000));
            
            Element E_firstname = document.createElement("firstname");
            E_firstname.appendChild(document.createTextNode(firstName));
            newNode.appendChild(E_firstname);

            Element E_lastname = document.createElement("lastname");
            E_lastname.appendChild(document.createTextNode(lastName));
            newNode.appendChild(E_lastname);
            
            Element E_address = document.createElement("address");
            E_address.appendChild(document.createTextNode(address));
            newNode.appendChild(E_address);
            
            Element E_phone = document.createElement("phone");
            E_phone.appendChild(document.createTextNode(phone));
            newNode.appendChild(E_phone);
            
            Element E_type = document.createElement("type");
            E_type.appendChild(document.createTextNode(type));
            newNode.appendChild(E_type);

            root.appendChild(newNode);
        

        DOMSource source = new DOMSource(document);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StreamResult result = new StreamResult(System.getProperty("user.dir")+"\\src\\main\\java\\xml\\beliefs\\beliefs.xml");
        transformer.transform(source, result);
    }


    
}