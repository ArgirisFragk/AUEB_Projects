#include "functions_classes.h"
#include <string>

using namespace std;

Game::Game()
{
}
Game::~Game()
{
}
void Game::init() {
	graphics::Brush br;
	br.texture = "assets\\space.png";
	graphics::setOrientation(90);
	graphics::setWindowBackground(br);
	
	br.outline_opacity = 0.0f;
	float offset = 4600 * sinf(graphics::getGlobalTime() / 60000) / 4;
	graphics::drawRect(C_WIDTH/2 , C_HEIGHT/2 + offset , 3000,1000 , br);
	graphics::setWindowBackground(br);
	graphics::resetPose();
}

//------------------------------------------------------DRAW------------------------------------------------------------------------------
void Game::startMenuDraw() { 
	graphics::Brush br;

	float blink = 0.5f + 0.5f + sinf(graphics::getGlobalTime() / 500);

	br.texture = "assets\\marine.png";
	br.outline_opacity = 0.0f;
	graphics::drawRect(500, 500, 150, 150, br);


	graphics::setFont("assets\\space age.ttf");
	br.fill_color[0] = 0.0f;
	br.fill_color[1] = 1.0f;
	br.fill_color[2] = 1.0f;

	drawText(90, 100, 80, "SPACE INVADERS!", br);
	br.fill_color[0] = 1.0f;
	br.fill_color[1] = 1.0f;
	br.fill_color[2] = 1.0f;


	br.fill_opacity = 1.0f;
	drawText(85, 380, 35, "USE 'A'+'D' TO MOVE AND 'SPACE' TO SHOOT", br);
	drawText(10, 590, 15, "ARGYRHS FRAGKOS 3190214", br);
	drawText(700, 590, 15, "GIANNHS MASTROGIANNHS 3190114", br);

	br.fill_color[0] = 1.0f;
	br.fill_color[1] = 0.0f;
	br.fill_color[2] = 0.0f;
	br.fill_opacity = blink;
	drawText(150, 250, 50, "PRESS ENTER TO BEGIN!", br);

}
//----------------------------------------------------------------------------------------------------------------------------------------

void Game::GameOverMenuDraw() {
	graphics::Brush br;

	graphics::drawText(250, 100, 70, "GAME OVER", br);
	br.fill_color[0] = 0.0f;
	br.fill_color[1] = 1.0f;
	br.fill_color[2] = 0.0f;
	graphics::drawText(90, 200, 30, "THANKS FOR PLAYING", br);
	graphics::drawText(90, 265, 30, "PRESS ESCAPE TO LEAVE THE GAME", br);
	br.fill_color[0] = 1.0f;
	br.fill_color[1] = 1.0f;
	br.fill_color[2] = 0.0f;
	graphics::drawText(90, 350, 30, "HIGHSCORE:" + to_string(hs), br);
	br.fill_color[0] = 1.0f;
	br.fill_color[1] = 0.0f;
	br.fill_color[2] = 1.0f;
	graphics::drawText(250, 500, 30, "BETTER LUCK NEXT TIME", br);
	br.fill_color[0] = 1.0f;
	br.fill_color[1] = 0.0f;
	br.fill_color[2] = 0.0f;
	graphics::drawText(220, 450, 40, "PRESS 'R' TO RESTART", br);
}
//----------------------------------------------------------------------------------------------------------------------------------------

void Game::GameWinMenuDraw() {
	graphics::Brush br;

	graphics::drawText(20, 100, 50, "ALL", br);
	graphics::drawText(120, 150, 50, "ALIEN", br);
	graphics::drawText(300, 200, 50, "FIGHTERS", br);
	graphics::drawText(560, 250, 50, "DEFEATED!", br);

	br.fill_color[0] = 0.0f;
	br.fill_color[1] = 1.0f;
	br.fill_color[2] = 0.0f;
	graphics::drawText(90, 300, 30, "THANKS FOR PLAYING", br);
	graphics::drawText(90, 335, 30, "YOUR SCORE: " + to_string(points), br);
	br.fill_color[0] = 1.0f;
	br.fill_color[1] = 0.0f;
	br.fill_color[2] = 0.0f;
	graphics::drawText(90, 375, 30, "HIGHSCORE:" + to_string(hs), br);
	br.fill_color[0] = 1.0f;
	br.fill_color[1] = 0.0f;
	br.fill_color[2] = 1.0f;
	graphics::drawText(20, 450, 30, "PRESS ESCAPE TO LEAVE THE GAME", br);
	graphics::drawText(550, 500, 30, "OR 'R' TO RESTART", br);
}

//---------------------------------------DRAW POINTS--------------------------------------------------------------------------------------
void Game::drawPoints() {
	graphics::Brush br;

	br.fill_color[0] = 1.0f;
	br.fill_color[1] = 0.0f;
	br.fill_color[2] = 0.0f;

	graphics::drawText(5, C_HEIGHT-580, 20, "SCORE: "+to_string(points), br);
	graphics::drawText(900, C_HEIGHT - 580, 20, "LIVES: " + to_string(p->getLives()), br);
	graphics::setFont("assets\\space age.ttf");
	graphics::resetPose();
}
//------------------------------------------------DRAW BOSS' LIFE-------------------------------------------------------------------------
void Game::drawBossLife()
{
	graphics::Brush br;

	br.outline_opacity = 0.0f;
	br.fill_color[0] = 1.0f;
	br.fill_color[1] = 0.2f;
	br.fill_color[2] = 0.2f;

	br.texture = "";

	br.fill_secondary_color[0] = 1.0f * (1.0f -boss->remaining_hp()) + boss->remaining_hp() * 0.2f;
	br.fill_secondary_color[1] = 0.2f;
	br.fill_secondary_color[2] = 0.2f * (1.0f -boss->remaining_hp()) + boss->remaining_hp() * 1.0f;
	br.gradient = true;
	br.gradient_dir_u = 1.0f;
	br.gradient_dir_v = 0.0f;
	graphics::drawRect(C_WIDTH/2 - ((1.0f - boss->remaining_hp()) * 180 / 2), 20, boss->remaining_hp() * 180, 20, br);

	br.outline_opacity = 1.0f;
	br.gradient = false;
	br.fill_opacity = 0.0f;
	graphics::drawRect(C_WIDTH/2, 20, 180, 20, br);
}

//----------------------------------------ALIEN COLLISION UPDATE--------------------------------------------------------------------------
void Game::collisionUpdateAliens() { //elenxei gia collision sfairas tou paixth me enan apo tous ekswghinous
	for (int i = 0; i < A_PINAK_W; i++) {
		for (int j = 0; j < A_PINAK_H; j++) {
			if (aliens[i][j] != nullptr && b) {//diatrexoume ton pinaka elenxontas oti iparxei o kathe ekswghinos
				if (collision(b, aliens[i][j])) {
					if (j == 0) {
						points += 50;
					}
					else if (j == 1 || j == 2) {
						points += 10;
					}
					else {
						points += 30;
					}	
					graphics::playSound("assets\\8- bit explosion.mp3",0.3);
					exp = new Explosion(aliens[i][j]->get_x(), aliens[i][j]->get_y());
					time = graphics::getGlobalTime();//an xtipithei dimiourgei ekrhksh
					delete aliens[i][j];			//kai kanei delete ton antistoixo ekswghino
					aliens[i][j] = nullptr;
					
					delete b;
					b = nullptr;
				}
			}
		}
	}
}
//----------------------------------------DRAW THE ENEMIES--------------------------------------------------------------------------------
void Game::EnemyDraw() {
	for (int x = 0; x < A_PINAK_W; x++) {
		for (int y = 0; y < A_PINAK_H; y++) {
			if (aliens[x][y] != nullptr) { // ftiaxnei tous ekswghinous 
				if (y == 0) {
					aliens[x][y]->draw("octo");//panw-panw seira gemizei me 'octo' 50 points
				}
				else if (y == 1 || y == 3) {
					aliens[x][y]->draw("squid");//2-4 seira gemizei me 'squid' 10 points
				}
				else {
					aliens[x][y]->draw("normal alien");//1-3 seira gemizei me 'normal alien' 30 points
				}
			}
		}
	}
}
//---------------------------------------ENEMY MOVEMENT/UPDATE---------------------------------------------------------------------------
void Game::EnemyUpdate() { //kinish ekswghinwn molis ftasoun deksia h aristera katevainoun kata 1.50f * graphics::getDeltaTime()
	bool changeY = false;
	for (int i = 0; i < A_PINAK_W; i++) {
		for (int j = 0; j < A_PINAK_H; j++) {
			if (aliens[i][j] != nullptr) {
				if (aliens[i][j]->get_x() + 15 >= C_WIDTH) {
					alien_movement = 1;
					changeY = true;			// edo allazei h changeY oste na allajei to ypsos ton aliens
				}
				if (aliens[i][j]->get_x() - 15 <= 0) {
					alien_movement = 0;
					changeY = true;
				}
			}
		}
	}
	if (changeY) {
		for (int i = 0; i < A_PINAK_W; i++) {
			for (int j = 0; j < A_PINAK_H; j++) {
				if (aliens[i][j] != nullptr) {// edw allazei to ypsos twn aliens "katevainoun pros ta katw"
					aliens[i][j]->move_y(1.50f * graphics::getDeltaTime());
				}
			}
		}
	}
	for (int i = 0; i < A_PINAK_W; i++) {
		for (int j = 0; j < A_PINAK_H; j++) {
			if (aliens[i][j] != nullptr) {
				if (check >= 25 && check < 35) {
					switch (alien_movement) {
					case 0:
						aliens[i][j]->move_x(0.22f * graphics::getDeltaTime()); // allazei h taxytita tou -> twn ekswghinwn se 0.20*deltatime
						break;															//                <-		otan exoun meinei <=15

					case 1:
						aliens[i][j]->move_x(-0.22f * graphics::getDeltaTime());	
						break;
					}
				}
				else if (check >= 35) {
					switch (alien_movement) {
					case 0:
						aliens[i][j]->move_x(0.30f * graphics::getDeltaTime());// allazei h taxytita tou -> twn ekswghinwn se 0.30*deltatime
						break;															//               <-				otan exoun meinei <=5

					case 1:
						aliens[i][j]->move_x(-0.30f * graphics::getDeltaTime());
						break;
					}
				}
				else {
					switch (alien_movement) {
					case 0:
						aliens[i][j]->move_x(0.14f * graphics::getDeltaTime()); //edw einai h kanonikh taxytita -> twn ekswghinwn sta 0.14*deltatime
						break;														//                          <-

					case 1:
						aliens[i][j]->move_x(-0.14f * graphics::getDeltaTime());
						break;
					}
				}
			}
		}
	}
	
}
//--------------------------------------------ALIENS' BULLET UPDATE----------------------------------------------------------------------
void Game::BulletAlienUpdate() {
	if ((!alien_bullet_visible)) {
		alien_bullet_visible = true;

		if (aliens[v1][v2] != nullptr) {
			delete b2;
			graphics::playSound("assets\\invader_shot.wav", 0.10f);
			b2 = new Bullet(aliens[v1][v2]->get_x(), aliens[v1][v2]->get_y() + 25); //ksekinaei mia sfaira <tuxaia> apo enan ekswghino
		}																			//tou pinaka mas, efoson iparxei
	}
	if (alien_bullet_visible && b2) {
		b2->move_y(0.34f * graphics::getDeltaTime()); // ALIENS' BULLET SPEED
		if (b2->get_y() > C_HEIGHT) {     //an ftasei katw xwris na xtipisei ton paikth h sfaira svinetai
			alien_bullet_visible = false;
			v1 = rand() % 8;
			v2 = rand() % 5;
		}
	}
}
//---------------------------------------------------------------------------------------------------------------------------------------
void Game::Check4level2() {
	int check1 = 0; //metraei ton arithmo twn ekswghinwn pou exoun pethanei
	for (int i = 0; i < A_PINAK_W; i++) {
		for (int j = 0; j < A_PINAK_H; j++) {
			if (aliens[i][j] == nullptr) {
				check1 += 1;
			}
			else {
				if (aliens[i][j] && p) {
					if (aliens[i][j]->get_y()+75 >= C_HEIGHT) {
						game_mode = 3;
					}
				}
			}
		}
	}
	check = check1;
	if (check1 == 40) { // otan o arithmos einai isos me to sunolo ton ekswghinwn pou xwraei o pinakas
		game_mode = 2;	// proxwrame sto epipedo 2
		
		delete m;							//edw svinontai to mothership
		for (int i = 0; i < 8; i++) {		//kai oi ekswghinoi kathws kai o pinakas tous
			for (int j = 0; j < 5; j++) {	//afou den mas xreiazontai sto epipedo 2
				delete aliens[i][j];
			}
			delete[] aliens[i];
		}
		delete[] aliens;

		if (!boss) {//ligo prin arxisei to epipedo 2 dimiourgeitai o boss antipalos tou epipedou							
			boss = new Boss(C_WIDTH / 2, (C_HEIGHT / 2 - 200));
		}
		graphics::playMusic("assets\\level2.mp3", 0.65f, 2000); // kai allazei h mousikh
	}
}

//----------------------------------------------PLAYER'S BULLET UPDATE------------------------------------------------------------------

void Game::BulletPlayerUpdate() {
	if (graphics::getKeyState(graphics::SCANCODE_SPACE) && !b && p) { //patwntas to space o paixtei purovolei
		graphics::playSound("assets\\Laser Gun.mp3",0.3);			// kai akougetai o antistixos hxos
		
		b = new Bullet(p->get_x(), p->get_y() - 25);
	}
	if (b) {
		b->move_y(-0.60f * graphics::getDeltaTime()); //h sfaira kineitai pros ta panw me taxuthta -0.60f * graphics::getDeltaTime()
		if (b->get_y() < 0) {
			delete b;
			b = nullptr;
			
		}
	}
}
//------------------------------------------------COLLISIONS----------------------------------------------------------------------------
//------------------------------------------------PLAYER-BULLET COLLISION---------------------------------------------------------------
void Game::PlayerCollisionUpdate() {
	if (b2) {
		if (collision(b2, p) && p->getLives() > 0) { //elenxoume me thn sinarthsh mas"collision" an sfaira antipalou xtipise ton paixth
			graphics::playSound("assets\\ship_impact.mp3", 1.0f); //an nai akougetai o antistixos hxos
			exp = new Explosion(b2->get_x(), b2->get_y()); //kai dimiourgeitai mia ekrhksh
			time = graphics::getGlobalTime();

			p->getsHit();	//edw o paixths xanei mia zwh
			alien_bullet_visible = false;
		}
		if (p->getLives() == 0) { //an oi zwes tou paixth = 0 tote
			delete p;			//diagrafetai o paixths
			p = nullptr;
			game_mode = 3;		//kai emfanizetai game over screen
		}
	}
}
//------------------------------------------------BULLET-BULLET COLLISION---------------------------------------------------------------
void Game::BulletCollisionUpdate() {
	if (b && b2) {
		if (collision(b2, b)) { //elegxos gia to an h sfaira tou paixth exei sygkroustei me thn sfaira tou alien
			graphics::playSound("assets\\8- bit explosion.mp3", 0.3); //an nai akougetai o antistixos hxos
			exp = new Explosion(b->get_x(), b->get_y()); //dhmioyrgia ekrhkshs
			time = graphics::getGlobalTime();

			points += 500; // o paixths kerdizei pontous
			
			delete b; //diagrafh ths sfairas tou paikth
			b = nullptr;
			
			alien_bullet_visible = false; 
		}
	}
}
//-------------------------------------------------BULLET-MOTHERSHIP COLLISION----------------------------------------------------------
void Game::BulletMothershipCollision() {
	if (b&&m) {
		if (collision(b, m)) { //elegxos sygkrousis metaksy sfairas paixth kai mothership
			m_visible = false;
			points += rand() % 100 + 101; // o paikths kerdizei tyxaio aritho ponton apo to 101 mexri to 200
			
			graphics::playSound("assets\\8- bit explosion.mp3", 0.3); //se periptosh sygkroushs akougetai o antistixos hxos
			exp = new Explosion(m->get_x(), m->get_y()); //dhmioyrgia ekrhkshs
			time = graphics::getGlobalTime();

			delete m; //diagrafh tou mothership
			m = nullptr;

			delete b; //diagrafh ths sfairas tou paikth
			b = nullptr;
			
		}
	}
}
//------------------------------------------------MOTHERSHIP MOVEMENT/UPDATE---------------------------------------------------------------------
void Game::MothershipUpdate() {
	if (points % 7 == 0 && !m_visible) { //ean oi pontoi tou paikth einai pollaplasio tou 7 kai to mothership den yparxei hdh mesa sthn othonh
		m = new Mothership;			//tote dimiourgei to mothership
		m_visible = true;
	}
	if (m && m_visible) {
		m->update(m_visible);//update eswterikh tou mothership
	}
}
//------------------------------------------------BOSS MOVEMENT/UPDATE----------------------------------------------------------------------------

void Game::BossUpdate()
{
	boss->update(); // update eswterikh tou boss
	if ((!alien_bullet_visible)) {
		alien_bullet_visible = true;

		if (boss != nullptr) {
			delete b2;
			b2 = new Bullet(boss->get_x(), boss->get_y() + 100); //sfaires tou boss
		}
	}
	if (alien_bullet_visible && b2) {
		b2->move_y(0.65f * graphics::getDeltaTime()); // BOSS' BULLET SPEED
		if (b2->get_y() > C_HEIGHT) {
			alien_bullet_visible = false;
		}
	}
}
//------------------------------------------------BOSS COLLISION------------------------------------------------------------------------
void Game::BossCollision()
{
	
	if (boss && b) {
		if (collision(b, boss)) {//elenxei gia collision metaksi sfairas paixth kai tou boss
			points += 50;
			
			exp = new Explosion(b->get_x(), b->get_y());
			time = graphics::getGlobalTime();

			delete b;
			b = nullptr;
			graphics::playSound("assets\\8- bit explosion.mp3", 0.3);

			time = graphics::getGlobalTime();

			boss->hit();	//o paixteis pairnei +50 pontous kai o boss xanei zwh

		}
	}

}
//--------------------------------------------CHECK BOSS FIGHT------------------------------------------------------------------------
void Game::checkbossfight()
{
	if (boss->remaining_hp() <= 0.0f) { //an h zwh tou boss einai 0
		delete boss;					//svinetai oti iparxei kai termatizei to paixnidi
		boss = nullptr;
		alien_bullet_visible = false;
		delete b2;
		b2 = nullptr;
		delete b;
		b = nullptr;
		delete p;
		p = nullptr;
		game_mode = 4;					//emfanizetai minima win screen
	}
}
//----------------------------------------UPDATE HIGHSCORE-------------------------------------------------------------------------------
void Game::highscore()
{//to highscore tou paixth katagrafetai se ena arxeio .txt me to onoma "hs" 
	ifstream myfile("hs.txt");
	string text;
	int x;
  if (myfile.is_open())
  {
	  hs = points;
	  myfile >> x;
	  
	  if (hs > x) {
		  ofstream file("hs.txt", ios::trunc);
		  file << hs;
		  file.close();
	  }else{
		myfile >> x;
		hs = x;
	  }
  }
    myfile.close();
}
//-----------------------------------------------UPDATE/DRAW---------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------------------------------------
void Game::update()
{
	switch (game_mode) {
	case 0:
		updateStart();
		break;
	case 1:
		if (p) {
			updateGame();
		}
		break;
	case 2:
		updateGame2();
		break;
	case 3:
		if (graphics::getKeyState(graphics::SCANCODE_R)) {//an patithei to "R" apo ton xrhsth to paixnidi arxizei apo thn arxh
			restart();
		}
	case 4:
		if (graphics::getKeyState(graphics::SCANCODE_R)) {//an patithei to "R" apo ton xrhsth to paixnidi arxizei apo thn arxh
			restart();
		}
	}

}
void Game::draw()
{
	switch (game_mode) {

	case -1:
		graphics::playMusic("assets\\A Theme For Space.mp3", 0.40f);
		game_mode = 0;
		break;
	case 0:
		startMenuDraw();
		break;
	case 1:
		if (p)
			drawLevel();
		break;
	case 2:
		drawLevel2();
		break;
	case 3:
		GameOverMenuDraw();
		break;
	case 4:
		if(!p)
		GameWinMenuDraw();
		break;
	}
}
//------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------
void Game::restart()
{
	graphics::playMusic("assets\\level1.mp3", 0.25f);
	//an to paixnidi ginei restart enw o paixths den exei perasei to 1o epipedo
	//tote svinontai oi ekwghinoi kai o pinakas tous,to mothership kai o idios o paixths kai arxikopoiountai ksana (22 grammes katw)
	if (game_mode == 1) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				delete aliens[i][j];
			}
			delete[] aliens[i];
		}
		delete[] aliens;
		delete m;
	}

	if (game_mode == 4){//an to paixnidi ginei restart enw o paixths exei termatisei to paixnidi tote den exeim nohma na svistoun
		alien_bullet_visible = false;//ta apo panw kathws exoun svistei sthn check4level2()
		delete b2;					//ara svinetai oti exei meinei kai ksekinaei apo thn arxh
		b2 = nullptr;
	}
	delete boss;
	boss = nullptr;
	delete p;
	p = nullptr;
	if (b) {
		delete b;
		b = nullptr;
	}
	aliens = create2DArray(A_PINAK_W, A_PINAK_H);
	p = new player(C_WIDTH / 2, C_HEIGHT - 25);//player initialization
	p->setLives(3);
	m = new Mothership();
	points = 0; // arxikopoiountai oi pontoi
	game_mode = 1; // metaferomaste sto 1o epipedo
}
//--------------------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------------
void Game::drawLevel()
{
	p->draw();
	drawPoints();
	EnemyDraw();
	if (exp) {
		exp->draw();
	}
	if (b) {
		b->draw(3,10);
	}
	if (alien_bullet_visible && b2) {
		b2->draw(3,10);
	}
	if (m && m_visible) {
		m->draw();
	}
}
void Game::drawLevel2()
{
	if (boss) {
		boss->draw();
		drawBossLife();
	}
	if (alien_bullet_visible && b2) {
		b2->draw(10, 30);
	}
	p->draw();
	drawPoints();
	if (exp) {
		exp->draw();
	}
	if (b) {
		b->draw(3, 10);
	}
}
void Game::updateGame()
{
	p->update(C_WIDTH);
	PlayerCollisionUpdate();
	EnemyUpdate();
	BulletPlayerUpdate();
	BulletAlienUpdate();
	if (graphics::getGlobalTime()-200 > time) {
		delete exp;
		exp = nullptr;
	}
	BulletCollisionUpdate();
	collisionUpdateAliens();
	MothershipUpdate();
	BulletMothershipCollision();
	highscore();
	Check4level2();
}
void Game::updateGame2()
{
	p->update(C_WIDTH);
	PlayerCollisionUpdate();
	BossUpdate();
	BulletPlayerUpdate();
	BossCollision();
	if (graphics::getGlobalTime() - 200 > time) {
		delete exp;
		exp = nullptr;
	}
	highscore();
	checkbossfight();
}
void Game::updateStart()
{
	if (graphics::getKeyState(graphics::SCANCODE_RETURN)) {
		game_mode = 1;
		p->setLives(3);
		graphics::playMusic("assets\\level1.mp3", 0.55f);
	}
}
//--------------------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------------


