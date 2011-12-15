



function plotHistoryCalendar(history, visModel, augmentationCallback) {
	
	var historyDates = history.dates;
	var historyData = history.data;
	
	var m = [19, 20, 20, 19], // top right bottom left margin
	w = 960 - m[1] - m[3], // width
	h = 136 - m[0] - m[2], // height
	z = 17; // cell size

	var day = d3.time.format("%w"),
	week = d3.time.format("%U"),
	percent = d3.format(".1%"),
	format = d3.time.format(getHistoryDateFormatString());

	var color = d3.scale.quantize()
	.domain([-.05, .05])
	.range(d3.range(9));

	var svg = d3.select("#chart").selectAll("svg")
	.data(d3.range(history.minYear, history.maxYear+1))
	.enter().append("svg:svg")
	.attr("width", w + m[1] + m[3])
	.attr("height", h + m[0] + m[2])
	.attr("class", "RdYlGn")
	.append("svg:g")
	.attr("transform", "translate(" + (m[3] + (w - z * 53) / 2) + "," + (m[0] + (h - z * 7) / 2) + ")");

	svg.append("svg:text")
	.attr("transform", "translate(-6," + z * 3.5 + ")rotate(-90)")
	.attr("text-anchor", "middle")
	.text(String);

	var rect = svg.selectAll("rect.day")
	.data(function(d) { 
		return d3.time.days(new Date(d, 0, 1), new Date(d + 1, 0, 1)); 
	})
	.enter().append("svg:rect")
	.attr("class", "day")
	.attr("width", z)
	.attr("height", z)
	.attr("x", function(d) { return week(d) * z; })
	.attr("y", function(d) { return day(d) * z; });

	svg.selectAll("path.month")
	.data(function(d) { return d3.time.months(new Date(d, 0, 1), new Date(d + 1, 0, 1)); })
	.enter().append("svg:path")
	.attr("class", "month")
	.attr("d", monthPath);


	var getAuthor = function(d) {
		var fd = format(d);
		var array = historyData[fd];
		if(array)
			return array[0].author;
		return null;
	};

	plotHistory = function(historyDates, historyData) {

		var data = d3.nest()
		.key(function(d) { 
			return d; 
		})
		.rollup(function(d) { 
			return  d[0];  
			})//(d[0].Close - d[0].Open) / d[0].Open; })
			.map(historyDates);

		rect.attr("class", function(d) { 
				return "day q" + color(data[format(d)]) + "-9"; 
			})
			.style("fill", function(d) { 
				var auth = getAuthor(d); 
				if(auth)
					return "steelblue";
				return "white";
			})
			.append("svg:title")
			.text(function(d)  { 
				var auth = getAuthor(d);
				if(auth)
				return auth;
				return (d = format(d)) + (d in data ? ": " + percent(data[d]) : ""); 

			});
			if (augmentationCallback) {
				rect.call(visModel.augment("entries",augmentationCallback));
			}
		};

		plotHistory(historyDates, historyData); 

		function monthPath(t0) {
			var t1 = new Date(t0.getUTCFullYear(), t0.getUTCMonth() + 1, 0),
			d0 = +day(t0), w0 = +week(t0),
			d1 = +day(t1), w1 = +week(t1);
			return "M" + (w0 + 1) * z + "," + d0 * z
			+ "H" + w0 * z + "V" + 7 * z
			+ "H" + w1 * z + "V" + (d1 + 1) * z
			+ "H" + (w1 + 1) * z + "V" + 0
			+ "H" + (w0 + 1) * z + "Z";
		}

	}

