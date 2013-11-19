package library;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sephiroth
 */
public class ValidatorComposerEvenement implements ValidatorComposer {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Boolean				enregistrement		= false;
	private List<String>		debut				= new ArrayList<String>();
	private List<String>		fin					= new ArrayList<String>();
	private List<String>		debutHamecon		= new ArrayList<String>();
	private List<String>		finHamecon			= new ArrayList<String>();

	public Boolean validate(Object event, Long time, String idEspion)
					throws Exception {

		if (enregistrement.booleanValue()) {

			for (String o : fin) {

				if (o.getClass().getName().equals(event.getClass().getName())
								&& this.finHamecon.contains(idEspion)) {
					this.enregistrement = false;
				}

			}

		} else {

			for (String o : debut) {

				if (o.equals(event.getClass().getName())
								&& this.debutHamecon.contains(idEspion)) {
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

	/**
	 * @param debut
	 */
	public void setDebutHamecon(final List<String> debut) {

		this.debutHamecon = debut;

	}

	/**
	 * @param fin
	 */
	public void setFinHamecon(final List<String> fin) {

		this.finHamecon = fin;

	}

	/**
	 * @return the debutHamecon
	 */
	public List<String> getDebutHamecon() {

		return debutHamecon;
	}

	/**
	 * @return the finHamecon
	 */
	public List<String> getFinHamecon() {

		return finHamecon;
	}

}
