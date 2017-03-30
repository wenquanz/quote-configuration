package com.gaotime.file;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.gaotime.mapper.BoardMapper;

public class BoardDataSource {

    private SqlSessionFactory sqlSessionFactory;
    
    public BoardDataSource(InputStream inputStream) {
	this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    public List<Map<String,String>> getIndustryBoard(){
	SqlSession session = sqlSessionFactory.openSession();
	try {
	    BoardMapper mapper = session.getMapper(BoardMapper.class);
	    List<Map<String,String>> result = mapper.getIndustryBoard();
	    return result;
	} finally {
	  session.close();
	}
	
    }
    
    public List<Map<String,String>> getDistrictBoard(){
	SqlSession session = sqlSessionFactory.openSession();
	try {
	    BoardMapper mapper = session.getMapper(BoardMapper.class);
	    List<Map<String,String>> result = mapper.getDistrictBoard();
	    return result;
	} finally {
	  session.close();
	}
	
    }
    
    
    public List<Map<String,String>> getConceptBoard(){
	SqlSession session = sqlSessionFactory.openSession();
	try {
	    BoardMapper mapper = session.getMapper(BoardMapper.class);
	    List<Map<String,String>> result = mapper.getConceptBoard();
	    return result;
	} finally {
	  session.close();
	}
	
    }
    
    
    public List<Map<String,String>> getBoardCodeRule(){
	SqlSession session = sqlSessionFactory.openSession();
	try {
	    BoardMapper mapper = session.getMapper(BoardMapper.class);
	    List<Map<String,String>> result = mapper.getBoardCodeRule();
	    return result;
	} finally {
	  session.close();
	}
	
    }
    
    public int updateBoardPrice(List<Map<String,Object>> list){
	SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH);
	try {
	    BoardMapper mapper = session.getMapper(BoardMapper.class);
	    mapper.deleteBoardPrice();
	    for(Map<String,Object> map : list){
		mapper.insertBoardPrice(map);
		session.flushStatements();
	    }
	    session.commit();
	    return 0;
	} finally {
	  session.close();
	}
	
    }
}
