#pragma once
#include "gameObject.h"
#include "graphics.h"
#include "Enemy.h"
#include "Player.h"

class Bullet : public gameObject {
private:
	graphics::Brush br;
public:
	Bullet(float x, float y);
	void draw(int x,int y);
	float get_width() override;
	float get_height() override;
};

bool collision(Bullet* b, gameObject* a);
