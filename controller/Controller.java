package controller;

import gui.MainFrame;

import java.util.HashMap;

import model.Data;
import model.Model;

 

@SuppressWarnings("rawtypes")
public class Controller {
	private MainFrame _view;	
	private Model _model;
	 
	public Controller(Model model) {
		_model = model;
		 
		_view = new MainFrame(this, _model);
		_model.setListener(_view);
	}
	
	public void displayView() {
		_view.setVisible(true);
	}
	
	public void notifyConfigure(String fileLocation) {
		HashMap config = (HashMap) Data.load(fileLocation);
		_model.configure(config);
	}
	
	public void notifyThreadInfosRequest(String espionGD, String espionG) {
		_model.getThreadInfosAsync(espionGD, espionG);
	}
}
