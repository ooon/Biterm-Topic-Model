package com.ooon.lzj.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * 生成Topic下的top word时候，需要找到前K大的主题来显示
 * K  代表概率
 * V  代表下标 
 * @author Administrator
 *
 * @param <K>
 * @param <V>
 */
public class Pair<K,V> implements Comparable<Pair<K,V>>{
	public K k;
	public V v;
	public Pair(K k, V v){
		this.k = k;
		this.v = v;
	}
	/**
	 * 
	 * @return K 该词语的概率
	 */
	public K getValue() {
		return (K)k;
	}
	/**
	 *
	 * @return 返回该词
	 */
	public V getIndex() {
		return (V)v;
	}
	@Override
	public String toString() {
		
		return (double)k+","+v;
	}
	@Override
	public int compareTo(Pair<K, V> p) {
		if((double)p.k > (double)this.k){
			return 1;
		}else
		if((double)p.k < (double)this.k){
			return -1;
		}
		return 0;
	}
	public static void main(String[] args) {
//		Pair<Double,Integer> p = new Pair<Double,Integer>(0.5, 1);
//		Pair<Double,Integer> q = new Pair<Double,Integer>(0.3, 2);
//		Pair<Double,Integer> a= new Pair<Double,Integer>(0.7, 1);
//		List<Pair<Double,Integer>> l = new ArrayList<Pair<Double,Integer>>();
//		l.add(p);
//		l.add(q);
//		l.add(a);
//		Collections.sort(l);
//		System.out.println(l);

	}
}
