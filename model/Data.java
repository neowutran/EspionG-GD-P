package model;

//
// IUT de Nice / Departement informatique / Module APO-Java
// Annee 2011_2012 - Gestion des fichiers de donnees
//
// Classe Data - Services de gestion des fichiers de donnees
//
// Edition A    : enregistrement et chargement d'un fichier de donnees
//
//    + Version 1.0.0  : version initiale
//    + Version 1.1.0  : avec trace de l'execution d'un service dans la console
//    + Version 1.2.0  : chargement d'un fichier de donnees depuis le sous
//                       repertoire Data du repertoire courant, a defaut de le
//                       trouver directement dans ce dernier     
/**
 *
 * La classe Data fournit deux services destinés à simplifier la gestion des 
 * fichiers de donnees.
 *
 * Les services fournis sont :
 *
 * load   : charger un objet depuis le repertoire courant ou depuis le
 *          sous repertoire Data
 * store  : enregistrer un objet dans le repertoire courant.
 *
 * @author Alain Thuaire - Universite de Nice/IUT - Departement informatique
**/

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

abstract public class Data {
/**
 *
 * La methode store enregistre dans un fichier du repertoire courant l'objet 
 * fourni en premier parametre. Le fichier resultant est cree avec l'extension .data. 
 * Le nom du fichier est forme automatiquement de la facon suivante : p2-p3.data, où 
 * p2 et p3 designent les deux derniers parametres effectifs. 
 * 
**/

   // ------------------------------------------     *** Methode store
   //
   public static boolean store (Object data, 
                                String  name  ,
                                String  version) {                          	
   String nomFichier;      
   FileOutputStream f= null;
   ObjectOutputStream out= null;
   
      // Controler l'existence de la donnee
      //
      if (data == null) return false;
      
      // Construire le nom du fichier de donnees
      //
      nomFichier= name + "-" + version + ".data";
      
      // Construire un fichier logique et le fichier physique associe
      //
      try {f= new FileOutputStream(nomFichier);}
      catch (Exception e) {return false;}
      
      // Construire un flux de sortie base sur le fichier logique
      //
      try {out= new ObjectOutputStream(f);}
      catch (Exception e) {return false;}
      
      // Serialiser l'objet contenant les donnees dans le flux de sortie
      //
      try{out.writeObject(data);}
      catch (Exception e) {return false;}
      
      System.out.println("Enregistrement du fichier " + nomFichier + " : OK");
      return true;
   }
   
/**
 *
 * La methode load charge le contenu d'un fichier de donnees depuis le repertoire courant 
 * ou a defaut depuis le sous repertoire Data. L'objet resultant est la valeur de retour. 
 * Le fichier origine possede obligatoirement l'extension .data. Le nom du fichier est forme 
 * automatiquement de la facon suivante : p1-p2.data, ou p1 et p2 designent les deux parametres 
 * effectifs. 
 * 
**/

   // ------------------------------------------     *** Methode load
   //
   public static Object load (String fileLocation) {     
	   FileInputStream f= null;
	   ObjectInputStream in= null;
	   Object resultat;
	 
	      // Construire un fichier logique correspondant
	      //
	      try {f= new FileInputStream(fileLocation);}
	      catch (Exception e1) {
	 return null;
	      } 
	      
	      // Construire un flux d'entree base sur le fichier logique
	      //
	      try {in= new ObjectInputStream(f);}
	      catch (Exception e) {return null;} 
	      
	      // Acquerir et deserialiser le flux d'entree
	      //
	      try{resultat=in.readObject();}
	      catch (Exception e) {return null;} 
	          
	      System.out.println("Chargement du fichier " + fileLocation + " : OK");
	      return resultat;
	   } 
}
