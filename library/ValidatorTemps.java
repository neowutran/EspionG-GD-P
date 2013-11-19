package library;

import java.util.Calendar;

/**
 * @author sephiroth
 */
public class ValidatorTemps implements Validator {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Calendar			tempsDebut;

	/**
	 * @return Calendar
	 */
	public Calendar getTempsDebut() {

		return this.tempsDebut;
	}

	/**
	 * @param tempsDebut
	 */
	public void setTempsDebut(final Calendar tempsDebut) {

		this.tempsDebut = tempsDebut;
	}

	/**
	 * @return Calendar
	 */
	public Calendar getTempsFin() {

		return this.tempsFin;
	}

	/**
	 * @param tempsFin
	 */
	public void setTempsFin(final Calendar tempsFin) {

		this.tempsFin = tempsFin;
	}

	private Calendar	tempsFin;

	public Boolean validate(final Object event, final Long time)
					throws Exception {

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		return c.after(tempsDebut) && c.before(tempsFin);
	}
}
