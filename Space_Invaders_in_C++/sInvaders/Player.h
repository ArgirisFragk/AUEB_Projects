#pragma once
#include "graphics.h"
#include "gameObject.h"

class player : public gameObject {//klironomei apo thn gameobject
	graphics::Brush br;
	int lives = 3;
public:
	player(float x, float y);
	void draw();
	void moveR_x(float x);
	void moveL_x(float x);
	void update(float c);
	int getLives();
	void setLives(int l);
	void getsHit();
	float get_width() override;
	float get_height() override;

};