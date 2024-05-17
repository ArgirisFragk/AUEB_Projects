#pragma once
#include "graphics.h"
#include"gameObject.h"


class Enemy : public gameObject {

private:
	graphics::Brush br;
public:
	Enemy(float x, float y);
	void draw(std::string s);
	float get_width() override;
	float get_height() override;

};

Enemy*** create2DArray(int height, int width);