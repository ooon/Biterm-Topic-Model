package com.ooon.lzj.entity;

public class Biterm {
	int wi;
	int wj;
	int z;
	
	public Biterm(int wi, int wj){
		if(wi > wj){//保证 wi < wj
			int temp = wi;
			wi = wj;
			wj = temp;
		}
		this.wi = wi;
		this.wj = wj;
		this.z = -1;
	}
	public String toString(){
		return "<"+wi+" ,"+wj+"> "+z+"";
	}
	public int getWi() {return wi;}
	public int getWj() {return wj;}
	
	public int getZ() {return z;}
	public void setZ(int z) {this.z = z;}
	
}
