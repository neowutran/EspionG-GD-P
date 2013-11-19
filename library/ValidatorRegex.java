package library;

import java.lang.reflect.Method;

/**
 * @author sephiroth
 */
public class ValidatorRegex implements Validator {

	/**
	 * 
	 */
	public enum Mode {
		CLASS, PACKAGE, METHODE
	}

	private static final long	serialVersionUID	= 1L;
	private String				expression;
	private Mode				mode				= Mode.CLASS;

	public Boolean validate(Object event, Long time) throws Exception {

		// TODO Auto-generated method stub
		if (mode == Mode.CLASS) {

			if (event.getClass().getSimpleName().matches(expression)) {
				return true;
			}

		}

		else if (mode == Mode.METHODE) {

			Method[] m = event.getClass().getMethods();
			for (Method methode : m) {

				if (methode.getName().matches(expression)) {

					return true;

				}

			}

		}

		else if (mode == Mode.PACKAGE) {

			if (event.getClass().getPackage().getName().matches(expression)) {
				return true;
			}

		}
		return false;
	}

	/**
	 * @return the mode
	 */
	public Mode getMode() {

		return mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(Mode mode) {

		this.mode = mode;
	}

	/**
	 * @return the expression
	 */
	public String getExpression() {

		return expression;
	}

	/**
	 * @param expression
	 *            the expression to set
	 */
	public void setExpression(String expression) {

		this.expression = expression;
	}

}
