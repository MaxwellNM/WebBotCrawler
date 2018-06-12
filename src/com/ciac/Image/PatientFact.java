package com.ciac.Image;

import java.util.ArrayList;

public class PatientFact {
	
	public ArrayList<Patient> ListedesPatients;
	
	public PatientFact(){
		
		ListedesPatients = new ArrayList<Patient>();
		
	}

	
	public void savePatient (Patient p){
		
		if (p!=null){
			ListedesPatients.add(p);
		}
		
	}
	
	public ArrayList<Patient> listerPatient (Patient p){
		return ListedesPatients;
	}
}
