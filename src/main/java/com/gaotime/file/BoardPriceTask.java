package com.gaotime.file;

import java.io.DataInputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 每天收盘后，读取板块指数的最新价，更新到数据库中
 * 
 * @author wenquanz
 *
 */
public class BoardPriceTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(BoardPriceTask.class);

    private BoardDataSource boardDataSource;

    private String bindexRtquotesPath;

    private DecimalFormat df = new DecimalFormat("#.00");

    private int headSize = 32;
    private int blockSize = 2048;
    private int currPosition = 0;

    public BoardPriceTask(BoardDataSource boardDataSource, Properties prop) {
	this.boardDataSource = boardDataSource;
	this.bindexRtquotesPath = prop.getProperty("bindex_rtquotes_path");
    }

    public BoardPriceTask() {
	this.bindexRtquotesPath = "C:/Users/wenquanz/Desktop/rtquotes.data";
    }

    @Override
    public void run() {
	// read code and last price
	logger.info("BoardPriceTask start");
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	RandomAccessFile raf = null;
	try {
	    raf = new RandomAccessFile(bindexRtquotesPath, "rw");
	    if (raf.length() > 0) {
		currPosition = headSize;
		final long len = raf.length() - headSize;
		final BufferedRandomAccess bra = new BufferedRandomAccess(raf,
			blockSize, 1000, BufferedRandomAccess.CACHE_AFTER);
		final DataInputStream dis = new DataInputStream(bra);

		int count = (int) (len % blockSize == 0 ? len / blockSize : len
			/ blockSize + 1);
		Map<String, Object> map = null;
		logger.info("read rtquotes.data");
		for (int i = 0; i < count; i++) {
		    map = new HashMap<String, Object>();
		    bra.seek(currPosition + 2);
		    Double lastprice = dis.readDouble();
		    
		    if(lastprice > 0){
			map.put("lastprice", df.format(lastprice));
		    }else{
			map.put("lastprice", "1000");
		    }
		    bra.seek(currPosition + 391);
		    map.put("stockcode", dis.readUTF());
		    list.add(map);
		    currPosition += blockSize;
		}
		logger.info("read rtquotes.data : ok");
		dis.close();

		// update
		boardDataSource.updateBoardPrice(list);
		logger.info("insert board lastprice : ok ");
	    }else{
		logger.error("rtquotes is empty");
	    }
	} catch (Exception e) {
	    logger.error(e.toString(), e);
	}
    }

    public static void main(String[] args) {
	BoardPriceTask boardPriceTask = new BoardPriceTask();
	Thread t = new Thread(boardPriceTask);
	t.start();
    }

}
