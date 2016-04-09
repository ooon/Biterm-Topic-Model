package com.ooon.lzj.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.java2d.pipe.BufferedBufImgOps;

public class ppp {
	public static void main(String[] args) throws IOException {
		String path = "data/WeiBo.dat";
		BufferedReader bdr = new BufferedReader(new FileReader(new File(path)));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while((line = bdr.readLine()) !=null){
			String c = getC(line);
			sb.append(c).append("\n");
		}
		FileWriter fw = new FileWriter(new File("data/corpus.dat"));
		fw.write(sb.toString());
	}
	public static String getC(String line){
		Pattern p = Pattern.compile("##\\+\\**");
		Matcher m = p.matcher(line);
		if(m.find()){
			line = line.substring(0,m.start());
		}
		return line;
	}
	
	
	public static String getE(String line){
		Pattern p = Pattern.compile("##\\+\\**");
		Matcher m = p.matcher(line);
		while(m.find()){
			line = line.substring(0,m.end());
		}
		return line;
	}
}
