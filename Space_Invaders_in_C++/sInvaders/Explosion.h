#pragma once
#include "graphics.h"
#include"gameObject.h"

class Explosion : public gameObject {
private:
	graphics::Brush br;
public:
	Explosion(float x, float y);
	void draw();

};