#include "Mothership.h"

Mothership::Mothership()
{
	objectx = C_WIDTH + (M_SHIP_W / 2);
	objecty = 25;
}

void Mothership::draw()
{
	br.outline_opacity = 0.0f;
	graphics::setOrientation(0);
	br.texture = "assets/mothership.png";
	graphics::drawRect(objectx, objecty, M_SHIP_W, M_SHIP_H, br);
}
void Mothership::update(bool& a) {
	move_x(-0.16f * graphics::getDeltaTime());
	if (get_x() < -M_SHIP_W) {
		objectx = C_WIDTH + (M_SHIP_W / 2);
		a = false;
	}
}

float Mothership::get_width()
{
	return M_SHIP_W;
}

float Mothership::get_height()
{
	return M_SHIP_H;
}
