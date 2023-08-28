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



var connectingElement = document.querySelector('.connecting');
var chatContent = document.querySelector('#chatContent');
var messageInput = document.querySelector('#message');

var stompClient = null;


// roomId 파라미터 가져오기
const url = new URL(location.href).searchParams;
const roomId = url.get('roomId');

console.log("방 값: ", roomId)

if (roomId != null) {
    connect()
}


// 소켓 실행 시
function connect() {
    console.log("현재 회원 이름: ", username)
    console.log("상대 회원 Id: ", receiverId)
    console.log("방 이름: ", roomId)


    if(username) {
        var socket = new SockJS('/ws-stomp');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }

}

// 소켓 연결 시
function onConnected() {
    // sub 할 url => /sub/chat/room?roomId로 구독하여 해당 경로로 메시지 발송시 onMessageReceived 함수 실행
    stompClient.subscribe('/sub/chat/room/'+ roomId, onMessageReceived);

    // 서버에 username을 가진 유저가 들어왔다는 것을 알림 즉, /pub/chat/enterUser로 메시지를 보냄
    stompClient.send("/pub/chat/enterUser",
            {},
            JSON.stringify({
                "roomId": roomId,
                sender: username,
                senderId: userId,
                receiverId: receiverId,
                type: 'ENTER'
            })
        )

    chatContent.scrollTop = chatContent.scrollHeight;
    messageInput.placeholder = "자유롭게 채팅을 입력해서 상대방에게 전송해보세요."
}

// 회원 퇴장시
function onLeave() {
    var confirmMessage = `퇴장하시겠습니까?`;

    if (confirm(confirmMessage)) {
        stompClient.send("/pub/chat/leaveUser",
                {},
                JSON.stringify({
                    "roomId": roomId,
                    sender: username,
                    senderId: userId,
                    receiverId: receiverId,
                    type: 'LEAVE'
                })
        )
    }
}

// 에러 발생시
function onError(error) {
    connectingElement.style.display = 'block';
    connectingElement.textContent = '연결이 원할하지 않습니다. 다시 참여해주세요!';
    connectingElement.style.color = 'red';
}

// input에서 입력해서 메시지 전송시 : 메시지 전송할 때는 JSON 형식을 메시지를 서버에 전달한다.
function sendMessage() {
    var messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        var chatMessage = {
            "roomId": roomId,
            sender: username,
            senderId: userId,
            receiverId: receiverId,
            message: messageInput.value,
            type: 'TALK'
        };

        stompClient.send("/pub/chat/sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
}

// 서버에서 보내온 메시지 받기
// 메시지를 받을 때도 마찬가지로 JSON 타입으로 받으며, 넘어온 JSON 형식의 메시지를 parse 해서 사용한다.
function onMessageReceived(payload) {
    console.log("payload 정보 : " + payload);
    var chat = JSON.parse(payload.body);

    if (chat.type == 'ENTER') {       // enter라면
        let enterMessage = `<li style="list-style-type: none; text-align: center; color: white;">`
                             + chat.message +
                           `</li>`

        $('#chatContent').append(enterMessage);
    } else if (chat.type == 'LEAVE') { // leave라면

        let leaveMessage = `<li style="list-style-type: none; text-align: center; color: white;">`
                             + chat.message +
                           `</li>`

        $('#chatContent').append(leaveMessage);
        chatContent.scrollTop = chatContent.scrollHeight;

        // 연결 끊기: 해당 클라이언트만 연결을 끊고 URL 변경
        if (chat.senderId == userId) {
            stompClient.disconnect(() => {
                window.location.href = "/";
            });
        }

        // 마지막 회원까지 퇴장하여 채팅방이 삭제된 경우 마지막 큻라이언트 회원 이동 처리
        if (chat.message.equals('채팅방 삭제')) {
            window.location.href = "/";
        }

    } else {                           // talk라면
        let date = new Date();
        let nowTime = createTime(date);

        if (chat.senderId == userId){ // 본인이 보낸 것이면
            let talkMessage = `<li style="margin-left: 40px; max-width: 280px; margin-top: 3%; list-style-type: none;">
                                 <table style="float: right;">
                                    <tr>
                                        <td style="vertical-align: bottom;">
                                            <span style="font-size: 15px; padding: 0px 5px 0px 5px;">`
                                                + nowTime +
                                            `</span>
                                         </td>

                                         <td style="background-color: lightyellow; padding: 5px 5px 5px 10px; border-radius: 1.0rem;">
                                            <span style="font-size: 15px; word-break: break-all;">`
                                                + chat.message +
                                            `</span>
                                        </td>
                                    </tr>
                                 </table>
                               </li>`

            $("#chatContent").append(talkMessage);
        } else { // 상대가 보낸 것이면
            let talkMessage = `<li style="width: 280px; float: left; list-style-type: none; margin-top: 3%; white-space: normal;">
                                    <table>
                                        <tr>
                                            <td>
                                                <table>
                                                    <tr>`
                                                        + chat.sender +
                                                    `</tr>

                                                    <tr>
                                                        <td style="background-color: violet; padding: 5px 5px 5px 10px; border-radius: 1.0rem;">
                                                            <span style="font-size: 15px; word-break: break-all;">`
                                                                + chat.message +
                                                            `</span>
                                                        </td>

                                                        <td style="vertical-align: bottom;">
                                                             <span style="font-size: 15px; padding: 0px 5px 0px 5px;">`
                                                                + nowTime +
                                                             `</span>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>
                                    </table>
                                 </li>`

            $("#chatContent").append(talkMessage);
        }
    }

    chatContent.scrollTop = chatContent.scrollHeight;
}


// 메시지 전송을 키보드 엔터키로 누를 때를 위한 Enter키 처리 함수
function handleKeyDown(event) {
    if (event.keyCode == 13) {
      sendMessage()
    }
 }


// 날짜 생성 : 현재 날짜를 시간:분 형태로 출력하기 위한 변환 함수
function createTime(date){
    let hour = date.getHours();
    let minute = date.getMinutes();

    if (hour < 10) {
        hour = '0' + hour;
    }

    if (minute < 10) {
        minute = '0' + minute;
    }

    let formattedTime = hour + ':' + minute;

    return formattedTime;
}
