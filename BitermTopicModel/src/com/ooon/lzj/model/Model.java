package com.ooon.lzj.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ooon.lzj.entity.Biterm;
import com.ooon.lzj.entity.Doc;
/**
 * BTM模型，生成theta 与 phi
 * theta 是每个biterm对应的主题 大小为 |B|
 * phi   是每个topic下词语的分布 大小为K*V
 * @author ooon
 *
 */

public class Model {
	public List<Biterm> B; // 二元组集合

	public 	  int V;				// vocabulary size
	public 	  int K;				// number of topics
	public 	  int n_iter;			// maximum number of iteration of Gibbs Sampling
	public 	  int save_step;
	
	public 	  double alpha;			// hyperparameters of p(z)
	public 	  double beta;			// hyperparameters of p(w|z)	
	
	public 	  int[] nb_z;	// n(b|z), size K*1
	public 	  int[][] nwz;	  // n(w,z), size K*W
	public 	  double[] pw_b;   // the background word distribution
	
	public Model(int K, int V,double alpha, double beta, int n_iter, int save_step){
		//设置参数
		this.K = K; this.V = V;
		this.alpha = alpha;
		this.beta = beta;
		this.n_iter = n_iter;
		this.save_step = save_step;
		
		pw_b = new double[V];//整个语料库的词语分布
		nwz = new int[K][V];// 主题-词语计数
		nb_z = new int[K]; //biterm对应的主题计数 
		B = new ArrayList<Biterm>();
	}
	/**
	 * 模型运行的方法
	 * @param tdocs_pt 输入文件，bow表示的文档
	 * @param tmodels_dir 模型保存路径
	 * @throws IOException
	 */
	public void run(String tdocs_pt , String res_dir) throws IOException{
		load_docs(tdocs_pt);
		model_init();
		System.out.println("Begin iteration");
		String out_dir = res_dir+"model.";
		for(int iter = 1 ; iter< n_iter + 1 ; iter ++){
			System.out.println("\riter "+ iter +"/" + n_iter);
			for(int i = 0 ; i < B.size() ; i ++){
				sample_biterm(B.get(i));
			}
			//if(iter % save_step == 0 ) save_res(out_dir);
		}
		save_res(out_dir);
	}
	/**
	 * -1 ->sample-> +1  三重奏
	 * @param b
	 */
	public void sample_biterm(Biterm b){
		int k = b.getZ();
		int w1 = b.getWi();
		int w2 = b.getWj();
		--nb_z[k];
		--nwz[k][w1];
		--nwz[k][w2];
		//计算当前biterm属于每个主题的概率pz[K]
		double[] pz = new double[K];
		compute_pz_b(b,pz);
		//依概率从pz中随机选取一个
		for(int i = 1 ; i < pz.length ; i ++){
			pz[i] += pz[i-1];
		}
		double u = Math.random()*pz[pz.length-1];
		
		for(int i = 0 ; i < pz.length ; i ++)
			if(u < pz[i]) {
				k = i;
				break;
			}
		assign_biterm_topic(b,k);
	}
	/**
	 *  Gibbs采样，计算分别属于每个K的概率
	 */
	public void compute_pz_b(Biterm b , double[] pz){
		int w1 = b.getWi();
		int w2 = b.getWj();
		double pw1k = 0.0d;
		double pw2k = 0.0d;
		double pk =   0.0d;
		for(int k = 0 ; k < K ; k++)
		{
			pw1k = (nwz[k][w1]+beta)/(2 * nb_z[k]+ V*beta);
			pw2k = (nwz[k][w2]+beta)/(2 * nb_z[k]+ V*beta);
			pk = (nb_z[k] +alpha) /(B.size() + K*alpha);
			pz[k] = pw1k * pw2k * pk;
		}
	}
	/**
	 * 读取文本
	 * 生成biterm
	 * 统计词语出现概率
	 * @param dir 输入文件路径
	 * @param dfile 输入文件名称
	 */
	public void load_docs(String doc_pt) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(doc_pt)));
		String line= "";
		while((line = br.readLine()) !=null)
		{
			Doc doc = new Doc(line);
			doc.gen_biterms(B);
			//统计词语概率
			for(int i = 0 ; i < doc.size() ; i++){
				int w = doc.get_w(i);
				++pw_b[w];
			}
		}
		System.out.println("biterm list Size = "+ B.size());
		normalize(pw_b, 0d);
		br.close();
	}
	
	/**
	 * Gibbs采样的初始化，采样对象为B（biterm的list）
	 */
	public void model_init(){
		for(int i = 0 ; i < B.size() ; i ++){
			//k 属于   0-(K-1) 之间
			int k = new Random().nextInt(K);
			assign_biterm_topic(B.get(i),k);
		}
	}
	/**
	 * 为biterm分配主题k
	 * @param b biterm
	 * @param k 对应的主题
	 */
	public void assign_biterm_topic(Biterm b , int k){
		b.setZ(k);
		int w1 = b.getWi();
		int w2 = b.getWj();
		++ nb_z[k];
		++ nwz[k][w1];
		++ nwz[k][w2];
	}
	/**
	 * 根据数据求均值，并且有平滑
	 * 可以用作计算先验 alpha beta
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
	
	/**
	 * 保存输出文件
	 * @param dir
	 * @throws IOException 
	 * 
	 */
	public void save_res(String dir) throws IOException{
		String pt = dir + "theta";
		System.out.println("save p(z) "+pt+"...");
		//保存p(z),p(z) 为 corpus 级别的 biterm 的主题分布
		// 转换为double数组便于操作，pz需要保存每个biterm的概率
		double[] pz = new double[nb_z.length];
		for(int i = 0 ; i < nb_z.length ; i ++)
		{
			pz[i] = (double)nb_z[i];
		}
		normalize(pz, alpha);//借助先验完成计算,相当于LDA中的主题分布theta
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(pt));
		
		for(int i = 0 ; i < pz.length ; i ++){
			dos.writeDouble(pz[i]);
		}
		dos.close();
		pt = dir + "phi";
		dos = new DataOutputStream(new FileOutputStream(pt));
		//保存p(z|w)
		System.out.println("save p(w|z) "+pt+"...");
		double[][] pw_z = new double[K][V];//相当于LDA中的phi
		for(int k = 0 ; k < K ; k ++){
			for(int v = 0 ; v < V ; v++){
				pw_z[k][v] = (nwz[k][v] + beta)/(nb_z[k]*2 + V*beta);
				dos.writeDouble(pw_z[k][v]);
			}
			dos.writeUTF("\n");
		}
		dos.close();
		System.out.println("Done...");
	} 
	
}
