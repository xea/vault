package so.blacklight.vault;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import so.blacklight.vault.entry.Entry;

public class Segment implements Serializable {

	private static final long serialVersionUID = 2812791L;

	private final List<Entry> entries;
	
	public Segment() {
		entries = new CopyOnWriteArrayList<>();
	}
	
	protected Segment(final Collection<Entry> newEntries) {
		this();
		entries.addAll(newEntries);
	}

	public List<Entry> getEntries() {
		return new CopyOnWriteArrayList<>(entries);
	}
	
	public Segment updateEntries(final Collection<Entry> newEntries) {
		return new Segment(newEntries);
	}

}
