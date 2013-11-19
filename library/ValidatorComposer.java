package library;

import java.io.Serializable;

/**
 * @author sephiroth
 */
public interface ValidatorComposer extends Serializable {

	/**
	 * @param event
	 * @param time
	 * @param idEspion
	 * @return Boolean
	 * @throws Exception
	 */
	public Boolean validate(final Object event, final Long time,
					final String idEspion) throws Exception;
}
