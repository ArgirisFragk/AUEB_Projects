#include <pthread.h>

//Definitions 
#define N_tel 3 
#define N_cook 2
#define N_oven 10
#define N_deliverer 7
#define T_orderlow 1 
#define T_orderhigh 5 
#define N_orderlow 1 
#define N_orderhigh 5
#define T_paymentlow 1
#define T_paymenthigh 2
#define C_pizza 10
#define P_fail 5
#define T_prep 1
#define T_bake 10
#define T_pack 2
#define T_dellow 5
#define T_delhigh 15

//Mutexes needed for each thread
pthread_mutex_t  mPhones;
pthread_mutex_t  mCooks;
pthread_mutex_t  mOvens;
pthread_mutex_t  mDeliverers;
pthread_mutex_t  mPackager;
pthread_mutex_t  mPrint;//Lock each time a thread wants to print
pthread_mutex_t  mUpdate;

//Condition variables for each thread
pthread_cond_t  cPhones;
pthread_cond_t  cCooks;
pthread_cond_t  cOvens;
pthread_cond_t  cDeliverers;
pthread_cond_t  cPackager;
pthread_cond_t  cPrint;//Used to inform a thread that it can print
pthread_cond_t  cUpdate;

//Variables to store the given arguments
int N_cust,seed;

//Measurement variables
int earnings = 0;
int successful_order = 0;
int failed_order = 0;
float sum_waiting_time = 0;
float max_waiting_time = -1;
float sum_service_time = 0;
float max_service_time = -1;
float sum_cold_time = 0;
float max_cold_time = -1;

//Available resources
int available_phones = N_tel;
int available_cooks = N_cook;
int available_ovens = N_oven;
int available_deliverers = N_deliverer;
int available_packager = 1;
int available_print = 1;
int available_service = 1;
int available_cold = 1;
int available_update = 1;

float seconds_to_minutes(float seconds) {
	if (seconds >= 60) {
		int m = seconds / 60;
		float s = ((int) seconds % 60) / 100.f;
		return m+s;
	}
	else {
		return seconds / 100.f; 
	}
}

