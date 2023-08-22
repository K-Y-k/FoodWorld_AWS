let chatRoomId = 0;
let chatMessageId = 0;
let chatMessageFirst = true;
let lastCursorChatMessageId = $(".cursorChatMessageId").attr("id");


// 채팅 메시지 리스트 세로 스크롤 끝에 닿을시 ajax 실행
var chatMessageContainer = document.getElementById('chatMessageContainer');
chatMessageContainer.addEventListener('scroll', function() {
    if (chatMessageContainer.scrollTop + chatMessageContainer.clientHeight >= chatMessageContainer.scrollHeight) {
        scrollChatMessage(chatRoomId);
    }
});


// 해당 채팅방에 따른 채팅 메시지 조회
function findChatMessage() {
    console.log("채팅방 클릭시의 lastCursorChatMessageId=", lastCursorChatMessageId);

    $("#chatMessageList").empty();
    chatMessageFirst = true;
    lastCursorChatMessageId = 0;

    // 클릭한 요소 가져오기
    var chatRoomElement = event.target;
    // 클릭한 요소의 data 속성 값 가져오기
    var getChatRoomId = chatRoomElement.getAttribute("data-chatRoom-id");
    var getMember1Name = chatRoomElement.getAttribute("data-member1-name");
    var getMember2Name = chatRoomElement.getAttribute("data-member2-name");
    console.log("채팅방 클릭시의 getChatRoomId=", getChatRoomId);
    console.log("채팅방 클릭시의 getMember1Name=", getMember1Name);
    console.log("채팅방 클릭시의 getMember2Name=", getMember2Name);

    chatRoomId = getChatRoomId;

    console.log("채팅방 클릭시의 chatMessageFirst=", chatMessageFirst);
    console.log("채팅방 클릭시의 chatRoomId=", chatRoomId);

	$.ajax({
	    type: "GET",
		url: '/admin/api/chatMessage',
		dataType: "json",
		data: {lastCursorChatMessageId, chatMessageFirst, chatRoomId},
		async: false,
		success: function(result) {
		    console.log("채팅 메시지 JSON", JSON.stringify(result))
		    let chatRoomInfo = `<h5 style="width: 380px; margin-top: 10px; text-align: center;">${chatRoomId}번 채팅방</h5>
                             <h5 style="width: 380px; text-align: center;">채팅 참여자: ${getMember1Name}, ${getMember2Name} </h5>`
            $("#chatMessageList").append(chatRoomInfo);

		    if (result.last ==  true) {
                console.log("마지막 페이지");

            	$.each(result.content, function(index, chatMessage){
                    console.log("채팅 메시지 JSON의 내용에서 가져온 요소: ", index);

                    let chatMessageItem = getChatMessageItem(chatMessage);
                    $("#chatMessageList").append(chatMessageItem);

                    lastCursorChatMessageId = chatMessage.id;
                 });
                 if (chatMessageFirst) {
                     chatMessageFirst = false;
                 }
            }
		    else {
		        $.each(result.content, function(index, chatMessage){
		            console.log("채팅 메시지 JSON의 내용에서 가져온 요소: ", index);

		            let chatMessageItem = getChatMessageItem(chatMessage);
                    $("#chatMessageList").append(chatMessageItem);

                    lastCursorChatMessageId = chatMessage.id;
		        });
		        if (chatMessageFirst) {
                    chatMessageFirst = false;
                }
		    }
	    },
	    error: function (error) {
            console.log("오류", error);
        }
	});
}


// 채팅 메시지 관리 창 스크롤 내릴 때의 조회
function scrollChatMessage(chatRoomId) {
    console.log("스크롤시의 lastCursorChatMessageId=", lastCursorChatMessageId);
    console.log("스크롤시의 chatMessageFirst=", chatMessageFirst);
    console.log("스크롤시의 chatRoomId=", chatRoomId);

	$.ajax({
	    type: "GET",
		url: '/admin/api/chatMessage',
		dataType: "json",
		data: {lastCursorChatMessageId, chatMessageFirst, chatRoomId},
		async: false,
		success: function(result) {
		    console.log("채팅 메시지 JSON", JSON.stringify(result))

            if (result.last ==  true) {
                console.log("마지막 페이지");

            	$.each(result.content, function(index, chatMessage){
                    console.log("채팅 메시지 JSON의 내용에서 가져온 요소: ", index);

                    let chatMessageItem = getChatMessageItem(chatMessage);
                    $("#chatMessageList").append(chatMessageItem);

                    lastCursorChatMessageId = chatMessage.id;
                 });
            }
		    else {
		        $.each(result.content, function(index, chatMessage){
		            console.log("채팅 메시지 JSON의 내용에서 가져온 요소: ", index);

		            let chatMessageItem = getChatMessageItem(chatMessage);
                    $("#chatMessageList").append(chatMessageItem);

                    lastCursorChatMessageId = chatMessage.id;
		        });
		    }
	    },
	    error: function (error) {
            console.log("오류", error);
        }
	});
}


// 채팅 메시지 삭제
function deleteChatMessage() {
    // 클릭한 요소 가져오기
    var chatMessageElement = event.target;
    // 클릭한 요소의 속성 값 가져오기
    var getChatMessageId = chatMessageElement.getAttribute("data-chatMessage-id");
    chatMessageId = getChatMessageId;
    var getMember1Name = chatMessageElement.getAttribute("data-member1-name");
    var getMember2Name = chatMessageElement.getAttribute("data-member2-name");

    console.log("chatRoomId=", chatRoomId);

    var confirmMessage = `채팅 메시지를 삭제하시겠습니까?`;
    if (confirm(confirmMessage)) {
        $("#chatMessageList").empty();
        chatMessageFirst = true;
        lastCursorChatMessageId = 0;

        $.ajax({
            type: "GET",
            url: '/admin/api/chatMessage/delete',
            dataType: "json",
            data: {lastCursorChatMessageId, chatMessageFirst, chatRoomId, chatMessageId},
            async: false,
            success: function(result) {
                console.log("채팅 메시지 JSON", JSON.stringify(result))
                let chatRoomInfo = `<h5 style="width: 380px; margin-top: 10px; text-align: center;">${chatRoomId}번 채팅방</h5>
                                    <h5 style="width: 380px; text-align: center;">채팅 참여자: ${getMember1Name}, ${getMember2Name} </h5>`
                $("#chatMessageList").append(chatRoomInfo);

                if (result.last ==  true) {
                    console.log("마지막 페이지");

                    $.each(result.content, function(index, chatMessage){
                        console.log("채팅 메시지 JSON의 내용에서 가져온 요소: ", index);

                        let chatMessageItem = getChatMessageItem(chatMessage);
                        $("#chatMessageList").append(chatMessageItem);

                        lastCursorChatMessageId = chatMessage.id;
                     });
                    if (chatMessageFirst) {
                        chatMessageFirst = false;
                    }
                }
                else {
                    $.each(result.content, function(index, chatMessage){
                        console.log("채팅 메시지 JSON의 내용에서 가져온 요소: ", index);

                        let chatMessageItem = getChatMessageItem(chatMessage);
                        $("#chatMessageList").append(chatMessageItem);

                        lastCursorChatMessageId = chatMessage.id;
                    });
                    if (chatMessageFirst) {
                        chatMessageFirst = false;
                    }
                }
	        },
            error: function (error) {
                console.log("오류", error);
            }
        });
	}
}


// 채팅 메시지 JSON을 html 형식으로 가공해서 넣기
function getChatMessageItem(chatMessage) {
    let date = new Date(chatMessage.createdDate);
    console.log(chatMessage.createdDate)

    let nowDate = new Date();
    console.log(nowDate);

    var comparedDate = dateCompare(date, nowDate);

    let item = `<table style="width: 370px; margin-left: 5px; margin-top: 20px; border: 1px solid black;">
                    <tr>
                        <td>
                            <img src="/profileImageUpload/${chatMessage.senderProfile}"
                                 class="rounded-circle"
                                 style="width: 50px; height: 50px;">
                        </td>

                        <td>
                            <span>${chatMessage.sender}</span>
                        </td>

                        <td>
                            <button class="btn btnEvent"
                                    data-chatMessage-id="${chatMessage.id}"
                                    data-member1-name="${chatMessage.chatRoom.member1.name}"
                                    data-member2-name="${chatMessage.chatRoom.member2.name}"
                                    onclick="deleteChatMessage()" type="button"
                                    style="float:right; width: 80px; background-color: #007bff; color: #ffffff;">
                                    삭제
                            </button>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="3" style="width: 370px; word-break: break-all;">
                            <span>${chatMessage.content}</span>
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