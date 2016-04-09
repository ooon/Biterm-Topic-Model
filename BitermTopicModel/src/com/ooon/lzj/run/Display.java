package com.ooon.lzj.run;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ooon.lzj.entity.Pair;
/**
 * 展示主题下top词的效果
 * @throws IOException 
 * @throws IOException 
 */
public class Display {
	int K;
	int V;
	int Top = 25;
	
	public Display(int K, int V, int Top){
		this.K = K;
		this.V =V;
		this.Top = Top;
	}
	public Display(int K, int V){
		this.K = K;
		this.V =V;
	}
	public Map<Integer,String> getDic(String dic_dir) throws IOException{
		String dic_path = "data/wordmap.dic";
		BufferedReader br = new BufferedReader(new FileReader(new File(dic_path)));
		String line = "";
		Map<Integer,String> dic = new HashMap<Integer, String>(Integer.valueOf(br.readLine().trim()));
		while((line = br.readLine())!=null){
			String[] word_id = line.split("\\s+");
			dic.put(Integer.valueOf(word_id[1]),word_id[0]);
		}
		return dic;
	}
	/**
	 * 
	 * @param dic_dir   id-word映射
	 * @param model_dir 模型（phi theta）路径
	 * @param out_dir   展示文件的保存
	 * @throws IOException
	 */
	
	public void disply(String dic_dir ,String model_dir ,
					String out_dir) throws IOException{
		System.out.println("generate top words to " +out_dir );
		StringBuilder sb = new StringBuilder();
		//读取字典映射 word -> id
		Map<Integer,String> dic = getDic(dic_dir);
		
		//读取phi矩阵
		System.out.println("read phi...");
		 double[][] pw_z = new double[K][V];
		 DataInputStream dis = new DataInputStream
				 	(new FileInputStream(model_dir+"model.phi"));
		 for(int k = 0 ; k < K ; k ++){
			for(int v = 0 ; v < V ; v++){
				pw_z[k][v] = dis.readDouble(); 
			}
			dis.readUTF();
		}
	
		dis.close();
		//找到top word
		
		System.out.println("find top words...");
		for(int k = 0 ; k < K  ; k ++){
			System.out.println("Topic "+ (k+1) +"...");
			
			List<Pair<Double,Integer>> pp = new ArrayList<Pair<Double,Integer>>();
			
		
			for(int v = 0 ; v < V ; v++){
				Pair<Double,Integer> p = new Pair<Double,Integer>(pw_z[k][v], v);
				pp.add(p);
			}
			Collections.sort(pp);
			sb.append("topic"+(k+1)+":").append("\n");
			for(int i = 0 ; i < Top ; i ++){
				sb.append("\t").append(dic.get(pp.get(i).getIndex()))
					.append(" ").append(pp.get(i).getValue()).append("\n");
			}
			sb.append("\r");
			
		}
		FileWriter fw = new FileWriter(new File(out_dir));
		fw.write(sb.toString());
		System.out.println("写入到"+ out_dir);
		fw.close();
	}
	public static void main(String[] args) {
	
	}
}
