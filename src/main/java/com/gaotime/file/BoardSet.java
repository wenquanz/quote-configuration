package com.gaotime.file;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;


@SuppressWarnings("restriction")
public class BoardSet{
    private String timestamp;
    
    private List<Board> board;

    public String getTimestamp() {
        return timestamp;
    }
    @XmlAttribute(name="timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Board> getBoard() {
        return board;
    }
    @XmlElement(name="Board")
    public void setBoard(List<Board> board) {
        this.board = board;
    }
    
    
}

class Board{
    private String id;
    private String name;
    private String type;
    private String desc;
    private List<CodeRule> codeRules = new ArrayList<CodeRule>();
    public String getId() {
        return id;
    }
    @XmlAttribute(name="id")
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    @XmlAttribute(name="name")
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
//    @XmlAttribute(name="type")
    public void setType(String type) {
        this.type = type;
    }
    public String getDesc() {
        return desc;
    }
    @XmlAttribute(name="desc")
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public List<CodeRule> getCodeRules() {
        return codeRules;
    }
    @XmlElement(name="CodeRule")
    public void setCodeRules(List<CodeRule> codeRules) {
        this.codeRules = codeRules;
    }
    
    
}

class CodeRule{
    private String market;
    private String type;
    private String start;
    private String end;
    private String value = null;
    
    public String getMarket() {
        return market;
    }
    @XmlAttribute(name="market")
    public void setMarket(String market) {
        this.market = market;
    }
    public String getType() {
        return type;
    }
    @XmlAttribute(name="type")
    public void setType(String type) {
        this.type = type;
    }
    public String getStart() {
        return start;
    }
    @XmlAttribute(name="start")
    public void setStart(String start) {
        this.start = start;
    }
    public String getEnd() {
        return end;
    }
    @XmlAttribute(name="end")
    public void setEnd(String end) {
        this.end = end;
    }
    public String getValue() {
        return value;
    }
    @XmlValue
    public void setValue(String value) {
	String s = value.trim();
	this.value = s.length() == 0 ? null : s; 
    }
    
    
}
