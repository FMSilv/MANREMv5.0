package xml;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.HashMap;

public class ReadXMLFile {


	  public HashMap<String, String> getMarketInfo(String tag) {
		  HashMap<String, String> hashMap = new HashMap<String, String>();
		    try {
		    	File fXmlFile = new File(System.getProperty("user.dir")+"\\src\\main\\java\\xml\\beliefs\\beliefs.xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
						
				doc.getDocumentElement().normalize();

				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
						
				NodeList nList = doc.getElementsByTagName(tag);
						
				System.out.println("----------------------------");

				for (int temp = 0; temp < nList.getLength(); temp++) {

					Node nNode = nList.item(temp);
							
					System.out.println("\nCurrent Element :" + nNode.getNodeName());
							
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;

						hashMap.put("id", eElement.getAttribute("id"));
						hashMap.put("firstName", eElement.getElementsByTagName("firstname").item(0).getTextContent());
						hashMap.put("lastName", eElement.getElementsByTagName("lastname").item(0).getTextContent());
						hashMap.put("phone", eElement.getElementsByTagName("phone").item(0).getTextContent());
						hashMap.put("address", eElement.getElementsByTagName("address").item(0).getTextContent());
						hashMap.put("type", eElement.getElementsByTagName("type").item(0).getTextContent());
					}
				}
			    } catch (Exception e) {
				e.printStackTrace();
			    }
		    return hashMap;
	  }

	}