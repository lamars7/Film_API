package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;

import database.FilmDAO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import models.Film;
import models.FilmList;

@WebServlet("/FilmControllerAPI")
public class FilmControllerAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// response.setContentType("text/html");

		String format = request.getHeader("Accept");
		String searchInput = request.getParameter("searchInput");

		System.out.println(format);

		if (("application/xml").equals(format)) {

			FilmDAO dao = new FilmDAO();

			try {
				response.setContentType("application/xml"); // sets the response content type to xml
				ArrayList<Film> allFilms = dao.getAllFilms(); // get all the films
				PrintWriter out = response.getWriter(); // will use this to print out a response when using the RESTful
														// service
				JAXBContext jaxbContext = JAXBContext.newInstance(FilmList.class); // create a JAXBcontext object
				Marshaller marshaller = jaxbContext.createMarshaller(); // create a marshalling object
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// formats the XML output
				FilmList filmList = new FilmList(); // create a new FilmList object and set films to allFilms
				filmList.setFilms(allFilms);
				QName qName = new QName("filmlist"); // create a QName object with "filmlist"
				JAXBElement<FilmList> root = new JAXBElement<>(qName, FilmList.class, filmList); // creating a
																									// JAXBElement
																									// object with the
																									// FilmList object
																									// as the root
																									// element and the
																									// QName
				marshaller.marshal(root, out); // marshalling the root element to the output
			} catch (JAXBException e) {
				e.printStackTrace();
			}

			// Get the ArrayList of objects

		} else if (("application/json").equals(format)) {
			FilmDAO dao = new FilmDAO(); // creates a new FilmDAO object
			response.setContentType("application/json"); // sets the response content type to JSON
			PrintWriter out = response.getWriter(); // used to print a response once using GET to the RESTful service
			ArrayList<Film> allFilmsJSON = dao.getAllFilms(); // create an ArrayList to get all the films
			Gson gson = new Gson(); // create a Gson object
			String json = gson.toJson(allFilmsJSON); // turn the list into a JSON string
			out.write(json); // prints out he string
			System.out.println("JSON");
			out.close();
		} else if (("text/plain").equals(format)) {
			FilmDAO dao = new FilmDAO();
			ArrayList<Film> allFilms = dao.getAllFilms();
			response.setContentType("text/plain"); // sets the content type to text/plain
			PrintWriter out = response.getWriter();
			for (Film film : allFilms) {
				out.println(film); // prints all the films to RESTful service
			}
			out.flush();
			out.close();
		} else if (searchInput != null) { // handles the search options (title,stars,year only)
			FilmDAO dao = new FilmDAO();
			String searchOption = request.getParameter("searchOption");

			if (searchOption.equals("title")) { // action when searched by title

				response.setHeader("Content-Type", "application/json");
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				ArrayList<Film> allFilmsJSON = dao.getFilmByTitle(searchInput);
				Gson gson = new Gson();
				String json = gson.toJson(allFilmsJSON);
				out.write(json);
				out.close();

			} else if (searchOption.equals("stars")) { // action when searched by stars
				response.setHeader("Content-Type", "application/json");
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				ArrayList<Film> allFilmsJSON = dao.getFilmByStar(searchInput);
				Gson gson = new Gson();
				String json = gson.toJson(allFilmsJSON);
				out.write(json);
				out.close();
			} else if (searchOption.equals("year")) { // action when searched by year
				System.out.println(searchOption + "in");
				int year = Integer.valueOf(searchInput);
				response.setHeader("Content-Type", "application/json");
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				ArrayList<Film> allFilmsJSON = dao.getFilmByYear(year);
				Gson gson = new Gson();
				String json = gson.toJson(allFilmsJSON);
				System.out.println(allFilmsJSON);
				out.write(json);
				out.close();
			}

		}

		else {
			FilmDAO dao = new FilmDAO();
			PrintWriter out = response.getWriter();
			ArrayList<Film> allFilms = dao.getAllFilms();
			Gson gson = new Gson();
			String filmJson = gson.toJson(allFilms);
			out.write(filmJson);

		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String format = request.getHeader("Content-Type");

		FilmDAO dao = new FilmDAO();

		if (format.equals("application/xml")) {
			PrintWriter out = response.getWriter();

			try {
				InputStream inputStream = request.getInputStream();
				JAXBContext jaxbContext = JAXBContext.newInstance(Film.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Film data = (Film) jaxbUnmarshaller.unmarshal(inputStream);
				Film film = new Film(data.getTitle(), data.getYear(), data.getDirector(), data.getStars(),
						data.getReview());
				dao.addFilm(film);
				out.write("Film added");
			} catch (JAXBException | SQLException e) {
				e.printStackTrace();
			}
		} else if (format.equals("application/json")) {
			Gson gson = new Gson();
			Film data = gson.fromJson(request.getReader(), Film.class);
			PrintWriter out = response.getWriter();
			String title = data.getTitle();
			int year = data.getYear();
			String director = data.getDirector();
			String stars = data.getStars();
			String review = data.getReview();
			Film film = new Film(title, year, director, stars, review);

			try {
				dao.addFilm(film);
				out.write("Film added");

			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else if (format.equals("text/plain")) {

			BufferedReader reader = request.getReader();
			PrintWriter out = response.getWriter();
			String line;

			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			String textData = sb.toString();
			int startStringTitle = textData.indexOf("title=") + 6; // starts to assign characters after "title=" to the	variable (title= is 6 characters therefore +6)
			int endStringTitle = textData.indexOf("|", startStringTitle); // text/plain has to end with comma to be able to be read
			String title = textData.substring(startStringTitle, endStringTitle); // find the string between title= and the line as a seperator
																					
			// repeat code for all of them, change digits to however many characters there
			// are for each
			int startIntYear = textData.indexOf("year=") + 5;
			int endIntYear = textData.indexOf("|", startIntYear);
			int year = Integer.parseInt(textData.substring(startIntYear, endIntYear));

			int startStringDirector = textData.indexOf("director=") + 9;
			int endStringDirector = textData.indexOf("|", startStringDirector);
			String director = textData.substring(startStringDirector, endStringDirector);

			int startStringStars = textData.indexOf("stars=") + 6;
			int endStringStars = textData.indexOf("|", startStringStars);
			String stars = textData.substring(startStringStars, endStringStars);

			int startStringReview = textData.indexOf("review=") + 7;
			String review = textData.substring(startStringReview);

			Film film = new Film(title, year, director, stars, review);

			System.out.println("title: " + title);
			System.out.println("year: " + year);
			System.out.println("director: " + director);
			System.out.println("stars: " + stars);
			System.out.println("review: " + review);

			try {
				dao.addFilm(film);
				out.write("Film inserted successfully");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		else {
			String title = request.getParameter("title");
			int year = Integer.valueOf(request.getParameter("year"));
			String director = request.getParameter("director");
			String stars = request.getParameter("stars");
			String review = request.getParameter("review");
			String formData = request.getParameter("formData");

			System.out.println(formData + " FORM DATA");
			System.out.println("Title: " + title);
			System.out.println("year: " + year);
			System.out.println("director: " + director);
			System.out.println("stars: " + stars);
			System.out.println("review: " + review);
			Film film = new Film(title, year, director, stars, review);
			try {
				dao.addFilm(film);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get the input stream from the request

		FilmDAO dao = new FilmDAO();
		String format = request.getHeader("Content-Type");
		
		System.out.println(format);

		if (request.getHeader("customHeader") != null){ // custom header as data wont pass through the other ways
			try {

				JAXBContext jaxbContext = JAXBContext.newInstance(Film.class); // create a JAXB object
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller(); // creates an unmarshalling object
				Film data = (Film) jaxbUnmarshaller.unmarshal(request.getInputStream()); // parsing XML input stream into a film object
				dao.editFilm(data.getId(), data.getTitle(), data.getYear(), data.getDirector(), data.getStars(),data.getReview()); // edit the information through the method implemented

			} catch (JAXBException | SQLException e) {
				e.printStackTrace();
			}
		}
		if (format.equals("application/xml")) {
			try {
				PrintWriter out = response.getWriter();
				InputStream inputStream = request.getInputStream();
				JAXBContext jaxbContext = JAXBContext.newInstance(Film.class); // create a JAXB object - same as "APIPut" (line 287 onwards to 290)																			
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Film data = (Film) jaxbUnmarshaller.unmarshal(inputStream);
				dao.editFilm(data.getId(), data.getTitle(), data.getYear(), data.getDirector(), data.getStars(),
						data.getReview());
				out.write("Film edited successfully");
			} catch (JAXBException | SQLException e) {
				e.printStackTrace();
			}
		} else if (format.equals("application/json")) {
			Gson gson = new Gson(); // creates a Gson object
			Film data = gson.fromJson(request.getReader(), Film.class);
			PrintWriter out = response.getWriter();
			int id = data.getId();
			String title = data.getTitle();
			int year = data.getYear();
			String director = data.getDirector();
			String stars = data.getStars();
			String review = data.getReview();
			try {
				dao.editFilm(id, title, year, director, stars, review);
				out.write(id + " has been edited");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (format.equals("text/plain")) {

			BufferedReader reader = request.getReader();
			PrintWriter out = response.getWriter();
			String line;

			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String textData = sb.toString();
			
			int beforeId = textData.indexOf("id=") + 3; // starts getting the txt after 'id=' (3 chars so +3)
			int afterId = textData.indexOf("|", beforeId); // gets the text between 'id=' and the line pipe as a seperator
			int id = Integer.parseInt(textData.substring(beforeId, afterId)); // parses it to an int as data is numerical

			
			int startStringTitle = textData.indexOf("title=") + 6; // starts to assign characters after "title=" to the	variable (title= is 6 characters therefore +6)
			int endStringTitle = textData.indexOf("|", startStringTitle); // text/plain has to end with comma to be able to be read
			String title = textData.substring(startStringTitle, endStringTitle); // find the string between title= and the line as a seperator
																					
			// repeat code for all of them, change digits to however many characters there
			// are for each
			int startIntYear = textData.indexOf("year=") + 5;
			int endIntYear = textData.indexOf("|", startIntYear);
			int year = Integer.parseInt(textData.substring(startIntYear, endIntYear));

			int startStringDirector = textData.indexOf("director=") + 9;
			int endStringDirector = textData.indexOf("|", startStringDirector);
			String director = textData.substring(startStringDirector, endStringDirector);

			int startStringStars = textData.indexOf("stars=") + 6;
			int endStringStars = textData.indexOf("|", startStringStars);
			String stars = textData.substring(startStringStars, endStringStars);

			int startStringReview = textData.indexOf("review=") + 7;
			String review = textData.substring(startStringReview);
			
			try {
				dao.editFilm(id, title, year, director, stars, review);
				out.write(id + " has been edited");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		FilmDAO dao = new FilmDAO();
		String format = request.getHeader("Content-Type");
		PrintWriter out = response.getWriter();


		if (format != null) {
			if (format.equals("application/xml")) {
				try {
					InputStream inputStream = request.getInputStream();
					JAXBContext jaxbContext = JAXBContext.newInstance(Film.class);
					Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					Film data = (Film) jaxbUnmarshaller.unmarshal(inputStream);
					out.write(data.getId() + " Deleted");
					dao.deleteFilm(data.getId());

				} catch (JAXBException | SQLException e) {
					e.printStackTrace();
				}
			} else if (format.equals("application/json")) {
				Gson gson = new Gson();
				Film data = gson.fromJson(request.getReader(), Film.class);

				try {
					dao.deleteFilm(data.getId());
					out.write("Film " + data.getId() + " deleted");
				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else if (format.equals("text/plain")) {
				BufferedReader reader = request.getReader();
				String line;

				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				String textData = sb.toString();

				int beforeId = textData.indexOf("id=") + 3; // starts getting the txt after 'id=' (3 chars so +3)
				int afterId = textData.indexOf("|", beforeId); // gets the text between 'id=' and the line pipe as a seperator
				int id = Integer.parseInt(textData.substring(beforeId, afterId)); // parses it to an int as data is numerical
																					
				String stringId = textData.substring(beforeId, afterId); // stringify id so its readable for postman

				try {
					dao.deleteFilm(id);
					System.out.println(id + " has been deleted");
					out.write(stringId + " has been deleted");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(request.getHeader("AJAXDelete") != null ) {
			try {
				int id = Integer.valueOf(request.getHeader("AJAXDelete"));
				dao.deleteFilm(id);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		 

	}

}
