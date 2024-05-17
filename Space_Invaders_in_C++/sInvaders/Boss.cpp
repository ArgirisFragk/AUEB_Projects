#include "Boss.h"

Boss::Boss(float x ,float y)
{
	objectx = x;
	objecty = y;
}

void Boss::draw()
{	//========paromoio animation me twn ekswghinwn==================
	float blink = 0.5f + 0.5f * sinf(graphics::getGlobalTime() / 50);
	float blink2 = 0.5f + 0.5f * sinf(graphics::getGlobalTime() / 100);

	br.outline_opacity = 0.0f;
	graphics::setOrientation(0);
	br.fill_opacity = blink;
	br.texture = "assets/normal alien.png";
	graphics::drawRect(objectx, objecty, 200, 200, br);

	br.fill_opacity = blink2;
	br.texture = "assets/normal alien2.png";
	graphics::drawRect(objectx, objecty, 200, 200, br);

	graphics::resetPose();
}

void Boss::update()
{
	bool changey = false;
	if (right) {
		move_x(0.30f * graphics::getDeltaTime());//metakinei ton boss deksia kai aristera opws tous ekswghinous
	}											//katevazontas ton opote ftanei sthn akrh kata 30
	if (left) {
		move_x(-0.30f * graphics::getDeltaTime());
	}

	if (get_x() + 100 > C_WIDTH) {
		right = false;
		left = true;
		changey = true;
	}
	if (get_x() - 100 < 0) {
		right = true;
		left = false;
		changey = true;
	}
	if (changey) {						//an ftasei sto katw orio tote metakinitai pros ta panw kai ksanakatevainei
		if (get_y() + 200 > C_HEIGHT) {
			changeYdir = true;
		}
		if (changeYdir) {
			move_y(-10.0f * graphics::getDeltaTime());
			if (get_y() + 100 < 300) { changeYdir = false; }
		}
		else
		{
			move_y(10.0f * graphics::getDeltaTime());
		}
			
	}

}

void Boss::hit()//kathe fora pou ginetai hit hanei mia zwh
{
	hp -= 0.05f;
}

float Boss::remaining_hp()
{
	return hp;
}

float Boss::get_width()
{
	return 200;
}

float Boss::get_height()
{
	return 200;
}