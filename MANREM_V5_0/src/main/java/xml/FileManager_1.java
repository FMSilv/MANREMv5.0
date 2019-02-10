package xml;

/**
 *
 * @author Hugo
 */
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileManager_1 {

    private String path = "files\\";
    private HashMap<String, Document> files = new HashMap();
    private NodeList elements_list;
    private String BUYER_PLAN_TEMPLATES;
    private String OBJECTIVES;
    private String agent_name;

    public FileManager_1(String agent_name) {
        this.agent_name = agent_name;
        BUYER_PLAN_TEMPLATES = "files\\" + agent_name + "\\plan_templates.xml";
        OBJECTIVES = "files\\" + agent_name + "\\objectives.xml";
    }

    private void readFile(String f) {
        try {
            File file = new File(f);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();



            files.put(f, doc);


            if (f.contains("plan_templates")) {
                elements_list = doc.getElementsByTagName("header");
            } else if (f.contains("beliefs")) {
                elements_list = doc.getElementsByTagName("localname");
            } else if (f.contains("objectives")) {
                elements_list = doc.getElementsByTagName("objective");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> lookupObjectives() {


        // FIQUEI AQUI 08-05
        readFile(OBJECTIVES);
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < elements_list.getLength(); i++) {

            list.add(elements_list.item(i).getFirstChild().getNodeValue());
        }
        return list;
    }

    public ArrayList<String> readBeliefsFile(String other_agent_name) {
        String BELIEFS_FILE = "files\\" + this.agent_name + "\\beliefs_" + other_agent_name + ".xml";
        ArrayList<String> beliefs_values = new ArrayList<>();;
        readFile(BELIEFS_FILE);

        for (int i = 0; i < elements_list.getLength(); i++) {
            beliefs_values = new ArrayList<>();
            Node localname = elements_list.item(i);
            String localname_value = localname.getFirstChild().getNodeValue();
            beliefs_values.add(localname_value);
            Element agent = (Element) localname.getParentNode();
            NodeList beliefs = agent.getElementsByTagName("belief");
            // values = (NodeList) agent.getChildNodes().item(1).getChildNodes();
            for (int j = 0; j < beliefs.getLength(); j++) {
                Element value = (Element) beliefs.item(j);
                String value_aux = value.getFirstChild().getNodeValue();
                beliefs_values.add(value_aux);
//                String[] value_aux_array = value_aux.split(";");
//                agent_values.add(value_aux_array[0]);
//                agent_values.add(value_aux_array[1]);
            }
        }
        return beliefs_values;
    }



    private String replaceArgs(String s, ArrayList<String> values, int size) {

        for (int i = 0; i < size; i++) {
            CharSequence old_string = "arg" + (i + 1);
            CharSequence new_string = values.get(i);
            s = s.replace(old_string, new_string);
        }
        return s;
    }

    public HashMap<String, Document> getFiles() {
        return files;
    }

public void printXmlBelief(String agent_name, String target_agent, ArrayList<String> beliefs) {


    try {
 
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("agent");
		doc.appendChild(rootElement);
 
		// staff elements
		Element localname = doc.createElement("localname");
		rootElement.appendChild(localname);
                localname.appendChild(doc.createTextNode(target_agent));
                
                
                Element beliefs_root = doc.createElement("beliefs");
		rootElement.appendChild(beliefs_root);
                
                for(int i=0; i<beliefs.size(); i++){
                Element belief = doc.createElement("belief");
		rootElement.appendChild(belief);
                belief.appendChild(doc.createTextNode(beliefs.get(i)));
                }
 
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(path+agent_name+"\\beliefs_"+target_agent+".xml"));
 
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		transformer.transform(source, result);
 
 
	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
	
    }
}
