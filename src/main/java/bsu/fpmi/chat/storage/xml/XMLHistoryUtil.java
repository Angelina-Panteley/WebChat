package bsu.fpmi.chat.storage.xml;

import bsu.fpmi.chat.model.Message;
import bsu.fpmi.chat.model.MessageStorage;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XMLHistoryUtil {
	private static final String STORAGE_LOCATION = "C:/Users/ly2708sokol/Desktop/my/history.xml";
	
    private static final XMLHistoryUtil instance = new XMLHistoryUtil();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm ");

    private XMLHistoryUtil() {
        super();
    }
    
    public static XMLHistoryUtil getInstance() {
        return instance;
    }

    public synchronized static void startWritingToXML() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("messages");
            doc.appendChild(rootElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void addMessageToXML(Message message, Date currentDate) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(STORAGE_LOCATION);

            Element msgElement = doc.createElement("message");
            msgElement.setAttribute("id", message.getId());
            Node root = doc.getFirstChild();
            root.appendChild(msgElement);

            Element author = doc.createElement("author");
            author.appendChild(doc.createTextNode(message.getUserName()));
            msgElement.appendChild(author);

            Element text = doc.createElement("description");
            text.appendChild(doc.createTextNode(message.getDescription()));
            msgElement.appendChild(text);

            Element date = doc.createElement("date");
            date.appendChild(doc.createTextNode(dateFormat.format(currentDate)));
            msgElement.appendChild(date);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
            transformer.transform(source, result);

        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteMessageFromXML(String id) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(STORAGE_LOCATION);

            NodeList messages = doc.getElementsByTagName("message");

            for(int i = 0; i < messages.getLength(); i++) {
                Node message = messages.item(i);
                if(message.getNodeType() == Node.ELEMENT_NODE) {
                    if(((Element)message).getAttribute("id").equals(id)) {
                        message.getChildNodes().item(0).setTextContent("");
                        message.getChildNodes().item(1).setTextContent("");
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void editMessageInXML(String id, String text) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(STORAGE_LOCATION);

            NodeList messages = doc.getElementsByTagName("message");

            for(int i = 0; i < messages.getLength(); i++) {
                Node message = messages.item(i);
                if(message.getNodeType() == Node.ELEMENT_NODE) {
                    if(((Element)message).getAttribute("id").equals(id)) {
                        message.getChildNodes().item(1).setTextContent(text);
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void oldHistory(Logger logger) {
        try {
            File xmlFile = new File(STORAGE_LOCATION);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList messages = doc.getElementsByTagName("message");

            if (messages.getLength() == 0) {
                logger.info("Message history is empty");
                return;
            }

            logger.info("Loading message history... " + messages.getLength() + " messages");

            for (int i = 0; i < messages.getLength(); i++) {
                Node message = messages.item(i);
                if (message.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) message;
                    String mDate = element.getElementsByTagName("date").item(0).getTextContent();
                    String mDescription = element.getElementsByTagName("description").item(0).getTextContent();
                    String mAuthor = element.getElementsByTagName("author").item(0).getTextContent();
                    String mId = element.getAttribute("id");

                    MessageStorage.addMessage(new Message(mId, mAuthor, mDescription));

                    logger.info(mDate + " " + mAuthor + " : " + mDescription);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static synchronized boolean doesStorageExist() {
		File file = new File(STORAGE_LOCATION);
		return file.exists();
	}
}
