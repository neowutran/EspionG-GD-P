package library;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sephiroth
 */
public class ValidatorClasse implements Validator {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	public static final Boolean	EXCLUSION_SAUF		= false;
	public static final Boolean	INCLUSION_SAUF		= true;
	private Boolean				type				= false;
	public List<String>			elements			= new ArrayList<String>();

	/**
	 * @return List<String>
	 */
	public List<String> getElements() {

		return this.elements;
	}

	/**
	 * @param elements
	 */
	public void setElements(final List<String> elements) {

		this.elements = elements;
	}

	public Boolean validate(Object event, Long time) throws Exception {

		
		for (String e : elements) {

			System.out.println(e);
			System.out.println(event.getClass().getName());
			if (event.getClass().getName().equals(e)) {

				if (type.booleanValue() == EXCLUSION_SAUF.booleanValue()) {

					System.out.println("sortie 1");
					return true;

				} else {

					System.out.println("sortie 2");
					return false;

				}

			}else{
	
				if (type.booleanValue() == EXCLUSION_SAUF.booleanValue()) {
	
					System.out.println("sortie 3");
					return false;
	
				} else {
	
					System.out.println("sortie 4");
					return true;
	
				}
			}

		}

		throw new Exception(
						"elements est null ou un probleme inconnu est survenu");

	}

	/**
	 * @param type
	 */
	public void setType(final Boolean type) {

		this.type = type;

	}

	/**
	 * @return Boolean
	 */
	public Boolean getType() {

		return this.type;

	}
}
