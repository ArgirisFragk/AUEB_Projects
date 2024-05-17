#pragma once
#include "graphics.h"
#include "gameObject.h"
#include "Enemy.h"
#include "Player.h"
#include "Bullet.h"
#include "Mothership.h"
#include "Explosion.h"
#include "Boss.h"
#include "config.h"
#include <stdlib.h>
#include <iostream>
#include <fstream>

using namespace std;

class Game {
private:
    int points = 0; //points will be added to this variable
    int check = 0; // xrhsimopoieitai meta gia ton elenxo twn zwntanwn ekswghinwn
    int time;   //mia metavlhth gia na kratame to in-game time

    Enemy*** aliens = create2DArray(A_PINAK_W, A_PINAK_H);
    player* p = new player(C_WIDTH / 2, C_HEIGHT - 25);//player initialization
    Mothership* m = new Mothership();
    Explosion* exp = nullptr;
    Boss* boss = nullptr;

    bool alien_bullet_visible = false;    
    int alien_movement = 0; //will be used for aliens' direction    
    int hs;                 //highscore
    bool m_visible = false; //mothership visible   
    int game_mode = -1;

    Bullet* b = nullptr;
    Bullet* b2= nullptr;

    int v1 = rand() % A_PINAK_W; //generate 2 random numbers
    int v2 = rand() % A_PINAK_H; //so as to select a random alien in the 2d array

    void drawLevel();
    void drawLevel2();
    void updateGame();
    void updateGame2();
    void updateStart();
    void startMenuDraw();
    void GameOverMenuDraw();
    void GameWinMenuDraw();
    void drawPoints();
    void drawBossLife();
    void restart();
    void collisionUpdateAliens();
    void EnemyDraw();
    void EnemyUpdate();
    void BulletAlienUpdate();
    void Check4level2();
    void BulletPlayerUpdate();
    void PlayerCollisionUpdate();
    void BulletCollisionUpdate();
    void BulletMothershipCollision();
    void MothershipUpdate();
    void BossUpdate();
    void BossCollision();
    void checkbossfight();
    void highscore();
public:
    Game();
    ~Game();
    void init();
    void update();
    void draw();

};