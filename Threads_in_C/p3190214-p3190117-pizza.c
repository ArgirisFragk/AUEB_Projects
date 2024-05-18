//Packages needed for the project
#include "p3190214-p3190117-pizza.h"
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void * Order(void * x) {

	int index = *(int *)x;//Number of thread we are in

	int checker;//Used for error checking in pthread functions

	struct timespec start,stop_pack;
	struct timespec stop_total,stop_phone;
	struct timespec start_cold;

	//random time for the customer to make a call
	if(index != 1) {
		sleep((rand() % (T_orderhigh - T_orderlow + 1)) + T_orderlow);
	}
	
	checker = clock_gettime(CLOCK_REALTIME, &start);//Start the time when the customer call

//=====================Phone section of the order - Start============================
	checker = pthread_mutex_lock(&mPhones);//lock the thread, critical section below 
	
	while(available_phones == 0) {//check if all the threads are locked so we have to wait. 
		checker = pthread_cond_wait(&cPhones,&mPhones);
	}
	available_phones--;

	checker = pthread_mutex_unlock(&mPhones);//Unlock thread

	checker = clock_gettime(CLOCK_REALTIME, &stop_phone);

	float waiting_time = stop_phone.tv_sec - start.tv_sec;//Time while the customer was on hold.
	

	int number_of_pizzas = (rand() % (N_orderhigh - N_orderlow + 1)) + N_orderlow;
	

//=========================Completion of the Order====================================
	pthread_mutex_lock(&mPhones);

	sleep((rand() % (T_paymenthigh - T_paymentlow + 1)) + T_paymentlow);

	int fail_rate = rand() % 100 + 1;
	if(fail_rate <= P_fail) {
		
		pthread_mutex_lock(&mPrint);

		while(available_print == 0) {
			checker = pthread_cond_wait(&cPrint,&mPrint);
		}

		available_print--;
		printf("Transaction failed! The order of %d is canceled\n\n",index);
		available_print++;
		checker = pthread_cond_signal(&cPrint);
		checker = pthread_mutex_unlock(&mPrint);

		failed_order++;
		available_phones++;
		checker = pthread_cond_signal(&cPhones);
		checker = pthread_mutex_unlock(&mPhones);	
		pthread_exit(NULL);
	}
	else {

		pthread_mutex_lock(&mPrint);

		while(available_print == 0) {
			checker = pthread_cond_wait(&cPrint,&mPrint);
		}

		available_print--;
		printf("Transaction was successful! The order of %d is waiting to be cooked!\n\n",index);
		available_print++;
		checker = pthread_cond_signal(&cPrint);
		checker = pthread_mutex_unlock(&mPrint);

		earnings += C_pizza * number_of_pizzas;
		successful_order++;
		available_phones++;
		checker = pthread_cond_signal(&cPhones);
		checker = pthread_mutex_unlock(&mPhones);	
	}
//=====================Phone section of the order - End==========================================

//=====================Cooking and Packaging section of the order - Start========================

	checker = pthread_mutex_lock(&mCooks);//lock thread critical section for coocks

	while(available_cooks == 0) {
		checker = pthread_cond_wait(&cCooks,&mCooks);
	}
	available_cooks--;
	checker = pthread_mutex_unlock(&mCooks);//unlock thread

	sleep(T_prep * number_of_pizzas);

	
	checker = pthread_mutex_lock(&mOvens);//lock thread critical section for ovens

	while(available_ovens < number_of_pizzas) {
		checker = pthread_cond_wait(&cOvens,&mOvens);
	}

	available_ovens -= number_of_pizzas;
	checker = pthread_mutex_unlock(&mOvens);

	checker = pthread_mutex_lock(&mCooks);//lock thread ctitical section for coocks
	available_cooks++;
	checker = pthread_cond_signal(&cCooks);
	checker = pthread_mutex_unlock(&mCooks);//unlock thread

	sleep(T_bake);

	checker = clock_gettime(CLOCK_REALTIME, &start_cold);//start of cold time

	checker = pthread_mutex_lock(&mPackager);//lock thread critical section for packager 

	while(available_packager == 0) {
		checker = pthread_cond_wait(&cPackager,&mPackager);
	}

	available_packager--;
	checker = pthread_mutex_unlock(&mPackager);//unlock thread

	sleep(T_pack);
	clock_gettime(CLOCK_REALTIME, &stop_pack);//end of pack time
	float pack_time = stop_pack.tv_sec - start.tv_sec;

	checker = pthread_mutex_lock(&mOvens);
	available_ovens += number_of_pizzas;
	checker = pthread_cond_signal(&cOvens);
	checker = pthread_mutex_unlock(&mOvens);

	checker = pthread_mutex_lock(&mPrint);

	while(available_print == 0) {
		checker = pthread_cond_wait(&cPrint,&mPrint);
	}
	available_print--;
	printf("The order of %d was packed in %.2f minutes and is ready to get delivered!\n\n",index,seconds_to_minutes(pack_time));
	available_print++;
	checker = pthread_cond_signal(&cPrint);
	checker = pthread_mutex_unlock(&mPrint);

	checker = pthread_mutex_lock(&mPackager);
	available_packager++;
	checker = pthread_cond_signal(&cPackager);
	checker = pthread_mutex_unlock(&mPackager);


//=====================Cooking and Packaging section of the order - End========================

//=====================Delivery section of the order - Start===================================

	checker = pthread_mutex_lock(&mDeliverers);

	while(available_deliverers == 0) {
		checker = pthread_cond_wait(&cDeliverers, &mDeliverers);
	}

	available_deliverers--;
	checker = pthread_mutex_unlock(&mDeliverers);

	int delivery_time = (rand() % (T_delhigh - T_dellow + 1)) + T_dellow;

	sleep(delivery_time);
	checker = clock_gettime(CLOCK_REALTIME, &stop_total);//end of order
	float total_time = stop_total.tv_sec - start.tv_sec;
	float cold_time = stop_total.tv_sec - start_cold.tv_sec;

	
	checker = pthread_mutex_lock(&mPrint);

	while(available_print == 0) {
		checker = pthread_cond_wait(&cPrint,&mPrint);
	}
	available_print--;
	printf("The order of %d was delivered succefully in %.2f minutes\n\n",index,seconds_to_minutes(total_time));
	available_print++;
	checker = pthread_cond_signal(&cPrint);
	checker = pthread_mutex_unlock(&mPrint);

	sleep(delivery_time);// waiting for deliverer to return to the store

	checker = pthread_mutex_lock(&mDeliverers);
	available_deliverers++;
	checker = pthread_cond_signal(&cDeliverers);
	checker = pthread_mutex_unlock(&mDeliverers);

//=====================Delivery section of the order - End==========================================

//================== UPDATE ALL VARIABLES NEEDED FOR THE PROGRAM ===================================
	checker = pthread_mutex_lock(&mUpdate);

	while(available_update == 0) {
		checker = pthread_cond_wait(&cUpdate, &mUpdate);
	}
	available_update--;

	//Update Sum and Max waiting time
	sum_waiting_time += waiting_time;
	if (waiting_time > max_waiting_time) {
		max_waiting_time = waiting_time;
	}

	//Update Sum and Max cold time
	sum_cold_time += cold_time;
	if (cold_time > max_cold_time) {
		max_cold_time = cold_time;
	}

	//Update Sum and Max service time
	sum_service_time += total_time;
	if(total_time > max_service_time) {
		max_service_time = total_time;
	}

	available_update++;
	checker = pthread_cond_signal(&cUpdate);
	checker = pthread_mutex_unlock(&mUpdate);

	pthread_exit(NULL);	
}

//========================Main thread of the program================================================
int main(int argc, char * argv[]) {

	//Check if the number of arguments given is incorrect.
	if (argc != 3) {
		printf("Wrong number of arguments\n\n");
		return -1;
	}
	//Get the given arguments.
	N_cust = atoi(argv[1]);
	seed = atoi(argv[2]);
	int id[N_cust];
	//Set seed for rand()
	srand(seed);

	//Checks if a thread was succefully created
	int checker; 

	pthread_t Customers[N_cust];

	//Allocate memory for the mutexes variables we are going to use
	pthread_mutex_init(&mPhones, NULL);
	pthread_mutex_init(&mCooks, NULL);
	pthread_mutex_init(&mOvens, NULL);
	pthread_mutex_init(&mPackager, NULL);
	pthread_mutex_init(&mDeliverers, NULL);

	//Allocate memory for the condition variables we are going to use
	pthread_cond_init(&cPhones, NULL);
	pthread_cond_init(&cCooks, NULL);
	pthread_cond_init(&cOvens, NULL);
	pthread_cond_init(&cPackager, NULL);
	pthread_cond_init(&cDeliverers, NULL);
	
	
	for(int i = 0; i < N_cust; i++) {
		id[i] = i+1;
		checker = pthread_create(&Customers[i],NULL,Order,&id[i]);
	}

	for(int i = 0; i < N_cust; i++) {
		pthread_join(Customers[i],NULL);
		
	}
	
	pthread_mutex_destroy(&mPhones);
	pthread_mutex_destroy(&mCooks);
	pthread_mutex_destroy(&mOvens);
	pthread_mutex_destroy(&mPackager);
	pthread_mutex_destroy(&mDeliverers);

	pthread_cond_destroy(&cPhones);
	pthread_cond_destroy(&cCooks);
	pthread_cond_destroy(&cOvens);
	pthread_cond_destroy(&cPackager);
	pthread_cond_destroy(&cDeliverers);

	float avg_waiting_time = sum_waiting_time / N_cust;
	float avg_service_time = sum_service_time / successful_order;
	float avg_cold_time = sum_cold_time / successful_order;

	printf("AVERAGE WAITING TIME : %.2f\nMAX WAITING TIME : %.2f\n",seconds_to_minutes(avg_waiting_time),seconds_to_minutes(max_waiting_time));
	printf("AVERAGE SERVICE TIME : %.2f\nMAX SERVICE TIME : %.2f\n",seconds_to_minutes(avg_service_time),seconds_to_minutes(max_service_time));
	printf("AVERAGE COLD TIME : %.2f\nMAX COLD TIME : %.2f\n",seconds_to_minutes(avg_cold_time),seconds_to_minutes(max_cold_time));
	printf("TOTAL EARNINGS ARE: %d\n",earnings);
	printf("SUCCESSFUL ORDERS : %d\n",successful_order);
	printf("UNSUCCESFUL ORDERS: %d\n",failed_order);
	return 0;
}