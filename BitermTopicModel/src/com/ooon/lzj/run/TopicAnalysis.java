package com.ooon.lzj.run;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  通过coherence score测试主题质量
 *  该方法适用于BTM与LDA生成的phi，即主题词语分布
 *  注意 LDA 与BTM 中的 phi保存的方式不同，注意读取时要区分开
 * 
 * @author ooon
 */

public class TopicAnalysis {
	public int T; // 每个主题下取T个词
	public int K; //主题数
	public int V; //词表数
	public double[][] pw_z;
	public String model_dir;
	public String doc_path;
	public List<List<Integer>> docs;
	//constructor
	public TopicAnalysis(int T,int K,int V, String model_dir,String doc_path){
		this.T = T;
		this.K = K;
		this.V = V;
		this.model_dir = model_dir;
	    this.doc_path = doc_path;
		pw_z = new double[K][V];
		docs = new ArrayList<List<Integer>>();
	}
	/**
	 * 读取phi 大小为 K*V
	 * @param path 主题-词语分布矩阵
	 * 
	 */
	public void readPhi_BTM() throws IOException{ 
		System.out.println("load phi...");
		DataInputStream dis = new DataInputStream
				 	(new FileInputStream(model_dir+"model.phi"));
		 
		 for(int k = 0 ; k < K ; k ++){
			for(int v = 0 ; v < V ; v++){
				pw_z[k][v] = dis.readDouble(); 
			}
			dis.readUTF();
		}
		dis.close();
	}
	/**
	 *  读取corpus，用来统计词频与词语的共线频率
	 *  来计算coherence score
	 * @param Doc_path 文档路径
	 * @throws IOException
	 */
	public void read_Doc() throws IOException{
		System.out.println("load corpus...");
		BufferedReader br = new BufferedReader(new FileReader(new File(doc_path)));
		String line ="";
		int idx = 1;
		while((line = br.readLine()) !=null ){
			String[] splitline = line.split("[\\s+]");
			List<Integer> doc = new ArrayList<Integer>(splitline.length);
			for(int i = 0 ; i < splitline.length ; i ++){
				doc.add( Integer.valueOf(splitline[i]));
			}
			docs.add(doc);
		}
		br.close();
	}
	/**
	 * 计算两个词的共现频率
	 * @param t
	 * @param l
	 * @return 统计结果
	 */
	public int D(int t,int l){
		int res = 0;
		for(List<Integer> doc : docs){
			if(doc.contains(t) && doc.contains(l))	++res;
		}

		return res;
	}
	/**
	 * 计算单个词的频率
	 * @param l
	 * @return 统计结果
	 */
	
	public int D(int l){
		int res = 0;
		for(List<Integer> doc : docs){
			if(doc.contains(l)) ++res;
		}
		return res;
	}
	/**
	 * 
	 * @return  主题1...K的得分
	 */
	public double[] Score(){
		System.out.println("rating...");
		double[] socre = new double[K];
		for(int k = 0 ; k < K ; k ++){
			// 当前主题为k计算c(k ,v^(k))

			System.out.println("K = "+(k+1)+"/"+K);
			Arrays.sort(pw_z[k]);//从小到大排序，取得时候取后T个
			double outersum = 0;
			//t= 2...T   
			//for(t = V-1 ; t > V-T ; t--)
			for(int t = V-1 ; t > V-T ; t--){
				//当前词为v
				double innersum = 0;	
				//l=1...t
				//for(int l = V - T ; l <= V ; l++)
				for(int l = V  ; l >= t ; l--){
					innersum += Math.log((D(t,l)+1)*1.0/D(l));
				}
				outersum += innersum;
			}
			socre[k] = outersum;
		}
		
		return socre;
	}
	public double coherence_score() throws IOException{
		readPhi_BTM();
		read_Doc();
		double[] score = Score();  //主题1...k的得分
		for(int i = 1 ; i < score.length ; i ++){
			score[i] += score[i-1];
		}
		double final_score  = score[K-1] / K;
		System.out.println("\tT : "+T );
		System.out.println("\tScore : "+final_score );
		return final_score;
	}
	public static void main(String[] args) {
//		double[][] c = new double[2][2];
//		c[0][0] = 1;
//		c[0][1] = 0;
//		c[1][0] = 3;
//		c[1][1] = 2;
//		for(int i = 0 ; i < c.length ; i ++){
//			Arrays.sort(c[i]);
//			System.out.println(Arrays.toString(c[i]));
//		}
	}
}
