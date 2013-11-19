package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import library.ValidatorClasse;
import model.Data;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ConfigWriter {
	
	public static void main(String[] args) {
		HashMap config = new HashMap();
		
		HashMap configEspionG = new HashMap();
		HashMap reglesEspionG = new HashMap();
		HashMap validateur = new HashMap();
		HashMap configValidateur1 = new HashMap(); 
		ValidatorClasse v1 = new ValidatorClasse();
		List<String> classes = new ArrayList<String>();
		classes.add("java.awt.event.MouseEvent");
		v1.setElements(classes);
		
		configValidateur1.put("action", "create");
		configValidateur1.put("validateur", v1);
		validateur.put("validateur1", configValidateur1);
		reglesEspionG.put("requis", "validateur1");
		reglesEspionG.put("rules", validateur);
		configEspionG.put("hamecon", reglesEspionG);
		
		HashMap configEspionGD = new HashMap();
		configEspionGD.put("requis", "validalol");
		HashMap rules = new HashMap();
		rules.put("validalol", null);
		configEspionGD.put("rules", rules);
		
		config.put("espionGD", configEspionGD);
		config.put("config", configEspionG);
		
		Data.store(config, "Espion", "1.0.0");
	}
}
