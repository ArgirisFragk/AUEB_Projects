#include "Player.h"
#include "config.h"

player::player(float x, float y) {
	objectx = x;
	objecty = y;
}

void player::moveR_x(float x) {//metakinei ton paixtei pros ta deksia
	objectx += x;
}

void player::moveL_x(float x) {//metakinei ton paixtei pros aristera
	objectx -= x;
}

void player::draw() {//zwgrafizei ton paixth
	graphics::setOrientation(0);
	br.texture = "assets/marine.png";
	br.outline_opacity = 0.0f;

	graphics::drawRect(objectx, objecty, PLAYER_W, PLAYER_H, br);
	graphics::resetPose();
}
void player::update(float cw) {//metakinei ton paixtei opote patietai to A h to D
	if (graphics::getKeyState(graphics::SCANCODE_D)) {
		moveR_x(0.60f * graphics::getDeltaTime());
		if (get_x() + 25.0f > cw) {
			set_x(cw - 25.0f);
		}
	}
	if (graphics::getKeyState(graphics::SCANCODE_A)) {
		moveL_x(0.60f * graphics::getDeltaTime());
		if (get_x() - 25.0f < 0) {
			set_x(25.0f);
		}
	}
}

int player::getLives()
{
	return lives;
}

void player::setLives(int l)
{
	lives = l;
}

void player::getsHit()//meiwnei tis xzwes tou paixth otan xtipithei
{
	lives -= 1;
}

float player::get_width() {//auta ta 2 xrhsimopoiountai sthn Bullet.cpp
	return PLAYER_W;
}
float player::get_height() {
	return PLAYER_H;
}
