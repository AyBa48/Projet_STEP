
class Dimensionnement_Lagunage {
	
	public double EquivalentHab(double charge, double ratio){
		return charge/ratio;
	}
	public double Volume_EH(double debit, double charge, double ratio){
		return debit/EquivalentHab(charge, ratio);
	}
	public double Con_DBO5(double charge, double debit){
		return charge/debit;
	}
	
}
 class BassinAnaero extends Dimensionnement_Lagunage {
	//private int Temp;
	
	//***************D'apres Mara&Pearson la charge volumique admissible en bassin anaérobie
    // **************est paramétrée en fonction de la temperature, elle est égale a 100 si la Temp est inferieur
	// ***************10 dégré; 20*temp-100 si la temp est comprise entre 10 et 20 et 300 lorsque la temp superieur à 20 degre
	
	//##********Calcul de la charge volumique en bassin anaérobie **********########
	
	public double Charge_Volumiq(int Temperature){
		double charge;
		if (Temperature < 10){
			charge = 100;
		}else {
			if (Temperature > 10 && Temperature < 20){
				charge = (20*Temperature - 100);
			}else {
				charge = 300;
			}
		}
		return charge;
	}
	//##*******************Caractéristiques des bassins *************************####
	
	
	
	public double Volume_Bassin_Anaérobie(double charge, int Temperature){
		return charge/Charge_Volumiq(Temperature);
	}
	
	public double Temps_Sejour(double debit, double charge, int Temperature){
		return Volume_Bassin_Anaérobie(charge, Temperature)/debit;
	}
	
	//##************************Vérification de la fréquence du curage Bassin Anaérobie*****************###
	
	               //********Charge volumique avant curage
	
	public double Volume_boue(double ratio_boue, double charge, double ratio){
		return ratio_boue*EquivalentHab(charge, ratio);
	}
	public double Volume_Bassin_Anaérobie_corrigé(double charge, int Temperature, double ratio, double ratio_boue){
		return Volume_Bassin_Anaérobie(charge, Temperature)+Volume_boue(ratio_boue, charge, ratio);
	}
	public double Frequence_Curage(double charge, int Temperature, double ratio, double ratio_boue, double debit, double temps){
		return (Volume_Bassin_Anaérobie_corrigé(charge, Temperature, ratio, ratio_boue)-(debit * temps))/Volume_boue(ratio_boue, charge, ratio);
	}
	                                      //****Frequence de curage --- Methode de Mara
	
	public double Frequence_Curage_Mara(double charge, int Temperature, double ratio, double ratio_boue){
		return ((1/3)*Volume_Bassin_Anaérobie_corrigé(charge, Temperature, ratio, ratio_boue))/(ratio_boue*EquivalentHab(charge, ratio));
	}
	
	                                       //********Charge volumique maximmum avant curage
	
	public double Charge_max(double charge, int Temperature, double ratio, double ratio_boue, double debit, double temps){
		return (charge)/(Volume_Bassin_Anaérobie_corrigé(charge, Temperature, ratio, ratio_boue)-(Volume_boue(ratio_boue, charge, ratio)*Frequence_Curage(charge, Temperature, ratio, ratio_boue, debit, temps)));
	}
	              
	
	                                       //*************Verification de la charge surfacique à saturation
	
		
	public double Surface_Bassin_Anaéro(double charge, int Temperature, double ratio, double ratio_boue, double profondeur){
		return (Volume_Bassin_Anaérobie_corrigé(charge, Temperature, ratio, ratio_boue))/profondeur;
	}
	
	public double Charge_surfaciq(double charge, int Temperature, double ratio, double ratio_boue, double profondeur){
		return charge/Surface_Bassin_Anaéro(charge, Temperature, ratio, ratio_boue, profondeur);
	}
	
	
	                                    //####*************Fonctionnement au demarrage
	
	                                               //*********** Paramètre de fonctionnement
	
	//private double chge_vol_dem;
	
	public double Charge_Volumiq(double charge, int Temperature, double ratio, double ratio_boue){
		return charge/Volume_Bassin_Anaérobie_corrigé(charge, Temperature, ratio, ratio_boue);
	}
	public double Temps_sejour_corrigé(double charge, int Temperature, double ratio, double ratio_boue, double debit){
		return Volume_Bassin_Anaérobie_corrigé(charge,Temperature, ratio, ratio_boue)/debit;
	}
		
	                                 //##****************************Rendement d'épuration DBO5
	
	// D'après Mara&Pearson le rendement escompté des bassins anaérobies peut être défini comme suit:
	   //40 % si la temp est 10 degre, (2*temp+20)% si la temperature est compris entre 10 et 25 degre, 70 % dans les autres cas 

	public double Rendement_Epuration_DBO5(int Temperature){
		double rend;
		if (Temperature < 10){
			rend = 40;
		}else {
			if (Temperature >10 && Temperature < 25){
				rend =(2*Temperature + 20);
			}else{
				rend = 70;
			}
		}
		return rend;
	}
	 //**********************#### ** Caractéristiques de l'eau à la sortie des anaérobies

	
	public double Charge_Anaerobie(double rendement, double charge){
		return (1-rendement)*charge;
	}
	public double Concentration_Anaero(double rendement, double charge, double debit){
		return Charge_Anaerobie(rendement, charge)/debit;
	}
	
	
}
