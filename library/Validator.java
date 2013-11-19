package library;

import java.io.Serializable;

/**
 * @author sephiroth
 */
public interface Validator extends Serializable {

	/**
	 * @param event
	 * @param time
	 * @return Boolean
	 * @throws Exception
	 */
	public Boolean validate(final Object event, final Long time)
					throws Exception;

}
