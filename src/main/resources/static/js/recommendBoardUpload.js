var select = document.getElementById("category-choice");
var restaurantArea = document.getElementById("restaurant-area");
var menuChoice = document.getElementById("menu-choice");
var form = document.getElementById("submit-form");
var areaMessage = '식당 주소를 넣어주세요';
var menuMessage = '메뉴 카테고리를 선택해주세요';

form.addEventListener("submit", function(event) {
    console.log("식당 = ", restaurantArea.value)
    console.log("메뉴 = ", menuChoice.value)

    var selectedIndex = select.selectedIndex;
    var selectedOption = select.options[selectedIndex];
    var selectedCategory = selectedOption.value;

    if (selectedCategory == '식당' && restaurantArea.value.trim() === "") {
        event.preventDefault();
        confirm(areaMessage);
    }
    else if (selectedCategory == '메뉴' && menuChoice.value.trim() === "") {
        event.preventDefault();
        confirm(menuMessage);
    }
});

function categoryHandleChange() {
    var selectedIndex = select.selectedIndex;
    var selectedOption = select.options[selectedIndex];
    var selectedCategory = selectedOption.value;

    if (selectedCategory == '카테고리 선택') {
        restaurantArea.style.display = 'none';
        restaurantArea.value = ''

        menuChoice.style.display = 'none';
        menuChoice.value = ''
    }

    if (selectedCategory == '식당') {
        restaurantArea.style.display = 'block';

        menuChoice.style.display = 'none';
        menuChoice.value = ''
    }

    if (selectedCategory == '메뉴') {
        menuChoice.style.display = 'block';

        restaurantArea.style.display = 'none';
        restaurantArea.value = ''
    }

}