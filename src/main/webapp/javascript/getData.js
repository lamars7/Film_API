var deleteVal = 0
function showOverlay() {
	document.getElementById("add-movie-overlay").classList.add("show");  // Show the overlay
}

function hideOverlay() {
	document.getElementById("add-movie-overlay").classList.remove("show");  // Hide the overlay
}


function showEditOverlay(filmID) {
	var input = document.getElementById("edit-id");
	input.value = filmID;
	document.getElementById("edit-overlay").classList.add("show");  // Show the overlay
}

function hideEditOverlay() {
	var input = document.getElementById("edit-id");
	input.value = null;
	document.getElementById("edit-overlay").classList.remove("show");  // Hide the overlay
}

function showDeleteOverlay(filmID) {
	var input = document.getElementById("delete-movie-id");
	input.value = filmID;
	deleteVal = filmID
	document.getElementById("delete-overlay").classList.add("show");  // Show the overlay
}

function hideDeleteOverlay() {
	var input = document.getElementById("delete-movie-id");
	//input.value = null;
	//deleteVal = null;
	document.getElementById("delete-overlay").classList.remove("show");  // Hide the overlay
}



$(document).ready(function() {
	$.ajax({
		type: "GET",
		url: "FilmControllerAPI",
		dataType: "json",
		success: function(data) {
			// data is the arraylist
			// you can loop through the arraylist and access the elements
			for (var i = 0; i < data.length; i++) {

				$('tbody').append('<tr id=' + data[i].id + '>');
				$('#' + data[i].id).append('<td>' + data[i].id + '</td>');
				$('#' + data[i].id).append('<td>' + data[i].title + '</td>');
				$('#' + data[i].id).append('<td>' + data[i].review + '</td>');
				$('#' + data[i].id).append('<td>' + data[i].year + '</td>');
				$('#' + data[i].id).append('<td>' + data[i].stars + '</td>');
				$('#' + data[i].id).append('<td>' + data[i].director + '</td>');
				$('#' + data[i].id).append("<td><button type='button' name='delete-movie' onclick='showEditOverlay(" + data[i].id + ")'><img src='images/edit.png' alt='Button 1'></button><button type='submit' onclick='showDeleteOverlay(" + data[i].id + ")'><img src='images/delete.png' alt='Button 2'></button></td>");
				$('#' + data[i].id).append('</tr>');
			}
		}
	});
});

function paginateTable() {
	$("#pagination").empty();
	setTimeout(function() { //pagination loads after 5 seconds

		var itemsPerPage = 100;

		// get total number of rows
		var rows = $("#table tbody tr").length;

		// calculate number of pages needed
		var numPages = Math.ceil(rows / itemsPerPage);

		// create pagination buttons
		for (var i = 1; i <= numPages; i++) {
			$("#pagination").append("<button class='page-button'>" + i + "</button>");
		}

		// hide all rows initially
		$("#table tbody tr").hide();

		// show only the appropriate rows when a button is clicked
		$(".page-button").click(function() {
			var pageNum = $(this).text();
			var startRow = (pageNum - 1) * itemsPerPage;
			var endRow = startRow + itemsPerPage;
			$("#table tbody tr").hide();
			$("#table tbody tr").slice(startRow, endRow).show();
		});

		// show only the first page of rows initially
		$("#table tbody tr").slice(0, itemsPerPage).show();
	}, 5000);
}

$("#add-movie-form").submit(function(event) {
	event.preventDefault(); // prevent the form from submitting the default way

	$.ajax({
		type: "POST", // form method
		url: "FilmControllerAPI", // where it posts to 
		data: $("#add-movie-form").serialize(), // get all the form data and send it 
		success: function() {
			hideOverlay(); // hides the UI overlay once done creating a movie
			$("#table tbody").empty(); // empties the table to refresh it
		}
	});


	setTimeout(function() {
		$("#table tbody").empty();
		$("#pagination").empty();
		var formatSelected = $("#format-select").val();

		if (formatSelected == "XML") {

			$.ajax({
				type: "GET",
				url: "FilmControllerAPI",
				headers: { Accept: "application/xml" },
				success: function(xml) {
					var table = $("#table");
					var tbody = $("<tbody>");

					$(xml).find("filmlist > films").each(function() {
						var row = $("<tr>");
						var id = $(this).find("id").text();
						var title = $(this).find("title").text();
						var review = $(this).find("review").text();
						var year = $(this).find("year").text();
						var stars = $(this).find("stars").text();
						var director = $(this).find("director").text();
						row.append($("<td>").text(id));
						row.append($("<td>").text(title));
						row.append($("<td>").text(review));
						row.append($("<td>").text(year));
						row.append($("<td>").text(stars));
						row.append($("<td>").text(director));
						row.append($("<td><button type='button' name='delete-movie' onclick='showEditOverlay(" + id + ")'><img src='images/edit.png' alt='Button 1'></button><button type='submit' onclick='showDeleteOverlay(" + id + ")'><img src='images/delete.png' alt='Button 2'></button></td>"));
						tbody.append(row);
					});
					table.append(tbody);
				}

			});



		} else if (formatSelected == "JSON") {
			$.ajax({
				type: "GET",
				url: "FilmControllerAPI",
				contentType: "application/json",
				headers: { 'Accept': 'application/json' },
				success: function(data) {
					for (var i = 0; i < data.length; i++) {
						$('tbody').append('<tr id=' + data[i].id + '>');
						$('#' + data[i].id).append('<td>' + data[i].id + '</td>');
						$('#' + data[i].id).append('<td>' + data[i].title + '</td>');
						$('#' + data[i].id).append('<td>' + data[i].review + '</td>');
						$('#' + data[i].id).append('<td>' + data[i].year + '</td>');
						$('#' + data[i].id).append('<td>' + data[i].stars + '</td>');
						$('#' + data[i].id).append('<td>' + data[i].director + '</td>');
						$('#' + data[i].id).append("<td><button type='button' name='delete-movie' onclick='showEditOverlay(" + data[i].id + ")'><img src='images/edit.png' alt='Button 1'></button><button type='submit' onclick='showDeleteOverlay(" + data[i].id + ")'><img src='images/delete.png' alt='Button 2'></button></td>");
						$('#' + data[i].id).append('</tr>');
					}
				}
			});
		} else if (formatSelected == "Text") {

			$.ajax({
				url: "FilmControllerAPI",
				type: "GET",
				dataType: "json",
				success: function(films) {

					for (var i = 0; i < films.length; i++) {
						var film = films[i];
						console.log(film.id)
						$('tbody').append('<tr id=' + film.id + '>');
						$('#' + film.id).append('<td>' + film.id + '</td>');
						$('#' + film.id).append('<td>' + film.title + '</td>');
						$('#' + film.id).append('<td>' + film.review + '</td>');
						$('#' + film.id).append('<td>' + film.year + '</td>');
						$('#' + film.id).append('<td>' + film.stars + '</td>');
						$('#' + film.id).append('<td>' + film.director + '</td>');
						$('#' + film.id).append("<td><button type='button' name='delete-movie' onclick='showEditOverlay(" + film.id + ")'><img src='images/edit.png' alt='Button 1'></button><button type='submit' onclick='showDeleteOverlay(" + film.id + ")'><img src='images/delete.png' alt='Button 2'></button></td>");
						$('#' + film.id).append('</tr>');
					}
				}
			});

		}

		
	}, 1000) // delay so it has time to post movie information
	paginateTable();

});

$("#edit-movie-form").submit(function(event) {
	event.preventDefault(); // prevent the form from submitting the default way
	var editId = $("#edit-id").val()

	var xmlData = "<films><id>" + $("#edit-id").val() + "</id><title>" + $("#edit-movie-form #name").val() + "</title><year>" + $("#edit-movie-form #year").val() + "</year><director>" + $("#edit-movie-form #director").val() + "</director><stars>" + $("#edit-movie-form #stars").val() + "</stars><review>" + $("#edit-movie-form #review").val() + "</review></films>";

	if (xmlData != null) {
		$.ajax({
			type: "PUT",
			url: "FilmControllerAPI",
			data: xmlData,
			headers: { customHeader: "APIPut" },
			contentType: "application/xml; charset=utf-8",
			success: function() {
				console.log("Successfully editted movie");
				hideEditOverlay();
			},
			error: function(error) {
				console.log("An error has occurred: " + error);
			}
		});
	}



});

$("#delete-movie-form").submit(function(e) { //  delete request (has to manually refresh when done)
	e.preventDefault();
	var id = $("delete-movie-form #delete-movie-id").val();
	$("#" + id).remove()
	console.log($("#" + id))
	console.log("Before: " + $("#delete-movie-form #delete-movie-id").val())
	$.ajax({
		url: "FilmControllerAPI",
		type: "DELETE",
		headers: {
			"AJAXDelete": $("#delete-movie-form #delete-movie-id").val() // delete through header as data errors occur when data sent through 'data' on DELETE method
		},
		success: function(response) {
			hideDeleteOverlay() // hides overlay once it has been deleted
		},
		error: function(error) {
			console.log(error); //handle errors
		}
	});

});




$("#search-bar-form").submit(function(event) {
	event.preventDefault(); // prevent the form from submitting the default way

	$.ajax({
		type: "GET",
		url: "FilmControllerAPI",
		data: { searchInput: $("#search-bar").val(), searchOption: $("#select-movie-type").val() },
		success: function(data) {
			$("#table tbody").empty();
			for (var i = 0; i < data.length; i++) {

				$('tbody').append('<tr id=' + data[i].id + '>');
				$('#' + data[i].id).append('<td>' + data[i].id + '</td>');
				$('#' + data[i].id).append('<td>' + data[i].title + '</td>');
				$('#' + data[i].id).append('<td>' + data[i].review + '</td>');
				$('#' + data[i].id).append('<td>' + data[i].year + '</td>');
				$('#' + data[i].id).append('<td>' + data[i].stars + '</td>');
				$('#' + data[i].id).append('<td>' + data[i].director + '</td>');
				$('#' + data[i].id).append("<td><button type='button' name='delete-movie' onclick='showEditOverlay(" + data[i].id + ")'><img src='images/edit.png' alt='Button 1'></button><button type='submit' onclick='showDeleteOverlay(" + data[i].id + ")'><img src='images/delete.png' alt='Button 2'></button></td>");
				$('#' + data[i].id).append('</tr>');
			}
			paginateTable();
		}
	});

});



$("#format-select").change(function() {
	// Get the selected option's value
	var formatSelected = $(this).val();
	$("#table tbody").empty();
	$("#pagination").empty();

	if (formatSelected == "XML") {
		$.ajax({
			type: "GET",
			url: "FilmControllerAPI",

			headers: { Accept: "application/xml" },
			success: function(xml) {
				var table = $("#table");
				var tbody = $("<tbody>");

				$(xml).find("filmlist > films").each(function() {
					var row = $("<tr>");
					var id = $(this).find("id").text();
					var title = $(this).find("title").text();
					var review = $(this).find("review").text();
					var year = $(this).find("year").text();
					var stars = $(this).find("stars").text();
					var director = $(this).find("director").text();
					row.append($("<td>").text(id));
					row.append($("<td>").text(title));
					row.append($("<td>").text(review));
					row.append($("<td>").text(year));
					row.append($("<td>").text(stars));
					row.append($("<td>").text(director));
					row.append($("<td><button type='button' name='delete-movie' onclick='showEditOverlay(" + id + ")'><img src='images/edit.png' alt='Button 1'></button><button type='submit' onclick='showDeleteOverlay(" + id + ")'><img src='images/delete.png' alt='Button 2'></button></td>"));
					tbody.append(row);
				});
				table.append(tbody);
			}

		});



	} else if (formatSelected == "JSON") {
		$.ajax({
			type: "GET",
			url: "FilmControllerAPI",
			contentType: "application/json",
			headers: { 'Accept': 'application/json' },
			success: function(data) {

				for (var i = 0; i < data.length; i++) {

					$('tbody').append('<tr id=' + data[i].id + '>');
					$('#' + data[i].id).append('<td>' + data[i].id + '</td>');
					$('#' + data[i].id).append('<td>' + data[i].title + '</td>');
					$('#' + data[i].id).append('<td>' + data[i].review + '</td>');
					$('#' + data[i].id).append('<td>' + data[i].year + '</td>');
					$('#' + data[i].id).append('<td>' + data[i].stars + '</td>');
					$('#' + data[i].id).append('<td>' + data[i].director + '</td>');
					$('#' + data[i].id).append("<td><button type='button' name='delete-movie' onclick='showEditOverlay(" + data[i].id + ")'><img src='images/edit.png' alt='Button 1'></button><button type='submit' onclick='showDeleteOverlay(" + data[i].id + ")'><img src='images/delete.png' alt='Button 2'></button></td>");
					$('#' + data[i].id).append('</tr>');
				}
			}
		});
	} else if (formatSelected == "Text") {

		$.ajax({
			url: "FilmControllerAPI",
			headers: { Accept: "text/plain" },
			type: "GET",
			success: function(response) {

				var lines = response.split("\n"); // splits the response into strings

				var filmObjects = []; // this array will store film objects

				for (var i = 0; i < lines.length; i++) { // iterates through lines

					var currentFilm = {}; // will store film as an object into filmObjects
					var line = lines[i];
					var filmInformationArray = line.split("| "); // split the string with the pipe character and space

					for (var j = 0; j < filmInformationArray.length; j++) { // for loop to iterate through the objects properties
						var filmInfo = filmInformationArray[j].split("="); //  filminfo is split by '=' e.g. title = xml will return "xml"
						var currentFilmObjectName = filmInfo[0];
						var currentFilmObjectText = filmInfo[1];
						currentFilm[currentFilmObjectName] = currentFilmObjectText;
					}
					// Add the newly created film object to the filmObjects array
					filmObjects.push(currentFilm); // add the current film object to the array
				}
				for (var i = 0; i < filmObjects.length; i++) { // append data to tables from the ArrayList
					$('tbody').append('<tr id=' + filmObjects[i].id + '>');
					$('#' + filmObjects[i].id).append('<td>' + filmObjects[i].id + '</td>');
					$('#' + filmObjects[i].id).append('<td>' + filmObjects[i].title + '</td>');
					$('#' + filmObjects[i].id).append('<td>' + filmObjects[i].review + '</td>');
					$('#' + filmObjects[i].id).append('<td>' + filmObjects[i].year + '</td>');
					$('#' + filmObjects[i].id).append('<td>' + filmObjects[i].stars + '</td>');
					$('#' + filmObjects[i].id).append('<td>' + filmObjects[i].director + '</td>');
					$('#' + filmObjects[i].id).append("<td><button type='button' name='delete-movie' onclick='showEditOverlay(" + filmObjects[i].id + ")'><img src='images/edit.png' alt='Button 1'></button><button type='submit' onclick='showDeleteOverlay(" + filmObjects[i].id + ")'><img src='images/delete.png' alt='Button 2'></button></td>");
					$('#' + filmObjects[i].id).append('</tr>');
				}



			}
		});



	}

	paginateTable();
});

setTimeout(function() { //pagination loads after 5 seconds
	var itemsPerPage = 100;

	// get total number of rows
	var rows = $("#table tbody tr").length;

	// calculate number of pages needed
	var numPages = Math.ceil(rows / itemsPerPage);

	// create pagination buttons
	for (var i = 1; i <= numPages; i++) {
		$("#pagination").append("<button class='page-button'>" + i + "</button>");
	}

	// hide all rows initially
	$("#table tbody tr").hide();

	// show only the appropriate rows when a button is clicked
	$(".page-button").click(function() {
		var pageNum = $(this).text();
		var startRow = (pageNum - 1) * itemsPerPage;
		var endRow = startRow + itemsPerPage;
		$("#table tbody tr").hide();
		$("#table tbody tr").slice(startRow, endRow).show();
	});

	// show only the first page of rows initially
	$("#table tbody tr").slice(0, itemsPerPage).show();
}, 2500);






