package com.gaotime.file;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;
/**
 * 根据dynamicconfig.xml的结构，定义的jaxb 解析的对象
 * @author wenquanz
 *
 */
@XmlRootElement
public class BoardConfig {
    
    private BoardIndexSet boardIndex;
    
    private BoardSet boardSet;

    
    public BoardIndexSet getBoardIndex() {
        return boardIndex;
    }
    @XmlElement(name="BoardIndexSet")
    public void setBoardIndex(BoardIndexSet boardIndex) {
        this.boardIndex = boardIndex;
    }
    
    public BoardSet getBoardSet() {
        return boardSet;
    }
    
    @XmlElement(name="BoardSet")
    public void setBoardSet(BoardSet boardSet) {
        this.boardSet = boardSet;
    }
    
    
    
    

	
}

