var categoryChoice = document.getElementById("category-choice");
var restaurantArea = document.getElementById("restaurant-area");
var menuChoice = document.getElementById("menu-choice");
var form = document.getElementById("category-form");

// 페이지 로드 시 sessionStorage에 저장된 카테고리 값에 따라 select 요소들의 display 속성 설정
window.onload = function() {
    var category = sessionStorage.getItem('category');
    var areaAndMenu = sessionStorage.getItem('areaAndMenu');

    if (category === '식당' && areaAndMenu === '') {
        categoryChoice.value = '식당';
        restaurantArea.style.display = 'block';
    }
    else if (category === '메뉴') {
       categoryChoice.value = '메뉴';
       menuChoice.style.display = 'block';
       menuChoice.value = areaAndMenu;
    }
    else if (category === '식당' && areaAndMenu != '') {
        categoryChoice.value = '식당';
        restaurantArea.style.display = 'block';
        restaurantArea.value = areaAndMenu;
    }
}

// 카테고리 선택 변경 시
function categoryHandleChange() {
    var category = categoryChoice.value;
    sessionStorage.setItem('category', category);
    sessionStorage.setItem('areaAndMenu', '');
    restaurantArea.value = null;
    menuChoice.value = null;

    if (category === '식당') {
        restaurantArea.style.display = 'block';
        menuChoice.style.display = 'none';
    } else if (category === '메뉴') {
        restaurantArea.style.display = 'none';
        menuChoice.style.display = 'block';
    } else {
        restaurantArea.style.display = 'none';
        menuChoice.style.display = 'none';
    }

    form.submit();
}

// 지역 선택 변경 시
function areaHandleChange() {
    var area = restaurantArea.value;
    sessionStorage.setItem('areaAndMenu', area);
    menuChoice.value = null;
    form.submit();
}

// 메뉴 선택 변경 시
function menuHandleChange() {
    var menu = menuChoice.value;
    sessionStorage.setItem('areaAndMenu', menu);
    restaurantArea.value = null;
    form.submit();
}



//function categoryHandleChange() {
//    var selectedIndex = select.selectedIndex;
//    var selectedOption = select.options[selectedIndex];
//    var selectedCategory = selectedOption.value;
//
//    if (selectedCategory == '카테고리 선택') {
//        console.log('뭐지', selectedCategory)
//        restaurantArea.style.display = 'none';
//        menuChoice.style.display = 'none';
//    }
//
//    if (selectedCategory == '식당') {
//        restaurantArea.style.display = 'block';
//        menuChoice.style.display = 'none';
//
//         $.ajax({
//             type: "GET",
//             url: '/boards/api/recommendBoard',
//             dataType: 'json',
//             data: { selectedCategory: selectedCategory },
//             beforeSend: function() {
//             },
//             async: false,
//             success: function(response) {
//                  console.log("성공 ")
//                  let boards = response.boards;
//                  let startPage = response.startPage;
//                  let nowPage = response.nowPage;
//                  let endPage = response.endPage;
//                  let localDateTime = response.localDateTime;
//                  let previous = response.previous;
//                  let next = response.next;
//                  let hasPrev = response.hasPrev;
//                  let hasNext = response.hasNext;
//             },
//             error: function (error) {
//                 console.log("오류", error);
//             }
//         });
//    }
//
//    if (selectedCategory == '메뉴') {
//        menuChoice.style.display = 'block';
//        restaurantArea.style.display = 'none';
//
//        $.ajax({
//            type: "GET",
//            url: '/boards/api/recommendBoard',
//            dataType: 'json',
//            data: { selectedCategory: selectedCategory },
//            beforeSend: function() {
//            },
//            async: false,
//            success: function(result) {
//                console.log("성공 ")
//            },
//            error: function (error) {
//                console.log("오류", error);
//            }
//        });
//    }
//}
//
//
//function areaHandleChange() {
//    var selectedIndex = restaurantArea.selectedIndex;
//    var selectedOption = restaurantArea.options[selectedIndex];
//    var selectedArea = selectedOption.value;
//
//    var selectedCategory = select.value;
//
//    $.ajax({
//        type: "GET",
//        url: '/boards/recommendBoard',
//        data: { selectedCategory: selectedCategory, selectedArea: selectedArea },
//        beforeSend: function() {
//        },
//        async: false,
//        success: function(result) {
//            console.log("성공 ")
//        },
//        error: function (error) {
//            console.log("오류", error);
//        }
//   });
//}
//
//
//function menuHandleChange() {
//    var selectedIndex = menuChoice.selectedIndex;
//    var selectedOption = menuChoice.options[selectedIndex];
//    var selectedMenu = menuChoice.value;
//
//    var selectedCategory = select.value;
//
//
//    $.ajax({
//        type: "GET",
//        url: '/boards/api/recommendBoard',
//        dataType: 'json',
//        data: { selectedCategory: selectedCategory, selectedMenu: selectedMenu },
//        beforeSend: function() {
//        },
//        async: false,
//        success: function(result) {
//            console.log("성공 ")
//        },
//        error: function (error) {
//            console.log("오류", error);
//        }
//   });
//}
