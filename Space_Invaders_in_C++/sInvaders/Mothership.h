#pragma once
#include "gameObject.h"
#include "config.h"
#include "graphics.h"

class Mothership : public gameObject {
private:
	graphics::Brush br;
public:
	Mothership();
	void draw();
	void update(bool& a);

	float get_width() override;
	float get_height() override;
};