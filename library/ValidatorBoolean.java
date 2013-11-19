package library;

/**
 * @author sephiroth
 */
public class ValidatorBoolean implements Validator {

	private Boolean				valeur				= false;

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public Boolean validate(Object event, Long time) throws Exception {

		return valeur.booleanValue();
	}

	/**
	 * @return Boolean
	 */
	public Boolean getValeur() {

		return this.valeur;

	}

	/**
	 * @param valeur
	 */
	public void setValeur(final Boolean valeur) {

		this.valeur = valeur;

	}

}
