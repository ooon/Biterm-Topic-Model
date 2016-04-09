package com.ooon.lzj.model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ooon.lzj.entity.Biterm;
import com.ooon.lzj.entity.Doc;
/**
 * Model运行完之后，保存的是二元组集合对应的主题theta，以及主题对应的词语矩阵theta
 * Infer的目的是根据BTM中Biterm的主题分布来推断每篇文章的主题分布
 * Infer方法氛围三种：采用的方法根据指定搞得参数type来决定
 * @author ooon
 *
 */
public class Infer {
	  public int K;
	  public int V;
	  public String type;			// infer type
	  public double[] pz;	    // p(z) = theta
	  public double[][] pw_z;   // p(w|z) = phi, size K * M
	
	  public Infer(int K ,int V, String type){
		  this.K =K;
		  this.V =V;
		  this.type = type;
	  }
	  /**
	   * @param docs_pt 待推断的文档
	   * @param model_dir 模型路径
	   * @throws IOException
	   */
	 public  void infer(String docs_pt, String model_dir) throws IOException{
		  load_para(model_dir);
		  
		  System.out.println( "Infer p(z|d) for docs in: " + docs_pt);
		  BufferedReader bfr  = new BufferedReader(new FileReader(new File(docs_pt)));
		  String pt = model_dir + "k" + K + ".pz_d";
		  FileWriter fw = new FileWriter(new File(pt));
		  String line = "";
		  StringBuilder res = new StringBuilder();
		  while((line = bfr.readLine()) != null){
			  Doc doc = new Doc(line);
			  double[] pz_d = new double[K];
			  doc_infer (doc,pz_d);
			  for(int i = 0 ; i <pz_d.length ; i ++){
				  res.append(pz_d[i]+" ");
			  }
			  res.append("\n");
		  } 
		  bfr.close();
		  System.out.println("write p(z|d): " +pt);
		  fw.write(res.toString());
		  fw.close();
		}
	 public void doc_infer(Doc doc , double [] pz_d){
		 if (type == "sum_b")
				doc_infer_sum_b(doc, pz_d);
			  else if (type == "sub_w")
				doc_infer_sum_w(doc, pz_d);
			  else if (type == "mix")
				doc_infer_mix(doc, pz_d);
			  else {
				System.out.println("[Err] unkown infer type:" + type) ;
				System.exit(1);
			  }
	 }
	 /**
	  *  从主题层面推断短文本的主题分布
	  * @param doc
	  * @param pz_d
	  */
	// p(z|d) = \sum_b{ p(z|b)p(b|d) }
	 public void doc_infer_sum_b(Doc doc , double [] pz_d){
		 if(doc.size() == 1){
			// doc is a single word
			//p(z|d) = p(z|w) \propo p(z)p(w|z)
			 for(int k = 0 ; k <K ; k++){
				 pz_d[k] = pz[k]*pw_z[k][doc.get_w(0)];
			 }
		 }else{
			 //生成每篇文章的biterm 序列
			 List<Biterm> bs = new ArrayList<Biterm>();
			 doc.gen_biterms(bs);
			 for( Biterm b: bs ){
				 int w1 = b.getWi();
				 int w2 = b.getWj();
				 // filter out-of-vocabulary words
				 if (w2 >= V) continue;
				 // compute p(z|b) \propo p(w1|z)p(w2|z)p(z)
				 double[] pz_b = new double[K];
				 for(int k = 0 ; k < K ; k ++){
					 pz_b[k] = pz[k]*pw_z[k][w1]*pw_z[k][w2];
				 }
				 normalize(pz_b, 0d);
				 //p(z|d) =  \sum_b p(z|b)p(b|d)
				 for (int k = 0; k < K; ++k)
					 pz_d[k] += pz_b[k];
			 }
		 }
		 normalize(pz_d ,0);
	 }
	 /**
	  * 从词语层面推断短文本的主题分布
	  * @param doc
	  * @param pz_d
	  */
	 public void doc_infer_sum_w(Doc doc , double [] pz_d){
		 List<Integer> ws= doc.get_ws();
		 for (int i = 0; i < ws.size(); ++i) {
				int w = ws.get(i);
				if (w >= V) continue;
				
				// compute p(z|w) \propo p(w|z)p(z)
				 double[] pz_w = new double[K];
				for (int k = 0; k < K; ++k) 
				  pz_w[k] = pz[k] * pw_z[k][w];
				
				normalize(pz_w,0);
				
				// sum for b, p(b|d) is unifrom
				for (int k = 0; k < K; ++k) 
				  pz_d[k] += pz_w[k];
			  }
			  normalize(pz_d,0);
	 }
	 //混合推断
	 public void doc_infer_mix(Doc doc , double [] pz_d){
		  for (int k = 0; k < K; ++k) 
				pz_d[k] = pz[k];

		  List<Integer> ws= doc.get_ws();
		  for (int i = 0; i < ws.size(); ++i) {
			int w = ws.get(i);
			if (w >= V) continue;
			//p(z|d) = \sum_z p(z)p(w|z)
			for (int k = 0; k < K; ++k) 
				pz_d[k] *= (pw_z[k][w] * V);
			 }
				// sum for b, p(b|d) is unifrom
			 normalize(pz_d,0);
	 }
	 /**
	  * 加载模型参数
	  * @param model_dir
	 * @throws IOException 
	  */
	 public void load_para(String model_dir) throws IOException{
		 //文件输出 
		 pz = new double[K];
		 String pt = model_dir+"model.theta";
		 System.out.println("load p(z)... from " + pt);
		 DataInputStream dis = new DataInputStream(new FileInputStream(pt));
		 for(int i = 0 ; i < K ; i++){
			 pz[i] = dis.readDouble();
		 }
		 //读取主题下词语的分布矩阵
		 pw_z = new double[K][V];
		 pt = model_dir+"model.phi";
		 System.out.println("load p(z|w)... from " + pt);
		 dis = new DataInputStream(new FileInputStream(pt));
		 for(int k = 0 ; k < K ; k ++){
			for(int v = 0 ; v < V ; v++){
				pw_z[k][v] = dis.readDouble(); 
			}
			dis.readUTF();
		}
	 }
	/**
	 * 数组进行归一化
	 * @param p
	 * @param smoother
	 */
	public void normalize(double p[],double smoother){
		double sum = 0 ;
		for(int i = 0 ; i < p.length ; ++ i){
			sum += p[i]; 
		}
		int K = p.length;
		for(int i = 0 ; i < K ; ++ i){
			p[i] =(p[i] + smoother)/( sum + K* smoother);
		}
	}
	
	
	public static void main(String[] args) throws IOException {
//		double a [][] = new double[2][10];
//		System.out.println(a.length);
//		DataOutputStream dos = new DataOutputStream(new FileOutputStream("model/123.txt"));
//		DataInputStream dis = new DataInputStream(new FileInputStream("model/123.txt"));
//		double aa = 0.001;
//		for(int i = 0 ; i < a.length ; i++){
//			for(int j = 0 ; j < 10; j ++){
//				a[i][j]= 0.1+aa;
//				dos.writeDouble(a[i][j]);
//				aa+=0.002;
//			}
//			dos.writeUTF("\n");
//		}
//		double b [][] = new double[2][10];
//		for(int i = 0 ; i < a.length ; i++){
//			for(int j = 0 ; j < 10; j ++){
//				b[i][j] =	dis.readDouble();
//			}
//			dis.readUTF();
//		}
//		System.out.println(Arrays.toString(b[0]));
//		System.out.println(Arrays.toString(b[1]));
	}
}
