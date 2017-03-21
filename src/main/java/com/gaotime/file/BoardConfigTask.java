package com.gaotime.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.ibatis.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

/**
 * 暂定每天0点执行
 * 
 * @author wenquanz
 *
 */
public class BoardConfigTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(BoardConfigTask.class);
    
    private BoardDataSource boardDataSource;
    private String dynamicconfigPath;
//    private String tmpDynamicconfigPath;
    private String boardconfigPath;

    public BoardConfigTask(BoardDataSource boardDataSource, Properties prop) {
	this.boardDataSource = boardDataSource;
	this.dynamicconfigPath = prop.getProperty("dynamicconfig_path");
	this.boardconfigPath = prop.getProperty("boarconfig_path");
//	this.tmpDynamicconfigPath = prop.getProperty("tmp_dynamicconfig_path");
    }

    
    @SuppressWarnings("restriction")
    public void run() {
	try {

	    logger.info("BoardConfigTask start");

	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    String date = sdf.format(new Date());

	    Document doc = DocumentBuilderFactory.newInstance()
		    .newDocumentBuilder()
		    .parse(new InputSource(dynamicconfigPath));

	    NodeList boardSets = doc.getElementsByTagName("BoardSet");
	    NodeList boardIndexSets = doc.getElementsByTagName("BoardIndexSet");
	    if (boardIndexSets.getLength() <= 0 || boardSets.getLength() <= 0) {
		logger.error("dynamicconfig.xml is invalid");
		return;
	    }
	    Element boardIndexSet = (Element) boardIndexSets.item(0);
	    boardIndexSet.setAttribute("timestamp", date);
	    Element boardSet = (Element) boardSets.item(0);
	    boardSet.setAttribute("timestamp", date);
	    // 行业
	    NodeList indexes = doc.getElementsByTagName("IndexCategory");

	    Node node = indexes.item(0);
	    removeIndexAndBoard(node, boardSet);
	    List<Map<String, String>> industryBoard = boardDataSource
		    .getIndustryBoard();
	    for (Map<String, String> map : industryBoard) {
		Element e = doc.createElement("Index");
		e.setAttribute("code", map.get("indexcode"));
		e.setAttribute("boardid", map.get("sectornum"));
		e.setAttribute("name", map.get("sectorname"));
		e.setAttribute("basedate", date);
		e.setAttribute("baseperiod", map.get("fmarketval"));
		e.setAttribute("basepoint", map.get("closingprice"));
		e.setAttribute("compositionstocknum", map.get("stnum"));
		e.setAttribute("tradabletotalcap", map.get("alistedshare"));
		node.appendChild(e);
	    }

	    node = indexes.item(1);
	    removeIndexAndBoard(node, boardSet);
	    industryBoard = boardDataSource.getDistrictBoard();
	    for (Map<String, String> map : industryBoard) {
		Element e = doc.createElement("Index");
		e.setAttribute("code", map.get("indexcode"));
		e.setAttribute("boardid", map.get("sectornum"));
		e.setAttribute("name", map.get("sectorname"));
		e.setAttribute("basedate", date);
		e.setAttribute("baseperiod", map.get("fmarketval"));
		e.setAttribute("basepoint", map.get("closingprice"));
		e.setAttribute("compositionstocknum", map.get("stnum"));
		e.setAttribute("tradabletotalcap", map.get("alistedshare"));
		node.appendChild(e);
	    }

	    node = indexes.item(2);
	    removeIndexAndBoard(node, boardSet);
	    industryBoard = boardDataSource.getConceptBoard();
	    for (Map<String, String> map : industryBoard) {
		Element e = doc.createElement("Index");
		e.setAttribute("code", map.get("indexcode"));
		e.setAttribute("boardid", map.get("sectornum"));
		e.setAttribute("name", map.get("sectorname"));
		e.setAttribute("basedate", date);
		e.setAttribute("baseperiod", map.get("fmarketval"));
		e.setAttribute("basepoint", map.get("closingprice"));
		e.setAttribute("compositionstocknum", map.get("stnum"));
		e.setAttribute("tradabletotalcap", map.get("alistedshare"));
		node.appendChild(e);
	    }

	    industryBoard = boardDataSource.getBoardCodeRule();
	    NodeList set = doc.getElementsByTagName("Board");
	    if (set != null) {
		int length = set.getLength();
		boolean isExist = false;
		for (Map<String, String> map : industryBoard) {
		    isExist = false;
		    for (int i = 0; i < length; i++) {
			if (set.item(i).getNodeType() == Node.ELEMENT_NODE) {
			    Element el = (Element) set.item(i);
			    if (el.getAttribute("id").equals(
				    map.get("sectornum"))) {
				// update element
				while (el.hasChildNodes()) {
				    el.removeChild(el.getFirstChild());
				}
				el.setAttribute("id", map.get("sectornum"));
				el.setAttribute("name", map.get("sectorname"));
				el.setAttribute("desc", map.get("sectorname"));
				el.setAttribute("type",
					"cn.sl.quote.quotebusiness.cn.CNBoard");

				if (map.get("shcode") != null
					&& map.get("shcode").length() > 0) {
				    Element c2 = doc.createElement("CodeRule");
				    c2.setAttribute("market", "1");
				    c2.setAttribute("type", "list");

				    c2.appendChild(doc.createTextNode(map
					    .get("shcode")));

				    el.appendChild(c2);
				}

				if (map.get("szcode") != null
					&& map.get("szcode").length() > 0) {
				    Element c = doc.createElement("CodeRule");
				    c.setAttribute("market", "2");
				    c.setAttribute("type", "list");
				    c.appendChild(doc.createTextNode(map
					    .get("szcode")));
				    el.appendChild(c);
				}
				isExist = true;
				continue;
			    }
			}
		    }
		    if (!isExist) {
			Element e = doc.createElement("Board");
			e.setAttribute("id", map.get("sectornum"));
			e.setAttribute("name", map.get("sectorname"));
			e.setAttribute("desc", map.get("sectorname"));
			e.setAttribute("type",
				"cn.sl.quote.quotebusiness.cn.CNBoard");

			if (map.get("shcode") != null
				&& map.get("shcode").length() > 0) {
			    Element c2 = doc.createElement("CodeRule");
			    c2.setAttribute("market", "1");
			    c2.setAttribute("type", "list");
			    c2.appendChild(doc.createTextNode(map.get("shcode")));
			    e.appendChild(c2);
			}
			if (map.get("szcode") != null
				&& map.get("szcode").length() > 0) {
			    Element c = doc.createElement("CodeRule");
			    c.setAttribute("market", "2");
			    c.setAttribute("type", "list");
			    c.appendChild(doc.createTextNode(map.get("szcode")));
			    e.appendChild(c);
			}
			boardSet.appendChild(e);

		    }

		}

	    }
	    
	    File file = new File(dynamicconfigPath);
	    String backupName = dynamicconfigPath
		    + String.valueOf(System.currentTimeMillis());
	    boolean isRenameSuccess = file.renameTo(new File(backupName));
	    if (!isRenameSuccess) {
		logger.error("failed to remane xml file ");
		return;
	    }
	    logger.info("rename dynamicconfig.xml to " + backupName);
	    Transformer xformer = TransformerFactory.newInstance()
		    .newTransformer();
	    xformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    xformer.setOutputProperty(OutputKeys.ENCODING, "GBK");
	    xformer.setOutputProperty(
		    "{http://xml.apache.org/xslt}indent-amount", "2");
	    xformer.transform(new DOMSource(doc), new StreamResult(new File(
		    dynamicconfigPath)));
	    logger.info("write dynamicconfig.xml : ok");
	    /**
	     * 生成boardconfig.json
	     */
	    logger.info("generate boardconfig.json");
	    BoardConfig config = JAXB.unmarshal(new File(dynamicconfigPath),
		    BoardConfig.class);
	    JsonConfig jsonConfig = new JsonConfig();
	    jsonConfig.setBoardIndex(config.getBoardIndex().getList());
	    jsonConfig.setBoardset(config.getBoardSet());

	    Writer writer = null;

	    String json = JSON.std.asString(jsonConfig);
	    writer = new BufferedWriter(new OutputStreamWriter(
		    new FileOutputStream(boardconfigPath)));
	    // writer = new OutputStreamWriter(new FileOutputStream(jsonPath));
	    writer.write(json);
	    writer.close();

	} catch (Exception e) {
	    logger.error("exception" + e.toString(), e);
	    e.printStackTrace();
	}

    }

    private void removeIndexAndBoard(Node indexCategory, Element boardSet) {
	// null check
	if (indexCategory != null) {
	    while (indexCategory.hasChildNodes()) {
		Node node = indexCategory.getFirstChild();
		if (node.getNodeType() == Node.ELEMENT_NODE) {
		    String boardid = ((Element) node).getAttribute("boardid");
		    if (boardSet != null) {
			NodeList list = boardSet.getChildNodes();

			for (int i = 0; i < list.getLength(); i++) {
			    if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) list.item(i);
				if (boardid.equals(e.getAttribute("id"))) {
				    boardSet.removeChild(e);
				}
			    }
			}
		    }

		}
		// remove index
		indexCategory.removeChild(node);
	    }
	}

    }
}
