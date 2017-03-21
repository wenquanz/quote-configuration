package com.gaotime.file;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


@SuppressWarnings("restriction")
public class BoardIndexSet{
    
    private List<IndexCategory> list;
    
    @XmlElement(name="IndexCategory")
    public List<IndexCategory> getList() {
        return list;
    }
    public void setList(List<IndexCategory> list) {
        this.list = list;
    }
    
    
    
    
}
class IndexCategory{
    
    private String id;
    
    private String name;
    
    private String maxid;
    
    private String marketid = "20";
    
    private List<Index> index;
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
    public String getMaxid() {
        return maxid;
    }
//    @XmlAttribute(name="maxid")
    public void setMaxid(String maxid) {
        this.maxid = maxid;
    }
    public List<Index> getIndex() {
        return index;
    }
    @XmlElement(name="Index")
    public void setIndex(List<Index> index) {
        this.index = index;
    }
    public String getMarketid() {
        return marketid;
    }
    public void setMarketid(String marketid) {
        this.marketid = marketid;
    }
    
    
}



class Index{
    //由于客户端对boardid做int处理，为了兼容，需要防止boardid溢出
    private String boardid;
    private String code;
    private String name;
    public String getBoardid() {
        return boardid;
    }
    @XmlAttribute(name="boardid")
    public void setBoardid(String boardid) {
	/*int i = (int) (Long.parseLong(boardid) / Integer.MAX_VALUE);
        this.boardid = boardid.substring(0, boardid.length() - i);*/
	this.boardid = boardid;
    }
    public String getCode() {
        return code;
    }
    @XmlAttribute(name="code")
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    @XmlAttribute(name="name")
    public void setName(String name) {
        this.name = name;
    }
    
}
