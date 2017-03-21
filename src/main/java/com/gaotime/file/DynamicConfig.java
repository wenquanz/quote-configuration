package com.gaotime.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

/**
 * 本方法处理dynamicconfig.xml,转为boardconfig.json 一些细节的处理：
 * xml和json的结构不对称，需要建立两个模型，设置对应属性 CodeRule分两种类型 去掉空白字符 null
 *
 */
@SuppressWarnings("restriction")
public class DynamicConfig {

    // default xml path
    private static String xmlPath = "C:\\Users\\wenquanz\\Desktop\\dynamicconfig.xml";

    private static String outputPath = "C:\\Users\\wenquanz\\Desktop\\dynamicconfig_tmp.xml";
    // default destination path
    private static String jsonPath = "C:\\Users\\wenquanz\\Desktop\\boardconfig.json";

    public static void main(String[] args) {
	if (args.length > 0 && !args[0].trim().isEmpty()) {
	    xmlPath = args[0];
	}
	if (args.length > 1 && !args[1].trim().isEmpty()) {
	    jsonPath = args[0];
	}

	updateXml();
	
//	generateJson();
    }

    // 更新dynamicconfig.xml
    // jdbc
    public static void updateXml() {
	try {
	    Document doc = DocumentBuilderFactory.newInstance()
		    .newDocumentBuilder().parse(new InputSource(xmlPath));

	    NodeList list = doc.getElementsByTagName("BoardIndexSet");
	    if (list.getLength() > 0) {
		Node boardIndexSet = list.item(0);

	    }
	    // 行业
	    NodeList indexes = doc.getElementsByTagName("IndexCategory");
	    
	    Node node = indexes.item(0);
	    while(node.hasChildNodes()){
		node.removeChild(node.getFirstChild());
	    }
	    
	    
	    Element e = doc.createElement("Index");
	    e.setAttribute("code", "BI0001");
	    e.setAttribute("boardid", "3073000000");
	    e.setAttribute("name", "农林牧渔");
	    e.setAttribute("basedate", "20170317");
	    e.setAttribute("baseperiod", "12345678");
	    e.setAttribute("basepoint", "1000");
	    e.setAttribute("compositionstocknum", "20");
	    e.setAttribute("tradabletotalcap", "12345678");
	    node.appendChild(e);

	    Transformer xformer;
	    try {
		xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform(new DOMSource(doc), new StreamResult(
			new File(outputPath)));
	    } catch (TransformerConfigurationException e1) {
		e1.printStackTrace();
	    } catch (TransformerFactoryConfigurationError e1) {
		e1.printStackTrace();
	    } catch (TransformerException e1) {
		e1.printStackTrace();
	    }

	    // 2- Locate the node(s) with xpath
	    // XPath xpath = XPathFactory.newInstance().newXPath();

	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}

	/*
	 * NodeList nodes =
	 * (NodeList)xpath.evaluate("//*[contains(@value, '!Here')]", doc,
	 * XPathConstants.NODESET);
	 * 
	 * // 3- Make the change on the selected nodes for (int idx = 0; idx <
	 * nodes.getLength(); idx++) { Node value =
	 * nodes.item(idx).getAttributes().getNamedItem("value"); String val =
	 * value.getNodeValue(); value.setNodeValue(val.replaceAll("!Here",
	 * "What?")); }
	 * 
	 * // 4- Save the result to a new XML doc Transformer xformer =
	 * TransformerFactory.newInstance().newTransformer();
	 * xformer.transform(new DOMSource(doc), new StreamResult(new
	 * File(outputFile)));
	 */
    }
    
    
    public static void generateJson(){
	BoardConfig config = JAXB.unmarshal(new File(xmlPath),
		BoardConfig.class);

	JsonConfig jsonConfig = new JsonConfig();
	jsonConfig.setBoardIndex(config.getBoardIndex().getList());
	jsonConfig.setBoardset(config.getBoardSet());

	Writer writer = null;
	try {
	    String json = JSON.std.asString(jsonConfig);
	    writer = new BufferedWriter(new OutputStreamWriter(
		    new FileOutputStream(jsonPath)));
	    // writer = new OutputStreamWriter(new FileOutputStream(jsonPath));
	    writer.write(json);
	    writer.close();
	    System.out.println("length: " + json.length());
	    System.out.println("last string: "
		    + json.substring(json.length() - 100));
	} catch (JSONObjectException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {

	}
    }
}
