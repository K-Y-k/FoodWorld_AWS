let memberId = 0;
let childId = 0;
let childFirst = true;
let lastCursorChildId = $(".lastCursorChildId").attr("id");
let dataType = '';
let writer = '';
let title = document.getElementById('container-title');

// 게시글/댓글/메뉴 리스트 세로 스크롤 끝에 닿을시 ajax 실행
var childContainer = document.getElementById('childContainer');
childContainer.addEventListener('scroll', function() {
    if (childContainer.scrollTop + childContainer.clientHeight >= childContainer.scrollHeight) {
        scrollChild(memberId);
    }
});


// 해당 회원에 따른 게시글/댓글/메뉴 조회
function findChild() {
    console.log("lastCursorChildId=", lastCursorChildId);

    $("#childList").empty();
    childFirst = true;
    lastCursorChildId = 0;

    // 클릭한 요소 가져오기
    var boardElement = event.target;
    // 클릭한 요소의 data 속성 값 가져오기
    var getMemberId = boardElement.getAttribute("data-member-id");
    var getWriter = boardElement.getAttribute("data-writer-id");
    var getDataType = boardElement.getAttribute("data-type-id");
    writer = getWriter;
    memberId = getMemberId;
    dataType = getDataType;

    // 선택한 것에 따른 관리창 제목 변경
    if (dataType === "board") {
        title.textContent = "선택한 회원의 게시글 관리"
    } else if (dataType === "comment") {
        title.textContent = "선택한 회원의 댓글 관리"
    } else if (dataType === "menu") {
        title.textContent = "선택한 회원의 메뉴 관리"
    }

    console.log("childFirst=", childFirst);
    console.log("memberId=", memberId);
    console.log("dataType=", dataType);


	$.ajax({
	    type: "GET",
		url: '/admin/api/member/' + dataType,
		dataType: "json",
		data: {lastCursorChildId, childFirst, memberId},
		async: false,
		success: function(result) {
		    console.log("자식 JSON", JSON.stringify(result))
		    let memberInfo = `<h5 style="width: 380px; text-align: center;">선택한 회원: ${writer}</h5>`
            $("#childList").append(memberInfo);

		    if (result.last ==  true) {
                console.log("마지막 페이지");

            	$.each(result.content, function(index, child){
                    console.log("자식 JSON의 내용에서 가져온 요소: ", index);

                    let childItem = getChildItem(child);
                    $("#childList").append(childItem);

                    lastCursorChildId = child.id;
                 });
		        if (childFirst) {
                    childFirst = false;
                }
            }
		    else {
		        $.each(result.content, function(index, child){
                    console.log("자식 JSON의 내용에서 가져온 요소: ", index);

                    let childItem = getChildItem(child);
                    $("#childList").append(childItem);

                    lastCursorChildId = child.id;
		        });
		        if (childFirst) {
                    childFirst = false;
                }
		    }
	    },
	    error: function (error) {
            console.log("오류", error);
        }
	});
}


// 게시글/댓글/메뉴 관리 창 스크롤 내릴 때의 조회
function scrollChild(memberId) {
    console.log("lastCursorChildId=", lastCursorChildId);
    console.log("childFirst=", childFirst);
    console.log("memberId=", memberId);

	$.ajax({
	    type: "GET",
		url: '/admin/api/member/' + dataType,
		dataType: "json",
		data: {lastCursorChildId, childFirst, memberId},
		async: false,
		success: function(result) {
		    console.log("자식 JSON", JSON.stringify(result))

		    if (result.last ==  true) {
                console.log("마지막 페이지");

            	$.each(result.content, function(index, child){
                    console.log("자식 JSON의 내용에서 가져온 요소: ", index);

                    let childItem = getChildItem(child);
                    $("#childList").append(childItem);

                    lastCursorChildId = child.id;
                 });
            }
		    else {
		        $.each(result.content, function(index, child){
                    console.log("자식 JSON의 내용에서 가져온 요소: ", index);

                    let childItem = getChildItem(child);
                    $("#childList").append(childItem);

                    lastCursorChildId = child.id;
		        });
		    }
	    },
	    error: function (error) {
            console.log("오류", error);
        }
	});
}


// 게시글/댓글/메뉴 삭제
function deleteChild() {
    // 클릭한 요소 가져오기
    var childElement = event.target;
    // 클릭한 요소의 data 속성 값 가져오기
    var getChildId = childElement.getAttribute("data-child-id");
    childId = getChildId;

    // 삭제후 다시 처음처럼 비우고 다시 가져오기
    var confirmMessage = `삭제하시겠습니까?`;
    if (confirm(confirmMessage)) {
        $("#childList").empty();
        childFirst = true;
        lastCursorChildId = 0;

        console.log("childFirst=", childFirst);
        console.log("memberId=", memberId);
        console.log("childId=", childId);
        console.log("dataType=", dataType);

        $.ajax({
            type: "GET",
            url: '/admin/api/member/' + dataType + '/delete',
            dataType: "json",
            data: {lastCursorChildId, childFirst, memberId, childId},
            async: false,
            success: function(result) {
                console.log("자식 JSON", JSON.stringify(result))
		        let memberInfo = `<h5 style="width: 380px; text-align: center;">선택한 회원: ${writer}</h5>`
                $("#childList").append(memberInfo);

            if (result.last ==  true) {
                console.log("마지막 페이지");

            	$.each(result.content, function(index, child){
                    console.log("자식 JSON의 내용에서 가져온 요소: ", index);

                    let childItem = getChildItem(child);
                    $("#childList").append(childItem);

                    lastCursorChildId = child.id;
                 });
		        if (childFirst) {
                    childFirst = false;
                }
            }
		    else {
		        $.each(result.content, function(index, child){
                    console.log("자식 JSON의 내용에서 가져온 요소: ", index);

                    let childItem = getChildItem(child);
                    $("#childList").append(childItem);

                    lastCursorChildId = child.id;
		        });
		        if (childFirst) {
                    childFirst = false;
                }
		    }
            },
            error: function (error) {
                console.log("오류", error);
            }
        });
	}
}


// 받아온 JSON을 html 형식으로 가공해서 넣기
function getChildItem(child) {
    let date = new Date(child.createdDate);
    console.log(child.createdDate)

    let nowDate = new Date();
    console.log(nowDate);

    var comparedDate = dateCompare(date, nowDate);

    if (dataType === "board") {
        let item = `<div style="margin-left: 5px; margin-top: 30px;">${child.id}</div>
                        <table style="width: 370px; margin-left: 5px; border: 1px solid black;">
                            <tr>
                                <td>
                                    <span style="font-size: 15px;"><${child.boardType}></span>
                                </td>

                                <td>
                                    <div style="width: 170px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${child.title}</div>
                                </td>

                                <td>
                                    <button class="btn btnEvent"
                                            data-child-id="${child.id}"
                                            data-writer-id="${child.writer}"
                                            onclick="deleteChild()" type="button"
                                            style="float:right; width: 80px; background-color: #007bff; color: #ffffff;">
                                            삭제
                                    </button>
                                </td>
                            </tr>

                            <tr>
                                <td colspan="3" style="width: 370px; word-break: break-all;">
                                    <span>${child.content}</span>
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
    else if (dataType === "comment") {
        let item = `<div style="margin-left: 5px; margin-top: 30px;">${child.id}</div>
                        <table style="width: 370px; margin-left: 5px; border: 1px solid black;">
                            <tr>
                                <td>
                                    <img src="/profileImageUpload/${child.member.profileFile.storedFileName}"
                                        class="rounded-circle"
                                        style="width: 50px; height: 50px;">
                                </td>

                                <td>
                                    <span>${child.member.name}</span>
                                </td>

                                <td>
                                    <button class="btn btnEvent"
                                            data-child-id="${child.id}"
                                            data-writer-id="${child.member.name}"
                                            onclick="deleteChild()" type="button"
                                            style="float:right; width: 80px; background-color: #007bff; color: #ffffff;">
                                            삭제
                                    </button>
                                </td>
                            </tr>

                            <tr>
                                <td colspan="3" style="width: 370px; word-break: break-all;">
                                    <span>${child.content}</span>
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
    else if (dataType === "menu") {
        let item = `<div style="margin-left: 5px; margin-top: 30px;">${child.id}</div>
                        <table style="width: 370px; margin-left: 5px; border: 1px solid black;">
                            <tr>
                                <td>
                                    <span style="font-size: 15px;"><${child.category}></span>
                                </td>

                                <td>
                                    <div style="width: 170px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${child.franchises}</div>
                                </td>

                                <td>
                                    <button class="btn btnEvent"
                                            data-child-id="${child.id}"
                                            onclick="deleteChild()" type="button"
                                            style="float:right; width: 80px; background-color: #007bff; color: #ffffff;">
                                            삭제
                                    </button>
                                </td>
                            </tr>

                            <tr>
                                <td colspan="3" style="width: 370px; word-break: break-all;">
                                    <span>${child.menuName}</span>
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