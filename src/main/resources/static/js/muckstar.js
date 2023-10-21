var page = 0;
var first = true;
var scrollCheck = true;
var lastCursorBoardId = $(".cursorBoardId").attr("id");

//if($(window).scrollTop() + $(window).height() == $(document).height()) {
//    storyLoad();
//}

storyLoad();

$(window).scroll(function() {
    if (Math.ceil($(window).scrollTop()) >= Math.floor($(document).height() - $(window).height())) {
        console.log(++window.page);

        console.log('스크롤 상태', window.scrollCheck);
        console.log('처음인가', window.first);
        console.log("마지막 글 id = ", window.lastCursorBoardId);

        if (window.scrollCheck == true) {
            storyLoad();
        }
//    넣는 방식 예제 : $("#card").append("<h1>Page " + page + "</h1>So<BR/>MANY<BR/>BRS<BR/>YEAHHH~<BR/>So<BR/>MANY<BR/>BRS<BR/>YEAHHH~");
    }
});


// 검색 기능 : 각 page, first, scrollCheck, lastCursorBoardId를 초기 값으로 다시 설정하고 검색을 시작한다.
var searchButton = document.getElementById('searchButton');

searchButton.addEventListener('click', function(e) {
    $("#mucstarList").empty()

    window.page = 0;
    window.first = true;
    window.scrollCheck = true;
    window.lastCursorBoardId = $(".cursorBoardId").attr("id");
    console.log("마지막 글 id = ", lastCursorBoardId);

    var writerSearchKeyword = $('#writerSearchKeyword').val();
    console.log("작성자 = ", writerSearchKeyword);

	$.ajax({
	    type: "GET",
		url: '/boards/api/muckstarBoard',
		dataType: "json",
		data: {lastCursorBoardId, first, writerSearchKeyword},
		beforeSend: function() {
		    $('#loading').show();
		},
		async: false,
		success: function(result) {
		    console.log(JSON.stringify(result))

		    $.each(result.content, function(index, board){
		        console.log("JSON의 내용에서 가져온 요소: ", index);

		        let muckstarItem = getStoryItem(board);
		        $("#mucstarList").append(muckstarItem);

		            lastCursorBoardId = board.id;
		    });
		    if (first) {
                first = false;
            }
	    },
	    error: function (error) {
            console.log("오류", error);
        }
	});
});


// Slice 페이징 처리
function storyLoad() {
    console.log("마지막 글 id = ", lastCursorBoardId);

    var writerSearchKeyword = $('#writerSearchKeyword').val();
    console.log("작성자 = ", writerSearchKeyword);

	$.ajax({
	    type: "GET",
		url: '/boards/api/muckstarBoard',
		dataType: "json",
		data: {lastCursorBoardId, first, writerSearchKeyword},
		beforeSend: function() {
		    $('#loading').show();
		},
		async: false,
		success: function(result) {
		    console.log(JSON.stringify(result))

		    if(result.last ==  true) {
		        console.log("마지막 페이지");
		        scrollCheck = false;

		        $.each(result.content, function(index, board){
                    console.log("JSON의 내용에서 가져온 요소: ", index);

                	let muckstarItem = getStoryItem(board);
                	$("#mucstarList").append(muckstarItem);

                	lastCursorBoardId = board.id;
                });
		    }
		    else {
		        $.each(result.content, function(index, board){
		            console.log("JSON의 내용에서 가져온 요소: ", index);

		            let muckstarItem = getStoryItem(board);
		            $("#mucstarList").append(muckstarItem);

		            lastCursorBoardId = board.id;
		        });
		        if (first) {
                    first = false;
                }
		    }
	    },
	    error: function (error) {
            console.log("오류", error);
        }
	});
}


function getStoryItem(board) {
    let date = new Date(board.createdDate);
    console.log(board.createdDate)

    let nowDate = new Date();
    console.log(nowDate);

    var comparedDate = dateCompare(date, nowDate);

    let item = `<div class="mucstarList_item" style="width: 1145px; height: 681px;">
                    <table style="margin-left: 100px;">
                        <tr>
                            <td> <img src="/image/main_img/main_background3.PNG" style="width: 376px; height: 472px;"></td>

                            <td>
                                <div class="card" id="card" style="margin-top: 50px; left: 10%; width: 652px; height: 660px;">
                                    <a href="/boards/muckstarBoard/${board.id}">
                                        <img class="muckstar-image" src="${board.boardFiles[0].path}"
                                             style="width: 645px; height: 600px;">
                                    </a>

                                    <div class="input-group" style="margin-left: 50px; width: 651px; height: 60px;">
                                        <button class="btn btnEvent"
                                                id="${board.id}"
                                                onclick="likeUpdate(this);" type="button">
                                            <img src="/image/muckstargram_img/favorite_icon.PNG" style="width: 53px; height: 46px;">
                                        </button>

                                        <span class="like" id="likeCount${board.id}" style="font-weight: 500; font-size: 30px;">${board.likeCount}</span>

                                        <img src="/image/muckstargram_img/comment_icon.PNG" style = "width: 49px; height: 46px; margin-left: 5%; margin-top:1%">
                                        <span class="comment" style ="font-weight: 500; font-size: 30px; margin-left: 20px;">${board.commentCount}</span>

                                        <span id="date" style="float: right; font-size: 20px; margin-left: 200px;">${comparedDate}</span>
                                    </div>
                                </div>
                            </td>

                            <td>
                                <img src="/image/food/sin.jpg" style="margin-left: 150px; width: 327px; height: 436px;">
                            </td>
                        </tr>
                    </table>
                </div>`;

    console.log("첫번째 이미지: ", board.boardFiles[0].storedFileName)
    console.log("가져온 요소의 출력 결과", item);

	return item;
}


function likeUpdate(boardId) {
    var boardId = boardId.id;
    var likeCount = document.getElementById('likeCount' + boardId);

    console.log("board=", boardId)
    console.log("userId=", userId)

    if (userId === 'GuestId'){
        var confirmMessage = `회원만 좋아요를 누를 수 있습니다. 로그인 먼저 해주세요!`;
        if (confirm(confirmMessage)) {
            var loginUrl = `/members/login`;
            location.href = loginUrl;
        }
    }
	else {
        $.ajax({
            type: "GET",
            url: '/boards/api/muckstarBoard/' + boardId + '/like',
            dataType: "json",
            data: {userId: userId},
            async: false,
            success: function(result) {
                console.log(JSON.stringify(result))
                likeCount.innerText = JSON.stringify(result);
            },
            error: function (error) {
                console.log("오류", error);
            }
        });
	}
}


function dateCompare(date, nowDate) {
        let year = date.getFullYear();
        let month = date.getMonth() + 1;
        let day = date.getDate();
        let hour = date.getHours();
        let minute = date.getMinutes();

        month = month >= 10 ? month : '0' + month;
        day = day >= 10 ? day : '0' + day;
        hour = hour >= 10 ? hour : '0' + hour;
        minute = minute >= 10 ? minute : '0' + minute;

        let nowYear = nowDate.getFullYear();
        let nowMonth = nowDate.getMonth() + 1;
        let nowDay = nowDate.getDate();

        nowMonth = month >= 10 ? month : '0' + month;
        nowDay = day >= 10 ? day : '0' + day;

        if (year == nowYear && month == nowMonth && day == nowDay) {
            return hour + ':' + minute;
        } else {
            return year + '-' + month + '-' + day + ' ' + hour + ':' + minute;
        }
}

//     // 다중 파일 모두 출력하기
//     // 1. $.each 반복 방식
//     $.each(board.boardFiles, function(index, boardFile){
//               item += `<img class="muckstar-image" src="/imageFileUpload/${boardFile.storedFileName}"
//                             style="max-width: 100%; height: 90%;">`;
//     console.log(index+"번째 이미지 파일 = ", boardFile.storedFileName);
//     })

//     // 2, 배열변수.forEach 반복 방식
//     board.boardFiles.forEach((boardFile)=>{
//               item += `<img class="muckstar-image" src="/imageFileUpload/${boardFile.storedFileName}"
//                             style="max-width: 100%; height: 90%;">`;
//     });

