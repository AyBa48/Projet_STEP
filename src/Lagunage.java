import java.util.Scanner;

public class Lagunage {
	
	    private int Année_horizon_sat;
		public double pop_sat;
		private double tx_branch_sat;
		private double debit_sat;
		private double chge_DBO5_sat;
		private double CF_brute_sat;                //Coliformes Fecaux par 100 ml*\
		private double NEM_brute_sat;               //Oeuf d'Helminthe par litre*\
		private double ratio_sat;               //Ratio DBO5 par équivalent habitant*\
		//private double Eq_hab_sat;              //Equivalent Habitant*\
		//private double Vol_Eq_sat;                  //Volume par equivalent habitant*\
		//private double Con_saturation;                 //Concentration en DBO5*\
		private int Année_horizon_dem;
		private double pop_dem;
		private double tx_branch_dem;
		private double debit_dem;
		private double chge_DBO5_dem;
		private double CF_brute_dem;                //Coliformes Fecaux par 100 ml*\
		private double NEM_brute;               //Oeuf d'Helminthe par litre*\
		private double ratio_dem;               //Ratio DBO5 par équivalent habitant*\
		private double ratio_boue_dem;
		//private double Eq_hab_dem;              //Equivalent Habitant*\
		//private double Vol_Eq_dem;                  //Volume par equivalent habitant*\
		//private double Con_demarrage;                 //Concentration en DBO5*\
		private int temperature_eau_usée;
		//private double vol_bassin;
		//private double Tps_sejour;
		private double ratio_boue_sat;  //Ratio de production des boues par equivalent habitant
		//private double tps_sejour_min;
		private double profondeurBassin_anaero;
		//private double rend_retenu;
		//private double debit_ana_dem;
		//private double chge_ana_dem;
		//private double con_ana_dem;
		Dimensionnement_Lagunage lagunage; 
		BassinAnaero Anaerobe;
	//	Lagunage(int anS, double popS, double txS, double debitS, double chgeS, double CFs, double NemS, double ratS, double ratBs){
		//	Année_horizon_sat=anS; pop_sat = popS; tx_branch_sat=txS; debit_sat=debitS; chge_DBO5_sat=chgeS; CF_brute_sat= CFs;
		//	NEM_brute_sat=NemS; ratio_sat=ratS; ratio_boue_sat=ratBs;
	//	}
		Lagunage(int anD, double pD, double txD, double debD, double chgD, double CFd, double NemD, double ratD, double ratBd, int temp, int anS, double popS, double txS, double debitS, double chgeS, double CFs, double NemS, double ratS, double ratBs){
			Année_horizon_dem=anD; pop_dem=pD; tx_branch_dem=txD; debit_dem=debD; chge_DBO5_dem=chgD; CF_brute_dem=CFd; NEM_brute=NemD;
			ratio_dem=ratD; ratio_boue_dem=ratBd; temperature_eau_usée = temp; Année_horizon_sat=anS; pop_sat = popS; tx_branch_sat=txS; debit_sat=debitS; chge_DBO5_sat=chgeS; CF_brute_sat= CFs;
			NEM_brute_sat=NemS; ratio_sat=ratS; ratio_boue_sat=ratBs;
			
		}
		// Donnée horizon de saturation de la STEP
		
		double EH_saturation = lagunage.EquivalentHab(chge_DBO5_sat, ratio_sat);
		double Voulme_EH_saturation = lagunage.Volume_EH(debit_sat, chge_DBO5_sat, ratio_sat);
		double Concentration_DBO5_saturation = lagunage.Con_DBO5(chge_DBO5_sat, debit_sat);
		
		// Donnée horizon de demarrage de la STEP
		
		double EH_demarrage = lagunage.EquivalentHab(chge_DBO5_dem, ratio_dem);
		double Voulme_EH_demarrage = lagunage.Volume_EH(debit_dem, chge_DBO5_dem, ratio_dem);
		double Concentration_DBO5_demarrage = lagunage.Con_DBO5(chge_DBO5_dem, debit_dem);
		
		// Calcul des Bassins Anaerobes
		
		double charge_volumique = Anaerobe.Charge_Volumiq(temperature_eau_usée);
		
		//Caractéristiques des Bassins Anaérobes
		
		double Volume_Bassin_Anaerobe=Anaerobe.Volume_Bassin_Anaérobie(chge_DBO5_sat, temperature_eau_usée);
		double Temps_sejour= Anaerobe.Temps_Sejour(debit_sat, chge_DBO5_sat, temperature_eau_usée);
		
		//Verification de la frequence de curage à saturation de la STEP
		
		double Volume_boue_saturation= ratio_boue_sat*EH_saturation;
		double Volume_Bassin_Anaerobe_corrigé = Anaerobe.Volume_Bassin_Anaérobie_corrigé(chge_DBO5_sat, temperature_eau_usée, ratio_sat, ratio_boue_sat);
		int temps_sejour_min = (int) Temps_sejour;
		double frequence_curage_sat = Anaerobe.Frequence_Curage(chge_DBO5_sat, temperature_eau_usée, ratio_sat, ratio_boue_sat, debit_sat, temps_sejour_min);
		double frequence_curage_mara_sat=Anaerobe.Frequence_Curage_Mara(chge_DBO5_sat, temperature_eau_usée, ratio_sat, ratio_boue_sat);
		int frequence_curage_retenu_sat=(int)Math.min(frequence_curage_sat, frequence_curage_mara_sat);
		
		//Verification de la charge surfacique à saturation
		
		int Profondeur_Bassin_Anaerobe = 4;  // 4 metre profondeur usuelle pour les bassins anaerobes
		double Surface_Bassin = Anaerobe.Surface_Bassin_Anaéro(chge_DBO5_sat, temperature_eau_usée, ratio_sat, ratio_boue_sat, Profondeur_Bassin_Anaerobe);
		double charge_surfaciq = Anaerobe.Charge_surfaciq(chge_DBO5_sat, temperature_eau_usée, ratio_sat, ratio_boue_sat, Profondeur_Bassin_Anaerobe);
		public double GetSurface(){
			return Surface_Bassin;
		}
		public double GetvolumeBassin(){
			return Volume_Bassin_Anaerobe_corrigé;
		}
		//Fonctionnement au demarrage
		    //Parametre de fonctionnement
		
		double charge_volumiq = Anaerobe.Charge_Volumiq(chge_DBO5_dem, temperature_eau_usée, ratio_sat, ratio_boue_sat);
		double Temps_sejour_dem = Anaerobe.Temps_sejour_corrigé(chge_DBO5_sat, temperature_eau_usée, ratio_sat, ratio_boue_sat, debit_dem);
		double Charge_surfaciq = Anaerobe.Charge_surfaciq(chge_DBO5_dem, temperature_eau_usée, ratio_sat, ratio_boue_sat, Profondeur_Bassin_Anaerobe);
		
		//Parametre de Curage des boues au demarrage
		
		double Volume_boue_demarrage = Anaerobe.Volume_boue(ratio_boue_dem, chge_DBO5_dem, ratio_dem);
		double Frequence_curage_demarrage = (Volume_Bassin_Anaerobe_corrigé-debit_dem*temps_sejour_min)/Volume_boue_demarrage;
		double Frequence_curage_mara_demarrage=((1/3)*Volume_Bassin_Anaerobe_corrigé)/(ratio_boue_dem*EH_demarrage);
		int frequence_curage_retenu_demarrage=(int)Math.min(Frequence_curage_demarrage, Frequence_curage_mara_demarrage);
		
		//Rendement d'Epuration en terme de DBO5
		
		double rendement = Anaerobe.Rendement_Epuration_DBO5(temperature_eau_usée);
		
		//Caractéristique de l'Eau à la sortie des bassins Anaérobes
		
		double rendement_retenu = Math.min(rendement, (rendement-5));
		
		//Horizon de saturation 		
		
		double charge_horizon_sat = Anaerobe.Charge_Anaerobie(rendement_retenu, chge_DBO5_sat);
		double Concentration_saturation = Anaerobe.Con_DBO5(charge_horizon_sat, debit_sat);
		
		//Horizon de demarrage
		
		double charge_demarrage=Anaerobe.Charge_Anaerobie(rendement_retenu, chge_DBO5_dem);
		double Concentration_demarrage=Anaerobe.Con_DBO5(charge_demarrage, debit_dem);
	
	 public static void main(String[] args) {
		 int valeur; double pop; double tx; double debit; double chge; double CF; double Nem; double rat; double ratB;
		 Scanner clavier = new Scanner(System.in);
		 System.out.println("Veuillez entrer l''horizon de saturation de la STEP :");
		 valeur = clavier.nextInt();
		 System.out.println("Veuillez entrer la population à l'horizon de la saturation de la STEP :");
		 pop = clavier.nextDouble();
		 System.out.println("Veuillez entrer le taux de branchement à l'horizon de saturation de la STEP :");
		 tx=clavier.nextDouble();
		 System.out.println("Veuillez saisir le debit d'eau à l'horizon de saturation :");
		 debit=clavier.nextDouble();
		 System.out.println("Veuillez saisir la charge organique en DBO5 :");
		 chge=clavier.nextDouble();
		 System.out.println("Veuillez saisir la concentration en coliforme fecaux :");
		 CF=clavier.nextDouble();
		 System.out.println("Veuillez indiquer le nombre d'oeuf Helminthes :");
		 Nem=clavier.nextDouble();
		 System.out.println("Veuillez indiquer le ratio de la charge organique : ");
		 rat=clavier.nextDouble();
		 System.out.println("Veuiller entrer le ratio de boue :");
		 ratB=clavier.nextDouble();
		 int anDem; double popD; double txDem; double debDem; double chgDem; double CFdem; double NemD; double ratDem; double ratBdem; int temp;
		 
		 System.out.println("Veuillez entrer l''horizon de demarrage de la STEP :");
		 anDem = clavier.nextInt();
		 System.out.println("Veuillez entrer la population à l'horizon de demarrage de la STEP :");
		 popD = clavier.nextDouble();
		 System.out.println("Veuillez entrer le taux de branchement à l'horizon de demarrage de la STEP :");
		 txDem=clavier.nextDouble();
		 System.out.println("Veuillez saisir le debit d'eau à l'horizon de demarrage :");
		 debDem=clavier.nextDouble();
		 System.out.println("Veuillez saisir la charge organique en DBO5 au demarrage :");
		 chgDem=clavier.nextDouble();
		 System.out.println("Veuillez saisir la concentration en coliforme fecaux au demarrage:");
		 CFdem=clavier.nextDouble();
		 System.out.println("Veuillez indiquer le nombre d'oeuf Helminthes au demarrage:");
		 NemD=clavier.nextDouble();
		 System.out.println("Veuillez indiquer le ratio de la charge organique au demarrage: ");
		 ratDem=clavier.nextDouble();
		 System.out.println("Veuiller entrer le ratio de boue au demarrage :");
		 ratBdem=clavier.nextDouble();
		 System.out.println("Veuiller entrer la temperature de l'eau");
		 temp= clavier.nextInt();
		 Lagunage lag = new Lagunage(anDem, popD, txDem, debDem, chgDem, CFdem, NemD, ratDem, ratBdem, temp, valeur, pop, tx, debit, chge, CF, Nem, rat, ratB);
		// Lagunage lag = new Lagunage ();
		 
		 System.out.println("*******"+ " Voila quelques résultats"+" *********");
		 System.out.println("Surface bassin Anaérobie "+ lag.GetSurface());
		 System.out.println("Volume Bassin Anaerobe "+ lag.GetvolumeBassin());
		   
	 }

}
