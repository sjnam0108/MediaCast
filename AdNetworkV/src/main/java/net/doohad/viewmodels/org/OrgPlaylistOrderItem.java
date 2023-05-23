package net.doohad.viewmodels.org;

import net.doohad.models.org.OrgPlaylist;

public class OrgPlaylistOrderItem {

	private int id;
	private int seq;
	
	private String name;
	
	public OrgPlaylistOrderItem(OrgPlaylist playlist, int seq) {
		this.id = playlist.getId();
		this.name = playlist.getName();
		this.seq = seq;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
