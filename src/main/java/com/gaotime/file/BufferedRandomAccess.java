package com.gaotime.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * ���漴��ȡ�ļ��Ĺ������ ���湦�ܣ����̳�ByteArrayInputStream
 * ���水����л���
 * @author hucy 2008-4-3 ����10:24:08
 *
 */
public class BufferedRandomAccess extends ByteArrayInputStream  {
	//������С
	private  int blockSize;
	//һ�λ���Ŀ����
	private  int cacheNum;
	//�漴��ȡ�ļ�
	private RandomAccessFile raf;
	//�ļ�����
	private long fileLen;
	//�ļ���ȡʱ ����ķ��������󻺴��ص�ǰλ�ÿ�ʼ��ȡ�������ǰ����Ϊ ָ��λ��-blockSize ��ʼ��ȡ
	private int direction;
	
	private long startRafPos;
	private long endRafPos;
	
	public static final int  CACHE_BEFORE =1;
	public static final int CACHE_AFTER =2;
	
	public BufferedRandomAccess(RandomAccessFile raf , int blockSize ,
			int cacheNum , int direction) throws IOException {
		super( new byte[blockSize *cacheNum] );
		//��ʼ��ʱû������
		super.pos =0;
		super.count =0;
		super.mark=0;
		
		this.raf = raf;
		this.blockSize = blockSize;
		this.cacheNum = cacheNum;
		this.direction = direction;
		
		this.startRafPos =0;
		this.endRafPos =0;
		this.fileLen = raf.length();
	}

	/**
	 * ��������λ��
	 * @param seekpos
	 * @throws IOException 
	 */
	public void seek(long seekpos) throws IllegalArgumentException{
		try{
			if( startRafPos ==0 &&  endRafPos==0) {
				fillBuf(seekpos);
			}else if(seekpos >= startRafPos && seekpos < endRafPos ){
				super.pos = (int)(seekpos - startRafPos);
			}else if( seekpos < startRafPos ||  seekpos >= endRafPos ){
				fillBuf(seekpos);
			}
		}catch(IOException ex){
			throw new IllegalArgumentException(ex);
		}
	}
	
	/**
	 * ���ļ����ݶ�ȡ��������
	 * @param seekpos
	 * @return
	 * @throws IOException
	 */
    private void fillBuf(long seekpos) throws IOException{
		//������仺��,�������в���
		if(direction == CACHE_BEFORE ){
			this.endRafPos = seekpos + blockSize > fileLen ? fileLen : seekpos + blockSize;
			this.startRafPos = this.endRafPos - cacheNum *blockSize >0 ? this.endRafPos - cacheNum *blockSize :0;
			raf.seek( startRafPos );
			super.count = raf.read( super.buf);
			super.pos = (int)(seekpos - this.startRafPos);
		}else if(direction == CACHE_AFTER ){
			this.startRafPos =  seekpos > fileLen ? fileLen  : seekpos ;
			this.endRafPos = this.startRafPos + cacheNum *blockSize  > fileLen ? fileLen :  this.startRafPos + cacheNum *blockSize;
			
			raf.seek( startRafPos );
			super.count = raf.read( super.buf);
			super.pos =(int)(seekpos - this.startRafPos);			
		}
    }	
	
    /**
     * ����RandomAccessFileʣ�೤��
     * @return
     */
    public int available() {
    	return (int)(this.fileLen - this.startRafPos - super.pos);
    }
    
    /**
     * ����RandomAccessFile����
     * @return
     */
    public long length(){
    	return this.fileLen;
    }

    /**
     * ���ص�ǰ��ȡ����RandomAccessFile��λ��
     * @return
     */
    public long getFilePointer() {
    	return this.startRafPos + super.pos;
    }
    
    /**
     * ��RandomAccessFile ��ȡlen���ֽڵ��ֽ�����b
     */
    public int read(byte b[], int off, int len) {
    	if (b == null) {
    	    throw new NullPointerException();
    	} else if ((off < 0) || (off > b.length) || (len < 0) ||
    		   ((off + len) > b.length) || ((off + len) < 0)) {
    	    throw new IndexOutOfBoundsException();
    	}
    	
    	int hasReadLen = 0 ;

    	while( this.startRafPos + super.pos <  this.fileLen  && hasReadLen < len ){
    		//����Ҫ��ȡ������
    		int remainlen = len - hasReadLen;
    		if( super.pos >= super.count){
    			seek(this.startRafPos + super.pos);
    		}
        	int onelen = pos + remainlen > count ? count - pos : remainlen;
    		
    		//System.out.println("count:"+count+" startpos: " +this.startRafPos+ "  endpos"+ this.endRafPos+ " buf.length: " +buf.length +"  pos:"+pos +" off:"+ off +"  onelen:" + onelen +" len:"+len);
    		System.arraycopy(buf, pos, b, off, onelen);
    		//�������
    		hasReadLen +=onelen;
        	pos += onelen;
        	off += onelen;
    	}
    	return hasReadLen == 0? -1: hasReadLen;
    }
    
    /**
     * ��ȡһ���ֽ�
     */
    public int read(){
    	if(pos >= count){
    		//seekһ��
    		seek(this.startRafPos + super.pos);
    	}
    	return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }
    

	
	/**
	 * ����n���ֽ�
	 */
    public long skip(long n) {
    	if ( super.pos + n +this.startRafPos > fileLen) {
    	    n = fileLen - (  super.pos + this.startRafPos );
    	}
    	if (n < 0) {
    	    return 0;
    	}
    	this.seek(this.startRafPos + n+ super.pos);
    	return n;
    }
    
    /**
     * �Ƿ�֧�����ñ��
     */
    public boolean markSupported() {
    	return true;
    }    
    
    public void mark(int readAheadLimit) {
    	mark = pos;
    }    
    
    public void reset() {
    	this.seek(mark);
    }    
    
    public void close() throws IOException {
    }
	
}
