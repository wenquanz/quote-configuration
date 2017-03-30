package com.gaotime.mapper;

import java.util.List;
import java.util.Map;

public interface BoardMapper {
    
    public List<Map<String,String>> getIndustryBoard();
    
    public List<Map<String,String>> getDistrictBoard();
    
    public List<Map<String,String>> getConceptBoard();
    
    public List<Map<String,String>> getBoardCodeRule();
    
    public int deleteBoardPrice();

    public int insertBoardPrice(Map<String,Object> list);
}
