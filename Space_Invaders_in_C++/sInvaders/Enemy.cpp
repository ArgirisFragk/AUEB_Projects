#include "Enemy.h"
#include "config.h"

Enemy::Enemy(float x = 0.0f, float y = 0.0f) {
	objectx = x;
	objecty = y;
}

void Enemy::draw(std::string s) {
	float blink = 0.5f + 0.5f * sinf(graphics::getGlobalTime() / 50); //xrhsimpoiountai kai ta 2 gia 
	float blink2 = 0.5f + 0.5f * sinf(graphics::getGlobalTime() / 100);//to 'animation' twn ekswghinwn

	br.outline_opacity = 0.0f;
	graphics::setOrientation(0);
	br.fill_opacity = blink;//einai 50 kai 100 wste th mia na deixnei thn mia eikona, thn allh fora thn allh
	br.texture = "assets/"+ s +".png";
	graphics::drawRect(objectx, objecty, ALIEN_W, ALIEN_H, br);
	
	br.fill_opacity = blink2;
	br.texture = "assets/"+ s +"2.png";
	graphics::drawRect(objectx, objecty, ALIEN_W, ALIEN_H, br);

	graphics::resetPose();
}
float Enemy::get_width() {
	return ALIEN_W;
}
float Enemy::get_height() {
	return ALIEN_H;
}



Enemy*** create2DArray(int height, int width) // einai enas custom pinakas 2 diastasewn gia pointers
{											//pairnei pointers se ekswghinous kai tous topothetei sthn swsth thesh ston canvas
	Enemy*** array2D = 0;
	array2D = new Enemy * *[height];
	float x = 0;
	float y = 0;
	for (int h = 0; h < height; h++)
	{
		x += 100;
		y = 0;
		array2D[h] = new Enemy * [width];
		for (int w = 0; w < width; w++)
		{
			y += 50;
			array2D[h][w] = new Enemy(x, y);

		}
	}
	return array2D;
}