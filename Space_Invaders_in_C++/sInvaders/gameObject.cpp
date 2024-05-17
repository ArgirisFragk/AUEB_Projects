#include "gameObject.h"

gameObject::gameObject()
{
}

float gameObject::get_x() {
	return objectx;
}
float gameObject::get_y() {
	return objecty;
}
void gameObject::move_x(float x) {
	objectx += x;
}
void gameObject::move_y(float y) {
	objecty += y;
}
void gameObject::set_x(float x) {
	objectx = x;
}
void gameObject::set_y(float y) {
	objecty = y;
}
void gameObject::draw() {
}

float gameObject::get_width()
{
	return 0.0f;
}

float gameObject::get_height()
{
	return 0.0f;
}

