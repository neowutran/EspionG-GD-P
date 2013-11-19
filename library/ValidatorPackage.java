package library;

import java.util.List;

/**
 * @author sephiroth
 *         Validateur selon le package d'appartenance de l'objet capturé
 *         En cas de sous package, le resultat est vrai par defaut
 */
public class ValidatorPackage implements Validator {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private List<String>		packages;
	private Boolean				exactitude			= false;

	public Boolean validate(Object event, Long time) throws Exception {

		for (String s : packages) {

			if (exactitude) {

				if (event.getClass().getPackage().getName().equals(s)) {

					return true;

				}

			} else {

				if (event.getClass().getPackage().getName().startsWith(s)) {

					return true;

				}
			}

		}
		return false;
	}

	/**
	 * @param packages
	 */
	public void setPackages(final List<String> packages) {

		this.packages = packages;

	}

	/**
	 * @return List<String>
	 */
	public List<String> getPackages() {

		return this.packages;
	}

	/**
	 * @return Boolean
	 */
	public Boolean getExactitude() {

		return this.exactitude;

	}

	/**
	 * @param exactitude
	 */
	public void setExactitude(final Boolean exactitude) {

		this.exactitude = exactitude;

	}

}
