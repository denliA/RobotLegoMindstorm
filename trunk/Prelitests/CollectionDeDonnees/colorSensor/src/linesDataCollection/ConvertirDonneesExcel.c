/*
 * ligneRouge.c
 *
 *  Created on: 13 févr. 2021
 *      Author: denli
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define NB 230

int main(){
	FILE *Fic = NULL;
	float rouge[NB];
	float vert[NB];
	float bleu[NB];
	float r,g,b;
	int ligne=0;
	int sortie=0;
	int i=0;
	char code;
	char nom[NB];

	printf("Entrez le chemin du fichier à ouvrir (ex: /Users/denli/Downloads/ligne_bleu_2.txt)\n ");
	fflush(stdout);
	scanf("%s",nom);
	fflush(stdin);

	Fic = fopen(nom,"r");
	if(Fic==NULL)
		printf("Erreur lors de l'ouverture du fichier\n");
	printf("fichier ouvert\n");

	//compter le nombre de lignes cad de couples RGB
	while(!sortie){
		code = fgetc(Fic);
		if (code=='\n')
			ligne++;
		if (code==EOF)
			sortie=1;
	}
	printf("lignes = %d\n",ligne);
	rewind(Fic);


	for (i=0;i<ligne;i++){
		fscanf(Fic, "R : %f;G : %f;B : %f\n", &r,&g,&b);
		printf("%.2f\t%.2f\t%.2f\n",r,g,b);
		rouge[i]=r;
		vert[i]=g;
		bleu[i]=b;
	}
	code = fclose(Fic);
	if(code==EOF)
		printf("Fichier pas fermé\n");
	else
		printf("Fichier fermé\n");


	printf("Entrez le nom du fichier pour le rouge (ex : LigneBleuCouleurRouge.txt)\n ");
	fflush(stdout);
	scanf("%s",nom);
	fflush(stdin);
	Fic = fopen(nom,"w");
	if(Fic==NULL)
			printf("Erreur lors de l'ouverture du fichier\n");
	printf("Fichier ligneRouge ouvert\n");
	for (i=0;i<ligne;i++){
		fprintf(Fic, "%.2f\n",rouge[i]);
	}
	code = fclose(Fic);
		if(code==EOF)
			printf("Fichier pas fermé\n");
	printf("Fichier ligneRouge fermé\n");

	printf("Entrez le nom du fichier pour le vert (ex : LigneBleuCouleurVerte.txt)\n ");
	fflush(stdout);
	scanf("%s",nom);
	fflush(stdin);
	Fic = fopen(nom,"w");
	if(Fic==NULL)
		printf("Erreur lors de l'ouverture du fichier\n");
	printf("Fichier ligneVerte ouvert\n");
	for (i=0;i<ligne;i++){
		fprintf(Fic, "%.2f\n",vert[i]);
	}
	code = fclose(Fic);
		if(code==EOF)
			printf("Fichier pas fermé\n");
	printf("Fichier ligneVerte fermé\n");

	printf("Entrez le nom du fichier pour le bleu (ex : LigneBleuCouleurBleu.txt)\n ");
	fflush(stdout);
	scanf("%s",nom);
	fflush(stdin);
	Fic = fopen(nom,"w");
	if(Fic==NULL)
			printf("Erreur lors de l'ouverture du fichier\n");
	printf("Fichier ligneBleue ouvert\n");
	for (i=0;i<ligne;i++){
		fprintf(Fic, "%.2f\n",bleu[i]);
	}
	code = fclose(Fic);
	if(code==EOF)
		printf("Fichier pas fermé\n");
	printf("Fichier ligneBleue fermé\n");
	return EXIT_SUCCESS;
}

