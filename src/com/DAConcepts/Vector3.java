package com.DAConcepts;

public class Vector3 {
	public float x;
	public float y;
	public float z;
	static public Vector3 zero = new Vector3(0,0,0);
	
	public Vector3(float xVal,float yVal,float zVal) {
		this.x = xVal;
		this.y = yVal;
		this.z = zVal;
	}
}
