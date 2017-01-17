package com.codewarrior.URLShortener;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.codewarrior.URLShortener.domain.ShortenURL;
import com.codewarrior.URLShortener.exception.InvalidURLException;

@Path("/urlshortenerservice")
public interface IURLShortener {
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response shortenURL(ShortenURL request) throws InvalidURLException;
	
	@GET
	@Produces("application/json")
	public Response redirect(String shortURL) throws InvalidURLException;
}
