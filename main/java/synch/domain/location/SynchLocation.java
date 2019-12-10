package synch.domain.location;

import com.eriklievaart.toolkit.convert.api.ConversionException;
import com.eriklievaart.toolkit.convert.api.validate.RegexValidator;

public class SynchLocation {

	private String name;
	private String location;

	public SynchLocation(String name, String location) throws ConversionException {
		this.name = name;
		this.location = location;

		new RegexValidator("\\w++").check(name);
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return name + " => " + location + "";
	}
}