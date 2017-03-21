package com.gaotime;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gaotime.file.BoardConfigTask;
import com.gaotime.file.BoardDataSource;
import com.gaotime.file.BoardPriceTask;

public class Main {
    private static Logger logger = LoggerFactory
	    .getLogger(BoardConfigTask.class);

    private static ScheduledExecutorService scheduleExe;

    public static void main(String[] args) {
	// read properties
	try {
	    logger.info("application start");
	    InputStream inputStream = Main.class
		    .getResourceAsStream("/application.properties");
	    // deploy
	    // InputStream inputStream = Main.class.getResourceAsStream("");
	    Properties prop = new Properties();

	    prop.load(inputStream);
	    inputStream.close();

	    // create database connection
	    String resource = "/mybatis.xml";
	    BoardDataSource boardDataSource = new BoardDataSource(
		    Main.class.getResourceAsStream(resource));

	    // create job
	    scheduleExe = Executors.newScheduledThreadPool(1);
	    BoardConfigTask boardConfigTask = new BoardConfigTask(
		    boardDataSource, prop);

	    long initialDelay = getInitialDelay(prop
		    .getProperty("update_dynamicconfig_time"));
	    scheduleExe.scheduleAtFixedRate(boardConfigTask, initialDelay,
		    24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
	    logger.info("boardConfigTask scheduled {} ms ", initialDelay);
	    
	    BoardPriceTask boardPriceTask = new BoardPriceTask(
		    boardDataSource, prop);

	    initialDelay = getInitialDelay(prop
		    .getProperty("update_board_lastprice_time"));
	    scheduleExe.scheduleAtFixedRate(boardPriceTask, initialDelay,
		    24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
	    logger.info("boardPriceTask scheduled {} ms ", initialDelay);
	} catch (Exception e) {
	    scheduleExe.shutdown();
	    logger.error("application shutdown " + e.toString(), e);
	}

    }

    public static long getInitialDelay(String time) throws ParseException {
	long now = System.currentTimeMillis();
	final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String currDate = sdf.format(new Date(now)).substring(0, 11);
	long updateTime = sdf.parse(currDate + time).getTime();
	long diff = updateTime - now;
	long initialDelay;
	if (diff < 0) {
	    initialDelay = diff + 24 * 60 * 60 * 1000;
	} else {
	    initialDelay = diff;
	}
	return initialDelay;
    }

}
