package com.ooon.lzj.entity;

import java.util.ArrayList;
import java.util.List;

public class Doc {
	/**
	 * 感觉doc这里有点多余了，先写上，不行再改
	 */
	private List<Integer> ws ;//word seqence
	//调节biterm生成时的windows的大小
	private static final int WIN = 15;
	
	public Doc(String line){
		ws = new ArrayList<Integer>();
		read_doc(line);
	}
	/**
	 * 返回字符序列的大小
	 */
	public int size(){
		return ws.size();
	}
	public int get_w(int i){
		return ws.get(i);
	}
	public List<Integer> get_ws(){
		return ws;
	}
	/**
	 * 把String数组改为int数组
	 * @param line
	 */
	public void read_doc(String line){
		String[] ids = line.split(" +");
		for(String id : ids){
			ws.add(Integer.valueOf(id));
		}
	}
	/**
	 * 生成biterm,WIN制定了windows的大小
	 * @param B 二元组序列
	 * 
	 */
	public void gen_biterms(List<Biterm> B){
		for(int i = 0 ; i < ws.size() ; i ++){
			//定义一个界限
			int bound = (i + WIN) > ws.size()
					? ws.size() : (i + WIN);
					
			for(int j = i+1 ; j < bound ; j ++)
			{
				B.add(new Biterm(ws.get(i),ws.get(j)) );
			}
		}
	}
}
