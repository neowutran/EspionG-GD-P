package library;

import java.util.Calendar;

/**
 * @author sephiroth
 */
public class ValidatorTempsCycle implements Validator {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Calendar			debut;
	private Long				delta;

	/**
	 * @return Long
	 */
	public Long getDelta() {

		return this.delta;
	}

	/**
	 * @param delta
	 */
	public void setDelta(final Long delta) {

		this.delta = delta;
	}

	/**
	 * @return Calendar
	 */
	public Calendar getDebut() {

		return this.debut;
	}

	/**
	 * @param debut
	 */
	public void setDebut(final Calendar debut) {

		this.debut = debut;
	}

	/**
	 * @return Calendar
	 */
	public Calendar getFin() {

		return this.fin;
	}

	/**
	 * @param fin
	 */
	public void setFin(final Calendar fin) {

		this.fin = fin;
	}

	/**
	 * @return Long
	 */
	public Long getIntervalle() {

		return this.intervalle;
	}

	/**
	 * @param intervalle
	 */
	public void setIntervalle(final Long intervalle) {

		this.intervalle = intervalle;
	}

	private Calendar	fin;
	private Long		intervalle;

	public Boolean validate(final Object event, final Long time)
					throws Exception {

		Long d = debut.getTimeInMillis();
		Long f = fin.getTimeInMillis() - d;
		Long t = time - d;

		if (time > d && time < (f + d)) {

			Long i;
			for (i = this.intervalle - this.delta; i <= this.intervalle
							+ this.delta; i++) {

				if (t % i == 0) {

					return true;

				}

			}
			return true;

		}

		return false;
	}
}
