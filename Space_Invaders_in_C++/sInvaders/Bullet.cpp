#include "Bullet.h"
#include "config.h"


Bullet::Bullet(float x, float y) {
	objectx = x;
	objecty = y;
}

void Bullet::draw(int x , int y) { //dimiourgei mia sfaira kitrinou h asprou xrwmatos(analoga to megethos tou parathirou)
	br.fill_color[0] = 1.0f;
	br.fill_color[1] = 1.0f;
	br.fill_color[2] = 0.0f;

	graphics::drawRect(objectx, objecty, x, y, br);//kai to megethos ths dinetai sthn sinarthsh ws orisma
	graphics::resetPose();
}

float Bullet::get_width(){
	return BULLET_W;
}
float Bullet::get_height(){
	return BULLET_H;
}

bool collision(Bullet* b, gameObject* a) {//elenxei an 2 antikeimena suggrouontai []
	bool col_x = false;
	bool col_y = false;
	if (b->get_x() >= a->get_x() - (a->get_width() / 2) && b->get_x() <= a->get_x() + (a->get_width() / 2)) {
		col_x = true;
	}
	if (b->get_y() >= a->get_y() - (a->get_height() / 2) && b->get_y() <= a->get_y() + (a->get_height() / 2)) {
		col_y = true;
	}
	if (col_x && col_y) {
		return true;
	}
	else {
		return false;
	}
}


