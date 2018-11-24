/* CONFIGURACAO TOOLTIP EM INPUT */
function carregarConfiguracoesTolltips() {
	$('input, select, textarea, i.check-enabled, i.fa-user, td').tooltip('hide');
	$('input, select, textarea, i.check-enabled, i.fa-user, td').tooltip({
		placement : 'top',
		trigger : 'hover',
		html : true,
		container : 'body'
	});
}

$(document)
		.ready(
				function() {
					
						$('.voltar').click(function(e) {
							e.preventDefault();
							history.back();
						});

					/* CHAMA INICIAIS */
					gerenciaContainers();	
					
					/*$('.geral').on('change', '.select-status', function() {
						ativaDesativaJustificativa();
					});
					
					function ativaDesativaJustificativa() {
						if ( $('.select-status').val() == 'false') {
							$('#modalJustificativaDesativacao').modal('show');
						} 
					}*/
										
					/* CONTAINER SEMPRE O RESTO DO CONTEUDO */
					function gerenciaContainers() {

						$('.container').fadeIn(1000);
						larguraUsuario = $(window).outerWidth();
						$('.menu').fadeIn(1000);

						if ($(window).outerWidth() < 1280) {
							$('.container').css('width',
									1280 - $('.menu').outerWidth());
							$('.geral').css('width', 1280);
						} else {
							$('.container').css('width',
									larguraUsuario - $('.menu').outerWidth());
							$('.geral').css('width', larguraUsuario);
						}

						heightMenu();
					}

					$('ul.menuUl li a').click(function(e) {
						if ($(this).attr('href') == '#') {
							e.preventDefault();
						}
						if ($(this).parent().next().is(':hidden')) {
							$('.submenu').hide('slow');
							$(this).parent().next().show('slow');
						} else {
							$('.submenu').hide('slow');
						}
					});

					/* MENU ESQUERDO SEMPRE 100% */
					function heightMenu() {
						$('.menu').css('height', 'auto');
						if ($('.geral').outerHeight() > $(window).outerHeight()) {
							$('.menu').css('height', $('.geral').outerHeight());
						} else {
							$('.menu').css('height', $(window).outerHeight());
						}
					}

					/* ACCORDION TABELAS DESENVOLVIMENTO */
					$('a.toogleDesenvolvimento').click(
							function(e) {
								e.preventDefault();
								$(this).parent().parent().parent().next()
										.children().toggle('slow');
							});

					/* MENU SEMPRE DO TAMANHO DO RODAPE */
					$(
							'a, nav li a, button, .linkaccorddion, .menu .menu-center p, .icomenu, label.tree-toggler, .permitirCheckbox, .outrasUnidades, .outrasUnidades1')
							.click(function() {
								setTimeout(function() {
									gerenciaContainers();
								}, 800);
							});

					

					/* CONFIGURACAO TOOLTIP EM MENU */
					$('.sub-header a i, .footer a i').tooltip({
						placement : 'right',
						trigger : 'hover',
						container : 'body'
					});

					/* TOOLTIP SUB-HEADER */
					$('.menu i').tooltip({
						placement : 'right',
						trigger : 'hover',
						container : 'body'
					});

					/* CONFIGURACAO TOOLTIP EM INPUT */
					$('input[type=password]').popover({
						placement : 'right',
						trigger : 'hover',
						container : 'body'
					});

					/* CONFIGURACAO MOSTRA CAMPO TROCAR FOTO */
					$('.image-user').mouseleave(function() {
						$('.image-user .alterar-foto').hide();
					});

					$('.image-user').mouseenter(function() {
						$('.image-user .alterar-foto').show();
					});

					/* TABS CONTENT */
					$('.tab-insercao a').click(function(e) {
						e.preventDefault()
						$(this).tab('show')
					});

					/* QUANDO ALGUEM REDIMENSIONAR O NAVEGADOR */
					$(window).resize(function() {
						setTimeout(function() {
							gerenciaContainers();
						}, 800);

					});
					
					carregarConfiguracoesTolltips();

					/* TIPO DE VISUALIZA��O PAGINA GESTAO METAS */
					$('#tipoVisualizacaoInformada')
							.change(
									function() {
										var selecionado = $(this).val();
										switch (selecionado) {
										case '2':
											$("#visuNomeColaborador")
													.slideUp(
															'slow',
															function() {
																$(
																		"#visuTipoDependencia")
																		.slideDown(
																				'slow',
																				function() {
																					gerenciaContainers();
																				});
															});
											break;
										default:
											$("#visuTipoDependencia")
													.slideUp(
															'slow',
															function() {
																$(
																		"#visuNomeColaborador")
																		.slideDown(
																				'slow',
																				function() {
																					gerenciaContainers();
																				});
															});
											break;
										}
									});

					/* ACAO PAGINA GESTAO METAS */
					$('#tipoAcao').change(function() {
						var selecionado = $(this).val();
						switch (selecionado) {
						case '3':
							$('#modalObservacao').modal('show');
							break;
						}
					});

					/* MENU ESQUERDO */
					$('.menu i.fa-bars').click(function() {
						$('.menu').width(178);
						$('.menu i.icomenu').hide();
						$('.menu .menu-center').css('margin-left', '0px');
						gerenciaContainers();
					});

					/* MENU ESQUERDO */
					$('.menu>.menu-center>p').click(function() {
						$('.menu').width(40);
						$('.menu i.icomenu').show();
						$('.menu .menu-center').css('margin-left', '40px');
						gerenciaContainers();
					});

					$('#tipoMetaTrabalhar')
							.change(
									function() {
										var selecionado = $(this).val();
										switch (selecionado) {
										case 'equipe':
											$(".dados-individual")
													.slideUp(
															'slow',
															function() {
																$(
																		".dados-equipe")
																		.slideDown(
																				'slow',
																				function() {
																					gerenciaContainers();
																				});
															});
											break;
										case 'individual':
											$(".dados-equipe")
													.slideUp(
															'slow',
															function() {
																$(
																		".dados-individual")
																		.slideDown(
																				'slow',
																				function() {
																					gerenciaContainers();
																				});
															});
											break;
										}
									});

					$('#tipoMetaTrabalharResultados')
							.change(
									function() {
										var selecionado = $(this).val();
										switch (selecionado) {
										case 'equipe':
											$(".dados-individual-resultados")
													.slideUp(
															'slow',
															function() {
																$(
																		".dados-equipe-resultados")
																		.slideDown(
																				'slow',
																				function() {
																					gerenciaContainers();
																				});
															});
											break;
										case 'individual':
											$(".dados-equipe-resultados")
													.slideUp(
															'slow',
															function() {
																$(
																		".dados-individual-resultados")
																		.slideDown(
																				'slow',
																				function() {
																					gerenciaContainers();
																				});
															});
											break;
										}
									});

				});