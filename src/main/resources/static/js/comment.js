var updateInputs = document.querySelectorAll('.commentUpdateInput');

// 댓글 내용 입력안하고 공백만 등록시
updateInputs.forEach(function(updateInput) {
    var textarea = updateInput.querySelector('textarea'); // 해당 form 태그의 내부 textarea 가져오기

    updateInput.addEventListener('submit', function(e) {
        if (textarea.value.trim() == '') {
            e.preventDefault();
            alert('수정할 내용을 입력하세요');
         }
    });
});

// 1) 기존 댓글 내용 안 보이게하고 수정하는 input 폼 나타나게함
// 2) 수정하는 input 폼이 나타난 상태에서 다른 댓글을 수정하려고 할 때 그 댓글의 수정폼만 남기게 하기위한 필터링 작업
function updateComment(commentId) {
    var commentContentClass = document.querySelectorAll('.updating-comment');
    var commentInputClass = document.querySelectorAll('.updating-input');


    if (commentContentClass.length > 0 && commentInputClass.length > 0) {
        commentContentClass.forEach(comment => {
            comment.style.display = 'block';
        });

        commentInputClass.forEach(input => {
            input.style.display = 'none';
        });

        var commentContentElement = document.getElementById('commentContent_' + commentId);
        var commentInputElement = document.getElementById('commentContentInput_' + commentId);

        commentContentElement.style.display = 'none';
        commentInputElement.style.display = 'block';

        commentContentElement.classList.add('updating-comment');
        commentInputElement.classList.add('updating-input');
    }
    else {
        var commentContentElement = document.getElementById('commentContent_' + commentId);
        var commentInputElement = document.getElementById('commentContentInput_' + commentId);

        commentContentElement.style.display = 'none';
        commentInputElement.style.display = 'block';

        commentContentElement.classList.add('updating-comment');
        commentInputElement.classList.add('updating-input');
    }
}