let page = 0;
let followerFirst = true;
let muckstarFirst = true;
let followerScrollCheck = true;
let muckstarScrollCheck = true;
let memberId = $(".memberId").attr("id");
let lastCursorFollowerId = $(".cursorFollowerId").attr("id");
let lastCursorBoardId = $(".cursorBoardId").attr("id");

var sectionHeight = document.getElementById('section-height');
let plusHeight = 0;

followerLoad();
//followRecommendLoad();
storyLoad();

// 팔로워 리스트 가로 스크롤 끝에 닿을시 ajax 실행
var followerContainer = document.getElementById('followerContainer');
followerContainer.addEventListener('scroll', function() {
    if (followerContainer.scrollLeft + followerContainer.clientWidth >= followerContainer.scrollWidth) {
        followerLoad();
    }
});

function followerLoad() {
	$.ajax({
	    type: "GET",
		url: '/members/api/follow/'+memberId,
		dataType: "json",
		data: {lastCursorFollowerId, followerFirst},
		async: false,
		success: function(result) {
		    console.log("팔로워 JSON", JSON.stringify(result))

		    if(result.last ==  true) {
                console.log("마지막 페이지");
            	followerScrollCheck = false;

            	$.each(result.content, function(index, follower){
                    console.log("팔로워 JSON의 내용에서 가져온 요소: ", index);

                    let followerItem = getFollowerItem(follower);
                    $("#followerContainer").append(followerItem);

                    lastCursorFollowerId = follower.id;
                 });
            }
		    else {
		        $.each(result.content, function(index, follower){
		            console.log("팔로워 JSON의 내용에서 가져온 요소: ", index);

		            let followerItem = getFollowerItem(follower);
                    $("#followerContainer").append(followerItem);

                    lastCursorFollowerId = follower.id;
		        });
		        if (followerFirst) {
                    followerFirst = false;
                }
		    }
	    },
	    error: function (error) {
            console.log("오류", error);
        }
	});
}

function getFollowerItem(follower) {
    let item = `<table style="margin-left: 20px; margin-top: 10px;">
                    <tr>
                        <td>
                            <a href="/members/profile/${follower.fromMember.id}">
                                <img src="${follower.fromMember.profileFile.path}"
                                    class="rounded-circle"
                                    style="width: 70px; height: 70px; margin-left: 20px; text-align: center;">
                            </a>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <h5 style="margin-left: 20px; text-align: center;">${follower.fromMember.name}</h5>
                        </td>
                    </tr>
                </table>`;

    console.log("가져온 요소의 출력 결과", item);
	return item;
}


//// 팔로우 추천 리스트 스크롤 끝에 닿을시 기존 데이터 비우고 ajax 실행하여 새로 가져옴
//var followRecommendContainer = document.getElementById('followRecommendContainer');
//followRecommendContainer.addEventListener('scroll', function() {
//});
//
//function followRecommendLoad() {
//	$.ajax({
//	    type: "GET",
//		url: '/members/api/followRecommend',
//		dataType: "json",
//		data: {userId},
//		async: false,
//		success: function(result) {
//		    console.log("팔로우 추천 리스트 JSON", JSON.stringify(result))
//
//            $.each(result.content, function(index, followRecommend){
//                console.log("팔로우 추천 리스트 JSON의 내용에서 가져온 요소: ", index);
//
//                let followRecommendItem = getFollowRecommendItem(followRecommend);
//                $("#followRecommendContainer").append(followRecommendItem);
//
//            });
//	    },
//	    error: function (error) {
//            console.log("오류", error);
//        }
//	});
//}
//
//function getFollowRecommendItem(followRecommend) {
//    let item = `<table style="margin-top: 5px;">
//                    <tr>
//                        <td>
//                             <a href="/members/profile/${followRecommend.id}">
//                                 <img src="/profileImageUpload/${followRecommend.profileFile.storedFileName}"
//                                      class="rounded-circle"
//                                      style="width: 70px; height: 70px;">
//                             </a>
//                        </td>
//                        <td>
//                            <a th:href="/members/profile/${followRecommend.id}" style="margin-left: 15px; text-decoration: none;">
//                                <span th:text="${followRecommend.name}"></span>
//                            </a>
//                        </td>
//                    </tr>
//               </table>`;
//
//    console.log("가져온 요소의 출력 결과", item);
//	return item;
//}


$(window).scroll(function() {
    if ($(window).scrollTop() == $(document).height() - $(window).height()) {
      console.log(++page);

      if (muckstarScrollCheck == true) {
            storyLoad();
      }
//    넣는 방식 예제 : $("#card").append("<h1>Page " + page + "</h1>So<BR/>MANY<BR/>BRS<BR/>YEAHHH~<BR/>So<BR/>MANY<BR/>BRS<BR/>YEAHHH~");
    }
});

function storyLoad() {
	$.ajax({
	    type: "GET",
		url: '/members/api/muckstarBoard',
		dataType: "json",
		data: {lastCursorBoardId, memberId, muckstarFirst},
		beforeSend: function() {
		    $('#loading').show();
		},
		async: false,
		success: function(result) {
		    console.log(JSON.stringify(result))

		    if(result.last ==  true) {
                console.log("마지막 페이지");
            	muckstarScrollCheck = false;

                plusHeight += 100
		        sectionHeight.style.height = plusHeight+'vh';

            	$.each(result.content, function(index, board){
                    console.log("JSON의 내용에서 가져온 요소: ", index);

                    let muckstarItem = getStoryItem(board);
                    $("#mucstarList").append(muckstarItem);

                    lastCursorBoardId = board.id;
                 });
            }
		    else {
		        plusHeight += 100
            	sectionHeight.style.height = plusHeight+'vh';

		        $.each(result.content, function(index, board){
		            console.log("JSON의 내용에서 가져온 요소: ", index);

		            let muckstarItem = getStoryItem(board);
		            $("#mucstarList").append(muckstarItem);

		            lastCursorBoardId = board.id;
		        });
		        if (muckstarFirst) {
                    muckstarFirst = false;
                }
		    }
	    },
	    error: function (error) {
            console.log("오류", error);
        }
	});
}

function getStoryItem(board) {
    let item = `<a href="/boards/muckstarBoard/${board.id}">
                    <img class="muckstar-image" src="${board.boardFiles[0].path}">
                </a>`;

    console.log("첫번째 이미지: ", board.boardFiles[0].storedFileName)
    console.log("가져온 요소의 출력 결과", item);

	return item;
}
