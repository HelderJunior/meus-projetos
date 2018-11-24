jQuery(document).ready(function(){

    /* CHAMA INICIAIS */
    centerLogin();

    /* CENTRALIZA LOGIN */
    function centerLogin() {
        var userHeight  = $(window).height();
        var loginHeight = $('.caixa-login').height();
        var result      = (userHeight-loginHeight)/2; 
        $('.caixa-login').css('margin-top',result-20);
    }

    /* QUANDO ALGUEM REDIMENSIONAR O NAVEGADOR */
    $(window).resize(function() {
        centerLogin();
    });
});