package com.truecaller.hw.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

import java.io.IOException;
import java.util.LinkedList;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.truecaller.hw.core.Person;
import com.truecaller.hw.core.Viewer;
import com.truecaller.hw.db.PersonDAO;

@Path("user")
public class PersonResource {

	private final PersonDAO personDAO;

	private final static int maxViewers=10;             // max viewers to return for a person
	private final static long maxTTLms=10*24*3600*1000;  // max TimeToLive for a view in ms
	private final static ObjectMapper mapper = new ObjectMapper();  

	public PersonResource(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	@GET
	@UnitOfWork
	@Path("hello")
	public String sayHello() {
		return "Howdy Bro :)";
	}

	@POST
	@UnitOfWork
	@Path("create")
	@Produces(MediaType.APPLICATION_JSON)
	public Person createPerson() {
		return personDAO.create(new Person());
	}


	@POST
	@UnitOfWork
	@Path("add_view/{personId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public synchronized Person addProfileView(@PathParam("personId") LongParam personId, Viewer viewer) throws JsonParseException, JsonMappingException, IOException 
	{
		// load person object
		Person person = findSafely(personId.get());
		LinkedList<Viewer> viewers;
		
		// Pending: make sure the viewer Id exists in db

		if(viewer.getId()==personId.get()) throw new BadRequestException("Viewer & viewee id's are same");
				
		if(person.getViewers()!=null)
		{
			// de-serialise viewers object to list from json
			viewers = mapper.readValue(person.getViewers(), new TypeReference<LinkedList<Viewer>>(){});

			// filter invalid/obsolete views 
			LinkedList<Viewer> tmp = new LinkedList<Viewer>();
			for(Viewer vr : viewers)
			{
				// if viewer is valid and not same as current one 
				if(vr.getId()!=viewer.getId() && isValid(vr)) 
					tmp.add(vr);
			}
			
			// if max viewers are present; remove last one
			if(tmp.size()==maxViewers) tmp.removeLast();
			
			viewers=tmp;
		}
		else
		{
			viewers=new LinkedList<Viewer>();
		}

		// add new viewer
		viewers.addFirst(viewer);
		
		person.setViewers(mapper.writeValueAsString(viewers));

		personDAO.update(person);
		
		return person;
	}


	@GET
	@UnitOfWork
	@Path("viewers/{personId}")
	@Produces(MediaType.APPLICATION_JSON)
	public LinkedList<Viewer> profileViews(@PathParam("personId") LongParam personId) throws JsonParseException, JsonMappingException, IOException {

		Person person = findSafely(personId.get());

		LinkedList<Viewer> viewers;
		
		if(person.getViewers()!=null)
		{
			viewers = mapper.readValue(person.getViewers(), new TypeReference<LinkedList<Viewer>>(){});
			
			// send only valid views; which happened in last ten days
			// do not update person object for invalid views : update only in addProfile function
			LinkedList<Viewer> tmp = new LinkedList<Viewer>();
			for(Viewer viewer : viewers)
			{
				if(isValid(viewer)) tmp.add(viewer);
			}
			
			viewers=tmp;
		}	
		else
			viewers=new LinkedList<Viewer>();

		return viewers;
	}


	private Person findSafely(long personId) {
		final Optional<Person> person = personDAO.findById(personId);
		if (!person.isPresent()) {
			throw new NotFoundException("No such user.");
		}
		return person.get();
	}


	// if viewer is valid
	private boolean isValid(Viewer viewer)
	{
		// view is more than maxTTLms old
		if(System.currentTimeMillis()-viewer.getTimestamp()>maxTTLms) return false;
		
		return true;
	}
}
