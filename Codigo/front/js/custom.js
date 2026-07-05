// to get current year
function getYear() {
    var year = new Date().getFullYear();
    var displayYear = document.getElementById("displayYear");
    if (displayYear) {
        displayYear.innerHTML = year;
    }
}

getYear();

// owl carousel 
if ($('.owl-carousel').length > 0) {
    $('.owl-carousel').owlCarousel({
        loop: true,
        margin: 10,
        nav: true,
        autoplay: true,
        autoplayHoverPause: true,
        responsive: {
            0: {
                items: 1
            },
            600: {
                items: 3
            },
            1000: {
                items: 6
            }
        }
    });
}