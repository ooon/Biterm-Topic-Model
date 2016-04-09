package com.ooon.lzj.pretreatment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
/**
 * 把word转化为id，并生成对应的文件 input.dat
 * @author Administrator
 *
 */
public class word2id {
	
	Map<String,Integer> word2id = new HashMap<String,Integer>();
	List<int[]> docs = new ArrayList<int[]>();
	public static void main(String[] args) throws IOException {
		String inpath = "data/result.txt";//原始语料库
		String outpath = "data/wordmap.dic"; //词到字典的映射
		String savepath ="data/input.dat";//转换为数字后的语料
		
		word2id wi = new word2id();
		wi.execute(inpath,outpath,savepath);
	
	}
	public void execute(String path,String outpath,String savepath)throws IOException{
		//读取文件并且把 word -> id
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		String line = "";
		while((line = br.readLine())!= null){
			String words[] = line.split(" +");
			int[] doc = new int[words.length];
			int i = 0;
			for(String word : words){
				int id  = word2id.size();
				if(word2id.containsKey(word))
					id = word2id.get(word);
				word2id.put(word, id);
				doc[i++] = id;
			}
			docs.add(doc);
		}
		br.close();
		//保存为一个map映射表
		System.out.println("created map！ prepare write to disk ！");
		FileWriter fw = new FileWriter(new File(outpath));
		StringBuilder sb = new StringBuilder();
		System.out.println("size V: "+word2id.size());
	
		Set<Entry<String, Integer>>  entryset = word2id.entrySet();
		sb.append(entryset.size()+"\n");
		for(Entry<String, Integer> entry : entryset){
			sb.append(entry.getKey()+" "+entry.getValue()+"\n");
		}
		fw.write(sb.toString());
		sb.delete(0, sb.length());
		fw.close();
		System.out.println("save to disk ok...");
		
		fw =  new FileWriter(new File(savepath));
		for(int[] doc : docs){
			for(int i = 0 ; i < doc.length ; i ++){
				if(i == doc.length-1){
					sb.append(doc[i]);
				}else{
					sb.append(doc[i]+" ");
				}
			}
			sb.append("\n");
		}
		fw.write(sb.toString());
		fw.close();
	}
	
}
