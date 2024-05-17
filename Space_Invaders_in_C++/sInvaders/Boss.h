#pragma once
#include "graphics.h"
#include "gameObject.h"
#include "config.h"

class Boss : public gameObject {
private:
	graphics::Brush br;
	graphics::Brush br2;
	float hp = 1.0f;
	bool left = false;
	bool right = true;
	bool changeYdir = false;
public:
	Boss(float x,float y);
	void draw();
	void update();
	void hit();
	float remaining_hp();
	float get_width() override;
	float get_height() override;

};