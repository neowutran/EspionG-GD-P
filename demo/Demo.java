package demo;

import model.Model;
import controller.Controller;

public class Demo {
	
	public static void main(String[] args) {	
		Model model = new Model((short)8080, (short)5);
		Controller controller = new Controller(model);
		controller.displayView();
	}
}
