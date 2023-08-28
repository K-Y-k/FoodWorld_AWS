var searchForm = document.getElementById("search-form");
var categoryChoice = document.getElementById("category-choice");


// 카테고리 선택 시
function categoryHandleChange() {
    var selectedCategory = categoryChoice.value;
    sessionStorage.setItem('category', selectedCategory);

    searchForm.submit();
}


// 페이지 로드 시 sessionStorage에 저장된 카테고리 값에 따라 select 요소들의 display 속성 설정
window.onload = function() {
    var selectedCategory = sessionStorage.getItem('category');
    categoryChoice.value = selectedCategory;
}