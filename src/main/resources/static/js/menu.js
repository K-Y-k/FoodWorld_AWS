var circle = document.getElementById("circle");
var up_btn = document.getElementById("up_btn");
var down_btn = document.getElementById("down_btn");

var rotateValue = circle.style.transform;
var rotateSum;

up_btn.onclick = function() {      // 카테고리 회줜을 원으로 배치하여 사용한 기능 클릭시 90도 회전하게 함
	rotateSum = rotateValue + "rotate(-90deg)";
	circle.style.transform = rotateSum;
	rotateValue = rotateSum;
}
down_btn.onclick = function() {
	rotateSum = rotateValue + "rotate(90deg)";
	circle.style.transform = rotateSum;
	rotateValue = rotateSum;
}


let selectedCategory;

$("select.form-select").change(function () {
    selectedCategory = $(this).val();
    console.log("선택된 카테고리: ", selectedCategory)
});

function randomMenu() {  // 랜덤 메뉴 함수
	$.ajax({
        type: "GET",
    	url: '/menu/api/randomMenu',
    	dataType: "json",
    	data: {selectedCategory},
    	beforeSend: function() {
    	},
    	async: false,
    	success: function(result) {
    	    console.log("선택된 카테고리: ", selectedCategory)
            console.log(JSON.stringify(result))
    		let randomPick = result; // 단일 객체

            $("#category").text("카테고리: "+randomPick.category);
            $("#menuImg").attr("src", "/menuRecommendImageUpload/"+randomPick.storedFileName)
            $("#franchises").text(randomPick.franchises);
            $("#menuName").text(randomPick.menuName);
    	},
    	error: function (error) {
            console.log("오류", error);
         }
    });
}



const random_btn = document.getElementById("random_btn"); // 버튼을 동작하기 위한 id를 가져온 변수 선언
random_btn.addEventListener("click", randomMenu);         // 클릭 시 랜덤 메뉴 함수 작동