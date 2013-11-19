package library;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sephiroth
 */
public class ValidatorEvenement implements Validator {

	private Boolean				enregistrement		= false;
	private List<String>		debut				= new ArrayList<String>();
	private List<String>		fin					= new ArrayList<String>();

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public Boolean validate(Object event, Long time) throws Exception {

		if (enregistrement.booleanValue()) {

			for (String o : fin) {

				if (o.equals(event.getClass().getName())) {
					this.enregistrement = false;
				}

			}

		} else {

			for (String o : debut) {

				if (o.equals(event.getClass().getName())) {
					this.enregistrement = true;
				}

			}

		}
		return enregistrement;
	}

	/**
	 * @return List<String>
	 */
	public List<String> getDebut() {

		return this.debut;

	}

	/**
	 * @return List<String>
	 */
	public List<String> getFin() {

		return this.fin;

	}

	/**
	 * @param debut
	 */
	public void setDebut(final List<String> debut) {

		this.debut = debut;

	}

	/**
	 * @param fin
	 */
	public void setFin(final List<String> fin) {

		this.fin = fin;

	}

}
