package so.blacklight.vault;

import java.io.Serializable;

public class Vault implements Serializable {
	
	private static final long serialVersionUID = 3159058613L;

	final Segment primarySegment;
	
	public Vault() {
		primarySegment = new Segment();
	}
	
	
}
