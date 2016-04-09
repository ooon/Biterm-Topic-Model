package com.ooon.lzj.run;

import java.io.IOException;

import com.ooon.lzj.model.Infer;
import com.ooon.lzj.model.Model;

public class Run {
	 /**
	  * 
	  * @param args
	  * @throws IOException
	  */
	public static void main(String[] args) throws IOException {
		int K = 100;//主题大小
		int V = 61719;// 词表大小 
		int n_iter = 800;	//迭代次数		
		int save_step = 1001;
		double alpha = 50/K ;	// a 50/K
		double beta = 0.005;	// b
		String tdocs_pt  = "data/input.dat"; //待推断文档
		String tmodel_dir= "model/"; //模型路径
		String type = "sum_b"; //计算文档分布的类型
		String dic_dir = "data/wordmap.dic";//  字典路径
		String top_topic_dir = "display/top_topic"; //主题展示 
		
//		 //生成模型
//		Model biterm_model = new Model(K, V, alpha, beta, n_iter, save_step);
//		biterm_model.run(tdocs_pt,tmodel_dir);
//		
//		//推断文档分布
//		Infer inf = new Infer(K ,V ,type);
//		
//		inf.infer(tdocs_pt, tmodel_dir);
//		
//		//展示
//		Display dis = new Display(K , V);
//		dis.disply(dic_dir, tmodel_dir,top_topic_dir);
		TopicAnalysis ta = new TopicAnalysis(5, K, V, tmodel_dir, tdocs_pt);
		ta.coherence_score();
		
	}
	/**
	 *
       << "btm est <K> <W> <alpha> <beta> <n_iter> <save_step> <docs_pt> <model_dir>\n"
       << "\tK  int, number of topics, like 20" << endl
       << "\tW  int, size of vocabulary" << endl
       << "\talpha   double, Pymmetric Dirichlet prior of P(z), like 1.0" << endl
       << "\tbeta    double, Pymmetric Dirichlet prior of P(w|z), like 0.01" << endl
       << "\tn_iter  int, number of iterations of Gibbs sampling" << endl
       << "\tsave_step   int, steps to save the results" << endl
       << "\tdocs_pt     string, path of training docs" << endl
       << "\tmodel_dir   string, output directory" << endl
       << "Inference Usage:" << endl
       << "btm inf <K> <docs_pt> <model_dir>" << endl
       << "\tK  int, number of topics, like 20" << endl
       << "\tdocs_pt     string, path of training docs" << endl
       << "\tmodel_dir  string, output directory" << endl;
	 */
}
