#pragma once
#ifndef GAMEOBJECT_CLASS
#define GAMEOBJECT_CLASS
#endif

class gameObject {
protected:
	float objectx = 0.0f;
	float objecty = 0.0f;

public:
	gameObject();
	float get_x();
	float get_y();
	void move_x(float x);
	void move_y(float y);
	void set_x(float x);
	void set_y(float y);
	void draw();
	virtual float get_width();
	virtual float get_height();

};