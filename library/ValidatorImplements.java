package library;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author sephiroth
 */
public class ValidatorImplements implements Validator {

	private List<Type>			implement;
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public Boolean validate(Object event, Long time) throws Exception {

		Type[] eventImplements = event.getClass().getGenericInterfaces();

		for (Type i : implement) {

			for (Type j : eventImplements) {

				if (i.getClass().getName().equals(j.getClass().getName())) {

					return true;

				}

			}

		}

		return false;
	}

	/**
	 * @return List<String>
	 */
	public List<Type> getImplements() {

		return this.implement;

	}

	/**
	 * @param implement
	 */
	public void setImplements(final List<Type> implement) {

		this.implement = implement;

	}

}
