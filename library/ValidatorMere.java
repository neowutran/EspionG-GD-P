package library;

import java.util.List;

/**
 * @author sephiroth
 */
public class ValidatorMere implements Validator {

	private List<Class>			implement;
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public Boolean validate(Object event, Long time) throws Exception {

		Class eventMere = (Class) event.getClass().getGenericSuperclass();

		for (Class i : implement) {

			if (i.getName().equals(eventMere.getName())) {

				return true;

			}

		}

		return false;
	}

	/**
	 * @return List<String>
	 */
	public List<Class> getMeres() {

		return this.implement;

	}

	/**
	 * @param implement
	 */
	public void setMeres(final List<Class> implement) {

		this.implement = implement;

	}

}
