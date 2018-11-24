/*function desenharAcompanhamento(divId, valorPreenchido) {

	var archtype = Raphael(divId, 200, 200);
	var set = archtype.set();

	function drawCircle() {
		var archtype = Raphael(divId, 200, 200);

		archtype.customAttributes.arc = function(xloc, yloc, value, total, R) {
			var alpha = 360 / total * value, a = (90 - alpha) * Math.PI / 180, x = xloc
					+ R * Math.cos(a), y = yloc - R * Math.sin(a), path;

			if (total == value) {
				path = [ [ "M", xloc, yloc - R ],
						[ "A", R, R, 0, 1, 1, xloc - 0.01, yloc - R ] ];
			} else {
				path = [ [ "M", xloc, yloc - R ],
						[ "A", R, R, 0, +(alpha > 180), 1, x, y ] ];
			}
			return {
				path : path
			};
		};

		archtype.circle(100, 100, 75).attr({
			"fill" : "#fff",
			"stroke" : "#CCCCCC",
			"stroke-width" : "30"
		});

		var my_arc = archtype.path().attr({
			"stroke" : "#A2A2A2",
			"stroke-width" : 30,
			arc : [ 100, 100, 0, 100, 75 ]
		});
		
		my_arc.animate({
			arc : [ 100, 100, valorPreenchido, 100, 75 ]
		}, 1000);
		
		var text1 = archtype.text(100, 100, valorPreenchido + "%");
		
		text1.attr({
			"font-family" : "arial",
			fill : "#666666",
			"font-size" : 40,
			'text-anchor':'start'
		});

	}

	drawCircle();
};*/

function desenharAcompanhamento(divId, valorPreenchido) {
	var circuloConteiner = $('#' + divId);
	var activeBorder = $("#activeBorder", circuloConteiner);
	$('.prec', circuloConteiner).text(valorPreenchido + "%");
	if (valorPreenchido > 100)
		valorPreenchido = 100;

	var deg = valorPreenchido * 3.6;

	if (deg <= 180) {
		activeBorder
				.css(
						'background-image',
						'linear-gradient('
								+ (90 + deg)
								+ 'deg, transparent 50%, #CCCCCC 50%),linear-gradient(90deg, #CCCCCC 50%, transparent 50%)');
	} else {
		activeBorder
				.css(
						'background-image',
						'linear-gradient('
								+ (deg - 90)
								+ 'deg, transparent 50%, #A2A2A2 50%),linear-gradient(90deg, #CCCCCC 50%, transparent 50%)');
	}

	var startDeg = $("#startDeg", circuloConteiner).attr("class");
	activeBorder.css('transform', 'rotate(' + startDeg + 'deg)');
	$("#circle", circuloConteiner).css('transform',
			'rotate(' + (-startDeg) + 'deg)');
}

function printByClass(cssclass) {
	html2canvas($('.' + cssclass).get(), {
		onrendered : function(canvas) {
			// document.body.appendChild(canvas);

			var newWindow = window.open();
			// newWindow.document.write(document.getElementById("output").innerHTML);
			newWindow.document.body.appendChild(canvas);
			// newWindow.print();
		}
	});
};

function printById(cssclass) {
	html2canvas($('#' + cssclass).get(), {
		onrendered : function(canvas) {
			// document.body.appendChild(canvas);

			var newWindow = window.open();
			// newWindow.document.write(document.getElementById("output").innerHTML);
			newWindow.document.body.appendChild(canvas);
			// newWindow.print();
		}
	});
};

function irPara(selector) {
	/*
	 * jQuery(document.body).animate({ 'scrollTop':
	 * jQuery(selector).offset().top }, 1000);
	 */

	window.scrollTo(0, 0);
}

SGM = {
	tempoRestante : 1800, // segundos
	inativo : false,
	timeoutFunction : null,
	atualizaHoraInterval : null,
	decrementarTempo : function() {
		if (SGM.inativo) {

			SGM.tempoRestante = SGM.tempoRestante - 1;

			if (SGM.tempoRestante <= 0) {
				window.location = urlLogout;
			}

			var minutos = ((SGM.tempoRestante % 3600) / 60);
			var segundos = (SGM.tempoRestante % 60);

			minutos = parseInt(''+minutos);
			segundos = parseInt(''+segundos);
			
			if(minutos < 10){
				minutos = '0'+minutos;
			}
			
			if(segundos < 10){
				segundos = '0'+segundos;
			}

			jQuery('#minutes').text(minutos);
			jQuery('#seconds').text(segundos == 0 ? '00' : segundos);

		}
	},

	onInative : function() {
		//jQuery("body").fadeTo("slow", .5);
		SGM.inativo = true;
		if (SGM.timeoutFunction == null) {
			SGM.timeoutFunction = window
					.setInterval(SGM.decrementarTempo, 1000);
		}
	},

	onActive : function() {
		//jQuery("body").fadeTo("fast", 1);
		SGM.inativo = false;
		SGM.tempoRestante = 1800;

		jQuery('#minutes').text(30);
		jQuery('#seconds').text('00');
		window.clearInterval(SGM.timeoutFunction);
		SGM.timeoutFunction = null;
	},
	
	atualizarHora: function(){
		SGM.atualizaHoraInterval = window.setInterval(function(){
			 var today = new Date();
		     var h = today.getHours();
		     var m = today.getMinutes();
		     var s = today.getSeconds();
		     
		     if(h < 10){
		    	 h = '0'+h;
		     }
		     
		     if(m < 10){
		    	 m = '0'+m;
		     }
		     
		     if(s < 10){
		    	 s = '0'+s;
		     }
		     
		     jQuery('#hora').text(h+":"+m+":"+s);
		}, 1000);
	}
};
