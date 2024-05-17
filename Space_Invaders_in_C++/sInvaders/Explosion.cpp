#include "Explosion.h"
#include "config.h"

Explosion::Explosion(float x, float y)
{
	objectx = x;
	objecty = y;
}

void Explosion::draw()
{//dimiourgei thn ekrhskh sto megethos enos ekswghinou
	br.outline_opacity = 0.0f;
	graphics::setOrientation(0);
	br.texture = "assets/exp.png";
	graphics::drawRect(objectx, objecty, ALIEN_W, ALIEN_H, br);
}
