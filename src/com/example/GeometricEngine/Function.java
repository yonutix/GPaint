package com.example.GeometricEngine;

public interface Function<T> {
	public T function(T v);
	public T motionFunction(T v, Vector2 translation, Vector2 rotation, Vector2 scale);
}
