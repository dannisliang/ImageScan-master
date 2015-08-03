package net.arvin.imagescan.entitys;

public class Point {
	public float x;
	public float y;
	public Point(){
	}
	public void setPoint(float x,float y){
		this.x = x;
		this.y = y;
	}
	
	public void setPoint(Point point){
		this.x = point.x;
		this.y = point.y;
	}
	
	public boolean isTheSamePoint(Point point){
		if(point.x == x && point.y == y){
			return true;
		}
		return false;
	}
}
