
public class Dimension_Boue_Activée {
	
	//##***********Calcul des capacités nominales de la Station***************##
	           //********Calcul des debits et des coefficients de pointes horaire*********//
	public double Debit (double Pop, double dotation, double tx_restitution){
		return Pop * dotation * tx_restitution;
	}
	public double coefficient_pointe_horaire (double debit){
		return 1.5 * (2.5 / Math.sqrt(debit));
	}
	public double debit_moyen_JsemTyp(int nbre_jour_sec, int nbre_jour_pluie, double debit_moyen_jour, double debit_pointe_tps_pluie){
		return (nbre_jour_sec/7)*debit_moyen_jour + (nbre_jour_pluie/7)*debit_pointe_tps_pluie;
	}
	         //********************Calcul des charges*****************************************//
	public double Charge_Organique_tempsSec (double concentration, double debitmoyen){
		return debitmoyen * concentration;
	}
	public double Charge_Organique_tempsPluie (double concentration, double debitmoyen, double facteur){
		return Charge_Organique_tempsSec (concentration, debitmoyen) * facteur;
	}
	public double Charge_jour_moyen_semaineTyp (int nbre_jour_sec, int nbre_jour_pluie, double concentration, double debitmoyen, double facteur){
		return ((nbre_jour_sec/7)* Charge_Organique_tempsSec (concentration, debitmoyen)) + ((nbre_jour_pluie/7)*Charge_Organique_tempsPluie(concentration, debitmoyen, facteur));
	}
	
	       //******************Calcul des concentrations d'entrée à la station*****************//
	public double concentration_entrée_BA_tpsSec(double debitmoyen_jourSem_TypeS, int nbre_jour_sec, int nbre_jour_pluie, double concentration, double debitmoyen, double facteur){
		return (1/debitmoyen_jourSem_TypeS) * Charge_jour_moyen_semaineTyp(nbre_jour_sec, nbre_jour_pluie, concentration, debitmoyen, facteur);
	}
	public double concentration_entrée_BA_tpsPluie(double debitpointe_TpsPluie, double concentration, double debitmoyen, double facteur){
		return (1/debitpointe_TpsPluie)*Charge_Organique_tempsPluie(concentration, debitmoyen, facteur);
	}
	
	      //******************Calcul rendement attendu de la station ***********************//
	              //****Les rendements épuratoires et le temps de sejour d'une station d'épuration varient selon que la
	              //**charge massique est faible, moyenne ou forte ************************//
	public double rendement_globaux_escompté(double Concentration_NiveauRjetDBO, double concentration_DBO){
		return 1 - (Concentration_NiveauRjetDBO/concentration_DBO);
	}
}
    //###*******************Dimensionnement du bassin d'aération*******************************************//

	class BassinAeration extends Dimension_Boue_Activée {
		
		//********************Calcul concentration des MES et boues de recirculation ****************************// 
		
		public double concentration_boue_recirculation_MES (double indice_Molhma) {
			//********l'Indice de Molhman permet de mesurer la décantabilité de la boue biologique, et represente le
			//***le volume occupé apres une demi_heure de decantation (volume decanté en 30 mn divisé par la masse des matieres seches
			//****Im < 50 (cm3/gr) la boue à un aspect granuleux et risque de former des dépots
			//*** 80 < Im < 150, la structure de la boue assure une bonne décantabilité
			//*** 150 < Im < 200,la boue est en gonflement
			return (1/indice_Molhma);			
		}
		      //*******Concentration des MES dans le bassin d'aération**********************//
		public double concentrationMax_MES_bassinAération (double indice_Molhman, double Tx_recirculation) {
			//********le taux de recirculation des boues doit etre modulable entre 50 et 150 %(=debit de recirculation divisé par debit eau brute
			//***Remarque: un taux de recirculation de 100% correspond à un facteur d'épaississement de 2, il descend à 1.6 pour un taux de 
			//***de recirculation de 150%
			return (1/indice_Molhman)*(Tx_recirculation/(1+Tx_recirculation));
		}
		//***********Masse des MES et MVS dans le bassin d&ération**********************//
		public double masse_MVS_BassinAéra (double chg_DBO_jourSemaineTyp, double Charge_massiq){
			return (chg_DBO_jourSemaineTyp / Charge_massiq);
		}
		public double masse_MES_Bassin_Aéra(double chg_DBO_jourSemaineTyp, double Charge_massiq, double Tx_MVS){
			return masse_MVS_BassinAéra (chg_DBO_jourSemaineTyp, Charge_massiq)/Tx_MVS;
		}
		                //********Volume du bassin d'aération et charge volumique *******//
		public double Volume_BassinAéra (double chg_DBO_jourSemaineTyp, double Charge_Massiq, double Tx_mvs, double concentration_MES_bassin){
			return chg_DBO_jourSemaineTyp/(Charge_Massiq*Tx_mvs*concentration_MES_bassin);
		}
		public double charge_volumique (double chg_DBO_jourSemaineTyp, double Charge_Massiq, double Tx_mvs, double concentration_MES ){
			return chg_DBO_jourSemaineTyp/Volume_BassinAéra (chg_DBO_jourSemaineTyp, Charge_Massiq, Tx_mvs, concentration_MES);
		}
		//***************Production spécifiquede boue*******************************************************//
		public double charg_MES_BioMinInert_eaubrute(double chargeMES_Jsemaine_Type, double pourcent_MES_BioMinInert_eaubrute){
			return chargeMES_Jsemaine_Type*pourcent_MES_BioMinInert_eaubrute;
		}
		public double concentMES_JsemTyp(double chargeMES_JmoyenSemainType, double debit_moyen_JsemainTyp){
			return chargeMES_JmoyenSemainType/debit_moyen_JsemainTyp;
		}
		              //**************rendement bassin d'aération*******************************//
		public double rendem_bassinAera(double concentNiv_rejetDBO, double concentNiv_rejetMES, double concentDBO_Jmoy_SemTyp){
			return (1-((concentNiv_rejetDBO-0.5*concentNiv_rejetMES)/concentDBO_Jmoy_SemTyp));
		}
		public double ChargMES_produite_par_DBO(double am, double rend_bassinAera, double charge_DBO_Jmoy_SemTyp){
			//*****am represente le besoin en oxygene pour la synthese de la matiere organique, elle represente le taux de conversion 
			//*****du substrat en biomasse(kg de MVS/kg de DBO detruit*******************************//
			return (am*rend_bassinAera*charge_DBO_Jmoy_SemTyp);
		}
		public double ChargMES_detruite_par_respirationEndogene(double masseMVS_bassin, double b){
			//****b represente le besoin en oxygene pour la respiration endogene, elle represente la quantité de matière cellulaire detruite
			//****par auto oxydation(kg MVS detruit/kg MVS de boue en reaction/jour)
			return b*masseMVS_bassin;
		}
		public double ChargMES_inerte_produite_bassin(double pourcent_matiereInerte_produit, double chargMES_detruite){
			return pourcent_matiereInerte_produit*chargMES_detruite;
		}
		public double chargMES_mineral_sorti_bassin(double MES_Mineral_EauBrute){
			return MES_Mineral_EauBrute;
		}
		public double chargMES_Inerte_sorti_bassin(double MES_inerte_eauBrut, double MES_inerte_produitBassin){
			return MES_inerte_eauBrut+MES_inerte_produitBassin;
		}
		public double chargMES_Bio_sorti_Bassin (double rend_bassin, double MES_Bio_eaubrut, double MES_produit_parDBO, double MES_detruitBassin){
			return MES_Bio_eaubrut*(1-rend_bassin)+MES_produit_parDBO- MES_detruitBassin;
		}
		public double chargMES_Bio_non_degrade(double rend_bassin, double MES_Bio_EauBrut){
			return MES_Bio_EauBrut*(1-rend_bassin);
		}
		public double chargMES_total_sorti_bassin(double MES_inerte_sorti_bassin, double MES_bio_sorti, double MES_mineral_sorti){
			return MES_inerte_sorti_bassin+MES_bio_sorti+MES_mineral_sorti;
		}
		public double pourcentage_MES_sorti_bassin(double MES_total_sorti, double MES_MinBioIner_sorti_bassin){
			return MES_MinBioIner_sorti_bassin/MES_total_sorti;
		}
		public double chargMES_sorti_clarificateur(double concen_niveau_RejetMES, double debit_moyen_JsemTyp){
			return concen_niveau_RejetMES*debit_moyen_JsemTyp;
		}
		public double chargMES_BioMinInert_sorti_clarificateur(double MES_sortiClari, double pourcent_MES_BioMinIne_sortiBassin){
			return  MES_sortiClari*pourcent_MES_BioMinIne_sortiBassin;
		}
		public double chargBoue_exces_Clarificateur (double MES_BioMinInert_sortiBassin, double MES_BioMinInert_sortiClarifi){
			return MES_BioMinInert_sortiBassin- MES_BioMinInert_sortiClarifi;
		}
		public double chargTotal_Boue_Exces_Clarifi(double MES_bio_exces_clarifi, double MES_Min_exces_Clarifi, double MES_Inert_excesClarifi){
			return MES_bio_exces_clarifi+MES_Min_exces_Clarifi+MES_Inert_excesClarifi;
		}
		//******************Age des boues et Temps de sejour de l'effluent**********************//
		         //les charges Cm et Cv sont liés par la concentration des boues maintenues en aération, Gould a introduit le facteur
		         //age des boues comme etant le rapport entre la quantité de boue contenue dans le bassin d'aération et celle extraite
		         //quotidiennement
		public double Age_des_boues (double masse_MES_bassin, double Total_Boue_Exces_Clarifi){
			return masse_MES_bassin/Total_Boue_Exces_Clarifi;
		}
		public double Temps_sejour_eauBassin(double concentDBO_JsemTyp, double charg_Volumiq){
			return concentDBO_JsemTyp / charg_Volumiq;
		}
		public double Temp_sejour_eauBassin(double volumeBassin, double debitmoyen_JsemTyp){
			return volumeBassin / debitmoyen_JsemTyp;
		}
		//*****************Volume des boues en exces**********************************************//
		public double Volume_Boue_exces_bassin (double concent_boue_recirc, double chargTotalboue_excesclarif){
			return chargTotalboue_excesclarif/concent_boue_recirc;
		}
	
	}
	class Clarificateur extends BassinAeration {
		
		public double surface_clarificateur(double debit_pointe_temps_pluie, double vitesse_ascensionnelle){
			//********la charge hydraulique superficielle ou vitesse ascensionnelle permetde fixer la surface utile de l'ouvrage
			//*********En pratique la surface du clarificateur est determinée à partir du débit de pointe en temps de pluie, pour une 
			//*** vitesse ascensionnelle (= debit de pointe à traiter divisé par surface utile du clarificateur) de 0.6m/h
			return debit_pointe_temps_pluie/vitesse_ascensionnelle;
		}
		public double volume_clarificateur(double surface_clarifi, double profondeur_clarifi){
			return surface_clarifi*profondeur_clarifi;
		}
		public double diametre_clarificateur(double surface_clarificateur){
			return Math.sqrt(4*surface_clarificateur/Math.PI);
		}
		public double debit_horaire_journalier_clarifi(double debit_horaire, double marge_horaire_journalier){
			return debit_horaire/marge_horaire_journalier;
		}
		public double temps_retention_horaire_journalier(double volume_clarifi, double debit_horaire_jour_clarif){
			return volume_clarifi/debit_horaire_jour_clarif;
		}
		public double vitesse_ascensionnelle_horaire_jour(double debit_horaire_jour, double surface_clarif){
			return debit_horaire_jour/surface_clarif;
		}
	}
	class Traitement_Azote {
		
		public double age_minimal_boue_nitrification (double temperature_mois_pluFroid){
			//#### condition de nitrification : températur doit être > 10°; formule d'Etancourt "###\
			return 6.5 * (0.914)*Math.exp(temperature_mois_pluFroid - 20);
		}
		public double Azote_residuel_particulaire (double debit_moy_SemTyp, double concen_niveau_RejetMES, double pourcent_Nrp_MES_rejet){
			//####l'azote residuel particulaire est calculé par rapport à leur pourcentage dans les MES rejetés 
			// Usuellement, il reprente 3 % des MES rejeté
			return pourcent_Nrp_MES_rejet * debit_moy_SemTyp*concen_niveau_RejetMES ;
		}
		public double Azote_residuel_dur (double charg_Azot_JSemTyp, double pourcent_NK_recu){
			//### l'azote residuel dur est calculé par rapport à l'azote kjeldahl recu ##### 
			//pour les calculs il est généralement pris à 3 % des NK reçus 
			return charg_Azot_JSemTyp*pourcent_NK_recu;
		}
		public double Azote_Kjeldahl_total (double Azote_residuel_dur, double Azote_residuel_particulaire){
			return Azote_residuel_particulaire+Azote_residuel_dur;
		}
		public double Azote_assimilé (double chargDBO_JsemTyp, double rend_BassinAera, double Taux_azote_assimil){
			// Usuellement on considere que 5 % de l'azote est assimilé par la biomasse organique 
			return Taux_azote_assimil*rend_BassinAera*chargDBO_JsemTyp;
		}
		public double Azote_restant_pr_nitrification (double charg_Azot_JSemTyp, double Azote_resid_particulair, double Azote_resid_dur, double Azote_assimile){
			return charg_Azot_JSemTyp - Azote_resid_particulair - Azote_resid_dur - Azote_assimile;
		}
	}
