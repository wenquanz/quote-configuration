package com.gaotime.file;

import java.util.List;

public class JsonConfig {
    
    private List<IndexCategory> boardIndex;
    private String result = "true";
    private BoardSet boardset;
    
    
    public List<IndexCategory> getBoardIndex() {
        return boardIndex;
    }
    public void setBoardIndex(List<IndexCategory> boardIndex) {
        this.boardIndex = boardIndex;
    }
    public BoardSet getBoardset() {
        return boardset;
    }
    public void setBoardset(BoardSet boardset) {
        this.boardset = boardset;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    
    
    

}
