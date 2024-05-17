#include "graphics.h"
#include "functions_classes.h"

void update(float ms) {
    Game* game = reinterpret_cast<Game*>(graphics::getUserData());
    game->update();
}
//--------------------------------------------------------------------------------------------------
void draw() {
    Game* game = reinterpret_cast<Game*>(graphics::getUserData());
    game->init();
    game->draw();
   
}
//--------------------------------------------------------------------------------------------------
int main()
{
    Game SpaceInvaders;

    graphics::createWindow(W_WIDTH, W_HEIGHT, "Space Invaders");

    graphics::setUserData(&SpaceInvaders);
    
    graphics::setDrawFunction(draw);
    graphics::setUpdateFunction(update);

    graphics::setCanvasSize(C_WIDTH, C_HEIGHT);
    graphics::setCanvasScaleMode(graphics::CANVAS_SCALE_FIT);

    graphics::startMessageLoop();
    graphics::destroyWindow();

    return 0;
}