package com.firkinofbrain.blackout.database;

import java.util.List;

import com.firkinofbrain.blackout.database.event.Events;
import com.firkinofbrain.blackout.database.geo.Geo;
import com.firkinofbrain.blackout.database.party.Party;
import com.firkinofbrain.blackout.database.tag.Tag;
import com.firkinofbrain.blackout.database.user.User;
import com.firkinofbrain.blackout.database.usury.Usury;

public interface DataManagerInterface {
	public Events getEvent(long eId);
	public Events getLastEvent();
	public List<Events> getAllEvents();
	public List<Events> getAllByUserAndParty(String partyID, String userID);
	public long saveEvent(Events event);
	public boolean deleteAllEvents();
	public boolean deleteEvent(long eId);
	public boolean deleteLastEvent();
	
	
	public Geo getGeo(long gId);
	public Geo getLastGeo();
	public List<Geo> getAllGeo();
	public List<Geo> getAllGeoByHash(String hash);
	public long saveGeo(Geo geo);
	public boolean deleteAllGeo();
	public boolean deleteGeo(long gId);
	public boolean deleteLastGeo();
	
	public Party getParty(long gId);
	public List<Party> getAllParty();
	public List<Party> getAllPartyBySync(int sync);
	public long saveParty(Party party);
	public void updateParty(Party party);
	public boolean deleteParty(long pId);
	public boolean deleteAllParties();
	public boolean deleteLastParty();
	
	public Usury getUsury(long uId);
	public List<Usury> getAllUsury();
	public long saveUsury(Usury usury);
	public boolean deleteUsury(long uId);
	public boolean deleteAllUsury();
	public boolean deleteLastUsury();
	
	public Tag getTag(long tId);
	public List<Tag> getAllTagByPhoto(String hash);
	public boolean saveTagList(List<Tag> tags);
	public boolean deleteTag(long tagId);
	public boolean deleteAllTagByPhoto(String hash);
	public boolean deleteAllTag();
	
	public boolean isUser();
	public User getUser();
	public boolean updateUser(User user);
	public boolean saveUser(User user);
	public boolean deleteUser(String id);
	Events getLastEvent(String partyID);
}
