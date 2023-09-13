let boardId = 0;
let commentId = 0;
let commentFirst = true;
let lastCursorCommentId = $(".cursorCommentId").attr("id");


// 댓글 리스트 세로 스크롤 끝에 닿을시 ajax 실행
var commentContainer = document.getElementById('commentContainer');
commentContainer.addEventListener('scroll', function() {
    if (commentContainer.scrollTop + commentContainer.clientHeight >= commentContainer.scrollHeight) {
        scrollComment(boardId);
    }
});


// 해당 게시글에 따른 댓글 조회
function findComment() {
    console.log("lastCursorCommentId=", lastCursorCommentId);

    $("#commentList").empty();
    commentFirst = true;
    lastCursorCommentId = 0;

    // 클릭한 요소 가져오기
    var boardElement = event.target;
    // 클릭한 요소의 data 속성 값 가져오기
    var getBoardId = boardElement.getAttribute("data-board-id");
    var writer = boardElement.getAttribute("data-writer-id");

    boardId = getBoardId;

    console.log("commentFirst=", commentFirst);
    console.log("boarId=", boardId);

	$.ajax({
	    type: "GET",
		url: '/admin/api/comment',
		dataType: "json",
		data: {lastCursorCommentId, commentFirst, boardId},
		async: false,
		success: function(result) {
		    console.log("팔로워 JSON", JSON.stringify(result))
		    let boardInfo = `<h5 style="width: 380px; margin-top: 10px; text-align: center;">${boardId}번 게시글의 댓글들</h5>
                             <h5 style="width: 380px; text-align: center;">게시글 작성자: ${writer}</h5>`
            $("#commentList").append(boardInfo);

		    if (result.last ==  true) {
                console.log("마지막 페이지");

            	$.each(result.content, function(index, comment){
                    console.log("댓글 JSON의 내용에서 가져온 요소: ", index);

                    let commentItem = getCommentItem(comment);
                    $("#commentList").append(commentItem);

                    lastCursorCommentId = comment.id;
                 });
		        if (commentFirst) {
                    commentFirst = false;
                }
            }
		    else {
		        $.each(result.content, function(index, comment){
		            console.log("댓글 JSON의 내용에서 가져온 요소: ", index);

		            let commentItem = getCommentItem(comment);
                    $("#commentList").append(commentItem);

                    lastCursorCommentId = comment.id;
		        });
		        if (commentFirst) {
                    commentFirst = false;
                }
		    }
	    },
	    error: function (error) {
            console.log("오류", error);
        }
	});
}


// 댓글 관리 창 스크롤 내릴 때의 조회
function scrollComment(boardId) {
    console.log("lastCursorCommentId=", lastCursorCommentId);
    console.log("commentFirst=", commentFirst);
    console.log("boarId=", boardId);

	$.ajax({
	    type: "GET",
		url: '/admin/api/comment',
		dataType: "json",
		data: {lastCursorCommentId, commentFirst, boardId},
		async: false,
		success: function(result) {
		    console.log("댓글 JSON", JSON.stringify(result))

		    if (result.last ==  true) {
                console.log("마지막 페이지");

            	$.each(result.content, function(index, comment){
                    console.log("댓글 JSON의 내용에서 가져온 요소: ", index);

                    let commentItem = getCommentItem(comment);
                    $("#commentList").append(commentItem);

                    lastCursorCommentId = comment.id;
                 });
            }
		    else {
		        $.each(result.content, function(index, comment){
		            console.log("댓글 JSON의 내용에서 가져온 요소: ", index);

		            let commentItem = getCommentItem(comment);
                    $("#commentList").append(commentItem);

                    lastCursorCommentId = comment.id;
		        });
		    }
	    },
	    error: function (error) {
            console.log("오류", error);
        }
	});
}


// 댓글 삭제
function deleteComment() {
    // 클릭한 요소 가져오기
    var commentElement = event.target;
    // 클릭한 요소의 data 속성 값 가져오기
    var getCommentId = commentElement.getAttribute("data-comment-id");
    var getBoardId = commentElement.getAttribute("data-board-id");
    var writer = commentElement.getAttribute("data-writer-id");

    commentId = getCommentId;

    console.log("commentFirst=", commentFirst);
    console.log("boarId=", boardId);
    console.log("commentId=", commentId);

    var confirmMessage = `댓글을 삭제하시겠습니까?`;
    if (confirm(confirmMessage)) {
        $("#commentList").empty();
        commentFirst = true;
        lastCursorCommentId = 0;

        $.ajax({
            type: "GET",
            url: '/admin/api/comment/delete',
            dataType: "json",
            data: {lastCursorCommentId, commentFirst, boardId, commentId},
            async: false,
            success: function(result) {
                console.log("팔로워 JSON", JSON.stringify(result))
                let boardInfo = `<h5 style="width: 380px; margin-top: 10px; text-align: center;">${boardId}번 게시글의 댓글들</h5>
                                 <h5 style="width: 380px; text-align: center;">게시글 작성자: ${writer}</h5>`
                $("#commentList").append(boardInfo);

                if (result.last ==  true) {
                    console.log("마지막 페이지");

                    $.each(result.content, function(index, comment){
                        console.log("댓글 JSON의 내용에서 가져온 요소: ", index);

                        let commentItem = getCommentItem(comment);
                        $("#commentList").append(commentItem);

                        lastCursorCommentId = comment.id;
                     });
                    if (commentFirst) {
                        commentFirst = false;
                    }
                }
                else {
                    $.each(result.content, function(index, comment){
                        console.log("댓글 JSON의 내용에서 가져온 요소: ", index);

                        let commentItem = getCommentItem(comment);
                        $("#commentList").append(commentItem);

                        lastCursorCommentId = comment.id;
                    });
                    if (commentFirst) {
                        commentFirst = false;
                    }
                }
            },
            error: function (error) {
                console.log("오류", error);
            }
        });
	}
}


// 댓글 JSON을 html 형식으로 가공해서 넣기
function getCommentItem(comment) {
    let date = new Date(comment.createdDate);
    console.log(comment.createdDate)

    let nowDate = new Date();
    console.log(nowDate);

    var comparedDate = dateCompare(date, nowDate);

    let item = `<table style="width: 370px; margin-left: 5px; margin-top: 20px; border: 1px solid black;">
                    <tr>
                        <td>
                            <img src="${comment.member.profileFile.path}"
                                 class="rounded-circle"
                                 style="width: 50px; height: 50px;">
                        </td>

                        <td>
                            <span>${comment.member.name}</span>
                        </td>

                        <td>
                            <button class="btn btnEvent"
                                    data-comment-id="${comment.id}"
                                    data-board-id="${comment.board.id}"
                                    data-writer-id="${comment.board.writer}"
                                    onclick="deleteComment()" type="button"
                                    style="float:right; width: 80px; background-color: #007bff; color: #ffffff;">
                                    삭제
                            </button>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="3" style="width: 370px; word-break: break-all;">
                            <div>${comment.content}</div>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <span id="date" style="float: right; font-size: 15px;">${comparedDate}</span>
                        </td>
                    </tr>
                </table>`;

    console.log("가져온 요소의 출력 결과", item);
	return item;
}


// 날짜 변환 함수
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