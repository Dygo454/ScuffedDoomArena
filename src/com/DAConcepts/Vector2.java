package com.DAConcepts;

public class Vector2 {
	public float x;
	public float y;
	
	public Vector2(float xVal,float yVal) {
		this.x = xVal;
		this.y = yVal;
	}

	public Vector2 add(Vector2 other) {
		return new Vector2(x+other.x, y+other.y);
	}

	public Vector2 mult(float other) {
		return new Vector2(x*other, y*other);
	}
}
